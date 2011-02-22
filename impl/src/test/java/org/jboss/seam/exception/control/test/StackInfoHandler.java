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

package org.jboss.seam.exception.control.test;

import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.ExceptionStack;
import org.jboss.seam.exception.control.Handles;
import org.jboss.seam.exception.control.HandlesExceptions;

import static org.junit.Assert.*;

@HandlesExceptions
public class StackInfoHandler
{
   public void outerInfoInspector(@Handles CaughtException<Exception> event)
   {
      ExceptionStack info = event.getExceptionStack();

      assertTrue(info.isLast());
      assertFalse(info.isRoot());
      assertNull(info.getNext());
      assertEquals(Exception.class, info.getCurrent().getClass());
      assertEquals(2, info.getCauseElements().size());
      assertEquals(0, info.getRemaining().size());
   }

   public void rootInfoInspector(@Handles CaughtException<NullPointerException> event)
   {
      ExceptionStack info = event.getExceptionStack();

      assertFalse(info.isLast());
      assertTrue(info.isRoot());
      assertNotNull(info.getNext());
      assertEquals(NullPointerException.class, info.getCurrent().getClass());
      assertEquals(2, info.getCauseElements().size());
      assertEquals(1, info.getRemaining().size());
      assertEquals(event.getException(), info.getCurrent());

      event.dropCause();
   }
}
