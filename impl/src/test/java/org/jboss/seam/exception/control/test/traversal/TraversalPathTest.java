/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.seam.exception.control.test.traversal;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.ExceptionToCatch;
import org.jboss.seam.exception.control.extension.CatchExtension;
import org.jboss.seam.exception.control.test.traversal.Exceptions.Exception1;
import org.jboss.seam.exception.control.test.traversal.Exceptions.Exception2;
import org.jboss.seam.exception.control.test.traversal.Exceptions.Exception3;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertArrayEquals;

@RunWith(Arquillian.class)
public class TraversalPathTest
{
   @Inject
   private BeanManager manager;

   @Deployment
   public static Archive<?> createTestArchive()
   {
      return ShrinkWrap.create(JavaArchive.class)
            .addPackage(CaughtException.class.getPackage())
            .addPackage(CatchExtension.class.getPackage())
            .addPackage(TraversalPathTest.class.getPackage())
            .addManifestResource("META-INF/services/javax.enterprise.inject.spi.Extension")
            .addManifestResource(new ByteArrayAsset(new byte[0]), ArchivePaths.create("beans.xml"));
   }

   /**
    * Tests SEAMCATCH-32, see JIRA for more information about this test. https://issues.jboss.org/browse/SEAMCATCH-32
    */
   @Test
   public void testTraversalPathOrder()
   {
      // create an exception chain E1 -> E2 -> E3
      Exception1 exception = new Exception1(new Exception2(new Exception3()));

      manager.fireEvent(new ExceptionToCatch(exception));

      Object[] expectedOrder = { 1, 2, 3, 4, 5, 6, 7, 8 };
      assertArrayEquals(expectedOrder, ExceptionHandler.getExecutionorder().toArray());
   }
}
