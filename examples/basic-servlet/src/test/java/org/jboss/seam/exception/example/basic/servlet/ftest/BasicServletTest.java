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
package org.jboss.seam.exception.example.basic.servlet.ftest;

import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.net.MalformedURLException;
import java.net.URL;
import org.jboss.test.selenium.AbstractTestCase;
import org.jboss.test.selenium.locator.XpathLocator;
import static org.jboss.test.selenium.locator.LocatorFactory.xp;
import static org.jboss.test.selenium.guard.request.RequestTypeGuardFactory.waitHttp;

/**
 * A functional test for the Basic Servlet example
 * 
 * @author Martin Gencur
 * 
 */
public class BasicServletTest extends AbstractTestCase
{
   protected XpathLocator NULLPOINTER_LINK = xp("//a[contains(@href,'NullPointerException')]");
   protected XpathLocator ASSERTIONERROR_LINK = xp("//a[contains(@href,'AssertionError')]");
   protected XpathLocator WRAPPEDILLEGALARG_LINK = xp("//a[contains(@href,'WrappedIllegalArg')]");
   protected XpathLocator IOEXCEPTION_LINK = xp("//a[contains(@href,'IOException')]");

   @BeforeMethod
   public void openStartUrl() throws MalformedURLException
   {
      selenium.setSpeed(100);
      selenium.open(new URL(contextPath.toString()));
   }

   @Test
   public void testNullPointerException()
   {
      waitHttp(selenium).click(NULLPOINTER_LINK);
      assertTrue(selenium.isTextPresent("using handler throwableHandler marking exception with markHandled " +
      		                            "message: Null pointer thrown"),
      		                            "The information about using throwableHandler should appear");
      assertTrue(selenium.isTextPresent("using handler nullPointerHandler marking exception with handled " +
      		                            "message: Null pointer thrown"),
      		                            "The information about using nullPointerHandler should appear");
   }
   
   @Test
   public void testAssertionError()
   {
      waitHttp(selenium).click(ASSERTIONERROR_LINK);
      assertTrue(selenium.isTextPresent("using handler throwableHandler marking exception with markHandled " +
                                        "message: No enum const class org.jboss.seam.exception.example.basic." +
                                        "servlet.navigation.NavigationServlet$NavigationEnum.ASSERTIONERROR"),
                                        "The information about using throwableHandler should appear");
      assertTrue(selenium.isTextPresent("using handler illegalArgumentBreadthFirstHandler marking exception with " +
                                        "dropCause message: No enum const class org.jboss.seam.exception.example.basic." +
                                        "servlet.navigation.NavigationServlet$NavigationEnum.ASSERTIONERROR"),
                                        "The information about using illegalArgumentBreadthFirstHandler should appear");
   }
   
   @Test
   public void testIllegalStateException()
   {
      waitHttp(selenium).click(WRAPPEDILLEGALARG_LINK);
      assertTrue(selenium.isTextPresent("using handler throwableHandler marking exception with markHandled message: " +
                                        "Inner IAE"), "The information about using throwableHandler should appear");
      assertTrue(selenium.isTextPresent("using handler illegalArgumentBreadthFirstHandler marking exception with " +
                                        "dropCause message: Inner IAE"),
                                        "The information about using illegalArgumentBreadthFirstHandler should appear");
      assertTrue(selenium.isTextPresent("using handler throwableHandler marking exception with markHandled message: " +
                                        "Wrapping IllegalStateException"),
                                        "The information about using throwableHandler should appear");
      assertTrue(selenium.isTextPresent("using handler illegalStateHandler marking exception with abort message: " +
                                        "Wrapping IllegalStateException"),
                                        "The information about using illegalStateHandler should appear");
   }
   
   @Test
   public void testIOException()
   {
      waitHttp(selenium).click(IOEXCEPTION_LINK);
      assertTrue(selenium.isTextPresent("java.lang.ArithmeticException: Re-thrown"), "An exception should have been thrown");
   }
}
