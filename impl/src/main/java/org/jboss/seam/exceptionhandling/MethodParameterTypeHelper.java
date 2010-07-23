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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper to encapsulate reflection code for finding parameter types on the
 * handler method.
 */
final public class MethodParameterTypeHelper
{
   private Set<Class> exceptionTypes;
   private Set<Class> stateTypes;

   /**
    * Constructor.
    *
    * @param handler Handler for which this helper is constructed
    */
   public MethodParameterTypeHelper(ExceptionHandler handler)
   {
      final Method[] methods = handler.getClass().getMethods();
      this.exceptionTypes = new HashSet<Class>();
      this.stateTypes = new HashSet<Class>();

      for (Method m : methods)
      {
         if ("handle".equals(m.getName()))
         {
            this.stateTypes.add(m.getParameterTypes()[1]);
            this.exceptionTypes.add(m.getParameterTypes()[2]);
         }
      }

      // Cleanup base types if needed
      if (this.stateTypes.size() > 1)
      {
         this.stateTypes.remove(State.class);
      }

      if (this.exceptionTypes.size() > 1)
      {
         this.exceptionTypes.remove(Throwable.class);
      }
   }

   /**
    * @return Unmodifiable collection of exception types found in the handle
    *         method(s).
    */
   public Collection<Class> getExceptionTypes()
   {
      return Collections.unmodifiableCollection(exceptionTypes);
   }

   /**
    * @return Unmodifiable collection of exception types found in the handle
    *         method(s).
    */
   public Collection<Class> getStateTypes()
   {
      return Collections.unmodifiableCollection(stateTypes);
   }

   /**
    * Looks for the given type or super types in the exception type collection.
    *
    * @param type type (or super types of) to search for
    * @return flag indicating if the type or super types of that type are found
    */
   public boolean containsExceptionTypeOrSuperType(Class type)
   {
      return this.containsTypeOrSuperType(this.exceptionTypes, type);
   }

   /**
    * Looks for the given type or super types in the state type collection.
    *
    * @param type type (or super types of) to search for
    * @return flag indicating if the type or super types of that type are found
    */
   public boolean containsStateTypeOrSuperType(Class type)
   {
      return this.containsTypeOrSuperType(this.stateTypes, type);
   }

   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o == null || getClass() != o.getClass())
      {
         return false;
      }

      MethodParameterTypeHelper that = (MethodParameterTypeHelper) o;

      return this.exceptionTypes.equals(that.exceptionTypes) && this.stateTypes.equals(that.stateTypes);
   }

   public int hashCode()
   {
      int result = exceptionTypes != null ? exceptionTypes.hashCode() : 5;
      result = 73 * result + (stateTypes != null ? stateTypes.hashCode() : 5);
      return result;
   }

   /**
    * Looks for the given type or super types in the given collection.
    *
    * @param types Collection to search
    * @param type  type (or super types of) to search for
    * @return flag indicating if the type or super types of that type are found
    */
   @SuppressWarnings("unchecked")
   private boolean containsTypeOrSuperType(Collection<Class> types, Class type)
   {
      if (types.contains(type))
      {
         return true;
      }

      for (Class t : types)
      {
         if (t.isAssignableFrom(type))
         {
            return true;
         }
      }

      return false;
   }
}
