package org.jboss.seam.exception.control.example.jaxrs.handler.declarative;

import java.security.AccessControlException;

import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.Handles;
import org.jboss.seam.exception.control.HandlesExceptions;
import org.jboss.seam.exception.control.example.jaxrs.handler.RestRequest;

@HandlesExceptions
@ExceptionResponseService
public interface DeclarativeRestExceptionHandlers
{
// Due to JAX-RS multiple handlers that modify the response builder, the last one wins, so you wouldn't see this anyway   
//   @SendHttpResponse(status = 404, message = "Requested resource does not exist")
//   void onNoResult(@Handles @RestRequest CaughtException<NoResultException> e);
   
   @SendHttpResponse(status = 403, message = "Access to resource denied (Annotation-configured response)")
   void onNoAccess(@Handles @RestRequest CaughtException<AccessControlException> e);
   
   @SendHttpResponse(status = 400, message = "Invalid identifier (Annotation-configured response)")
   void onInvalidIdentifier(@Handles @RestRequest CaughtException<IllegalArgumentException> e);
}
