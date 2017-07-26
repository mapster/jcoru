# JCoru

Java compile and JUnit test runner service.

To run the application through the packaged jar file you need to supply the jar-packaged libraries from _target/classes/lib_ in
a directory on the host that runs it, and specify the path through the commandline argument _--compilerLibsPath._

```java -jar target/jcoru.jar --compilerLibsPath=target/classes/lib```
