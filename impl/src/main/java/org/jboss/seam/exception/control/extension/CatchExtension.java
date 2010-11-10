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

import org.jboss.seam.exception.control.DuringAscTraversal;
import org.jboss.seam.exception.control.DuringDescTraversal;
import org.jboss.seam.exception.control.ExceptionHandlerComparator;
import org.jboss.seam.exception.control.Handles;
import org.jboss.seam.exception.control.HandlesExceptions;
import org.jboss.weld.extensions.reflection.HierarchyDiscovery;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessManagedBean;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings({"unchecked"})
public class CatchExtension implements Extension
{
   private final Map<? super Type, Collection<AnnotatedMethod>> allHandlers;

   public CatchExtension()
   {
      this.allHandlers = new HashMap<Type, Collection<AnnotatedMethod>>();
   }

   public void findHandlers(@Observes final ProcessManagedBean pmb, final BeanManager bm)
   {
      final AnnotatedType type = pmb.getAnnotatedBeanClass();

      if (type.isAnnotationPresent(HandlesExceptions.class))
      {
         final Set<AnnotatedMethod> methods = type.getMethods();

         for (AnnotatedMethod method : methods)
         {
            if (method.getParameters().size() > 0
                && ((AnnotatedParameter) method.getParameters().get(0)).isAnnotationPresent(Handles.class))
            {
               final AnnotatedParameter p = (AnnotatedParameter) method.getParameters().get(0);

               if (p.isAnnotationPresent(DuringAscTraversal.class) && p.isAnnotationPresent(DuringDescTraversal.class))
               {
                  pmb.addDefinitionError(new IllegalStateException(
                     "A handler cannot be both DuringDescTraversal and DuringAscTraversal."));
               }
               final Class exceptionType = (Class) ((ParameterizedType) p.getBaseType()).getActualTypeArguments()[0];

               if (this.allHandlers.containsKey(exceptionType))
               {
                  this.allHandlers.get(exceptionType).add(method);
               }
               else
               {
                  this.allHandlers.put(exceptionType, new HashSet<AnnotatedMethod>(Arrays.asList(method)));
               }
            }
         }
      }
   }

   public Collection<AnnotatedMethod> getHandlersForExceptionType(Type exceptionClass)
   {
      final Set<AnnotatedMethod> returningHandlers = new TreeSet<AnnotatedMethod>(new ExceptionHandlerComparator());
      final HierarchyDiscovery h = new HierarchyDiscovery(exceptionClass);
      final Set<Type> closure = h.getTypeClosure();

      for (Type hierarchyType : closure)
      {
         if (this.allHandlers.get(hierarchyType) != null)
         {
            returningHandlers.addAll(this.allHandlers.get(hierarchyType));
         }
      }

      return Collections.unmodifiableCollection(returningHandlers);
   }
}
