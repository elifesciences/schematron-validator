<?php

namespace eLife\App\Service;

use GuzzleHttp\Client;

/**
 * A client to talk to the {@code schematron-validator} backend service and return its outputs.
 */
class BackendClient
{
    /**
     * @var Client
     */
    private $client;

    public function __construct(Client $client)
    {
        $this->client = $client;
    }

    /**
     * Validate a document and.
     *
     * @param string $schemaId
     * @param $file
     *
     * @return array
     */
    public function validateDocument(string $schemaId, $file) : array
    {
        $response = $this->client->post(
            "/document-validator/$schemaId/file",
            [
                'multipart' => [
                    [
                        'name' => 'document',
                        'contents' => $file,
                    ],
                ],
            ]
        );

        $data = json_decode((string) $response->getBody(), true);
        if (JSON_ERROR_NONE !== json_last_error()) {
            throw new \RuntimeException('Failed to decode JSON from backend.');
        }

        return $data;
    }
}
