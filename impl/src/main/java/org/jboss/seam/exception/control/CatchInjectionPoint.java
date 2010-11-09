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

import org.jboss.weld.extensions.bean.ForwardingInjectionPoint;

import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CatchInjectionPoint extends ForwardingInjectionPoint
{
   private InjectionPoint delegate;
   private Set<Annotation> qualifiers;

   public CatchInjectionPoint(InjectionPoint delegate)
   {
      this.delegate = delegate;
      this.qualifiers = new HashSet<Annotation>(delegate.getQualifiers());
   }

   public void addQualifier(Annotation qualifier)
   {
      this.qualifiers.add(qualifier);
   }

   public void addAllQualifiers(Collection<Annotation> qualifiers)
   {
      this.qualifiers.addAll(qualifiers);
   }

   @Override
   protected InjectionPoint delegate()
   {
      return this.delegate;
   }

   @Override public Set<Annotation> getQualifiers()
   {
      return this.qualifiers;
   }
}
