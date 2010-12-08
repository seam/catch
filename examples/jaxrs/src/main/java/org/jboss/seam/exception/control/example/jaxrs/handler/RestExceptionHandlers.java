/*
 * JBoss, Home of Professional Open Source
 * Copyright [2010], Red Hat, Inc., and individual contributors
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

package org.jboss.seam.exception.control.example.jaxrs.handler;

import java.util.List;

import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.seam.exception.control.CatchResource;
import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.ExceptionResponse;
import org.jboss.seam.exception.control.ExceptionStack;
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
   public void logException(
      @Handles(during = TraversalPath.DESCENDING) @RestRequest final CaughtException<Throwable> event)
   {
      ExceptionStack c = event.getExceptionStack();
      System.out.println(
         "Caught exception (" + (c.getIndex() + 1) + " in stack of " + c.getCauseElements().size() + ") => " +
         event.getException().getClass().getSimpleName() + "(\"" + event.getException().getMessage() + "\") ");
   }

   /**
    * An example that demonstrates using XML-based exception mapping configuration to handle an
    * exception that occurs during a REST resource request by sending an HTTP error response.
    */
   public void configurableExceptionHandler(
      @Handles(precedence = -100) @RestRequest final CaughtException<Throwable> event,
      @CatchResource final ResponseBuilder responseBuilder,
      @RestRequest final List<ExceptionResponse> exceptionResponses)
   {
      final Class<?> exceptionClass = event.getException().getClass();

      for (ExceptionResponse response : exceptionResponses)
      {
         if (exceptionClass.equals(response.getForType()))
         {
            responseBuilder.status(((RestExceptionResponse) response).getStatusCode());

            if (response.getMessage() != null)
            {
               responseBuilder.entity(new ErrorMessageWrapper(response.getMessage()));
            }

            //event.markHandled(); ??
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
//         new ExceptionResponse(NoResultException.class, 404, "Request resource does not exist (Java-configured response)"),
//         new ExceptionResponse(IllegalArgumentException.class, 400, "Illegal value (Java-configured response)")
//      );
//   }
}
