/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.exception.control;

import org.jboss.seam.exception.control.extension.CatchExtension;
import org.jboss.weld.extensions.reflection.annotated.InjectableMethod;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 */
public class ExceptionHandlerDispatch
{
   @Inject
   private CatchExtension extension;

   /**
    * Observes the event, finds the correct exception handler(s) and invokes them.
    *
    * @param eventException
    */
   @SuppressWarnings({"unchecked", "MethodWithMultipleLoops", "ThrowableResultOfMethodCallIgnored"})
   public void executeHandlers(@Observes Throwable eventException, final BeanManager bm)
   {
      final Stack<Throwable> unwrappedExceptions = new Stack<Throwable>();
      CreationalContext<Object> ctx = null;

      Throwable exception = eventException;

      do
      {
         unwrappedExceptions.push(exception);
      }
      while ((exception = exception.getCause()) != null);

      try
      {
         ctx = bm.createCreationalContext(null);

         // Inbound handlers
         int indexOfException = 0;
         while (indexOfException < unwrappedExceptions.size())
         {
            CatchEvent ehe = new CatchEvent(new StackInfo(unwrappedExceptions, indexOfException), true);

            List<AnnotatedMethod> handlerMethods = new ArrayList<AnnotatedMethod>(
               this.extension.getHandlersForExceptionType(unwrappedExceptions.get(indexOfException).getClass()));

            for (AnnotatedMethod handler : handlerMethods)
            {
               if (((AnnotatedParameter) handler.getParameters().get(0)).isAnnotationPresent(Inbound.class))
               {
                  invokeHandler(bm, ctx, handler, ehe);
               }

               // TODO: Make sure things like mute are handled
            }

            // TODO rollbacks, throws, etc

            indexOfException++;
         }

         // Run outbound handlers, same list, just reversed
         indexOfException = 0;
         while (indexOfException < unwrappedExceptions.size())
         {
            CatchEvent ehe = new CatchEvent(new StackInfo(unwrappedExceptions, indexOfException),
                                            false);

            List<AnnotatedMethod> handlerMethods = new ArrayList<AnnotatedMethod>(
               this.extension.getHandlersForExceptionType(unwrappedExceptions.get(indexOfException).getClass()));

            Collections.reverse(handlerMethods);

            for (AnnotatedMethod handler : handlerMethods)
            {
               // Defining Outbound as the absence of Inbound
               if (!((AnnotatedParameter) handler.getParameters().get(0)).isAnnotationPresent(Inbound.class))
               {
                  invokeHandler(bm, ctx, handler, ehe);
               }

               // TODO: Make sure things like mute are handled
            }

            // TODO rollbacks, throws, etc

            indexOfException++;
         }

      }
      finally
      {
         if (ctx != null)
         {
            ctx.release();
         }
      }

   }

   @SuppressWarnings({"unchecked"})
   private void invokeHandler(BeanManager bm, CreationalContext<Object> ctx, AnnotatedMethod handler, CatchEvent event)
   {
      Bean<?> handlerBean = bm.resolve(bm.getBeans(handler.getJavaMember().getDeclaringClass(),
                                                   HandlesExceptionsLiteral.INSTANCE));
      Object handlerInstance = bm.getReference(handlerBean, handler.getBaseType(), ctx);

      InjectableMethod im = new InjectableMethod(handler, handlerBean, bm);

      im.invoke(handlerInstance, ctx, new OutboundParameterValueRedefiner(event, bm, handlerBean));
   }
}
