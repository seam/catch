package org.jboss.seam.exception.control.example.jaxrs.handler.declarative;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SendHttpResponse
{
   int status() default 500;
   String message() default "";
   boolean passthru() default true;
}
