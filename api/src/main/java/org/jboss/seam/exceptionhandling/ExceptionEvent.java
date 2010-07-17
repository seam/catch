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

package org.jboss.seam.exceptionhandling;

/**
 * Payload for an exception to be handled.
 */
public class ExceptionEvent
{
   private State state;
   private Throwable exception;
   private boolean exceptionHandled;

   public ExceptionEvent(Throwable exception, State state)
   {
      this.exception = exception;
      this.state = state;
      this.exceptionHandled = false;
   }

   /**
    * @return the exception to be handled.
    */
   public Throwable getException()
   {
      return exception;
   }

   /**
    * @return State instance related to the environment. This will often need to be cast to the correct sub class.
    */
   public State getState()
   {
      return state;
   }

   /**
    * @return flag indicating the exception has been handled.
    */
   public boolean isExceptionHandled()
   {
      return exceptionHandled;
   }

   /**
    * This should be set if the exception has been handled in an event observer or handler.
    * @param exceptionHandled new value
    */
   public void setExceptionHandled(boolean exceptionHandled)
   {
      this.exceptionHandled = exceptionHandled;
   }
}
