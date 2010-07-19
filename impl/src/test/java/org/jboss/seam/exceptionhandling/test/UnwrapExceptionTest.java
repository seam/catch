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
package org.jboss.seam.exceptionhandling.test;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.exceptionhandling.ExceptionEvent;
import org.jboss.seam.exceptionhandling.ExceptionHandlerExecutor;
import org.jboss.seam.exceptionhandling.StateImpl;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.asset.ByteArrayAsset;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class UnwrapExceptionTest extends BaseExceptionHandlerTest {
    @Inject
    private UnsupportedOperationExceptionHandler unsupportedOperationExceptionHandler;

    @Inject
    private NullPointerExceptionHandler nullPointerExceptionHandler;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create("test.jar", JavaArchive.class)
                .addClasses(UnsupportedOperationExceptionHandler.class,
                        ExceptionHandlerExecutor.class, NullPointerExceptionHandler.class)
                .addManifestResource(new ByteArrayAsset(new byte[0]), ArchivePaths.create("beans.xml"));
    }

    @Test
    public void assertInnerExceptionHandledOnlyCalled() {
        Exception e = new UnsupportedOperationException("test", new NullPointerException("test"));

        this.nullPointerExceptionHandler.shouldCallEnd(true);

        ExceptionEvent event = new ExceptionEvent(e, new StateImpl(this.beanManager));
        this.beanManager.fireEvent(event);

        assertTrue(this.nullPointerExceptionHandler.isHandleCalled());
        assertFalse(this.unsupportedOperationExceptionHandler.isHandleCalled());

    }
}
