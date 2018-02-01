# Schematron Validator

Schematron is an API to validate the conformance of XML documents. This implementation checks its against the [eLife](https://elifesciences.org)'s standards for [JATS](https://jats.nlm.nih.gov/) XML.

## Usage

Submit an XML file via a POST HTTP request:

```
<form id="upload-form" enctype="multipart/form-data"
      action="https://schematron.elifesciences.org/document-validator/final/file"
      method="post">
    <input type="file" name="document" id="document"/>
    <input type="submit" value="Submit"/>
</form>
```

A JSON document will be returned, listing errors or warnings in the form of `diagnostics`.

The steps needed are:

1. Prepare XML document for validation (make sure there are 0 syntax errors in the document – some XML tooling is more lenient with regards to XML parsing, we're using Saxon, which should be fine in most cases)
1. Decide which document validation stage to run against (`pre-edit` or `final`).
1. Send a POST (multipart-form) request to https://schematron.elifesciences.org/document-validator/$type/file with a `document` field containing the file, and `$type` being the validation stage `pre-edit` or `final`.
1. Parse returned JSON in the format of:

```
{
  "status": "INVALID",
  "diagnostics": [
    {
      "context": "/article[1]/back[1]/ref-list[1]/ref[4]/element-citation[1]/year[1]",
      "message": "[err-elem-cit-gen-date-1-7]\n      If the <year> element contains any letter other than 'a' after the digits, there must be another \n      reference with the same first author surname (or collab) with the preceding letter after the year. \n      Reference '' does not fulfill this requirement.",
      "level": "ERROR"
    }
  ]
}
```

where the status can be `INVALID`, `VALID`, or `VALID_WITH_WARNINGS`.
