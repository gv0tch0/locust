LOngest Common sUbSTring.
-------------------------

### Building

[![Build Status](https://travis-ci.org/gv0tch0/locust.png)](https://travis-ci.org/gv0tch0/locust)

The implementation is in java. To build please use [maven](http://maven.apache.org/ "Maven Home").

Simply pull the repository and type `mvn` at the top-level repository directory. This would attempt to build the `dflt` profile. The default profile includes all modules but the [functest](https://github.com/gv0tch0/locust/tree/master/functest) one. The `functest` module build depends on a running locust installation.

##### Build Dependencies
- Internet connection. This dependency gets relaxed if all of the dependent jars are present in the user's .m2/repository or if the user has access to a local-network maven proxy which has all of the dependencies.
- Maven 2.2.1+. Builds have been tested with 2.2.1, 3.0.5, and 3.3.1.
- JDK 1.6+. Builds have been tested with OracleJDK version 1.7 and OpenJDK versions 1.6 and 1.7. For the actual version numbers please refer to the project's [travis-ci](https://travis-ci.org/gv0tch0/locust) page.
