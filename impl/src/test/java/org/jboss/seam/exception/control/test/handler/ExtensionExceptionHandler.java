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

package org.jboss.seam.exception.control.test.handler;

import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.Handles;
import org.jboss.seam.exception.control.HandlesExceptions;
import org.jboss.seam.exception.control.TraversalPath;
import org.jboss.seam.exception.control.test.qualifier.Arquillian;
import org.jboss.seam.exception.control.test.qualifier.CatchQualifier;

@HandlesExceptions
public class ExtensionExceptionHandler
{
   public void catchException(@Handles(during = TraversalPath.DESCENDING) CaughtException<Exception> event)
   {
      // Nothing to do currently
   }

   public void catchRuntime(@Handles(during = TraversalPath.ASCENDING) CaughtException<RuntimeException> event)
   {
      // Nothing to do currently
   }

   public void catchThrowable(
      @Handles(precedence = 10, during = TraversalPath.ASCENDING) CaughtException<Throwable> event)
   {
      // Nothing to do currently
   }

   public void catchThrowableP20(
      @Handles(precedence = 20, during = TraversalPath.ASCENDING) CaughtException<Throwable> event)
   {
      // Nothing to do currently
   }

   public void catchIAE(@Handles CaughtException<IllegalArgumentException> event)
   {
      // Nothing to do currently
   }

   public void qualifiedHandler(@Handles @CatchQualifier CaughtException<Exception> event)
   {
      // Method to verify the qualifiers are working correctly for handlers
   }

   public void arqHandler(@Handles @Arquillian CaughtException<Throwable> event)
   {
      // Method to verify the qualifiers are working correctly for handlers
   }

   public void arqTestingHandler(@Handles @Arquillian @CatchQualifier CaughtException<Throwable> event)
   {
      // Method to verify the qualifiers are working correctly for handlers
   }

   public void doNothingMethod()
   {
      // Method to make sure only @Handles methods are found
   }

   public void doNothingTwo(String p1, String p2, int p3)
   {
      // Same as above
   }
}
