<?php

require_once __DIR__.'/bootstrap.php';

$config = include __DIR__.'/../config/config.php';

(new eLife\App\Kernel($config))->run();
