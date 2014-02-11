# LOngest Common sUbSTring   [![Build Status](https://travis-ci.org/gv0tch0/locust.png)](https://travis-ci.org/gv0tch0/locust)

## Building

The implementation is in java. To build please use [maven](http://maven.apache.org/ "Maven Home"). The build is exercised regularly by [travis](https://travis-ci.org/gv0tch0/locust).

Pull the repository and type `mvn` at the top-level repository directory. This would attempt to build the `dflt` profile. This profile includes all modules but the [functest](https://github.com/gv0tch0/locust/tree/master/functest) one. The `functest` build depends on a running locust installation.

### Build Dependencies

- Internet connection. This dependency gets relaxed if all of the dependent jars are present in the user's .m2/repository or if the user has access to a local-network maven proxy which has all of the dependencies.
- Maven 3.0.5+. Builds have been tested with 3.0.5 and 3.1.1.
- JDK 1.7+. Builds have been tested with OracleJDK version 1.7 and OpenJDK version 1.7. For the actual version numbers please refer to the project's [travis-ci](https://travis-ci.org/gv0tch0/locust) page.

## Running

The service can be run out-of-the-box on any java application container that comes with support for version [3.0](http://jcp.org/en/jsr/detail?id=315) of the Java Servlet API. The service has been tested on [Tomcat 7](http://tomcat.apache.org/download-70.cgi) and [Jetty 8](http://download.eclipse.org/jetty/stable-8/dist/).

A current version of the service can be found running on RedHat's [OpenShift](https://www.openshift.com) cloud at `http://locust-gv0tch0.rhcloud.com/lcs`.

### Tomcat 7

1. Build the `dflt` profile. Just type `mvn` at the project's toplevel directory.
2. Copy the `lcs.war` from the project's `webapi/target` folder to tomcat's `webapps` directory.
3. Start tomcat. To start tomcat in the foreground run `bin/catalina.sh run`. Or just run `bin/catalina.sh` to see the available options. If all is good and the tomcat configuration has not been touched, the service would be available at `http://localhost:8080/lcs`

### Jetty 8

1. Build the `dflt` profile. Just type `mvn` at the project's toplevel directory.
2. Copy the `lcs.war` from the project's `webapi/target` folder to jetty's `webapps` directory.
3. Define a context for the lcs.war in jetty's `contexts` directory.
4. Start jetty. To start it in the foreground run `bin/jetty.sh run`. Or just run `bin/jetty.sh` to see the available options. If all is good, the jetty configuration has not been touched, and the context for the lcs/war was defined as `/lcs` the service would be available at `http://localhost:8080/lcs/` (note the trailing `/`, which strictly speaking should be required by tomcat as well, since otherwise clients would fail to resolve embedded relative links). Jetty responds to requests to `http://localhost:8080/lcs` with a `302 Found` temporary redirect response which has its Location header set to the proper URI.

## API

The service exposes a single endpoint, `POST /lcs`, which allows clients to submit longest common substring requests.

### Request

The body of the request needs to be a [JSON](http://json.org) document and the `Content-type` request header needs to have a value of `application/json`. The document consists of a single field, `setOfStrings`, which is an array of documents. These nested documents consist of a single string field named `value`. For example the request to `POST /lcs` with the following body:
```javascript
{
  "setOfStrings" : [ { "value" : "foo" },
                     { "value" : "bar" } ]
}
```
asks the service to compute the longest common substring for the words `foo` and `bar`.

### Responses

#### For valid requests

When the request for longest common substring computation passes the validation rules the service responds with a `200 OK` response that contains a JSON document that looks almost the same as the request document with the exception that the array field's name is `lcs` as opposed to `setOfStrings`. The `lcs` array contains the longest common substring(s). It contains multiple substring objects when there are more than one common substring of a greatest length, in which case the substring objects are ordered in string natural ordering.

For example the response body for the request above is:
```javascript
{ "lcs" : [] }
```

The response body for a single word request:
```javascript
{
  "setOfStrings" : [ { "value" : "bar" } ]
}
```
is the word itself:
```javascript
{
  "lcs" : [ { "value" : "bar" } ]
}
```

The response body for a request that results in multiple longest common substrings:
```javascript
{
  "setOfStrings" : [ { "value" : "bartender" },
                     { "value" : "banter" } ]
}
```
is the substrings ordered alphabetically:
```javascript
{
  "lcs": [ { "value" : "ba" },
           { "value" : "er" },
           { "value" : "te" } ]
}
```

#### For invalid requests

The service responds with a `400 Bad Request` response when the request:
- Does not contain a JSON document in its body.
- Contains a syntactically incorrect JSON document.
- Represents a longest common substring request that:
  - Contains no words.
  - Contains an empty word.
  - Contains duplicate words.

The service responds with a `415 Unsupported Media Type` response when the `Content-type` header of the request is other than `application/json`.

## Test

The `core` and `service` modules are covered by [JUnit](http://junit.org) tests. The `functest` module exercises the service using the service's JSON API. The `functest` module is not part of the default `mvn` profile. Building it, means sending requests against a live deployment. It uses [HttpClient](http://hc.apache.org/httpclient-3.x/) to send the requests and `JUnit` to validate the responses.

The LCS service URL endpoint is configured in the [functest.properties](https://github.com/gv0tch0/locust/blob/master/functest/src/test/resources/io/github/gv0tch0/locust/functest/functest.properties) file. If the URL of the service that is to be exercised is different (e.g. does not run on localhost) this file needs to be updated before running the tests.

To run the functional tests make sure that the service is running and is reachable on the URL endpoint discussed above, and then run `mvn clean install` in the `functest` directory, or run `mvn -P ft clean install` in the project toplevel directory.

## TODO

- Add a `web` module, which exposes a longest common substring client that could be rendered by a browser and operated by a human.
- Doctor the JSON API. It is too verbose (no need for the nested documents that have the single `value` field).
- Make the request parsing stricter. Currently word values that are not strings (e.g. boolean `true`) are implicitly converted to strings.
- Make response bodies for invalid requests consistent. The validation that happnens in the application consistently responds with `JSON`. Unfortunately, the validation that we get for free from [Jersey](https://jersey.java.net/) spits back `XML` responses (with proper response codes, nevertheless).
- Fix the [OpenShift](https://www.openshift.com/) deployment from `travis`.
