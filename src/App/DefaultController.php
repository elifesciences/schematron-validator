<?php

namespace eLife\App;

use Symfony\Component\HttpFoundation\Response;
use Monolog\Logger;

final class DefaultController
{

  private $logger;

  /**
   * DefaultController constructor.
   */
  public function __construct(Logger $logger) {
    $this->logger = $logger;
  }

  public function indexAction()
    {

//      $this->logger->info("testing");
//      $this->logger->debug("debug test");
        return new Response('Hello world!');
    }
}
