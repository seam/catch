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

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Observer of {@link org.jboss.seam.exception.control.ExceptionToCatchEvent} events and handler dispatcher.
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
   public void executeHandlers(@Observes ExceptionToCatchEvent eventException, final BeanManager bm,
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
                  final CatchEvent event = new CatchEvent(new CauseContainer(unwrappedExceptions, exceptionIndex), true);
                  handler.notify(event, bm);

                  if (!event.isUnMute())
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

            Collections.reverse(handlerMethods);

            for (HandlerMethod handler : handlerMethods)
            {
               // Defining DuringAscTraversal as the absence of DuringDescTraversal
               if (handler.getTraversalPath() == TraversalPath.ASCENDING && !processedHandlers.contains(handler))
               {
                  final CatchEvent event = new CatchEvent(new CauseContainer(unwrappedExceptions, exceptionIndex), false);
                  handler.notify(event, bm);

                  if (!event.isUnMute())
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
