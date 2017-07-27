<?xml version="1.0" encoding="UTF-8"?>
<schema id="schematron-validator-test"
   xmlns="http://purl.oclc.org/dsdl/schematron"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xlink="http://www.w3.org/1999/xlink"
   queryBinding="xslt2">

  <title>eLife schematron validator test fixture schema</title>

  <pattern
     id="element-citation-book-tests"
     xmlns="http://purl.oclc.org/dsdl/schematron"
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
     <rule context="body" id="sch-body">
      <assert test="count(child)=1" role="error" id="sch-body-child">
          Body must have 1 child
      </assert>
    </rule>
  </pattern>
</schema>
