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

import org.jboss.seam.exception.control.CatchEvent;
import org.jboss.seam.exception.control.Handles;
import org.jboss.seam.exception.control.HandlesExceptions;
import org.jboss.seam.exception.control.TraversalPath;

import javax.enterprise.inject.spi.BeanManager;

@HandlesExceptions
public class CalledExceptionHandler
{
   public static boolean OUTBOUND_HANDLER_CALLED = false;
   public static int OUTBOUND_HANDLER_TIMES_CALLED = 0;
   public static int INBOUND_HANDLER_TIMES_CALLED = 0;
   public static boolean BEANMANAGER_INJECTED = false;

   public void basicHandler(@Handles CatchEvent<Exception> event)
   {
      OUTBOUND_HANDLER_CALLED = true;
      OUTBOUND_HANDLER_TIMES_CALLED++;
   }

   public void basicInboundHandler(@Handles(during = TraversalPath.DESCENDING) CatchEvent<Exception> event)
   {
      INBOUND_HANDLER_TIMES_CALLED++;
      event.proceed();
   }

   public void extraInjections(@Handles CatchEvent<IllegalArgumentException> event, BeanManager bm)
   {
      if (bm != null)
      {
         BEANMANAGER_INJECTED = true;
      }
   }
}
