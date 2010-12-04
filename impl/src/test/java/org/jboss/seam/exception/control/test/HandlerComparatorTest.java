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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.exception.control.HandlerMethod;
import org.jboss.seam.exception.control.extension.CatchExtension;
import org.jboss.seam.exception.control.test.handler.ExtensionExceptionHandler;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class HandlerComparatorTest
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      return ShrinkWrap.create(JavaArchive.class)
         .addClasses(CatchExtension.class, ExtensionExceptionHandler.class)
         .addManifestResource("META-INF/services/javax.enterprise.inject.spi.Extension")
         .addManifestResource(new ByteArrayAsset(new byte[0]), ArchivePaths.create("beans.xml"));
   }

   @Inject CatchExtension extension;
   @Inject BeanManager bm;

   @Test
   public void assertOrderIsCorrect()
   {
      List<HandlerMethod> handlers = new ArrayList<HandlerMethod>(extension.getHandlersForExceptionType(
         IllegalArgumentException.class, bm, Collections.<Annotation>emptySet()));

      assertEquals("catchDescException", handlers.get(0).getJavaMethod().getName());
      assertEquals("catchFrameworkDescException", handlers.get(1).getJavaMethod().getName());
      assertEquals("catchIAE", handlers.get(2).getJavaMethod().getName());
      assertEquals("catchRuntime", handlers.get(3).getJavaMethod().getName());
      assertEquals("catchThrowable", handlers.get(4).getJavaMethod().getName());
      assertEquals("catchThrowableP20", handlers.get(5).getJavaMethod().getName());
   }
}
