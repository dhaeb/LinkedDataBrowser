# Linked Data Browser

This is a rich client scala play application to show easyly human consumable information form RDF graphs.

## Getting started

For development purposes, install the [typesafe acitvator](https://www.typesafe.com/get-started).
We are also using node js and npm to run bower for your javascript library dependency management. 
So ensure, that [npm / nodejs](https://nodejs.org/) is in the path of your plattform before running **activator run** in the root folder of our application.

## Deployment

- create a zip file using the **sbt dist** goal, which contains all library dependencies and also the embedded netty webserver which is advised when deploying play projects
- extract the zip on your server where you like
- adjust config parameters in application.conf (e.g. http.port which is the application listening on for http requests), 
  **important:** Adjust lucene search suggestions settings: 
  Don't forget the change the filepath property "ldb.dbpedia_indexdir", if you already got the index present on the server (got it as a zip).
  When creating the index from screch, ensure that the "ldb.dbpedia_surfaceforms" points to a valid "surface_forms.ttl" file and the indexdir points to a existing directory
- start the application with the scripts, which are located in the bin subdirectory of the deployment zip, according to the platform you running on


## License

The MIT License (MIT)

Copyright (c) 2015 Yves Bugge, Dan Häberlein

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
