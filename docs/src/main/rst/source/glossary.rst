.. _glossary:

Glossary
========

.. glossary::
  :sorted:
  
  Exception Chain
    An exception chain is made up of many different exceptions or causes
    until the root exception is found at the bottom of the chain. When
    all of the causes are removed or looked at this forms the causing
    container. The container may be traversed either ascending (root
    cause first) or descending (outer most first).

  Handler Bean
    A CDI enabled Bean which contains handler methods nnotated with the
    A``@HandlesExceptions`` annotation.

  Handler Method
    A method within a handler bean which is marked as a handler
    using the @Handlers on an argument, which must be an instance of
    CaughtException. Handler methods typically are public with a void
    return. Other parameters of the method will be treated as injection
    points and will be resolved via CDI and injected upon invocation.
