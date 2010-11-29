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

package org.jboss.seam.exception.control.test.handler;

import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.CauseContainer;
import org.jboss.seam.exception.control.Handles;
import org.jboss.seam.exception.control.HandlesExceptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@HandlesExceptions
public class StackInfoHandler
{
   public void outerInfoInspector(@Handles CaughtException<Exception> event)
   {
      CauseContainer info = event.getCauseContainer();

      assertTrue(info.isLast());
      assertFalse(info.isRoot());
      assertEquals(0, info.getIndex());
      assertNull(info.getNextCause());
      assertEquals(Exception.class, info.getCurrentCause().getClass());
      assertEquals(2, info.getCauseElements().size());
      assertEquals(0, info.getRemainingCauses().size());
   }

   public void rootInfoInspector(@Handles CaughtException<NullPointerException> event)
   {
      CauseContainer info = event.getCauseContainer();

      assertFalse(info.isLast());
      assertTrue(info.isRoot());
      assertEquals(info.getCauseElements().size() - 1, info.getIndex());
      assertNotNull(info.getNextCause());
      assertEquals(NullPointerException.class, info.getCurrentCause().getClass());
      assertEquals(2, info.getCauseElements().size());
      assertEquals(1, info.getRemainingCauses().size());
      assertEquals(event.getException(), info.getCurrentCause());

      event.proceedToCause();
   }
}
