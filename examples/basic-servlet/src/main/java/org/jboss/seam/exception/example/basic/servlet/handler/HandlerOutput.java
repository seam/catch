/**
 *
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 */
package org.jboss.seam.exception.example.basic.servlet.handler;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.exception.control.CaughtException;

/**
 * DRY helper to output the message to the response.
 */
public class HandlerOutput {
   public static void printToResponse(final ResourceBundle messages, final CaughtException<? extends Throwable> event,
                                      final HttpServletResponse response) {
      final String output = MessageFormat.format(messages.getString("handler_output"), event.getException(),
                                                                                       "throwableHandler",
                                                                                       "proceed");
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
