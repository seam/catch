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

package org.jboss.seam.exception.control.test.handler;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.exception.control.HandlerMethod;
import org.jboss.seam.exception.control.HandlerMethodImpl;
import org.jboss.seam.exception.control.TraversalMode;
import org.jboss.seam.exception.control.extension.CatchExtension;
import org.jboss.seam.exception.control.test.BaseWebArchive;
import org.jboss.seam.exception.control.test.extension.Account;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(Arquillian.class)
public class HandlerComparatorTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        return BaseWebArchive.createBase("handlerComparator.war")
                .addClasses(ExtensionExceptionHandler.class, Account.class);
    }

    @Inject
    CatchExtension extension;
    @Inject
    BeanManager bm;

    @Test
    public void assertOrderIsCorrectDepthFirst() {
        List<HandlerMethod<? extends Throwable>> handlers = new ArrayList<HandlerMethod<? extends Throwable>>(extension.getHandlersForExceptionType(
                IllegalArgumentException.class, bm, Collections.<Annotation>emptySet(), TraversalMode.DEPTH_FIRST));

        assertEquals("catchThrowable", ((HandlerMethodImpl<?>) handlers.get(0)).getJavaMethod().getName());
        assertEquals("catchThrowableP20", ((HandlerMethodImpl<?>) handlers.get(1)).getJavaMethod().getName());
        assertEquals("catchRuntime", ((HandlerMethodImpl<?>) handlers.get(2)).getJavaMethod().getName());
        assertEquals("catchIAE", ((HandlerMethodImpl<?>) handlers.get(3)).getJavaMethod().getName());
    }

    @Test
    public void assertOrderIsCorrectBreadthFirst() {
        List<HandlerMethod<? extends Throwable>> handlers = new ArrayList<HandlerMethod<? extends Throwable>>(extension.getHandlersForExceptionType(
                Exception.class, bm, Collections.<Annotation>emptySet(), TraversalMode.BREADTH_FIRST));

        assertThat(handlers.size(), is(4));
    }
}
