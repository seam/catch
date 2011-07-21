.. _framework_integration:

Framework Integration
==================================

Integration of Seam Catch with other frameworks consists of one main
step, and two other optional (but highly encouraged) steps:

- creating and firing an ``ExceptionToCatch`` 
- adding any default handlers and qualifiers with annotation literals (optional) 
- supporting ServiceHandlers for creating exception handlers

.. _integration.exceptiontocatchevent:

Creating and Firing an ExceptionToCatch event
---------------------------------------------

An ``ExceptionToCatch`` is constructed by passing a ``Throwable``
and optionally qualifiers for handlers. Firing the event is done via
CDI events (either straight from the ``BeanManager`` or injecting a
``Event<ExceptionToCatch>`` and calling fire).

To ease the burden on the application developers, the integration should
tie into the exception handling mechanism of the integrating framework,
if any exist. By tying into the framework's exception handling, any
uncaught exceptions should be routed through the Seam Catch system and
allow handlers to be invoked. This is the typical way of using the Seam
Catch framework. Of course, it doesn't stop the application developer
from firing their own ``ExceptionToCatch`` within a catch block.

.. _integration.built-in:

Default Handlers and Qualifiers
-------------------------------

.. _integration.default_handlers:

Default Handlers
~~~~~~~~~~~~~~~~

An integration with Catch can define it's own handlers to always be
used. It's recommended that any built-in handler from an integration
have a very low precedence, be a handler for as generic an exception as
is suitable (i.e. Seam Persistence could have a built-in handler for
PersistenceExceptions to rollback a transaction, etc), and make use of
qualifiers specific for the integration. This helps limit any collisions
with handlers the application developer may create.

.. note:: 
  Hopefully at some point there will be a way to conditionally
  enable handlers so the application developer will be able to selectively
  enable any default handlers. Currently this does not exist, but is
  something that will be explored.

.. _integration.qualifiers:

Qualifiers
~~~~~~~~~~

Catch supports qualifiers for the ``CaughtException``. To add qualifiers
to be used when notifying handlers, the qualifiers must be added to the
``ExceptionToCatch`` instance via the constructor (please see API docs
for more info). Qualifiers for integrations should be used to avoid
collisions in the application error handling both when defining handlers
and when firing events from the integration.

.. _integration.servicehandlers:

Supporting ServiceHandlers
--------------------------

`ServiceHandlers
<http://docs.jboss.org/seam/3/solder/latest/reference/en-US/html_single/
#servicehandler>`_ make for a very easy and concise way to define
exception handlers. The following example comes from the jaxrs example
in the distribution:

.. code-block:: java
  :linenos:

  @HandlesExceptions
  @ExceptionResponseService
  public interface DeclarativeRestExceptionHandlers {
    @SendHttpResponse(status = 403, message = "Access to resource denied (Annotation-configured response)")
    void onNoAccess(@Handles @RestRequest CaughtException<AccessControlException> e);

    @SendHttpResponse(status = 400, message = "Invalid identifier (Annotation-configured response)")
    void onInvalidIdentifier(@Handles @RestRequest CaughtException<IllegalArgumentException> e);
  }

All the vital information that would normally be done in the handler
method is actually contained in the ``@SendHttpResponse`` annotation.
The only thing left is some boiler plate code to setup the ``Response``.
In a jax-rs application (or even in any web application) this approach
helps developers cut down on the amount of boiler plate code they have
to write in their own handlers and should be implemented in any Catch
integration, however, there may be situations where ServiceHandlers
simply do not make sense.

.. note:: 
  If ServiceHandlers are implemented make sure to document if any
  of the methods are called from ``CaughtException``, specifically
  ``abort()``, ``handled()`` or ``rethrow()``. These methods affect
  invocation of other handlers (or rethrowing the exception in the case
  of ``rethrow()``).

.. _catch-integration-programatic-registration:

Programmatic Handler Registration
---------------------------------

Handlers can be registered programatically at runtime
instead of solely at deploy time. This done very simply
by injecting ``HandlerMethodContainer`` and calling
``registerHandlerMethod(HandlerMethod)``.

``HandlerMethod`` has been relaxed in this version as well, and is not
tied directly to Java. It is therefore feasible handlers written using
other JVM based languages could be easily registered and participate in
exception handling.
