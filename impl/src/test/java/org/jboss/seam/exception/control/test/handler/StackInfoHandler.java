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
import org.jboss.seam.exception.control.CauseContainer;
import org.jboss.seam.exception.control.Handles;
import org.jboss.seam.exception.control.HandlesExceptions;

import static org.junit.Assert.*;

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
