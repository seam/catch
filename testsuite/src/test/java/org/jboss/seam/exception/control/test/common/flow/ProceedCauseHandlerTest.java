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

package org.jboss.seam.exception.control.test.common.flow;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.exception.control.ExceptionToCatch;
import org.jboss.seam.exception.control.test.common.BaseWebArchive;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(Arquillian.class)
public class ProceedCauseHandlerTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        return BaseWebArchive.createBase("proceedCauseHandler.war")
                .addClasses(ProceedCauseHandler.class);
    }

    @Inject
    private BeanManager bm;

    @Test
    public void assertCorrectNumberOfHandlerCallsForProceedCause() {
        bm.fireEvent(new ExceptionToCatch(new Exception(new IllegalArgumentException(new NullPointerException()))));
        assertEquals(0, ProceedCauseHandler.BREADTH_FIRST_NPE_LOWER_PRECEDENCE_CALLED);
        assertEquals(1, ProceedCauseHandler.BREADTH_FIRST_NPE_CALLED);

        assertEquals(0, ProceedCauseHandler.DEPTH_FIRST_NPE_HIGHER_PRECEDENCE_CALLED);
        assertEquals(0, ProceedCauseHandler.DEPTH_FIRST_NPE_CALLED);
    }
}
