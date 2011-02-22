/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

/**
 *
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 */
package org.jboss.seam.exception.filter;

import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jboss.seam.exception.control.ExceptionStack;
import org.jboss.seam.exception.control.ExceptionStackItem;

/**
 * This replaces the typical output of originalException stack traces. The stack is printed inverted of the
 * standard way, meaning the stack is unwrapped and the root cause is printed first followed by the next
 * exception that wrapped the root cause. This class is immutable.
 *
 * It may also make use of {@link ExceptionStackFrameFilter} instances to filter the stack trace output.
 */
public class ExceptionStackOutput<T extends Throwable>
{
   private final ExceptionStack exceptionStack;
   private final ExceptionStackFrameFilter<T> filter;

   // TODO: Really needs to be a properties file or something
   public static final String ROOT_CAUSE_TEXT = new StringBuilder().append("Root exception {0}").append(System.getProperty("line.separator")).toString();
   public static final String AT_TEXT = new StringBuilder().append("\t at {0}").append(System.getProperty("line.separator")).toString();

   public ExceptionStackOutput(final T exception)
   {
      this(exception, null);
   }

   public ExceptionStackOutput(final T exception, final ExceptionStackFrameFilter<T> filter)
   {
      this.exceptionStack = new ExceptionStack(exception);
      this.filter = filter;
   }

   /**
    * Prints the stack trace for this instance, using any current filters.
    * @return stack trace in string representation
    */
   public String printTrace()
   {
      final StringBuilder traceBuffer = new StringBuilder();

      for (int i = 0; i < this.exceptionStack.getOrigExceptionStackItems().size(); i++)
      {
         final ExceptionStackItem item = this.exceptionStack.getOrigExceptionStackItems().removeFirst();
         
         if (i == 0)
         {
            traceBuffer.append(MessageFormat.format(ROOT_CAUSE_TEXT, item.getThrowable()));
         }

         trace_loop:
         for (StackFrame stackFrame : this.createStackFrameCollectionFrom(item.getThrowable()))
         {
               switch (this.filter.process(stackFrame))
               {
                  case TERMINATE_AFTER:
                     traceBuffer.append(MessageFormat.format(AT_TEXT, stackFrame.getStackTraceElement()));
                  case TERMINATE:
                  case DROP_REMAINING:
                     break trace_loop;
                  case DROP:

                     continue;
                  default:
                     traceBuffer.append(MessageFormat.format(AT_TEXT, stackFrame.getStackTraceElement()));
               }
         }
      }

      return traceBuffer.toString();
   }

   private Collection<StackFrame> createStackFrameCollectionFrom(final Throwable throwable)
   {
      final List<StackFrame> frameList = new ArrayList<StackFrame>(throwable.getStackTrace().length);

      for (int i = 0; i < throwable.getStackTrace().length; i++)
      {
         if (i == 0)
         {
            frameList.add(new StackFrameImpl(throwable));
         }
         else
         {
            frameList.add(new StackFrameImpl((StackFrameImpl) frameList.get(i - 1), throwable.getStackTrace()[i], i));
         }
      }
      return frameList;
   }
}
