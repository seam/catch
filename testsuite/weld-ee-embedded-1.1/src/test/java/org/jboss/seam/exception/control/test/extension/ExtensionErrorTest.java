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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.exception.control.test.BaseWebArchive;
import org.jboss.seam.exception.control.test.handler.HandlerWhichThrowsExceptions;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.weld.exceptions.DefinitionException;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ExtensionErrorTest {
    @Deployment(name = "weldErrorArchive")
    @ShouldThrowException(DefinitionException.class)
    public static Archive<?> createTestArchive() {
        return BaseWebArchive.createBase("extensionError.war")
                .addClasses(HandlerWhichThrowsExceptions.class);
    }

    @Test
    @OperateOnDeployment("weldErrorArchive")
    public void assertHandlersAreFound() {
    }
}
