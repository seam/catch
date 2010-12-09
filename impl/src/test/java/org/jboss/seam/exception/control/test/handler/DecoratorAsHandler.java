package org.jboss.seam.exception.control.test.handler;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.el.ELResolver;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.Handles;
import org.jboss.seam.exception.control.HandlesExceptions;

/**
 * A decorator which declares itself as an exception handler,
 * which is not allowed.
 */
@Decorator
@HandlesExceptions
public abstract class DecoratorAsHandler extends ELResolver
{
   @Inject @Delegate @Any
   private ELResolver delegate;
   
   public void handlesAll(@Handles CaughtException<Throwable> caught)
   {
   }
}
