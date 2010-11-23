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

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Entry point event into the Catch system.  This object is nearly immutable, the only mutable portion
 * is the handled flag.
 */
public class ExceptionToCatchEvent
{
   private final Throwable exception;
   private boolean handled;
   private final Set<Annotation> qualifiers;

   /**
    * Constructor that adds qualifiers for the handler(s) to run.
    * Typically only integrators will be using this constructor.
    *
    * @param exception  Exception to handle
    * @param qualifiers qualifiers to use to narrow the handlers called
    */
   public ExceptionToCatchEvent(Throwable exception, Annotation... qualifiers)
   {
      this.exception = exception;
      this.qualifiers = new HashSet<Annotation>();
      Collections.addAll(this.qualifiers, qualifiers);
   }

   /**
    * Basic constructor without any qualifiers defined.
    *
    * @param exception Exception to handle.
    */
   public ExceptionToCatchEvent(Throwable exception)
   {
      this.exception = exception;
      this.qualifiers = Collections.emptySet();
   }

   public Throwable getException()
   {
      return exception;
   }

   public void setHandled(boolean handled)
   {
      this.handled = handled;
   }

   public boolean isHandled()
   {
      return handled;
   }

   public Set<Annotation> getQualifiers()
   {
      return Collections.unmodifiableSet(qualifiers);
   }
}