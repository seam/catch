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

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

public interface HandlerMethod<T extends Throwable>
{
   /**
    * Obtains the bean class of the bean that declares the observer method.
    */
   Class<?> getBeanClass();

   /**
    * Obtains the Bean reference that declares the observer method.
    */
   Bean<?> getBean();

   /**
    * Obtains the set of handled event qualifiers.
    */
   Set<Annotation> getQualifiers();

   /**
    * Obtains the handled event type.
    */
   Type getExceptionType();

   /**
    * Calls the handler method, passing the given event object.
    *
    * @param event event to pass to the handler.
    * @param bm    Active BeanManager
    */
   void notify(CatchEvent<T> event, BeanManager bm);

   /**
    * Obtains the direction of the traversal path the handler will be listening.
    */
   TraversalPath getTraversalPath();

   /**
    * Obtains the precedence of the handler.
    */
   int getPrecedence();

   /**
    * Obtains the actual method of the handler.
    */
   Method getJavaMethod();
}
