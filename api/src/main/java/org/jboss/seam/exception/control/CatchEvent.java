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
 * Payload for an exception to be handled.
 *
 * @param <T> Exception type this event represents
 */
@SuppressWarnings({"unchecked"})
public class CatchEvent<T extends Throwable>
{
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
   boolean unMute;
   private ExceptionHandlingFlow flow;
   boolean inbound;
   boolean outbound;

   public CatchEvent(final StackInfo stackInfo, final boolean inbound)
   {
      this.exception = (T) stackInfo.getCurrentCause();
      this.stackInfo = stackInfo;
      this.inbound = inbound;
      this.outbound = !inbound;
   }

   public T getException()
   {
      return this.exception;
   }

   public void handled()
   {
      this.flow = ExceptionHandlingFlow.HANDLED;
   }

   public void proceed()
   {
      this.flow = ExceptionHandlingFlow.PROCEED;
   }

   public void proceedToCause()
   {
      this.flow = ExceptionHandlingFlow.PROCEED_TO_CAUSE;
   }

   public void unMute()
   {
      this.unMute = true;
   }

   public boolean isInbound()
   {
      return inbound;
   }

   public boolean isOutbound()
   {
      return outbound;
   }

   protected boolean isUnMute()
   {
      return this.unMute;
   }

   protected StackInfo getStackInfo()
   {
      return this.stackInfo;
   }

   protected ExceptionHandlingFlow getFlow()
   {
      return this.flow;
   }
}
