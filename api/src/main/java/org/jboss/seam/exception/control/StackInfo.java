/*
 * JBoss, Home of Professional Open Source
 *  Copyright 2010, Red Hat, Inc., and individual contributors
 *  by the @authors tag. See the copyright.txt in the distribution for a
 *  full listing of individual contributors.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.seam.exception.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Information about the current exception and exception cause chain.  This object is immutable.
 */
public class StackInfo
{
   private final boolean root;
   private final boolean last;
   private final int index;
   private final Throwable nextCause;
   private final Collection<Throwable> remainingCauses;
   private final Collection<Throwable> elements;
   private final Throwable currentCause;

   /**
    * Basic constructor.
    *
    * @param causeChainElements  collection of all causing elements for an exception from top to bottom (not unwrapped).
    * @param currentElementIndex index of current element within the causeChainElements.
    *
    * @throws IllegalArgumentException if causeChainElements is empty or null.
    */
   public StackInfo(final Collection<Throwable> causeChainElements, final int currentElementIndex)
   {
      if (causeChainElements == null || causeChainElements.size() == 0)
      {
         throw new IllegalArgumentException("Null or empty collection of causeChainElements is not valid");
      }
      this.elements = Collections.unmodifiableCollection(causeChainElements);
      this.index = currentElementIndex;

      this.last = this.index == 0;

      this.root = this.index == causeChainElements.size() - 1;

      this.nextCause = this.index - 1 >= 0 ? (Throwable) this.elements.toArray()[this.index - 1] : null;

      this.remainingCauses = new ArrayList<Throwable>(this.elements).subList(0, currentElementIndex);

      this.currentCause = (Throwable) this.elements.toArray()[this.index];
   }

   public Collection<Throwable> getElements()
   {
      return Collections.unmodifiableCollection(this.elements);
   }

   public int getIndex()
   {
      return this.index;
   }

   public boolean isLast()
   {
      return this.last;
   }

   public Throwable getNextCause()
   {
      return this.nextCause;
   }

   public Collection<Throwable> getRemainingCauses()
   {
      return Collections.unmodifiableCollection(this.remainingCauses);
   }

   public boolean isRoot()
   {
      return this.root;
   }

   public Throwable getCurrentCause()
   {
      return currentCause;
   }
}
