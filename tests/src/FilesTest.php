<?php


use Silex\WebTestCase;
use eLife\App\Kernel;
use Symfony\Component\HttpFoundation\File\UploadedFile;

class ValidatorTest extends WebTestCase
{
    private $manifest;

    public function createApplication()
    {
        $config = require __DIR__ . "/../../config/config.php";
        $this->kernel = new Kernel($config);
        return $this->kernel->getApp();
    }

    private function getManifest(){


    }

    public function testPing()
    {
        $client = $this->createClient();
        $crawler = $client->request('GET', '/ping');
        $this->assertTrue($client->getResponse()->isOk());
    }


    public static function testFilesProvider()
    {

        $directory = new \RecursiveDirectoryIterator(__DIR__ . "/../resources/test-files");
        $iterator = new \RecursiveIteratorIterator($directory);
        $files = array();
        foreach ($iterator as $info) {
            if (!$info->isDir() && $info->getFileName() != '.DS_Store'){
                $files[] = array($info->getPathname());
            }
        }
        return $files;
    }

    /**
     * @param $testFiles
     *
     * @dataProvider testFilesProvider
     */
    public function testFiles($testFiles){

        $file = new UploadedFile($testFiles, $testFiles, 'application/xml', filesize($testFiles), true, true);
        $client = $this->createClient();

        $client->request(
            'POST',
            '/document-validator/final/file',
            array(),
            array('document' => $file)
        );

        $result = json_decode($client->getResponse()->getContent(), true);
        $expectedStatus = (substr($testFiles, 0, 4) === 'pass') ? 'VALID' : 'INVALID';
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
            $actualStatus
        );


    }


}