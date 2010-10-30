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
import org.jboss.seam.exception.control.ExceptionEvent;
import org.jboss.seam.exception.control.ExceptionHandlerExecutor;
import org.jboss.seam.exception.control.StateImpl;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.asset.ByteArrayAsset;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class UnsupportedOperationExceptionHandlerTest extends BaseExceptionHandlerTest
{
   @Inject
   private UnsupportedOperationExceptionHandler handler;

   @Deployment
   public static Archive<?> createTestArchive()
   {
      return ShrinkWrap.create("test.jar", JavaArchive.class)
            .addClasses(UnsupportedOperationExceptionHandler.class, ExceptionHandlerExecutor.class)
            .addManifestResource(new ByteArrayAsset(new byte[0]), ArchivePaths.create("beans.xml"));
   }

   @Test
   public void testHandlerIsCalled() throws IOException
   {
      this.handler.shouldCallEnd(true); // Set so I can reuse this handler in different tests
      ExceptionEvent event = new ExceptionEvent(new UnsupportedOperationException(), new StateImpl(this.beanManager));
      this.beanManager.fireEvent(event);

      assertTrue(this.handler.isHandleCalled());
      assertTrue(event.isExceptionHandled());
   }
}
