/**
 *
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 */
package org.jboss.seam.exception.control.test.event.literal;

import java.io.Serializable;

import javax.enterprise.util.AnnotationLiteral;

import org.jboss.seam.exception.control.test.event.EventQualifier;

public class EventQualifierLiteral extends AnnotationLiteral<EventQualifier> implements EventQualifier, Serializable
{
   private static final long serialVersionUID = -5718416079228791575L;

   public static final EventQualifier INSTANCE = new EventQualifierLiteral();

   private EventQualifierLiteral() {}
}
