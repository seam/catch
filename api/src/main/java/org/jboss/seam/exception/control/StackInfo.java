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

public class StackInfo<T extends Throwable>
{
   private boolean root;
   private boolean last;
   private int index;
   private Throwable nextCause;
   private Collection<Throwable> remainingCauses;
   private Collection<Throwable> elements;
   private Throwable currentCause;

   public StackInfo(final Collection<Throwable> elements, final int index)
   {
      this.elements = elements;
      this.index = index;

      if (this.index == 0)
      {
         this.root = true;
      }

      if (this.index == elements.size() - 1)
      {
         this.last = true;
      }

      if (this.index + 1 < this.elements.size())
      {
         this.nextCause = (Throwable) this.elements.toArray()[this.index + 1];
      }

      this.remainingCauses = new ArrayList<Throwable>(elements).subList(index, elements.size() - 1);

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
