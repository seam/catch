package org.jboss.seam.exception.control.example.jaxrs.handler.declarative;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.seam.solder.servicehandler.ServiceHandler;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ServiceHandler(ExceptionResponseServiceHandler.class)
public @interface ExceptionResponseService
{
}
