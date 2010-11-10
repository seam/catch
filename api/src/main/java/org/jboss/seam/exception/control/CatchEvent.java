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

/**
 * Payload for an exception to be handled.  This object is not immutable as small pieces of the state
 * may be set by the handler.
 *
 * @param <T> Exception type this event represents
 */
@SuppressWarnings({"unchecked"})
public class CatchEvent<T extends Throwable>
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

   private StackInfo stackInfo;
   private T exception;
   private boolean unMute;
   private ExceptionHandlingFlow flow;
   private boolean descendingTraversal;
   private boolean ascendingTraversal;

   /**
    * Initial state constructor.
    *
    * @param stackInfo           Information about the current exception and cause chain.
    * @param descendingTraversal flag indicating the direction of the cause chain traversal
    *
    * @throws IllegalArgumentException if stackInfo is null
    */
   public CatchEvent(final StackInfo stackInfo, final boolean descendingTraversal)
   {
      if (stackInfo == null)
      {
         throw new IllegalArgumentException("null is not valid for stackInfo");
      }

      this.exception = (T) stackInfo.getCurrentCause();
      this.stackInfo = stackInfo;
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
    * Similar to {@link org.jboss.seam.exception.control.CatchEvent#proceed()}, but instructs the dispatcher
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

   public StackInfo getStackInfo()
   {
      return this.stackInfo;
   }

   protected ExceptionHandlingFlow getFlow()
   {
      return this.flow;
   }
}
