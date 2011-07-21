.. _usage: 

Usage
=====

.. _eventing.into.catch:

Eventing into Catch
-------------------
The entire Seam Catch process starts with an event. This helps keep your
application minimally coupled to Catch, but also allows for further
extension! Catch is all about letting you take care of exceptions the
way that makes the most sense for your application. Events provide this
delicate balance.

There are three means of firing the event to start the Catch process:

- manual firing of the event
- using an interceptor
- module integration - no code needs to be changed

.. _manual.firing.of.the.event:

Manual firing of the event
~~~~~~~~~~~~~~~~~~~~~~~~~~
Manually firing an event to use Catch is primarily used in your own
try/catch blocks. It's very painless and also easy. Let's examine an
sample that might exist inside of a simple business logic lookup into an
inventory database:

.. code-block:: java
  :linenos:

  @Stateless
  public class InventoryActions {
    @PersistenceContext private EntityManager em;
    @Inject private Event<ExceptionToCatch> catchEvent; 

    public Integer queryForItem(Item item) {
      try {
        Query q = em.createQuery("SELECT i from Item i where i.id = :id");
        q.setParameter("id", item.getId());
        return q.getSingleResult();
     } catch (PersistenceException e) {
       catchEvent.fire(new ExceptionToCatch(e));
     }
    }
  }

- Line 4: The ``Event`` of generic type ``ExceptionToCatch`` is injected
  into your class for use later within a try / catch block.
- Line 12: The event is fired with a new instance of ``ExceptionToCatch``
  constructed with the exception to be handled

.. _using.the.ExceptionHandled.interceptor:

Using the ``@ExceptionHandled`` Interceptor
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
A CDI Interceptor has been added to help with integration of Catch into
your application. It's used just like any interceptor, and must be
enabled in the beans.xml file for your bean archive. This interceptor
can be used at the class or method level.

This interceptor is a typical AroundInvoke interceptor and is invoked
before the method (which in this case merely wraps the call to the
intercepted method in a try / catch block). The intercepted method
is called then, if an exception (actually a Throwable) occurs during
execution of the intercepted method the exception is passed to Catch
(without any qualifiers). Normal flow continues from there, however,
take not of the following warning:

.. Warning::
  Using the interceptor may cause unexpected behavior to methods that
  call intercepted methods in which an exception occurs, please see the
  API docs for more information about returns if an exception occurs.

.. _exception.handlers:

Exception handlers
------------------
As an application developer (i.e., an end user of Catch), you'll be
focused on writing exception handlers. An exception handler is a method
on a CDI bean that is invoked to handle a specific type of exception.
Within that method, you can implement any logic necessary to handle or
respond to the exception.

Given that exception handler beans are CDI beans, they can make use of
dependency injection, be scoped, have interceptors or decorators and any
other functionality available to CDI beans.

Exception handler methods are designed to follow the syntax and
semantics of CDI observers, with some special purpose exceptions
explained in this guide. The advantage of this design is that exception
handlers will be immediately familiar to you if you are studying or
well-versed in CDI.

In this and subsequent chapters, you'll learn how to define an exception
handler, explore how and when it gets invoked, modify an exception
and a stack trace, and even extend Catch further through events that
are fired during the handling workflow. We'll begin by covering
the two annotations that are used to declare an exception handler,
``@HandlesExceptions`` and ``@Handles``.

.. _exception.handler.annotations:

Exception handler annotations
-----------------------------
Exception handlers are contained within exception handler beans,
which are CDI beans annotated with ``@HandlesExceptions``. Exception
handlers are methods which have a parameter which is an instance of
``CaughtException<T extends Throwable>`` annotated with the @Handles
annotation.

.. _HandlesExceptions:

``@HandlesExceptions``
~~~~~~~~~~~~~~~~~~~~~~
The ``@HandlesException`` annotation is simply a marker annotation that
instructs the Seam Catch CDI extension to scan the bean for handler
methods.

Let's designate a CDI bean as an exception handler by annotating it with
``@HandlesException``.

.. code-block:: java

  @HandlesExceptions 
  public class MyHandlers {}

That's all there is to it. Now we can begin defining exception handling
methods on this bean.

.. Note::
  The ``@HandlesExceptions`` annotation may be deprecated
  in favor of annotation indexing done by `Seam Solder
  <http://seamframework.org/Seam3/Solder>`_.

.. _Handles:

``@Handles``
~~~~~~~~~~~~~~~~~~~~~~
``@Handles`` is a method parameter annotation that designates a method
as an exception handler. Exception handler methods are registered on
beans annotated with ``@HandlesExceptions``. Catch will discover all
such methods at deployment time.

Let's look at an example. The following method is invoked for every
exception that Catch processes and prints the exception message to
stout. (``Throwable`` is the base exception type in Java and thus
represents all exceptions).

.. code-block:: java
  :linenos:

  @HandlesExceptions
  public class MyHandlers {
     void printExceptions(@Handles CaughtException<Throwable> evt) {
        System.out.println("Something bad happened: " +
              evt.getException().getMessage());
        evt.markHandled();
     }
  }

- Line 1: The ``@HandlesExceptions`` annotation signals that
  this bean contains exception handler methods. 
- Line 3: The ``@Handles`` annotation on the first parameter designates
  this method as an exception handler (though it is not required to be the
  first parameter). This parameter must be of type ``CaughtException<T
  extends Throwable>``, otherwise it's detected as a definition error. The
  type parameter designates which exception the method should handle. This
  method is notified of all exceptions (requested by the base exception
  type ``Throwable``).
- Line 5: The ``CaughtException`` instance provides access to
  information about the exception and can be used to control exception
  handling flow. In this case, it's used to read the current exception
  being handled in the exception chain, as returned by ``getException()``.
- Line 6: This handler does not modify the invocation of subsequent
  handlers, as designated by invoking ``markHandled()`` on
  ``CaughtException``. As this is the default behavior, this line could be
  omitted.

The ``@Handles`` annotation must be placed on a parameter of the method,
which must be of type ``CaughtException<T extends Throwable>``.
Handler methods are similar to CDI observers and, as such, follow
the same principles and guidelines as observers (such as invocation,
injection of parameters, qualifiers, etc) with the following exceptions:

-  a parameter of a handler method must be a ``CaughtException``
- handlers are ordered before they are invoked (invocation order of
  observers is non-deterministic)
-  any handler can prevent subsequent handlers from being invoked

In addition to designating a method as exception handler, the
``@Handles`` annotation specifies two pieces of information about when
the method should be invoked relative to other handler methods:

- a precedence relative to other handlers for the same exception type. 
  Handlers with higher precedence are invoked before handlers with lower
  precedence that handle the same exception type. The default precedence
  (if not specified) is 0 
- the type of the traversal mode (i.e., phase) during which the handler 
  is invoked. The default traversal mode (if not specified) is 
  ``TraversalMode.DEPTH_FIRST``

Let's take a look at more sophisticated example that uses all the
features of handlers to log all exceptions.

.. code-block:: java
  :linenos:

  @HandlesExceptions
  public class MyHandlers {
     void logExceptions(@Handles(during = TraversalMode.BREADTH_FIRST)
           @WebRequest CaughtException<Throwable> evt, Logger log) {
        log.warn("Something bad happened: " + evt.getException().getMessage());
     }
  }

- Line 1 The ``@HandlesExceptions`` annotation signals
  that this bean contains exception handler methods.
- Line 3 This handler has a default precedence of 0 (the default value
  of the precedence attribute on ``@Handles``). It's invoked during the
  breadth first traversal mode. For more information on traversal, see the
  section :ref:`handler.ordering.hierarchy.traversal`.
- Line 4 This handler is qualified with ``@WebRequest``. When Catch
  calculates the handler chain, it filters handlers based on the exception
  type and qualifiers. This handler will only be invoked for exceptions
  passed to Catch that carry the ``@WebRequest`` qualifier. We'll assume
  this qualifier distinguishes a web page request from a REST request.
- Line 4 Any additional parameters of a handler method are treated as
  injection points. These parameters are injected into the handler when it
  is invoked by Catch. In this case, we are injecting a ``Logger`` bean
  that must be defined within the application (or by an extension).

A handler is guaranteed to only be invoked once per exception
(automatically muted), unless it re-enables itself by invoking the
``unmute()`` method on the ``CaughtException`` instance.

Handlers must not throw checked exceptions, and should avoid throwing
unchecked exceptions. Should a handler throw an unchecked exception it
will propagate up the stack and all handling done via Catch will cease.
Any exception that was being handled will be lost.

.. _exception.chain.processing:

Exception chain processing
--------------------------
When an exception is thrown, chances are it's nested (wrapped) inside
other exceptions. (If you've ever examined a server log, you'll
appreciate this fact). The collection of exceptions in its entirety is
termed an exception chain.

The outermost exception of an exception chain (e.g., EJBException,
ServletException, etc) is probably of little use to exception handlers.
That's why Catch doesn't simply pass the exception chain directly to
the exception handlers. Instead, it intelligently unwraps the chain and
treats the root exception cause as the primary exception.

The first exception handlers to be invoked by Catch are those that
match the type of root cause. Thus, instead of seeing a vague
``EJBException``, your handlers will instead see an meaningful exception
such as ``ConstraintViolationException``. *This feature, alone, makes
Catch a worthwhile tool.*

.. TODO: a graphic of layers (like rings) would be good here

Catch continues to work through the exception chain, notifying handlers
of each exception in the stack, until a handler flags the exception as
handled. Once an exception is marked as handled, Catch stops processing
the exception. If a handler instructed Catch to rethrow the exception
(by invoking ``CaughtException#rethrow()``, Catch will rethrow the
exception outside the Catch infrastructure. Otherwise, it simply returns
flow control to the caller.

Consider a exception chain containing the following nested causes (from
outer cause to root cause):

- ``EJBException``
- ``PersistenceException``
- ``SQLGrammarException``

Catch will unwrap this exception and notify handlers in the following
order:

1. ``SQLGrammarException``
2. ``PersistenceException``
3. ``EJBException``

If there's a handler for ``PersistenceException``, it will likely
prevent the handlers for ``EJBException`` from being invoked, which is
a good thing since what useful information can really be obtained from
``EJBException``?

.. _handler.ordering:

Exception handler ordering
--------------------------

While processing one of the causes in the exception chain, Catch has a
specific order it uses to invoke the handlers, operating on two axes:

- traversal of exception type hierarchy 
- relative handler precedence

We'll first address the traversal of the exception type hierarchy, then
cover relative handler precedence.

.. _handler.ordering.hierarchy.traversal:

Traversal of exception type hierarchy
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Catch doesn't simply invoke handlers that match the exact type of
the exception. Instead, it walks up and down the type hierarchy of
the exception. It first notifies least specific handler in breadth
first traversal mode, then gradually works down the type hierarchy
towards handlers for the actual exception type, still in breadth first
traversal. Once all breadth first traversal handlers have been invoked,
the process is reversed for depth first traversal, meaning the most
specific handlers are notified first and Catch continues walking up the
hierarchy tree.

There are two modes of this traversal:

- BREADTH_FIRST 
- DEPTH_FIRST

By default, handlers are registered into the DEPTH_FIRST traversal path.
That means in most cases, Catch starts with handlers of the actual
exception type and works up towards the handler for the least specific
type.

However, when a handler is registered to be notified during the
BREADTH_FIRST traversal, as in the example above, Catch will notify that
exception handler before the exception handler for the actual type is
notified.

Let's consider an example. Assume that Catch is handling the
``SocketException``. It will notify handlers in the following order:

1. ``Throwable`` (BREADTH_FIRST) 
2. ``Exception`` (BREADTH_FIRST) 
3. ``IOException`` (BREADTH_FIRST) 
4. ``SocketException`` (BREADTH_FIRST) 
5. ``SocketException`` (DEPTH_FIRST) 
6. ``IOException`` (DEPTH_FIRST) 
7. ``Exception`` (DEPTH_FIRST) 
8. ``Throwable`` (DEPTH_FIRST)

The same type traversal occurs for each exception processed in the
chain.

In order for a handler to be notified of the ``IOException`` before
the ``SocketException``, it would have to specify the BREADTH_FIRST
traversal path explicitly:

.. code-block:: java
   :linenos:

      void handleIOException(@Handles(during = TraversalMode.BREADTH_FIRST)
            CaughtException<IOException> evt) {
         System.out.println("An I/O exception occurred, but not sure what type yet");
      }

BREADTH_FIRST handlers are typically used for logging exceptions because
they are not likely to be short-circuited (and thus always get invoked).

.. _precedence:

Handler precedence
------------------

When Catch finds more than one handler for the same exception type, it
orders the handlers by precedence. Handlers with higher precedence are
executed before handlers with a lower precedence. If Catch detects two
handlers for the same type with the same precedence, it detects it as an
error and throws an exception at deployment time.

Let's define two handlers with different precedence:

.. code-block:: java
   :linenos:

      void handleIOExceptionFirst(@Handles(precedence = 100) CaughtException<IOException> evt) {
         System.out.println("Invoked first");
      }
      void handleIOExceptionSecond(@Handles CaughtException<IOException> evt) {
         System.out.println("Invoked second");
      }

The first method is invoked first since it has a higher precedence (100)
than the second method, which has the default precedence (0).

To make specifying precedence values more convenient, Catch provides
several built-in constants, available on the ``Precedence`` class:

- BUILT_IN = -100 
- FRAMEWORK = -50 
- DEFAULT = 0 
- LOW = 50 
- HIGH = 100

To summarize, here's how Catch determines the order of handlers to
invoke (until a handler marks exception as handled):

1. Unwrap exception stack 
2. Begin processing root cause 
3. Find handler for least specific handler marked for BREADTH_FIRST
   traversal
4. If multiple handlers for same type, invoke handlers with higher
   precedence first
5. Find handler for most specific handler marked for DEPTH_FIRST
   traversal
6. If multiple handlers for same type, invoke handlers with higher
   precedence first
7. Continue above steps for each exception in stack

.. _api:

APIs for exception information and flow control
-----------------------------------------------

There are two APIs provided by Catch that should be familiar to
application developers:

- ``CaughtException`` 
- ``ExceptionStack`` 
  
.. _api.caughtexception:

CaughtException
~~~~~~~~~~~~~~~

In addition to providing information about the exception being handled,
the ``CaughtException`` object contains methods to control the exception
handling process, such as rethrowing the exception, aborting the handler
chain or unmuting the current handler.

Five methods exist on the ``CaughtException`` object to give flow
control to the handler

``abort()``
  terminate all handling immediately after this handler, does not mark
  the exception as handled, does not re-throw the exception.

``rethrow()``
  continues through all handlers, but once all handlers have been called
  (assuming another handler does not call abort() or handled()) the
  initial exception passed to Catch is rethrown. Does not mark the
  exception as handled.

``handled()``
  marks the exception as handled and terminates further handling.

``markHandled()``
  default. Marks the exception as handled and proceeds with the rest of
  the handlers.

``dropCause()``
  marks the exception as handled, but proceeds to the next cause in the
  cause container, without calling other handlers for the current cause.

Once a handler is invoked it is muted, meaning it will not be run again
for that exception chain, unless it's explicitly marked as unmuted via
the ``unmute()`` method on ``CaughtException``.

.. _api.stackinfo:

ExceptionStack
~~~~~~~~~~~~~~

``ExceptionStack`` contains information about the exception causes
relative to the current exception cause. It is also the source of
the exception types the invoked handlers are matched against. It is
accessed in handlers by calling the method ``getExceptionStack()``
on the ``CaughtException`` object. Please see `API docs
<http://docs.jboss.org/seam/3/catch/latest/api/org/jboss/seam/exception/
control/ExceptionStack.html>`_ for more information, all methods are
fairly self-explanatory.

.. tip:: 
  This object is mutable and can be modified before any handlers are
  invoked by an observer:

  .. code-block:: java
   :linenos:

   public void modifyStack(@Observes ExceptionStack stack) {
     ...
   }

  Modifying the ExceptionStack may be useful to remove exception types
  that are effectively meaningless such as ``EJBException``, changing
  the exception type to something more meaningful such as cases like
  ``SQLException``, or wrapping exceptions as custom application
  exception types.

.. _troubleshooting:

Troubleshooting
---------------

The issues to date with Seam Catch have all be around eventing into
Catch. The information at the top of this chapter should give details
how to correctly use into Seam Catch and allow your handlers to be
notified of exceptions.

For questions involving integrations such as JSF or REST for navigation
cases, or exceptions not being passed correctly to Seam Catch, please
see documentation for that module as an exhaustive review of each
integration and hazards pertaining to those integrations is beyond the
scope of this guide.
