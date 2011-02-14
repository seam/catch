/**
 *
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 */
package org.jboss.seam.exception.example.basic.servlet.handler;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletResponse;

/**
 * DRY helper to output the message to the response.
 */
public class HandlerOutput
{
   /**
    * Prints the message out to the response
    *
    * @param messages      ResourceBundle to use for messages
    * @param exception     Exception that was caught
    * @param response      response object used to write
    * @param handler       name of handler
    * @param markException method being called from the handler for flow control
    */
   public static void printToResponse(final ResourceBundle messages, final Throwable exception,
                                      final HttpServletResponse response, final String handler,
                                      final String markException)
   {
      final String output = MessageFormat.format(messages.getString("handler_output"), exception.getClass(),
            handler,
            markException,
            exception.getMessage());
      try
      {
         response.getWriter().println(output);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
}
