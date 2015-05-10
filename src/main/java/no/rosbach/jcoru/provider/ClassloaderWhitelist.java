package no.rosbach.jcoru.provider;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

@Target({FIELD, TYPE, METHOD, PARAMETER})
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassloaderWhitelist {
}
