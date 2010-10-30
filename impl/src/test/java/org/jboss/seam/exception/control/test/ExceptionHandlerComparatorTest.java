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

package org.jboss.seam.exception.control.test;

import org.jboss.seam.exception.control.ExceptionHandler;
import org.jboss.seam.exception.control.ExceptionHandlerComparator;
import org.jboss.seam.exception.control.State;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ExceptionHandlerComparatorTest
{
   @Test
   public void testHandlersCalledInCorrectOrder()
   {
      NullPointerExceptionHandler nullPointerExceptionHandler = new NullPointerExceptionHandler();
      ExceptionExceptionHandler exceptionExceptionHandler = new ExceptionExceptionHandler();
      UnsupportedOperationExceptionHandler unsupportedOperationExceptionHandler = new UnsupportedOperationExceptionHandler();
      List<? extends ExceptionHandler<? extends Exception, State>> handlerList;
      handlerList = Arrays.asList(nullPointerExceptionHandler, exceptionExceptionHandler, unsupportedOperationExceptionHandler);

      Collections.sort(handlerList, new ExceptionHandlerComparator());

      assertEquals(unsupportedOperationExceptionHandler, handlerList.get(0));
      assertEquals(nullPointerExceptionHandler, handlerList.get(1));
      assertEquals(exceptionExceptionHandler, handlerList.get(2));
   }

   @Test
   public void testHandlersCalledInCorrectOrder2()
   {
      NullPointerExceptionHandler nullPointerExceptionHandler = new NullPointerExceptionHandler();
      ExceptionExceptionHandler exceptionExceptionHandler = new ExceptionExceptionHandler();
      UnsupportedOperationExceptionHandler unsupportedOperationExceptionHandler = new UnsupportedOperationExceptionHandler();
      List<? extends ExceptionHandler<? extends Exception, State>> handlerList;
      handlerList = Arrays.asList(unsupportedOperationExceptionHandler, exceptionExceptionHandler, nullPointerExceptionHandler);

      Collections.sort(handlerList, new ExceptionHandlerComparator());

      assertEquals(unsupportedOperationExceptionHandler, handlerList.get(0));
      assertEquals(nullPointerExceptionHandler, handlerList.get(1));
      assertEquals(exceptionExceptionHandler, handlerList.get(2));
   }
}
