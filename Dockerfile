FROM maven:3.5-jdk-8-onbuild
ENTRYPOINT [ "sh", "-c", "mvn spring-boot:run -Drun.arguments=--compilerLibsPath=target/classes/lib"]
