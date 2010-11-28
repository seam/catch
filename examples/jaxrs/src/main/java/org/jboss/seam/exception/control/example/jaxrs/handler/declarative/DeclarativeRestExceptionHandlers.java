package org.jboss.seam.exception.control.example.jaxrs.handler.declarative;

import java.security.AccessControlException;

import javax.persistence.NoResultException;

import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.Handles;
import org.jboss.seam.exception.control.HandlesExceptions;
import org.jboss.seam.exception.control.example.jaxrs.handler.RestRequest;

@HandlesExceptions
@ExceptionResponseService
public interface DeclarativeRestExceptionHandlers
{
   @SendHttpResponse(status = 404, message = "Requested resource does not exist :(")
   void onNoResult(@Handles @RestRequest CaughtException<NoResultException> e);
   
   @SendHttpResponse(status = 403, message = "Access to resource denied :0")
   void onNoAccess(@Handles @RestRequest CaughtException<AccessControlException> e);
   
   @SendHttpResponse(status = 400, message = "Invalid identifier")
   void onInvalidIdentifier(@Handles @RestRequest CaughtException<IllegalArgumentException> e);
}
