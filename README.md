Apereo CAS WAR Overlay Template
=====================================

### Some config changes for ease:

In this example project, some default cas config behaviours changed:

- ssl `keystore` location is under `resources/config/ssl`. You may need to change or import keystore
  to your jdk's `/lib/security/cacerts`.
- log4j2 config is under `resources/config/logging` and default level is set to `warn`.
- service registry is under `resources/config/services` and a default is added.

An example working cas for a question about webflow
decorations (https://groups.google.com/a/apereo.org/g/cas-user/c/pScwsrnyLTQ/m/bOf7a8tVBQAJ):

A smtp information needed for the problem to be seen(in the password changing screen) so you need to
set up the fields in the [application.yml](src%2Fmain%2Fresources%2Fapplication.yml) file(a gmail
smtp should work too):

```yaml
spring:
  mail:
    test-connection: true
    host: 'mail.myexample.com.tr'
    port: 587
    username: 'no-reply@myexample.com.tr'
    password: 'smtppassword'
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

### run the example

First change the user defined in
the [InMemoryAuthnHandlerConfig.java](src%2Fmain%2Fjava%2Forg%2Fapereo%2Fcas%2Fconfig%2FInMemoryAuthnHandlerConfig.java),
change the `demo` user's email for password reset instructions, you can use a temporary mail
from https://temp-mail.org

To run the example, run this command:

```shell
./gradlew run
```

go to https://localhost:8443/cas/login and use the given example user and password `demo` with a
password of `demo`

### Expected:

A defined flow variable `customData`, which has a method `getRandomVideoSrc()`, that returns a
random video src as string, locations of webm files in
the [resources/static/themes/customtheme/img](src%2Fmain%2Fresources%2Fstatic%2Fthemes%2Fcustomtheme%2Fimg)
directory for web page to display a random video each time page loads.

`customData` is working for the `login` webflow as explained in the docs, but,
to make it work on logout, a setter method should be called in the configuration class. but
for `pswdreset` webflow, i couldn't make it work. Here's the log on password change page.

```
Caused by: org.thymeleaf.exceptions.TemplateProcessingException: Exception evaluating SpringEL expression: "customData.getRandomVideoSrc()" (template: "layout" - line 52, col 9)

	at org.springframework.web.servlet.DispatcherServlet.processDispatchResult(DispatcherServlet.java:1148) ~[spring-webmvc-5.3.22.jar!/:5.3.22]
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1087) ~[spring-webmvc-5.3.22.jar!/:5.3.22]
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:963) ~[spring-webmvc-5.3.22.jar!/:5.3.22]
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006) ~[spring-webmvc-5.3.22.jar!/:5.3.22]
	... 41 more
Caused by: org.springframework.expression.spel.SpelEvaluationException: EL1011E: Method call: Attempted to call method getRandomVideoSrc() on null context object
```

Details:

From
the [WebFlowCustomDecorator.java](src%2Fmain%2Fjava%2Forg%2Fapereo%2Fcas%2Fconfig%2FWebFlowCustomDecorator.java)
file

```java 
//...
@Override
protected void doInitialize() {
// This works as explained in the docs. You'll see customData working in the main page and video element will be random on each refresh.
  super.createFlowVariable(super.getLoginFlow(), "customData", WebFlowCustomData.class);

  // This does not work
  // super.createFlowVariable(super.getFlow("logout"), "customData", WebFlowCustomData.class);

  // But if you register the logoutFlowDefinitionRegistry by calling the parent class's
  // setLogoutFlowDefinitionRegistry method like done in the WebFlowConfig class,
  // this code will register the variable in logout and you'll see that logoutFlow has the customData.
  super.

      createFlowVariable(super.getLogoutFlow(), "customData", WebFlowCustomData.class);

  // This does now work either.
  super.

      createFlowVariable(super.getFlow("pswdreset"), "customData", WebFlowCustomData.class);
}
//...
```

Webm video files in the project are added from:

- https://github.com/projectivetech/media-samples/blob/master/sample.webm
- https://www.webmfiles.org/demo-files/
- https://file-examples.com/index.php/sample-video-files/sample-webm-files-download/

WAR Overlay Type: `cas-overlay`

# Versions

- CAS Server `6.6.15.1`
- JDK `11`

# Build

To build the project, use:

```bash
# Use --refresh-dependencies to force-update SNAPSHOT versions
./gradlew clean build
```

To see what commands/tasks are available to the build script, run:

```bash
./gradlew tasks
```

If you need to, on Linux/Unix systems, you can delete all the existing artifacts
(artifacts and metadata) Gradle has downloaded using:

```bash
# Only do this when absolutely necessary
rm -rf $HOME/.gradle/caches/
```

Same strategy applies to Windows too, provided you switch `$HOME` to its equivalent in the above
command.

# Keystore

For the server to run successfully, you might need to create a keystore file.
This can either be done using the JDK's `keytool` utility or via the following command:

```bash
./gradlew createKeystore
```

Use the password `changeit` for both the keystore and the key/certificate entries.
Ensure the keystore is loaded up with keys and certificates of the server.

## Extension Modules

Extension modules may be specified under the `dependencies` block of
the [Gradle build script](build.gradle):

```gradle
dependencies {
    implementation "org.apereo.cas:cas-server-some-module"
    ...
}
```

To collect the list of all project modules and dependencies in the overlay:

```bash
./gradlew dependencies
```                                                                       

# Deployment

On a successful deployment via the following methods, the server will be available at:

* `https://localhost:8443/cas`

## Executable WAR

Run the server web application as an executable WAR. Note that running an executable WAR requires
CAS to use an embedded container such as Apache Tomcat, Jetty, etc.

The current servlet container is specified as `-tomcat`.

```bash
java -jar build/libs/cas.war
```

Or via:

```bash
./gradlew run
```

It is often an advantage to explode the generated web application and run it in unpacked mode.
One way to run an unpacked archive is by starting the appropriate launcher, as follows:

```bash
jar -xf build/libs/cas.war
cd build/libs
java org.springframework.boot.loader.launch.JarLauncher
```

This is slightly faster on startup (depending on the size of the WAR file) than
running from an unexploded archive. After startup, you should not expect any differences.

Debug the CAS web application as an executable WAR:

```bash
./gradlew debug
```

Or via:

```bash
java -Xdebug -Xrunjdwp:transport=dt_socket,address=5000,server=y,suspend=y -jar build/libs/cas.war
```

Run the CAS web application as a *standalone* executable WAR:

```bash
./gradlew clean executable
```

### CDS Support

CDS is a JVM feature that can help reduce the startup time and memory footprint of Java
applications. CAS via Spring Boot
now has support for easy creation of a CDS friendly layout. This layout can be created by extracting
the CAS web application file
with the help of the `tools` jarmode:

```bash
# Note:
# You must first build the web application with "executable" turned off
java -Djarmode=tools -jar build/libs/cas.war extract

# Perform a training run once
java -XX:ArchiveClassesAtExit=cas.jsa -Dspring.context.exit=onRefresh -jar cas/cas.war

# Run the CAS web application via CDS
java XX:SharedArchiveFile=cas.jsa -jar cas/cas.war
```

## External

Deploy the binary web application file in `build/libs` after a successful build to a servlet
container of choice.

# Retrieve Overlay Resources

To fetch and overlay a CAS resource or view, use:

```bash
./gradlew getResource -PresourceName=[resource-name]
```

# Create User Interface Themes Structure

You can use the overlay to construct the correct directory structure for custom inMemoryUser
interface themes:

```bash
./gradlew createTheme -Ptheme=redbeard
```

The generated directory structure should match the following:

```
├── redbeard.properties
├── static
│ └── themes
│     └── redbeard
│         ├── css
│         │ └── cas.css
│         └── js
│             └── cas.js
└── templates
    └── redbeard
        └── fragments
```

HTML templates and fragments can be moved into the above directory structure,
and the theme may be assigned to applications for use.

# List Overlay Resources

To list all available CAS views and templates:

```bash
./gradlew listTemplateViews
```

To unzip and explode the CAS web application file and the internal resources jar:

```bash
./gradlew explodeWar
```

# Configuration

- The `etc` directory contains the configuration files and directories that need to be copied
  to `/etc/cas/config`.

```bash
./gradlew copyCasConfiguration
```

- The specifics of the build are controlled using the `gradle.properties` file.

## Configuration Metadata

Configuration metadata allows you to export collection of CAS properties as a report into a file
that can later be examined. You will find a full list of CAS settings along with notes, types,
default and accepted values:

```bash
./gradlew exportConfigMetadata
```                           

# Duct

`duct` is a Gradle task to do quick smoke tests of multi-node CAS high-availability deployments. In
particular, it tests correctness of ticket
sharing between multiple individual CAS server nodes backed by distributed ticket registries such as
Hazelcast, Redis, etc.

This task requires CAS server nodes to **enable the CAS REST module**. It will **NOT** work without
it.

The task accepts the following properties:

- Arbitrary number of CAS server nodes specified via the `duct.cas.X` properties.
- URL of the service application registered with CAS specified via `duct.service`, for which tickets
  will be requested.
- `duct.username` and `duct.password` to use for authentication, when requesting ticket-granting
  tickets.

It automates the following scenario:

- Authenticate and issue a service ticket on one CAS node
- Validate this service ticket on the another node
- Repeat (You may cancel and stop the task at any time with `Ctrl+C`)

If the task succeeds, then we effectively have proven that the distributed ticket registry has been
set up and deployed
correctly and that there are no connectivity issues between CAS nodes.

To run the task, you may use:

```bash
./gradlew duct
    -Pduct.cas.1=https://node1.example.org/cas \
    -Pduct.cas.2=https://node2.example.org/cas \
    -Pduct.cas.3=https://node3.example.org/cas \
    -Pduct.cas.4=https://node4.example.org/cas \
    -Pduct.service=https://apereo.github.io \
    -Pduct.username=casuser \
    -Pduct.password=Mellon
```

You may also supply the following options:

- `duct.debug`: Boolean flag to output debug and verbose logging.
- `duct.duration`: Number of seconds, i.e. `30` to execute the scenario.
- `duct.count`: Number of iterations, i.e. `5` to execute the scenario.

