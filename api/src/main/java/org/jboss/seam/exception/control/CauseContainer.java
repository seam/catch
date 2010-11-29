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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Information about the current exception and exception cause container.  This object is immutable.
 */
public class CauseContainer
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
   public CauseContainer(final Collection<Throwable> causeChainElements, final int currentElementIndex)
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

   public Collection<Throwable> getCauseElements()
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
