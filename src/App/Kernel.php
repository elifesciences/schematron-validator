<?php

namespace eLife\App;

use Closure;
use ComposerLocator;
use eLife\ApiValidator\MessageValidator\JsonMessageValidator;
use eLife\ApiValidator\SchemaFinder\PathBasedSchemaFinder;
use eLife\App\Service\BackendClient;
use eLife\Logging\LoggingFactory;
use eLife\Ping\PingController;
use GuzzleHttp\Client;
use JsonSchema\Validator;
use Monolog\Logger;
use Silex\Application;
use Silex\Provider;
use Symfony\Bridge\PsrHttpMessage\Factory\DiactorosFactory;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Throwable;

final class Kernel implements MinimalKernel
{
    const ROOT = __DIR__.'/../..';

    private $app;

    public function __construct($config = [])
    {
        $app = new Application();
        $app->register(new Provider\ServiceControllerServiceProvider());

        // Load config
        $app['config'] = array_merge(
            [
                'debug' => true,
                'validate' => false,
                'file_logs_path' => __DIR__.'/logs',
            ],
            $config
        );

        $this->dependencies($app);
        $this->app = $this->applicationFlow($app);
    }

    public function dependencies(Application $app)
    {
        $app['psr7.bridge'] = function () {
            return new DiactorosFactory();
        };

        $app['message-validator'] = function (Application $app) {
            return new JsonMessageValidator(
                new PathBasedSchemaFinder(ComposerLocator::getPath('elife/api').'/dist/model'),
                new Validator()
            );
        };

        $app['logger'] = function (Application $app) {
            $factory = new LoggingFactory($app['config']['file_logs_path'], 'schematron-validator');

            return $factory->logger();
        };

        $app['schematron.backend_client'] = function (Application $app) {
            return new BackendClient(
                new Client(
                    [
                        'base_uri' => $app['config']['backend_uri'],
                    ]
                )
            );
        };

        $app['schematron.controller'] = function (Application $app) {
            return new DefaultController($app['schematron.backend_client']);
        };
        $app['ping.controller'] = function () {
            return new PingController();
        };
    }

    public function applicationFlow(Application $app): Application
    {
        $this->routes($app);
        $app->error([$this, 'handleException']);

        return $app;
    }

    public function routes(Application $app)
    {
        $app->post('/document-validator/{schemaId}/file', 'schematron.controller:validateFile');
        $app->get('/ping', 'ping.controller:pingAction');
    }

    public function handleException($e): Response
    {
        return new JsonResponse([
            'message' => $e->getMessage(),
            'line' => $e->getLine(),
            'file' => $e->getFile(),
            'trace' => $e->getTraceAsString(),
        ]);
    }

    public function withApp(callable $fn)
    {
        $boundFn = Closure::bind($fn, $this);
        $boundFn($this->app);

        return $this;
    }

    public function run()
    {
        return $this->app->run();
    }

    public function get($d)
    {
        return $this->app[$d];
    }

    public function validate(Request $request, Response $response)
    {
        try {
            if (strpos($response->headers->get('Content-Type'), 'json')) {
                $this->app['message-validator']->validate(
                    $this->app['psr7.bridge']->createResponse($response)
                );
            }
        } catch (Throwable $e) {
            if ($this->app['config']['debug']) {
                throw $e;
            }
        }
    }

    public function getApp() : Application
    {
        return $this->app;
    }
}
