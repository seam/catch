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

package org.jboss.seam.exception.control;

import org.jboss.weld.extensions.reflection.annotated.ParameterValueRedefiner;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

/**
 * Redefiner allowing to inject a non contextual instance of {@link CaughtException} into the first parameter.
 * This class is immutable.
 */
public class OutboundParameterValueRedefiner implements ParameterValueRedefiner
{
   final private CaughtException event;
   final private BeanManager bm;
   final private Bean<?> declaringBean;

   /**
    * Sole constructor.
    *
    * @param event         instance of CaughtException to inject.
    * @param manager       active BeanManager
    * @param declaringBean Class containing the handler method
    */
   public OutboundParameterValueRedefiner(CaughtException event, final BeanManager manager, Bean<?> declaringBean)
   {
      this.event = event;
      this.bm = manager;
      this.declaringBean = declaringBean;
   }

   /**
    * {@inheritDoc}
    */
   public Object redefineParameterValue(ParameterValue value)
   {
      CreationalContext<?> ctx = this.bm.createCreationalContext(this.declaringBean);

      try
      {
         switch (value.getPosition())
         {
            case 0:
            {
               return event;
            }
         }
         return value.getDefaultValue(ctx);
      }
      finally
      {
         if (ctx != null)
         {
            ctx.release();
         }
      }
   }
}
