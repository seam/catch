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
package org.jboss.seam.exception.filter.test;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.text.MessageFormat;

import org.jboss.seam.exception.filter.ExceptionStackOutput;
import org.jboss.seam.exception.filter.StackFrame;
import org.jboss.seam.exception.filter.StackFrameFilter;
import org.jboss.seam.exception.filter.StackFrameFilterResult;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StackFilteringTest {
    @Test
    public void terminateTest() {
        final StackFrameFilter<NoClassDefFoundError> filter = new StackFrameFilter<NoClassDefFoundError>() {
            @Override
            public StackFrameFilterResult process(StackFrame frame) {
                return StackFrameFilterResult.TERMINATE;
            }
        };

        final NoClassDefFoundError noClassDefFoundError = new NoClassDefFoundError("java.lang.String Not found");
        final ExceptionStackOutput<NoClassDefFoundError> exceptionStackOutput = new ExceptionStackOutput<NoClassDefFoundError>(noClassDefFoundError, filter);
        final String result = exceptionStackOutput.printTrace();
        final String expectedResult = MessageFormat.format(ExceptionStackOutput.ROOT_CAUSE_TEXT, noClassDefFoundError);

        assertThat(result, is(expectedResult));
    }

    @Test
    public void terminateAfterTest() throws IOException {
        final StackFrameFilter<NoClassDefFoundError> filter = new StackFrameFilter<NoClassDefFoundError>() {
            @Override
            public StackFrameFilterResult process(StackFrame frame) {
                return StackFrameFilterResult.TERMINATE_AFTER;
            }
        };

        final NoClassDefFoundError noClassDefFoundError = new NoClassDefFoundError("java.lang.String Not found");
        final ExceptionStackOutput<NoClassDefFoundError> exceptionStackOutput = new ExceptionStackOutput<NoClassDefFoundError>(noClassDefFoundError, filter);
        final String result = exceptionStackOutput.printTrace();
        final LineNumberReader lineNumberReader = new LineNumberReader(new StringReader(result));

        while (lineNumberReader.readLine() != null) {
        } // just get the line numbers

        assertThat(lineNumberReader.getLineNumber(), is(2)); // The five at lines and the one root cause
    }

    @Test
    public void dropRemainingTest() throws IOException {
        final StackFrameFilter<NoClassDefFoundError> filter = new StackFrameFilter<NoClassDefFoundError>() {
            @Override
            public StackFrameFilterResult process(StackFrame frame) {
                if (frame.getIndex() >= 5) {
                    return StackFrameFilterResult.DROP_REMAINING;
                }
                return StackFrameFilterResult.INCLUDE;
            }
        };

        final NoClassDefFoundError noClassDefFoundError = new NoClassDefFoundError("java.lang.String Not found");
        final ExceptionStackOutput<NoClassDefFoundError> exceptionStackOutput = new ExceptionStackOutput<NoClassDefFoundError>(noClassDefFoundError, filter);
        final String result = exceptionStackOutput.printTrace();
        final LineNumberReader lineNumberReader = new LineNumberReader(new StringReader(result));

        while (lineNumberReader.readLine() != null) {
        } // just get the line numbers

        assertThat(lineNumberReader.getLineNumber(), is(6)); // The five at lines and the one root cause
    }

    @Test
    public void dropTest() throws IOException {
        final StackFrameFilter<NoClassDefFoundError> filter = new StackFrameFilter<NoClassDefFoundError>() {
            @Override
            public StackFrameFilterResult process(StackFrame frame) {
                // We want to drop calls referring to reflection
                if (frame.isMarkSet("reflections.invoke")) {
                    if (!frame.getStackTraceElement().toString().contains("reflect")) {
                        frame.clearMark("reflections.invoke");
                        return StackFrameFilterResult.INCLUDE;
                    }
                }

                if (frame.getStackTraceElement().toString().contains("reflect")) {
                    frame.mark("reflections.invoke");
                    return StackFrameFilterResult.DROP;
                }

                if (frame.getIndex() > 18) {
                    return StackFrameFilterResult.DROP_REMAINING;
                }

                return StackFrameFilterResult.INCLUDE;
            }
        };

        final NoClassDefFoundError noClassDefFoundError = new NoClassDefFoundError("java.lang.String Not found");
        final ExceptionStackOutput<NoClassDefFoundError> exceptionStackOutput = new ExceptionStackOutput<NoClassDefFoundError>(noClassDefFoundError, filter);
        final String result = exceptionStackOutput.printTrace();
        final LineNumberReader lineNumberReader = new LineNumberReader(new StringReader(result));
        int reflectLines = 0;
        String line;

        while ((line = lineNumberReader.readLine()) != null) {
            if (line.contains("reflect")) {
                reflectLines++;
            }
            System.out.println(line);
        }


        assertThat("Actual: " + result, lineNumberReader.getLineNumber() < 22, is(true));
        assertThat(reflectLines, is(0));
    }

    @Test
    public void testBuldingWrappedExceptionsWorksCorrectly() throws IOException {
        Exception e1 = new NullPointerException("Inside");
        Exception e2 = new RuntimeException("Outer", e1);

        ExceptionStackOutput<Exception> exceptionStackOutput = new ExceptionStackOutput<Exception>(e2);

        final String result = exceptionStackOutput.printTrace();
        final LineNumberReader lineNumberReader = new LineNumberReader(new StringReader(result));
        String line;

        while ((line = lineNumberReader.readLine()) != null) {
            if (lineNumberReader.getLineNumber() == 3) {
                assertThat(line.startsWith("Wrapped"), is(true));
            }
        }
    }
}
