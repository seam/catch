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

package org.jboss.seam.exception.control;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.seam.solder.reflection.annotated.ParameterValueRedefiner;

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
