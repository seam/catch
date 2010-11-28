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

import java.util.List;

import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.CauseContainer;
import org.jboss.seam.exception.control.Handles;
import org.jboss.seam.exception.control.HandlesExceptions;
import org.jboss.seam.exception.control.TraversalPath;
import org.jboss.seam.rest.exceptions.ErrorMessageWrapper;

@HandlesExceptions
public class RestExceptionHandlers
{
   /**
    * An example that demonstrates logging all exceptions to stdout that are caught during a REST resource request
    */
   public void logException(@Handles(during = TraversalPath.DESCENDING) @RestRequest final CaughtException<Throwable> event)
   {
      CauseContainer c = event.getCauseContainer();
      System.out.println("Caught exception (" + (c.getIndex() + 1) + " in stack of " + c.getCauseElements().size() + ") => " +
            event.getException().getClass().getSimpleName() + "(\"" + event.getException().getMessage() + "\") ");
   }

   /**
    * An example that demonstrates using XML-based exception mapping configuration to handle an
    * exception that occurs during a REST resource request by sending an HTTP error response.
    */
   public void configurableExceptionHandler(@Handles(precedence = -100) @RestRequest final CaughtException<Throwable> event,
                                  @CatchResource final ResponseBuilder responseBuilder,
                                  @RestRequest final List<ExceptionResponse> exceptionResponses)
   {
      final Class<?> exceptionClass = event.getException().getClass();

      for (ExceptionResponse response : exceptionResponses)
      {
         if (exceptionClass.equals(response.getForType()))
         {
            responseBuilder.status(response.getStatusCode());

            if (response.getMessage() != null)
            {
               responseBuilder.entity(new ErrorMessageWrapper(response.getMessage()));
            }

            //event.proceed(); ??
            break;
         }
      }
   }
   
   // Java-based config
//   @Produces
//   @ApplicationScoped
//   @RestRequest
//   public List<ExceptionResponse> getExceptionResponseMappings()
//   {
//      return Arrays.asList(
//         new ExceptionResponse(NoResultException.class, 404, "Request resource does not exist"),
//         new ExceptionResponse(IllegalArgumentException.class, 400, "Illegal value")
//      );
//   }
}
