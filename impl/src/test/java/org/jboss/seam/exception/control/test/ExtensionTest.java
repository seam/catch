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

package org.jboss.seam.exception.control.test;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.exception.control.extension.CatchExtension;
import org.jboss.seam.exception.control.test.handler.ExtensionExceptionHandler;
import org.jboss.seam.exception.control.test.qualifier.ArquillianLiteral;
import org.jboss.seam.exception.control.test.qualifier.CatchQualifierLiteral;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

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
      qualifiers.add(CatchQualifierLiteral.INSTANCE);
      assertEquals(7, extension.getHandlersForExceptionType(IllegalArgumentException.class, bm, qualifiers).size());
   }

   @Test
   public void assertAllValidHandlersAreFound()
   {
      HashSet<Annotation> qualifiers = new HashSet<Annotation>();
      qualifiers.add(CatchQualifierLiteral.INSTANCE);
      qualifiers.add(ArquillianLiteral.INSTANCE);
      assertEquals(8, extension.getHandlersForExceptionType(IllegalArgumentException.class, bm, qualifiers).size());
   }
}
