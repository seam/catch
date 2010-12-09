package org.jboss.seam.exception.control.test.handler;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.Handles;
import org.jboss.seam.exception.control.HandlesExceptions;

@HandlesExceptions
@PretendInterceptorBinding
@Interceptor
public class InterceptorAsHandler
{
   @AroundInvoke
   public Object intercept(InvocationContext ctx) throws Exception
   {
      return ctx.proceed();
   }
   
   public void handlesAll(@Handles CaughtException<Throwable> caught)
   {
   }
}
