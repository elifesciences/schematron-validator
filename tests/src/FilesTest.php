<?php

use eLife\App\Kernel;
use Silex\WebTestCase;
use Symfony\Component\HttpFoundation\File\UploadedFile;

class ValidatorTest extends WebTestCase
{
    private static $fileToStatusMap = [
        'warning' => 'VALID_WITH_WARNINGS',
        'fail' => 'INVALID',
        'pass' => 'VALID',
    ];

    private static $validStages = [
        'pre-edit',
        'final',
    ];

    public function createApplication()
    {
        $config = require __DIR__.'/../../config/config.php';
        $this->kernel = new Kernel($config);

        return $this->kernel->getApp();
    }

    public function testPing()
    {
        $client = $this->createClient();
        $client->request('GET', '/ping');

        $this->assertTrue($client->getResponse()->isOk());
    }

    public static function testFilesProvider()
    {
        $directory = new \RecursiveDirectoryIterator(__DIR__.'/../../vendor/elife/reference-schematron/test-files');
        $iterator = new \RecursiveIteratorIterator($directory);
        $files = array();
        foreach ($iterator as $info) {
            if (!$info->isDir() && '.DS_Store' != $info->getFileName()) {
                $files[] = array($info->getPathname());
            }
        }

        return $files;
    }

    /**
     * @param $documentPath
     *
     * @dataProvider testFilesProvider
     */
    public function testFiles(string $documentPath)
    {
        $documentFilename = basename($documentPath);
        list($code, $stage) = explode('-', $documentFilename, 2);

        if (!isset(self::$fileToStatusMap[$code])) {
            throw new RuntimeException("Found invalid filename prefix: $code");
        }

        foreach (self::$validStages as $validStage) {
            if (0 === strpos($stage, $validStage)) {
                $stage = $validStage;
                break;
            }
        }

        if (!in_array($stage, ['pre-edit', 'final'])) {
            $stage = 'final';
        }

        $file = new UploadedFile(
            $documentPath,
            $documentFilename,
            'application/xml',
            filesize($documentPath),
            null,
            true
        );

        $client = $this->createClient();
        $client->request(
            'POST',
            "/document-validator/$stage/file",
            [],
            ['document' => $file]
        );

        $result = json_decode($client->getResponse()->getContent(), true);

        $expectedStatus = self::$fileToStatusMap[$code];
        $actualStatus = $result['status'];

        $message = implode(
            "\n",
            array_map(
                function (array $diagnostic) {
                    return $diagnostic['message'];
                },
                $result['diagnostics']
            )
        );

        $this->assertEquals(
            $expectedStatus,
            $actualStatus,
            "$documentFilename should $code on $stage, but didn't: $message"
        );
    }
}
