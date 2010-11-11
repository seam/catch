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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

/**
 * Comparator to sort exception handlers according qualifier
 * ({@link org.jboss.seam.exception.control.TraversalPath#ASCENDING} first), precedence (highest to lowest) and
 * finally hierarchy (least to most specific).
 */
@SuppressWarnings({"MethodWithMoreThanThreeNegations", "unchecked"})
public final class ExceptionHandlerComparator implements Comparator<HandlerMethod>
{

   /**
    * {@inheritDoc}
    */
   public int compare(HandlerMethod lhs, HandlerMethod rhs)
   {
      if (lhs.equals(rhs))
      {
         return 0;
      }

      if (lhs.getExceptionType().equals(rhs.getExceptionType()))
      {
         // Really this is so all handlers are returned in the TreeSet (even if they're of the same type, but one is
         // inbound, the other is outbound
         if (lhs.getTraversalPath() == rhs.getTraversalPath())
         {
            final int returnValue = this.comparePrecedence(lhs.getPrecedence(), rhs.getPrecedence());
            // Compare number of qualifiers if they exist so handlers that handle the same type
            // are both are returned and not thrown out (order doesn't really matter)
            if (returnValue == 0 && !lhs.getQualifiers().isEmpty())
            {
               return -1;
            }

            // Either precedence is non-zero or lhs doesn't have qualifiers so return the precedence compare
            // If it's 0 this is essentially the same handler for our purposes
            return returnValue;
         }
         else if (lhs.getTraversalPath() == TraversalPath.DESCENDING)
         {
            return -1; // DuringDescTraversal first
         }
         else
         {
            return 1;
         }
      }
      return compareHierarchies(lhs.getExceptionType(), rhs.getExceptionType());
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
