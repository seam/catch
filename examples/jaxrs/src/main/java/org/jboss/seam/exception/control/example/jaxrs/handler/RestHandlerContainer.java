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

package org.jboss.seam.exception.control.example.jaxrs.handler;

import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.Handles;
import org.jboss.seam.exception.control.HandlesExceptions;
import org.jboss.seam.rest.exceptions.ErrorMessageWrapper;
import org.jboss.seam.rest.exceptions.ExceptionMapping;
import org.jboss.seam.rest.exceptions.ExceptionMappingConfiguration;

import javax.ws.rs.core.Response;

@HandlesExceptions
public class RestHandlerContainer
{
   public void genericRestHandler(@Handles(precedence = -100) @RestCatch CaughtException<Throwable> event,
                                  @RestCatch Response.ResponseBuilder responseBuilder,
                                  ExceptionMappingConfiguration exceptionMapping)
   {
      final Class exceptionClass = event.getException().getClass();

      for (ExceptionMapping mapping : exceptionMapping.getExceptionMappings())
      {
         if (exceptionClass.equals(mapping.getExceptionType()))
         {
            responseBuilder.status(mapping.getStatusCode());

            if (mapping.getMessage() != null)
            {
               responseBuilder.entity(new ErrorMessageWrapper(mapping.getMessage()));
            }
            break;
         }
      }
   }
}
