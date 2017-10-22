FROM maven:3.5-jdk-8-onbuild
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar target/jcoru.jar" ]
