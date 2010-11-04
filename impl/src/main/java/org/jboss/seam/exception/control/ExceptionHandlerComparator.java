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

import org.jboss.weld.extensions.reflection.HierarchyDiscovery;

import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

/**
 * Comparator to sort exception handlers according to priority (highest number first) then by class hierarchy of the Exception types
 * being handled (most specific first).
 */
@SuppressWarnings({"MethodWithMoreThanThreeNegations", "unchecked"})
public final class ExceptionHandlerComparator implements Comparator<AnnotatedMethod>
{

   /**
    * {@inheritDoc}
    */
   public int compare(AnnotatedMethod lhs, AnnotatedMethod rhs)
   {
      final AnnotatedParameter lhsEventParam = (AnnotatedParameter) lhs.getParameters().get(0);
      final AnnotatedParameter rhsEventParam = (AnnotatedParameter) rhs.getParameters().get(0);

      final Type lhsExceptionType = ((ParameterizedType) lhsEventParam.getBaseType()).getActualTypeArguments()[0];
      final Type rhsExceptionType = ((ParameterizedType) rhsEventParam.getBaseType()).getActualTypeArguments()[0];

      if (lhsExceptionType.equals(rhsExceptionType))
      {
         final int lhsPrecedence = lhsEventParam.getAnnotation(Handles.class).precedence();
         final int rhsPrecedence = rhsEventParam.getAnnotation(Handles.class).precedence();
         return this.comparePrecedence(lhsPrecedence, rhsPrecedence);
      }
      return compareHierarchies(lhsExceptionType, rhsExceptionType);
   }

   private int compareHierarchies(Type lhsExceptionType, Type rhsExceptionType)
   {
      HierarchyDiscovery lhsHierarchy = new HierarchyDiscovery(lhsExceptionType);
      Set<Type> lhsTypeclosure = lhsHierarchy.getTypeClosure();

      if (lhsTypeclosure.contains(rhsExceptionType))
      {
         final int indoxOfLhsType = new ArrayList(lhsTypeclosure).indexOf(lhsExceptionType);
         final int indoxOfRhsType = new ArrayList(lhsTypeclosure).indexOf(rhsExceptionType);

         return indoxOfLhsType - indoxOfRhsType;
      }
      return -1;
   }

   private int comparePrecedence(final int lhs, final int rhs)
   {
      return (lhs - rhs) * -1;
   }
}
