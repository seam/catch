/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.exception.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.seam.exception.control.extension.CatchExtension;

/**
 * Observer of {@link ExceptionToCatch} events and handler dispatcher. All handlers are invoked from this class.  This
 * class is immutable.
 */
public class ExceptionHandlerDispatch
{
   /**
    * Observes the event, finds the correct exception handler(s) and invokes them.
    *
    * @param eventException exception to be invoked
    * @param bm             active bean manager
    * @param extension      catch extension instance to obtain handlers
    * @throws Throwable If a handler requests the exception to be re-thrown.
    */
   @SuppressWarnings( { "unchecked", "MethodWithMultipleLoops", "ThrowableResultOfMethodCallIgnored" })
   public void executeHandlers(@Observes @Any ExceptionToCatch eventException, final BeanManager bm,
                               CatchExtension extension) throws Throwable
   {
      final Stack<Throwable> unwrappedExceptions = new Stack<Throwable>();
      CreationalContext<Object> ctx = null;

      Throwable exception = eventException.getException();
      Throwable throwException = null;

      do
      {
         unwrappedExceptions.push(exception);
      }
      while ((exception = exception.getCause()) != null);

      try
      {
         ctx = bm.createCreationalContext(null);

         final Set<HandlerMethod> processedHandlers = new HashSet<HandlerMethod>();

         // DuringDescTraversal handlers
         int exceptionIndex = unwrappedExceptions.size() - 1;
         inbound_cause:
         while (exceptionIndex >= 0)
         {

            final List<HandlerMethod> breadthFirstHandlerMethods = new ArrayList<HandlerMethod>(
                  extension.getHandlersForExceptionType(unwrappedExceptions.get(exceptionIndex).getClass(),
                        bm, eventException.getQualifiers(), TraversalMode.BREADTH_FIRST));

            for (HandlerMethod handler : breadthFirstHandlerMethods)
            {
               if (!processedHandlers.contains(handler))
               {
                  final ExceptionStack stack = new ExceptionStack(unwrappedExceptions, exceptionIndex);
                  final CaughtException breadthFirstEvent = new CaughtException(stack, true, eventException.isHandled());
                  handler.notify(breadthFirstEvent, bm);

                  if (!breadthFirstEvent.isUnmute())
                  {
                     processedHandlers.add(handler);
                  }

                  switch (breadthFirstEvent.getFlow())
                  {
                     case HANDLED:
                        eventException.setHandled(true);
                        return;
                     case MARK_HANDLED:
                        eventException.setHandled(true);
                        break;
                     case ABORT:
                        return;
                     case DROP_CAUSE:
                        eventException.setHandled(true);
                        exceptionIndex--;
                        continue inbound_cause;
                     case RETHROW:
                        throwException = eventException.getException();
                        break;
                     case THROW:
                        throwException = breadthFirstEvent.getThrowNewException();
                  }
               }
            }

            final List<HandlerMethod> depthFirstHandlerMethods = new ArrayList<HandlerMethod>(
                  extension.getHandlersForExceptionType(unwrappedExceptions.get(exceptionIndex).getClass(),
                        bm, eventException.getQualifiers(), TraversalMode.DEPTH_FIRST));

            // Reverse these so category handlers are last
            Collections.reverse(depthFirstHandlerMethods);

            for (HandlerMethod handler : depthFirstHandlerMethods)
            {
               if (!processedHandlers.contains(handler))
               {
                  final ExceptionStack stack = new ExceptionStack(unwrappedExceptions, exceptionIndex);
                  final CaughtException depthFirstEvent = new CaughtException(stack, false, eventException.isHandled());
                  handler.notify(depthFirstEvent, bm);

                  if (!depthFirstEvent.isUnmute())
                  {
                     processedHandlers.add(handler);
                  }

                  switch (depthFirstEvent.getFlow())
                  {
                     case HANDLED:
                        eventException.setHandled(true);
                        return;
                     case MARK_HANDLED:
                        eventException.setHandled(true);
                        break;
                     case ABORT:
                        return;
                     case DROP_CAUSE:
                        eventException.setHandled(true);
                        exceptionIndex--;
                        continue inbound_cause;
                     case RETHROW:
                        throwException = eventException.getException();
                        break;
                     case THROW:
                        throwException = depthFirstEvent.getThrowNewException();
                  }
               }
            }

            exceptionIndex--;
         }

         if (throwException != null)
         {
            throw throwException;
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
}
