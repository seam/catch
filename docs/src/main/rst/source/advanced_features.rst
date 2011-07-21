.. _advanced_features:

Advanced Features
==============================

.. _catch-exception-modification:

Exception Modification
----------------------

.. _catch-exception-modification-intro:

Introduction
~~~~~~~~~~~~

At times it may be useful to change the exception to something a little
more specific or meaningful before it sent to handlers. Seam Catch
provides means to make this happen. A prime use case for this behavior
is a persistence related exception coming from the database. Many
times what we get from the database is an error number inside of a
``SQLException``, which isn't very helpful.

.. _catch-exception-modification-usage:

Usage
~~~~~

Before any handlers are notified of an exception, Catch will
raise an event of type ``ExceptionStack``. This type contains all
the exceptions in the chain, and will allow you to change the
exception elements that will be used to notify handlers using the
``setCauseElements(Collection)`` method. Do not use any of the other
methods as they only return copies of the chain.

.. tip:: When changing the exception, it is strongly recommended you
  keep the same stack trace for the exceptions you are changing. If the
  stack trace is not set then the new exception will not contain any stack
  information save from the time it was created, which is likely to be of
  little use to any handler.

.. _catch-filter:

Filtering Stack Traces
----------------------

.. _catch-filter-intro:

Introduction
~~~~~~~~~~~~

Stack traces are an everyday occurence for the Java developer,
unfortunately the base stack trace isn't very helpful and can be
difficult to understand and see the root problem. Catch helps make this
easier by

- turning the stack upside down and showing the root cause first, and 
- allowing the stack trace to be filtered

The great part about all of this: it's done without a need for CDI! You
can use it in a basic Java project, just include the Seam Catch jar.
There are four classes to be aware of when using filtering

- ExceptionStackOutput 
- StackFrameFilter 
- StackFrameFilterResult 
- StackFrame

.. _catch-filter.exceptionstackoutput:

ExceptionStackOutput
~~~~~~~~~~~~~~~~~~~~

There's not much to this, pass it the exception to print and the
filter to use in the constructor and call ``printTrace()`` which
returns a string -- the stack trace (filtered or not). If no filter is
passed to the constructor, calling ``printTrace()`` will still unwrap
the stack and print the root cause first. This can be used in place
of``Throwable#printStackTrace()``, provided the returned string is
actually printed to standard out or standard error.

.. _catch-filter.stackframefilter:

StackFrameFilter
~~~~~~~~~~~~~~~~

This is the workhorse interface that will need to be implemented to
do any filtering for a stack trace. It only has one method:``public
StackFrameFilterResult process(StackFrame frame)``. Further below are
methods on ``StackFrame`` and``StackFrameFilterResult``. Some examples
are included below to get an idea what can be done and how to do it.

.. _catch-filter.stackframefilterresult:

StackFrameFilterResult
~~~~~~~~~~~~~~~~~~~~~~

This is a simple enumeration of valid return values
for the ``process`` method. Please see the `API docs
<http://docs.jboss.org/seam/3/catch/latest/api/>`_ for definitions of
each value.

.. _catch-filter.stackframe:

StackFrame
~~~~~~~~~~

This contains methods to help aid in determining what to do
in the filter, it also allows you to completely replace the
``StackTraceElement`` if desired. The four "mark" methods deal with
marking a stack trace and are used if "folding" a stack trace is
desired, instead of dropping the frame. The ``StackFrame`` will allow
for multiple marks to be set. The last method,``getIndex()``, will
return the index of the ``StackTraceElement`` from the exception.

.. topic:: Terminate

  .. code-block:: java
    :linenos:

    @Override
    public StackFrameFilterResult process(StackFrame frame) {
      return StackFrameFilterResult.TERMINATE;
    }

  This example will simply show the exception, no stack trace.

.. topic:: Terminate After

  .. code-block:: java
    :linenos:

    @Override
    public StackFrameFilterResult process(StackFrame frame) {
      return StackFrameFilterResult.TERMINATE_AFTER;
    }

  This is similar to the previous example, save the first line of the
  stack is shown.

.. topic:: Drop Remaining

  .. code-block:: java
    :linenos:

    @Override
    public StackFrameFilterResult process(StackFrame frame) {
      if (frame.getIndex() >= 5) {
         return StackFrameFilterResult.DROP_REMAINING;
      }
      return StackFrameFilterResult.INCLUDE;
    }

  This filter drops all stack elements after the fifth element.

.. topic:: Folding

  .. code-block:: java
    :linenos:

    @Override
    public StackFrameFilterResult process(StackFrame frame) {
      if (frame.isMarkSet("reflections.invoke")) {
         if (frame.getStackTraceElement().getClassName().contains("java.lang.reflect")) {
            frame.clearMark("reflections.invoke");
            return StackFrameFilterResult.INCLUDE;
         }
         else if (frame.getStackTraceElement().getMethodName().startsWith("invoke")) {
            return StackFrameFilterResult.DROP;
         }
      }
      if (frame.getStackTraceElement().getMethodName().startsWith("invoke")) {
         frame.mark("reflections.invoke");
         return StackFrameFilterResult.DROP;
      }
      return StackFrameFilterResult.INCLUDE;
    }

  Certainly the most complicated example, however, this shows a
  possible way of "folding" a stack trace. In the example any
  internal reflection invocation methods are folded into a single
  ``java.lang.reflect.Method.invoke()`` call, no more internal com.sun
  calls in the trace.
