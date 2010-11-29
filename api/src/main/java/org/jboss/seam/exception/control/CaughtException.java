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

/**
 * Payload for an exception to be handled.  This object is not immutable as small pieces of the state
 * may be set by the handler.
 *
 * @param <T> Exception type this event represents
 */
@SuppressWarnings({"unchecked"})
public class CaughtException<T extends Throwable>
{
   /**
    * Flow control enum.  Used in the dispatcher to determine how to proceed.
    */
   protected enum ExceptionHandlingFlow
   {
      HANDLED,
      PROCEED,
      PROCEED_TO_CAUSE,
      ABORT,
      RETHROW
   }

   private CauseContainer causeContainer;
   private T exception;
   private boolean unMute;
   private ExceptionHandlingFlow flow;
   private boolean descendingTraversal;
   private boolean ascendingTraversal;

   /**
    * Initial state constructor.
    *
    * @param causeContainer      Information about the current exception and cause chain.
    * @param descendingTraversal flag indicating the direction of the cause chain traversal
    *
    * @throws IllegalArgumentException if causeContainer is null
    */
   public CaughtException(final CauseContainer causeContainer, final boolean descendingTraversal)
   {
      if (causeContainer == null)
      {
         throw new IllegalArgumentException("null is not valid for causeContainer");
      }

      this.exception = (T) causeContainer.getCurrentCause();
      this.causeContainer = causeContainer;
      this.descendingTraversal = descendingTraversal;
      this.ascendingTraversal = !descendingTraversal;
      this.flow = ExceptionHandlingFlow.PROCEED;
   }

   public T getException()
   {
      return this.exception;
   }

   /**
    * Instructs the dispatcher to abort further processing of handlers.
    */
   public void abort()
   {
      this.flow = ExceptionHandlingFlow.ABORT;
   }

   /**
    * Instructs the dispatcher to rethrow the event exception after handler processing.
    */
   public void rethrow()
   {
      this.flow = ExceptionHandlingFlow.RETHROW;
   }

   /**
    * Instructs the dispatcher to terminate additional handler processing and mark the event as handled.
    */
   public void handled()
   {
      this.flow = ExceptionHandlingFlow.HANDLED;
   }

   /**
    * Default instruction to dispatcher, continues handler processing.
    */
   public void proceed()
   {
      this.flow = ExceptionHandlingFlow.PROCEED;
   }

   /**
    * Similar to {@link CaughtException#proceed()}, but instructs the dispatcher
    * to proceed to the next element in the cause chain without processing additional handlers for this cause
    * chain element.
    */
   public void proceedToCause()
   {
      this.flow = ExceptionHandlingFlow.PROCEED_TO_CAUSE;
   }

   /**
    * Instructs the dispatcher to allow this handler to be invoked again.
    */
   public void unMute()
   {
      this.unMute = true;
   }

   public boolean isDescendingTraversal()
   {
      return descendingTraversal;
   }

   public boolean isAscendingTraversal()
   {
      return ascendingTraversal;
   }

   protected boolean isUnMute()
   {
      return this.unMute;
   }

   public CauseContainer getCauseContainer()
   {
      return this.causeContainer;
   }

   protected ExceptionHandlingFlow getFlow()
   {
      return this.flow;
   }
}
