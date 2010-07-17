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

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class ExceptionExceptionHandler extends BaseExceptionHandler implements ExceptionHandler<Exception, State>
{
   /**
    * @return the numeric priority of this handler in relationship to
    *         other handlers, 1 being top priority
    */
   public int getPriority()
   {
      return 0;  //To change body of implemented methods use File | Settings | File Templates.
   }

   /**
    * Method called to execute logic for an uncaught exception.
    *
    * @param chain Chain object used to continue handling chain
    * @param state container for any useful application state
    * @param e     uncaught exception
    */
   public void handle(HandlerChain chain, State state, Exception e)
   {
      super.baseHandle(chain, state, e);
   }
}
