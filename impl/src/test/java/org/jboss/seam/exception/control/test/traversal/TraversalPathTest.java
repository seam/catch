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
import org.jboss.seam.exception.control.test.traversal.Exceptions.SuperException;
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
    * 
    * The following exception chain is thrown: Exception1 -> Exception2 -> Exception3 
    * (where "X -> Y" means X is caused by Y).
    * Besides, the {@link SuperException} is a superclass of Exception3.
    * 
    * The expected order of execution is as follows:
    * 1) Exception1 handler in the descending traversal path
    * 2) Exception2 handler in the descending traversal path
    * 3) Exception3 handler in the descending traversal path
    * 4) Exception3 handler in the ascending traversal path
    * 5) SuperException handler in the ascending traversal path
    * 6) Exception2 handler in the ascending traversal path
    * 7) Exception1 handler in the ascending traversal path
    * 
    **/ 
   @Test
   public void testTraversalPathOrder()
   {
      // create an exception chain E1 -> E2 -> E3
      Exception1 exception = new Exception1(new Exception2(new Exception3()));
      
      manager.fireEvent(new ExceptionToCatch(exception));
      
      Object[] expectedOrder = {1,2,3,4,5,6,7};
      assertArrayEquals(expectedOrder, ExceptionHandler.getExecutionorder().toArray());
   }
}
