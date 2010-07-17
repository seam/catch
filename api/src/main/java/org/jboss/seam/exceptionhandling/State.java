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
package org.jboss.seam.exceptionhandling;

import javax.enterprise.inject.spi.BeanManager;

/**
 * The State object is meant to provide any state that may be helpful for the developer
 * in determining what to do with a particular exceptionhandling. It may include and application
 * state, active processes, or other convenience methods for the {@link ExceptionHandler}
 * developer.
 * <p>
 * A servlet state may include methods to retrieve the HttpServletRequest, HttpServletResponse
 * and possibly a navigation convenience method.
 */
public interface State
{
   /**
    * @return current BeanManager.
    */
   public BeanManager getBeanManager();
}
