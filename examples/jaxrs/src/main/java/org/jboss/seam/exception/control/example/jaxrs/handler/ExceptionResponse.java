package org.jboss.seam.exception.control.example.jaxrs.handler;

public class ExceptionResponse
{
   private Class<? extends Throwable> forType;
   private int statusCode;
   private String message;

   public ExceptionResponse() {}

   public ExceptionResponse(Class<? extends Throwable> forType, int statusCode, String message)
   {
      this.forType = forType;
      this.statusCode = statusCode;
      this.message = message;
   }

   public Class<? extends Throwable> getForType()
   {
      return forType;
   }

   public void setForType(Class<? extends Throwable> forType)
   {
      this.forType = forType;
   }
   
   public int getStatusCode()
   {
      return statusCode;
   }

   public void setStatusCode(int statusCode)
   {
      this.statusCode = statusCode;
   }

   public String getMessage()
   {
      return message;
   }

   public void setMessage(String message)
   {
      this.message = message;
   }

}
