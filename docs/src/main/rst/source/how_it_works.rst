.. how_it_works:

How Seam Catch Works
====================
Seam Catch is based around the CDI eventing model. While the implementation of
exception handlers may not be the same as a CDI event, and the programming model
is not exactly the same as specifying a CDI event / observer, the concepts are
very similar. Seam Catch makes use of events for many of it's features. Eventing
in is actually the only way to start using Catch.

This event is fired either by the application or a Catch integration. Catch then
hands the exception off to a chain of registered handlers, which deal with the
exception appropriately. The use of CDI events to connect exceptions to handlers
makes this strategy of exception handling non-invasive and minimally coupled to
Catch's infrastructure.

The exception handling process remains mostly transparent to the developer. In
most cases, you register an exception handler simply by annotating a handler
method. Alternatively, you can handle an exception programmatically, just as you
would observe an event in CDI.

There are other events that are fired during the exception handling process that
will allow great customization of the exception, stack trace, and status. This
allows the application developer to have the most control possible while still
following a defined workflow. These events and other advanced usages will be
covered in the next chapter.
