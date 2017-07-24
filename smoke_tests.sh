#!/usr/bin/env bash

[[ $(curl --output /dev/null -F "file=@article.xml" "http://localhost:8080/document-validator/pre-edit/file") == 200 ]]
