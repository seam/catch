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
package org.jboss.seam.exception.control.test.traversal;

@SuppressWarnings("serial")
public class Exceptions
{
   public static class Exception1 extends Exception
   {
      public Exception1(Throwable cause)
      {
         super(cause);
      }
   }
   
   public static class Exception2 extends Exception
   {
      public Exception2(Throwable cause)
      {
         super(cause);
      }
   }
   
   public static class SuperOfException3 extends Exception
   {
   }
   
   public static class Exception3 extends SuperOfException3
   {
   }
}
