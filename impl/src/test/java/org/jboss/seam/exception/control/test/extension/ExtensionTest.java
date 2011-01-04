/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

package org.jboss.seam.exception.control.test.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.exception.control.HandlerMethod;
import org.jboss.seam.exception.control.TraversalMode;
import org.jboss.seam.exception.control.extension.CatchExtension;
import org.jboss.seam.exception.control.test.extension.literal.ArquillianLiteral;
import org.jboss.seam.exception.control.test.extension.literal.CatchQualifierLiteral;
import org.jboss.seam.exception.control.test.handler.ExtensionExceptionHandler;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

@RunWith(Arquillian.class)
public class ExtensionTest
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      return ShrinkWrap.create(JavaArchive.class)
            .addClasses(CatchExtension.class, ExtensionExceptionHandler.class, StereotypedHandler.class,
                  InterceptorAsHandler.class, PretendInterceptorBinding.class, DecoratorAsHandler.class)
            .addManifestResource(new StringAsset(
                  "<beans>" +
                        "   <interceptors><class>" + InterceptorAsHandler.class.getName() + "</class></interceptors>" +
                        "   <decorators><class>" + DecoratorAsHandler.class.getName() + "</class></decorators>" +
                        "</beans>"), "beans.xml")
            .addServiceProvider(Extension.class, CatchExtension.class);
   }

   @Inject
   CatchExtension extension;
   @Inject
   BeanManager bm;

   @Test
   public void assertAnyHandlersAreFound()
   {
      assertFalse(extension.getHandlersForExceptionType(IllegalArgumentException.class, bm,
            Collections.<Annotation>emptySet(), TraversalMode.DEPTH_FIRST).isEmpty());
   }

   /**
    * Verifies that the expected number of handlers are found. If the extension where to scan interceptors and
    * decorators for handlers, this assertion would fail.
    *
    * @see ExtensionExceptionHandler
    * @see InterceptorAsHandler
    * @see DecoratorAsHandler
    */
   @Test
   public void assertNumberOfHandlersFoundMatchesExpectedDepthFirst()
   {
      assertEquals(5, extension.getHandlersForExceptionType(IllegalArgumentException.class, bm,
            Collections.<Annotation>emptySet(), TraversalMode.DEPTH_FIRST).size());
   }

   @Test
   public void assertNumberOfHandlersFoundMatchesExpectedBreathFirst()
   {
      assertEquals(2, extension.getHandlersForExceptionType(IllegalArgumentException.class, bm,
            Collections.<Annotation>emptySet(), TraversalMode.BREADTH_FIRST).size());
   }

   @Test
   public void assertSQLHandlerFound()
   {
      final List<HandlerMethod> handlerMethods = new ArrayList<HandlerMethod>(extension.getHandlersForExceptionType(
            SQLException.class, bm, Collections.<Annotation>emptySet(), TraversalMode.DEPTH_FIRST));
      assertThat(handlerMethods.size(), is(4));
      assertThat(handlerMethods.get(3).getExceptionType(), equalTo((Type) SQLException.class));
   }

   @Test
   public void assertQualifiedHandlerAndOthersAreFound()
   {
      HashSet<Annotation> qualifiers = new HashSet<Annotation>();
      qualifiers.add(CatchQualifierLiteral.INSTANCE);
      assertEquals(7, extension.getHandlersForExceptionType(IllegalArgumentException.class, bm, qualifiers,
            TraversalMode.DEPTH_FIRST).size());
   }

   @Test
   public void assertAllValidHandlersAreFoundDepthFirst()
   {
      HashSet<Annotation> qualifiers = new HashSet<Annotation>();
      qualifiers.add(CatchQualifierLiteral.INSTANCE);
      qualifiers.add(ArquillianLiteral.INSTANCE);
      assertEquals(8, extension.getHandlersForExceptionType(IllegalArgumentException.class, bm, qualifiers,
            TraversalMode.DEPTH_FIRST).size());
   }

   @Test
   public void assertAllValidHandlersAreFoundBreadthFirst()
   {
      HashSet<Annotation> qualifiers = new HashSet<Annotation>();
      qualifiers.add(CatchQualifierLiteral.INSTANCE);
      qualifiers.add(ArquillianLiteral.INSTANCE);
      assertEquals(2, extension.getHandlersForExceptionType(IllegalArgumentException.class, bm, qualifiers,
            TraversalMode.BREADTH_FIRST).size());
   }
}
