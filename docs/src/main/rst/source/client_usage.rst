.. _usage: 

Usage
=====
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

- Line 4: The ``Event` of generic type ``ExceptionToCatch`` is injected
  into your class for use later within a try / catch block.
- Line 12: The event is fired with a new instance of ``ExceptionToCatch``
  constructed with the exception to be handled

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

Exception handler annotations
-----------------------------
Exception handlers are contained within exception handler beans,
which are CDI beans annotated with ``@HandlesExceptions``. Exception
handlers are methods which have a parameter which is an instance of
``CaughtException<T extends Throwable>`` annotated with the @Handles
annotation.

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
  in favor of annotation indexing done by <ulink
  href="http://seamframework.org/Seam3/Solder">Seam Solder</ulink>.

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
  public class MyHandlers
  {
     void printExceptions(@Handles CaughtException<Throwable> evt)
     {
        System.out.println("Something bad happened: " +
              evt.getException().getMessage());
        evt.markHandled();
     }
  }

- Line 1: The ``@HandlesExceptions`` annotation signals that
  this bean contains exception handler methods. 
- Line 4: The <literal>@Handles</literal> annotation on the first parameter 
  designates this method as an exception handler (though it is not required to 
  be the first parameter). This parameter must be of type ``CaughtException<T
  extends Throwable>``, otherwise it's detected as a definition error. The
  type parameter designates which exception the method should handle. This
  method is notified of all exceptions (requested by the base exception
  type ``Throwable``).  
- Line 7: The ``CaughtException`` instance provides access to
  information about the exception and can be used to control exception
  handling flow. In this case, it's used to read the current exception
  being handled in the exception chain, as returned by ``getException()``.
- Line 8: This handler does not modify the invocation of subsequent
  handlers, as designated by invoking ``markHandled()`` on
  ``CaughtException``. As this is the default behavior, this line could be
  omitted.

