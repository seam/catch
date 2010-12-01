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

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.ExceptionToCatch;
import org.jboss.seam.exception.control.extension.CatchExtension;
import org.jboss.seam.exception.control.test.handler.CalledExceptionHandler;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class CallingHandlersTest
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      return ShrinkWrap.create(JavaArchive.class)
         .addPackage(CaughtException.class.getPackage())
         .addClasses(CalledExceptionHandler.class, CatchExtension.class)
         .addManifestResource("META-INF/services/javax.enterprise.inject.spi.Extension")
         .addManifestResource(new ByteArrayAsset(new byte[0]), ArchivePaths.create("beans.xml"));
   }

   @Inject
   private BeanManager bm;

   @Test
   public void assertOutboundHanldersAreCalled()
   {
      bm.fireEvent(new ExceptionToCatch(new IllegalArgumentException()));

      assertTrue(CalledExceptionHandler.OUTBOUND_HANDLER_CALLED);
   }

   @Test
   public void assertOutboundHanldersAreCalledOnce()
   {
      CalledExceptionHandler.OUTBOUND_HANDLER_TIMES_CALLED = 0;
      bm.fireEvent(new ExceptionToCatch(new IllegalArgumentException()));
      assertEquals(1, CalledExceptionHandler.OUTBOUND_HANDLER_TIMES_CALLED);
   }

   @Test
   public void assertInboundHanldersAreCalledOnce()
   {
      CalledExceptionHandler.INBOUND_HANDLER_TIMES_CALLED = 0;
      bm.fireEvent(new ExceptionToCatch(new IllegalArgumentException()));
      assertEquals(1, CalledExceptionHandler.INBOUND_HANDLER_TIMES_CALLED);
   }

   @Test
   public void assertAdditionalParamsAreInjected()
   {
      bm.fireEvent(new ExceptionToCatch(new RuntimeException(new IllegalArgumentException())));
      assertTrue(CalledExceptionHandler.BEANMANAGER_INJECTED);
   }

   @Test
   public void assertProtectedHandlersAreCalled()
   {
      bm.fireEvent(new ExceptionToCatch(new IllegalStateException()));
      assertTrue(CalledExceptionHandler.PROTECTED_HANDLER_CALLED);
   }
}
