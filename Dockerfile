FROM gcr.io/google-appengine/openjdk
COPY target/jcoru.jar $APP_DESTINATION
