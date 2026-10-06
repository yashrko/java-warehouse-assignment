# Java Code Assignment

This is a short code assignment that explores various aspects of software development, including API implementation, documentation, persistence layer handling, and testing.

## About the assignment

You will find the tasks of this assignment on [CODE_ASSIGNMENT](CODE_ASSIGNMENT.md) file

## About the code base

This is based on https://github.com/quarkusio/quarkus-quickstarts

### Requirements

To compile and run this demo you will need:

- JDK 17+

In addition, you will need either a PostgreSQL database, or Docker to run one.

### Configuring JDK 17+

Make sure that `JAVA_HOME` environment variables has been set, and that a JDK 17+ `java` command is on the path.

## Building the demo

Run commands from `java-assignment/`. JDK 17+ is required. The Maven wrapper downloads Maven automatically.

## Build and test

```sh
./mvnw verify
```

## Running the demo

The default test configuration uses Quarkus Dev Services and therefore requires Docker. In CI or where Dev Services are unavailable, point `QUARKUS_DATASOURCE_JDBC_URL`, `QUARKUS_DATASOURCE_USERNAME`, and `QUARKUS_DATASOURCE_PASSWORD` at a PostgreSQL database. `verify` runs unit and integration tests, packages the runnable application, creates `target/site/jacoco/index.html`, and enforces at least 81% line coverage in the warehouse use-case package.

### Live coding with Quarkus

The Maven Quarkus plugin provides a development mode that supports
live coding. To try this out:

```sh
./mvnw quarkus:dev
```

In this mode you can make changes to the code and have the changes immediately applied, by just refreshing your browser.

    Hot reload works even when modifying your JPA entities.
    Try it! Even the database schema will be updated on the fly.

## (Optional) Run Quarkus in JVM mode

When you're done iterating in developer mode, you can run the application as a conventional jar file.

First compile it:

```sh
./mvnw package
```

Next we need to make sure you have a PostgreSQL instance running (Quarkus automatically starts one for dev and test mode). To set up a PostgreSQL database with Docker:

```sh
docker run -it --rm=true --name quarkus_test -e POSTGRES_USER=quarkus_test -e POSTGRES_PASSWORD=quarkus_test -e POSTGRES_DB=quarkus_test -p 15432:5432 postgres:13.3
```

Connection properties for the Agroal datasource are defined in the standard Quarkus configuration file,
`src/main/resources/application.properties`.

Then run it. On hosts whose default timezone is `Asia/Calcutta`, use UTC because PostgreSQL rejects that legacy timezone alias:

```sh
java -Duser.timezone=UTC -jar ./target/quarkus-app/quarkus-run.jar
```

In PowerShell, quote the JVM option and set the Java 17 installation first if needed:

```powershell
$env:JAVA_HOME = 'C:\path\to\jdk-17'
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
java '-Duser.timezone=UTC' '-Dquarkus.http.port=8080' -jar .\target\quarkus-app\quarkus-run.jar
```

The Docker client API is pinned to 1.44 in `src/main/resources/docker-java.properties` so Testcontainers can connect to Docker Engine 29. If you use an older Docker Engine that does not support API 1.44, adjust that compatibility setting to a version supported by your daemon.
    Have a look at how fast it boots.
    Or measure total native memory consumption...


## See the demo in your browser

Navigate to:

<http://localhost:8080/index.html>

Have fun, and join the team of contributors!

## Troubleshooting

Using **IntelliJ**, in case the generated code is not recognized and you have compilation failures, you may need to add `target/.../jaxrs` folder as "generated sources".
