package org.jboss.seam.exception.control.test.extension;

import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.Handles;

@BleedingObject
public class StereotypedHandler
{
   public void handle(@Handles CaughtException<Throwable> ex) {}
}
