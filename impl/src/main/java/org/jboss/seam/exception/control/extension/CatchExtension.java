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

package org.jboss.seam.exception.control.extension;

import org.jboss.seam.exception.control.Handles;
import org.jboss.seam.exception.control.qualifier.HandlesExceptions;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessManagedBean;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CatchExtension implements Extension
{
   private Map<Class<?>, Collection<AnnotatedMethod>> handlers;

   public CatchExtension()
   {
      this.handlers = new HashMap<Class<?>, Collection<AnnotatedMethod>>();
   }

   public void findHandlers(@Observes ProcessManagedBean pmb)
   {
      AnnotatedType type = pmb.getAnnotatedBeanClass();
      if (type.isAnnotationPresent(HandlesExceptions.class))
      {
         HandlesExceptions declaration = type.getAnnotation(HandlesExceptions.class);
         Class<? extends Throwable>[] handlesTypes = declaration.value();
         Set<AnnotatedMethod> methods = type.getMethods();
         Iterator<AnnotatedMethod> itr = methods.iterator();

         while (itr.hasNext())
         {
            AnnotatedMethod m = itr.next();
            if (!m.isAnnotationPresent(Handles.class))
            {
               itr.remove();
            }
         }

         for (Class<? extends Throwable> c : handlesTypes)
         {
            if (this.handlers.containsKey(c))
            {
               this.handlers.get(c).addAll(methods);
            }
            else
            {
               this.handlers.put(c, methods);
            }
         }
      }
   }

   public Map<Class<?>, Collection<AnnotatedMethod>> getHandlers()
   {
      return Collections.unmodifiableMap(handlers);
   }
}
