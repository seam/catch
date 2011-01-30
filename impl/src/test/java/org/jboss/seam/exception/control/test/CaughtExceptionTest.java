/**
 *
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 */
package org.jboss.seam.exception.control.test;

import org.jboss.seam.exception.control.CaughtException;
import org.junit.Test;

public class CaughtExceptionTest
{
   @Test(expected = IllegalArgumentException.class)
   public void assertIllegalArgumentExceptionThrownWhenExceptionStackIsNull()
   {
      final CaughtException<Exception> ce = new CaughtException<Exception>(null, true, false);
   }
}
