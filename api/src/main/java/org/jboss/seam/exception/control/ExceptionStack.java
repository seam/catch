/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Information about the current exception and exception cause container.  This object is not immutable.
 */
public class ExceptionStack implements Serializable
{
   private static final long serialVersionUID = 5988683320170873619L;

   private boolean root;
   private boolean last;
   private int index;
   private Throwable next;
   private Collection<Throwable> remaining;
   private Collection<Throwable> elements;
   private Throwable current;

   public ExceptionStack()
   {
   } // needed to be a bean

   /**
    * Builds the stack from the given exception.
    *
    * @param exception Caught exception
    */
   public ExceptionStack(final Throwable exception)
   {
      if (exception == null)
      {
         throw new IllegalArgumentException("exception must not be null");
      }

      Throwable e = exception;
      this.elements = new ArrayList<Throwable>();

      do
      {
         this.elements.add(e);
         if (e instanceof SQLException)
         {
            SQLException sqlException = (SQLException) e;
            while (sqlException.getNextException() != null)
            {
               sqlException = sqlException.getNextException();
               this.elements.add(sqlException);
            }
         }
      }
      while ((e = e.getCause()) != null);

      this.index = elements.size() - 1;
      this.init();
   }

   /**
    * Basic constructor.
    *
    * @param causeChainElements  collection of all causing elements for an exception from top to bottom (not
    *                            unwrapped).
    * @param currentElementIndex index of current element within the causeChainElements.
    * @throws IllegalArgumentException if causeChainElements is empty or null.
    */
   public ExceptionStack(final Collection<Throwable> causeChainElements, final int currentElementIndex)
   {
      if (causeChainElements == null || causeChainElements.size() == 0)
      {
         throw new IllegalArgumentException("Null or empty collection of causeChainElements is not valid");
      }

      if (currentElementIndex >= causeChainElements.size())
      {
         throw new IllegalArgumentException("currentElementIndex must be less than or equals to causeChainElements.size()");
      }
      this.elements = Collections.unmodifiableCollection(causeChainElements);
      this.index = currentElementIndex;
      this.init();
   }

   private void init()
   {
      this.root = this.index == this.elements.size() - 1;
      this.next = this.index - 1 >= 0 ? (Throwable) this.elements.toArray()[this.index - 1] : null;

      if (this.index >= 0)
      {
         this.remaining = new ArrayList<Throwable>(this.elements).subList(0, this.index);
         this.current = (Throwable) this.elements.toArray()[this.index];
      }
      else
      {
         this.remaining = Collections.emptyList();
         this.current = null;
      }

      this.last = this.remaining.isEmpty();
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

   public Throwable getNext()
   {
      return this.next;
   }

   public Collection<Throwable> getRemaining()
   {
      return Collections.unmodifiableCollection(this.remaining);
   }

   public boolean isRoot()
   {
      return this.root;
   }

   public Throwable getCurrent()
   {
      return current;
   }

   public void setCauseElements(Collection<Throwable> elements)
   {
      this.elements = Collections.unmodifiableCollection(elements);
      this.init();
   }

   public void setIndex(int index)
   {
      if (index >= this.elements.size())
      {
         throw new IllegalArgumentException("Index greater than elements.size()");
      }
      this.index = index;
      this.init();
   }

   /**
    * Move to the next causing exception.
    */
   protected void advanceToNextCause()
   {
      this.setIndex(this.index - 1);
   }
}
