<?php

// Remove this if you want to suppress the warning.
require_once __DIR__.'/extra/gearman_shim.php';

return [
    'debug' => true,
    'validate' => true,
    'api_url' => 'http://0.0.0.0:1234',
    'annotation_cache' => false,
    'ttl' => 0,
];
