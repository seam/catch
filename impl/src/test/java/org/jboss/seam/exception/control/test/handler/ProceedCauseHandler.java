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
import org.jboss.seam.exception.control.Handles;
import org.jboss.seam.exception.control.HandlesExceptions;
import org.jboss.seam.exception.control.TraversalPath;

@HandlesExceptions
public class ProceedCauseHandler
{
   public static int INBOUND_NPE_CALLED = 0;
   public static int INBOUND_NPE_LOWER_PRECEDENCE_CALLED = 0;

   public static int OUTBOUND_NPE_CALLED = 0;
   public static int OUTBOUND_NPE_HIGHER_PRECEDENCE_CALLED = 0;

   public void npeInboundHandler(
      @Handles(during = TraversalPath.DESCENDING) CaughtException<NullPointerException> event)
   {
      INBOUND_NPE_CALLED++;
      event.proceedToCause();
   }

   public void npeLowerPrecedenceInboundHandler(
      @Handles(precedence = -1, during = TraversalPath.DESCENDING) CaughtException<NullPointerException> event)
   {
      INBOUND_NPE_LOWER_PRECEDENCE_CALLED++;
      event.proceed();
   }

   public void npeOutboundHandler(@Handles CaughtException<NullPointerException> event)
   {
      OUTBOUND_NPE_CALLED++;
      event.proceedToCause();
   }

   public void npeHigherPrecedenceOutboundHandler(@Handles(precedence = 1) CaughtException<NullPointerException> event)
   {
      OUTBOUND_NPE_HIGHER_PRECEDENCE_CALLED++;
      event.proceed();
   }
}
