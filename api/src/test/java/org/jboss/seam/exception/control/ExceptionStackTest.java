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

/**
 * This class is in the same package as it does test some protected methods.
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 */
package org.jboss.seam.exception.control;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLRecoverableException;
import java.sql.SQLSyntaxErrorException;
import java.sql.SQLTransactionRollbackException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hamcrest.core.IsNull;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

public class ExceptionStackTest {
    @Test
    public void testSQLExceptionUnwrap() {
        SQLTransactionRollbackException transactionRollbackException = new SQLTransactionRollbackException();
        SQLRecoverableException recoverableException = new SQLRecoverableException();
        SQLSyntaxErrorException syntaxErrorException = new SQLSyntaxErrorException();
        recoverableException.setNextException(syntaxErrorException);
        transactionRollbackException.setNextException(recoverableException);
        Throwable e = new Exception(transactionRollbackException);

        ExceptionStack es = new ExceptionStack(e);

        assertThat(es.getCauseElements().size(), is(4));
        assertThat(es.getCauseElements(), hasItems(e, transactionRollbackException, recoverableException, syntaxErrorException));
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertIllegalArgumentExceptionIfCreatedWithEmptyCollection() {
        final ExceptionStack es = new ExceptionStack(Collections.<Throwable>emptyList(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertIllegalArgumentExceptionIfCreatedWithNullCollection() {
        final ExceptionStack es = new ExceptionStack(null, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertIllegalArgumentExceptionIfCreatedWithNull() {
        final ExceptionStack es = new ExceptionStack(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertIllegalArgumentExceptionIfCreatedWithInitialIndexEqualToSize() {
        final List<Throwable> exceptionList = new ArrayList<Throwable>(Arrays.asList(new Exception()));
        final ExceptionStack es = new ExceptionStack(exceptionList, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertIllegalArgumentExceptionIfCreatedWithInitialIndexGreaterThanSize() {
        final List<Throwable> exceptionList = new ArrayList<Throwable>(Arrays.asList(new Exception()));
        final ExceptionStack es = new ExceptionStack(exceptionList, 4);
    }

    @Test
    public void assertStackCreatedAndTraversedCorrectly() {
        final List<Throwable> exceptionList = new ArrayList<Throwable>(Arrays.asList(new RuntimeException(),
                new IOException(), new FileNotFoundException(), new NullPointerException()));
        final ExceptionStack es = new ExceptionStack(exceptionList, 3);

        assertThat(es.getCurrent(), is(exceptionList.get(3)));

        assertThat(es.getNext(), is(exceptionList.get(2)));
    }

    @Test
    public void assertNestedStackInfoIsCorrect() {
        final ExceptionStack stack = new ExceptionStack(new Exception(new NullPointerException()));

        assertThat(stack.isLast(), is(false));
        assertThat(stack.isRoot(), is(true));
        assertThat(stack.getCurrent(), is(NullPointerException.class));
        assertThat(stack.getNext(), is(Exception.class));
        assertThat(stack.getCauseElements().size(), is(2));
        assertThat(stack.getRemaining().size(), is(1));

        stack.dropCause();

        assertThat(stack.isLast(), is(true));
        assertThat(stack.isRoot(), is(false));
        assertThat(stack.getNext(), new IsNull());
        assertThat(stack.getCurrent(), is(Exception.class));
        assertThat(stack.getCauseElements().size(), is(2));
        assertThat(stack.getRemaining().size(), is(0));
    }

    @Test
    public void assertSingleStackInfoIsCorrect() {
        final ExceptionStack stack = new ExceptionStack(new Exception());
        assertThat(stack.isLast(), is(true));
        assertThat(stack.isRoot(), is(true));
        assertThat(stack.getNext(), new IsNull());
        assertThat(stack.getCurrent(), is(Exception.class));
        assertThat(stack.getCauseElements().size(), is(1));
        assertThat(stack.getRemaining().size(), is(0));
    }
}
