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
package org.jboss.seam.exception.control.test.traversal;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.Handles;
import org.jboss.seam.exception.control.HandlesExceptions;
import org.jboss.seam.exception.control.test.traversal.Exceptions.SuperException;

import static org.jboss.seam.exception.control.test.traversal.Exceptions.Exception1;
import static org.jboss.seam.exception.control.test.traversal.Exceptions.Exception2;
import static org.jboss.seam.exception.control.test.traversal.Exceptions.Exception3;
import static org.jboss.seam.exception.control.TraversalPath.ASCENDING;
import static org.jboss.seam.exception.control.TraversalPath.DESCENDING;

@HandlesExceptions
public class ExceptionHandler
{
   private static final List<Integer> executionOrder = new ArrayList<Integer>();

   public void handleException1Desc(@Handles(during = DESCENDING) CaughtException<Exception1> event)
   {
      executionOrder.add(1);
   }

   public void handleException2Desc(@Handles(during = DESCENDING) CaughtException<Exception2> event)
   {
      executionOrder.add(2);
   }

   public void handleException3Desc(@Handles(during = DESCENDING) CaughtException<Exception3> event)
   {
      executionOrder.add(3);
   }
   
   public void handleException3Asc(@Handles(during = ASCENDING) CaughtException<Exception3> event)
   {
      executionOrder.add(4);
   }
   
   public void handleException3SuperclassAsc(@Handles(during = ASCENDING) CaughtException<SuperException> event)
   {
      executionOrder.add(5);
   }

   public void handleException2Asc(@Handles(during = ASCENDING) CaughtException<Exception2> event)
   {
      executionOrder.add(6);
   }

   public void handleException1Asc(@Handles(during = ASCENDING) CaughtException<Exception1> event)
   {
      executionOrder.add(7);
   }

   public static List<Integer> getExecutionorder()
   {
      return executionOrder;
   }
}
