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
 *
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 */
package org.jboss.seam.exception.control.test.stack;

import java.sql.SQLRecoverableException;
import java.sql.SQLSyntaxErrorException;
import java.sql.SQLTransactionRollbackException;

import org.jboss.seam.exception.control.ExceptionStack;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

public class ExceptionStackTest
{
   @Test
   public void testSQLExceptionUnwrap()
   {
      SQLTransactionRollbackException transactionRollbackException = new SQLTransactionRollbackException();
      SQLRecoverableException recoverableException = new SQLRecoverableException();
      SQLSyntaxErrorException syntaxErrorException = new SQLSyntaxErrorException();
      recoverableException.setNextException(syntaxErrorException);
      transactionRollbackException.setNextException(recoverableException);
      Throwable e = new Exception(transactionRollbackException);

      ExceptionStack es = new ExceptionStack(e);

      assertThat(es.getElements().size(), is(4));
      assertThat(es.getElements(), hasItems(e, transactionRollbackException, recoverableException, syntaxErrorException));
   }
}
