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
import org.jboss.seam.exception.control.DuringDescTraversal;
import org.jboss.seam.exception.control.Handles;
import org.jboss.seam.exception.control.HandlesExceptions;

@HandlesExceptions
public class ProceedCauseHandler
{
   public static int INBOUND_NPE_CALLED = 0;
   public static int INBOUND_NPE_LOWER_PRECEDENCE_CALLED = 0;

   public static int OUTBOUND_NPE_CALLED = 0;
   public static int OUTBOUND_NPE_HIGHER_PRECEDENCE_CALLED = 0;

   public void npeInboundHandler(@Handles @DuringDescTraversal CatchEvent<NullPointerException> event)
   {
      INBOUND_NPE_CALLED++;
      event.proceedToCause();
   }

   public void npeLowerPrecedenceInboundHandler(
      @Handles(precedence = -1) @DuringDescTraversal CatchEvent<NullPointerException> event)
   {
      INBOUND_NPE_LOWER_PRECEDENCE_CALLED++;
      event.proceed();
   }

   public void npeOutboundHandler(@Handles CatchEvent<NullPointerException> event)
   {
      OUTBOUND_NPE_CALLED++;
      event.proceedToCause();
   }

   public void npeHigherPrecedenceOutboundHandler(@Handles(precedence = 1) CatchEvent<NullPointerException> event)
   {
      OUTBOUND_NPE_HIGHER_PRECEDENCE_CALLED++;
      event.proceed();
   }
}
