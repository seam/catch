package org.jboss.seam.exception.control.test.extension;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.inject.Stereotype;

import org.jboss.seam.exception.control.HandlesExceptions;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Stereotype
@HandlesExceptions
@Target({ TYPE, METHOD, FIELD })
@Retention(RUNTIME)
@Documented
public @interface BleedingObject
{
}
