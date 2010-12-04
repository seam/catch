/*
 * JBoss, Home of Professional Open Source
 * Copyright [2010], Red Hat, Inc., and individual contributors
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.seam.exception.control.extension.CatchExtension;

/**
 * Observer of {@link ExceptionToCatch} events and handler dispatcher.
 * All handlers are invoked from this class.  This class is immutable.
 */
public class ExceptionHandlerDispatch
{
   /**
    * Observes the event, finds the correct exception handler(s) and invokes them.
    *
    * @param eventException exception to be invoked
    * @param bm             active bean manager
    * @param extension      catch extension instance to obtain handlers
    *
    * @throws Throwable If a handler requests the exception to be re-thrown.
    */
   @SuppressWarnings({"unchecked", "MethodWithMultipleLoops", "ThrowableResultOfMethodCallIgnored"})
   public void executeHandlers(@Observes ExceptionToCatch eventException, final BeanManager bm,
                               CatchExtension extension) throws Throwable
   {
      final Stack<Throwable> unwrappedExceptions = new Stack<Throwable>();
      CreationalContext<Object> ctx = null;

      Throwable exception = eventException.getException();

      do
      {
         unwrappedExceptions.push(exception);
      }
      while ((exception = exception.getCause()) != null);

      try
      {
         ctx = bm.createCreationalContext(null);

         final Set<HandlerMethod> processedHandlers = new HashSet<HandlerMethod>();
         boolean rethrow = false;

         // DuringDescTraversal handlers
         int exceptionIndex = unwrappedExceptions.size() - 1;
         inbound_cause:
         while (exceptionIndex >= 0)
         {

            List<HandlerMethod> handlerMethods = new ArrayList<HandlerMethod>(
               extension.getHandlersForExceptionType(unwrappedExceptions.get(exceptionIndex).getClass(),
                                                     bm, eventException.getQualifiers()));

            for (HandlerMethod handler : handlerMethods)
            {
               if (handler.getTraversalPath() == TraversalPath.DESCENDING && !processedHandlers.contains(handler))
               {
                  final CaughtException event = new CaughtException(new ExceptionStack(unwrappedExceptions,
                                                                                       exceptionIndex), true);
                  handler.notify(event, bm);

                  if (!event.isUnmute())
                  {
                     processedHandlers.add(handler);
                  }

                  switch (event.getFlow())
                  {
                     case HANDLED:
                        eventException.setHandled(true);
                        return;
                     case PROCEED:
                        eventException.setHandled(true);
                        break;
                     case ABORT:
                        return;
                     case PROCEED_TO_CAUSE:
                        eventException.setHandled(true);
                        exceptionIndex--;
                        continue inbound_cause;
                     case RETHROW:
                        rethrow = true;
                  }
               }
            }

            exceptionIndex--;
         }

         // Run outbound handlers, same list, just reversed
         exceptionIndex = unwrappedExceptions.size() - 1;
         outbound_cause:
         while (exceptionIndex >= 0)
         {

            List<HandlerMethod> handlerMethods = new ArrayList<HandlerMethod>(
               extension.getHandlersForExceptionType(unwrappedExceptions.get(exceptionIndex).getClass(), bm,
                                                     eventException.getQualifiers()));

            for (HandlerMethod handler : handlerMethods)
            {
               // Defining DuringAscTraversal as the absence of DuringDescTraversal
               if (handler.getTraversalPath() == TraversalPath.ASCENDING && !processedHandlers.contains(handler))
               {
                  final CaughtException event = new CaughtException(new ExceptionStack(unwrappedExceptions,
                                                                                       exceptionIndex), false);
                  handler.notify(event, bm);

                  if (!event.isUnmute())
                  {
                     processedHandlers.add(handler);
                  }

                  switch (event.getFlow())
                  {
                     case HANDLED:
                        eventException.setHandled(true);
                        return;
                     case PROCEED:
                        eventException.setHandled(true);
                        break;
                     case ABORT:
                        return;
                     case PROCEED_TO_CAUSE:
                        eventException.setHandled(true);
                        exceptionIndex--;
                        continue outbound_cause;
                     case RETHROW:
                        rethrow = true;
                  }
               }
            }
            exceptionIndex--;
         }

         if (rethrow)
         {
            throw eventException.getException();
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
