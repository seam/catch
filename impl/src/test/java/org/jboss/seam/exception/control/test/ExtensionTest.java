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

package org.jboss.seam.exception.control.test;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.exception.control.extension.CatchExtension;
import org.jboss.seam.exception.control.test.handler.ExtensionExceptionHandler;
import org.jboss.seam.exception.control.test.qualifier.ArquillianLiteral;
import org.jboss.seam.exception.control.test.qualifier.TestingLiteral;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(Arquillian.class)
public class ExtensionTest
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      return ShrinkWrap.create(JavaArchive.class)
         .addClasses(CatchExtension.class, ExtensionExceptionHandler.class)
         .addManifestResource(EmptyAsset.INSTANCE, "beans.xml")
         .addServiceProvider(Extension.class, CatchExtension.class);
   }

   @Inject CatchExtension extension;
   @Inject BeanManager bm;

   @Test
   public void assertHandlersAreFound()
   {
      assertFalse(extension.getHandlersForExceptionType(IllegalArgumentException.class, bm,
                                                        Collections.<Annotation>emptySet()).isEmpty());
   }

   @Test
   public void assertFiveHandlersAreFound()
   {
      assertEquals(5, extension.getHandlersForExceptionType(IllegalArgumentException.class, bm,
                                                            Collections.<Annotation>emptySet()).size());
   }

   @Test
   public void assertQualifiedHandlerAndOthersAreFound()
   {
      HashSet<Annotation> qualifiers = new HashSet<Annotation>();
      qualifiers.add(TestingLiteral.INSTANCE);
      assertEquals(7, extension.getHandlersForExceptionType(IllegalArgumentException.class, bm, qualifiers).size());
   }

   @Test
   public void assertAllValidHandlersAreFound()
   {
      HashSet<Annotation> qualifiers = new HashSet<Annotation>();
      qualifiers.add(TestingLiteral.INSTANCE);
      qualifiers.add(ArquillianLiteral.INSTANCE);
      assertEquals(8, extension.getHandlersForExceptionType(IllegalArgumentException.class, bm, qualifiers).size());
   }
}
