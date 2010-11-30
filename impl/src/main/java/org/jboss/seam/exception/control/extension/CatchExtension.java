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

package org.jboss.seam.exception.control.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;

import org.jboss.seam.exception.control.ExceptionHandlerComparator;
import org.jboss.seam.exception.control.HandlerMethod;
import org.jboss.seam.exception.control.HandlerMethodImpl;
import org.jboss.seam.exception.control.Handles;
import org.jboss.seam.exception.control.HandlesExceptions;
import org.jboss.weld.extensions.reflection.HierarchyDiscovery;

/**
 * CDI extension to find handlers at startup.
 */
@SuppressWarnings({"unchecked"})
public class CatchExtension implements Extension
{
   private final Map<? super Type, Collection<HandlerMethod>> allHandlers;

   public CatchExtension()
   {
      this.allHandlers = new HashMap<Type, Collection<HandlerMethod>>();
   }

   /**
    * Listener to ProcessBean event to locate handlers.
    *
    * @param pmb Event from CDI SPI
    * @param bm  Activated Bean Manager
    *
    * @throws TypeNotPresentException if any of the actual type arguments refers to a non-existent type declaration when
    *                                 trying to obtain the actual type arguments from a {@link ParameterizedType}
    * @throws java.lang.reflect.MalformedParameterizedTypeException
    *                                 if any of the
    *                                 actual type parameters refer to a parameterized type that cannot
    *                                 be instantiated for any reason when trying to obtain the actual type arguments
    *                                 from a {@link ParameterizedType}
    */
   public void findHandlers(@Observes final ProcessBean pmb, final BeanManager bm)
   {
      // TODO also ignore decorators and interceptors
      if (!(pmb.getAnnotated() instanceof AnnotatedType))
      {
         return;
      }
      
      final AnnotatedType type = (AnnotatedType) pmb.getAnnotated();

      if (type.isAnnotationPresent(HandlesExceptions.class))
      {
         final Set<AnnotatedMethod> methods = type.getMethods();

         for (AnnotatedMethod method : methods)
         {
            if (method.getParameters().size() > 0
                && ((AnnotatedParameter) method.getParameters().get(0)).isAnnotationPresent(Handles.class))
            {
               if (method.getJavaMember().getExceptionTypes().length != 0)
               {
                  pmb.addDefinitionError(new IllegalArgumentException(
                     MessageFormat.format("Handler method %s must not throw exceptions", method.getJavaMember())));
               }
               final AnnotatedParameter p = (AnnotatedParameter) method.getParameters().get(0);
               final Class exceptionType = (Class) ((ParameterizedType) p.getBaseType()).getActualTypeArguments()[0];

               if (this.allHandlers.containsKey(exceptionType))
               {
                  this.allHandlers.get(exceptionType).add(new HandlerMethodImpl(method, bm));
               }
               else
               {
                  this.allHandlers.put(exceptionType, new HashSet<HandlerMethod>(Arrays.asList(new HandlerMethodImpl(
                     method, bm))));
               }
            }
         }
      }
   }

   /**
    * Obtains the applicable handlers for the given type or super type of the given type.  Also makes use of
    * {@link org.jboss.seam.exception.control.ExceptionHandlerComparator} to order the handlers.
    *
    * @param exceptionClass    Type of exception to narrow handler list
    * @param bm                active BeanManager
    * @param handlerQualifiers additional handlerQualifiers to limit handlers
    *
    * @return An order collection of handlers for the given type.
    */
   public Collection<HandlerMethod> getHandlersForExceptionType(Type exceptionClass, BeanManager bm,
                                                                Set<Annotation> handlerQualifiers)
   {
      final Set<HandlerMethod> returningHandlers = new TreeSet<HandlerMethod>(new ExceptionHandlerComparator());
      final HierarchyDiscovery h = new HierarchyDiscovery(exceptionClass);
      final Set<Type> closure = h.getTypeClosure();

      for (Type hierarchyType : closure)
      {
         if (this.allHandlers.get(hierarchyType) != null)
         {
            for (HandlerMethod handler : this.allHandlers.get(hierarchyType))
            {
               if (handler.getQualifiers().isEmpty())
               {
                  returningHandlers.add(handler);
               }
               else
               {
                  if (!handlerQualifiers.isEmpty())
                  {
                     // containsAny functionality
                     for (Annotation qualifier : handlerQualifiers)
                     {
                        if (handler.getQualifiers().contains(qualifier))
                        {
                           returningHandlers.add(handler);
                        }
                     }
                  }
               }
            }
         }
      }

      return Collections.unmodifiableCollection(returningHandlers);
   }
}
