package org.jboss.seam.exception.control.example.jaxrs.handler.declarative;

import java.lang.reflect.Method;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.example.jaxrs.handler.CatchResource;
import org.jboss.seam.rest.exceptions.ErrorMessageWrapper;

public class ExceptionResponseServiceHandler
{
   // does this have to be Instance?
   @Inject @CatchResource
   private Instance<ResponseBuilder> builderProvider;
   
   @AroundInvoke
   public Object processException(InvocationContext ctx)
   {
      Method m = ctx.getMethod();
      if (ctx.getParameters().length > 0 && ctx.getParameters()[0] instanceof CaughtException)
      {
         ResponseBuilder builder = builderProvider.get();
         CaughtException<?> c = (CaughtException<?>) ctx.getParameters()[0];
         if (m.isAnnotationPresent(SendHttpResponse.class))
         {
            SendHttpResponse r = m.getAnnotation(SendHttpResponse.class);
            String message = r.message();
            if (r.message().length() == 0 && r.passthru())
            {
               message = c.getException().getMessage();
            }
            
            builder.status(r.status());
            if (message != null && message.length() > 0)
            {
               builder.entity(new ErrorMessageWrapper(message));
            }
            
            c.handled();
         }
         else
         {
            builder.entity(new ErrorMessageWrapper("Unknown error"));
            c.handled();
         }
      }
      return Void.TYPE;
   }
}
