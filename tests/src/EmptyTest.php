<?php

use Silex\WebTestCase;
use Symfony\Component\HttpFoundation\Request;

class EmptyTest extends \PHPUnit_Framework_TestCase
{
    /**
     * @before
     */
    public function setUpApp()
    {
        // bootstrap should return a Kernel object
        // to be used by tests or web/app_*.php files
        $this->app = require __DIR__.'/../../web/bootstrap.php';
    }

    public function testPing()
    {
        $response = $this->app->handle(Request::create('/ping'));
        $this->assertSame(
            200, 
            $response->getStatusCode(),
            var_export(json_decode($response->getContent(), true), true)
        );
    }
}
