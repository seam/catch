package org.jboss.seam.exception.control.example.jaxrs.handler.declarative;

import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.example.jaxrs.handler.CatchResource;
import org.jboss.seam.exception.control.example.jaxrs.handler.ResponseBuilderHolder;
import org.jboss.seam.rest.exceptions.ErrorMessageWrapper;

public class ExceptionResponseServiceHandler
{
   @Inject @CatchResource
   private ResponseBuilderHolder holder;
   
   @AroundInvoke
   public Object processException(InvocationContext ctx)
   {
      Method m = ctx.getMethod();
      if (ctx.getParameters().length > 0 && ctx.getParameters()[0] instanceof CaughtException)
      {
         CaughtException<?> c = (CaughtException<?>) ctx.getParameters()[0];
         if (m.isAnnotationPresent(SendHttpResponse.class))
         {
            SendHttpResponse r = m.getAnnotation(SendHttpResponse.class);
            String message = r.message();
            if (r.message().length() == 0 && r.passthru())
            {
               message = c.getException().getMessage();
            }
            
            ResponseBuilder builder = holder.getResponseBuilder().status(r.status());
            if (message != null && message.length() > 0)
            {
               builder = builder.entity(new ErrorMessageWrapper(message));
            }
            
            holder.setResponseBuilder(builder);
            c.handled();
         }
         else
         {
            holder.setResponseBuilder(holder.getResponseBuilder().entity(new ErrorMessageWrapper("Unknown error")));
            c.handled();
         }
      }
      return Void.TYPE;
   }
}
