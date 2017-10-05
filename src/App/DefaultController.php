<?php

namespace eLife\App;

use eLife\App\Service\BackendClient;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;

final class DefaultController
{
    public function __construct(BackendClient $client)
    {
        $this->client = $client;
    }

    public function validateFile(Request $request, string $schemaId)
    {
        $documentFilePath = $request->files->get('document');
        $documentFile = fopen($documentFilePath->getRealPath(), 'r');

        try {
            return new JsonResponse($this->client->validateDocument($schemaId, $documentFile));
        } finally {
            fclose($documentFile);
        }
    }
}
