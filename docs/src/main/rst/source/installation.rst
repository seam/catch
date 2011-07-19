.. _installation:

Installation
============
To use the Seam Catch module, you need to add the Seam Catch API to
your project as a compile-time dependency. At runtime, you'll also need
the Seam Catch implementation, which you either specify explicitly or
through a transitive dependency of another module that depends on it (as
part of exposing its own Catch integration).

First, check your application's library dependencies to see whether
Seam Catch is already being included by another module (such as Seam
Servlet). If not, you'll need to setup the dependencies as described
below.

Maven dependency configuration
------------------------------
If you are using Maven_ as your build tool, you can add the following
single dependency to your pom.xml file to include Seam Catch:

.. code-block:: xml
  :linenos:

  <dependency> 
    <groupId>org.jboss.seam.catch</groupId> 
    <artifactId>seam-catch</artifactId> 
    <version>${seam.catch.version}</version> 
  </dependency>

.. _Maven: http://maven.apache.org/

.. Tip::
  Substitute the expression ${seam.catch.version} with the most recent
  or appropriate version of Seam Catch. Alternatively, you can create a
  Maven user-defined property to satisfy this substitution so you can
  centrally manage the version.

Alternatively, you can use the API at compile time and only include
the implementation at runtime. This protects you from inadvertently
depending on an implementation class.

.. code-block:: xml
  :linenos:
   
  <dependency>
    <groupId>org.jboss.seam.catch</groupId>
    <artifactId>seam-catch-api</artifactId>
    <version>${seam.catch.version}</version>
    <scope>compile</scope>
  </dependency>

  <dependency>
    <groupId>org.jboss.seam.catch</groupId>
    <artifactId>seam-catch-impl</artifactId>
    <version>${seam.catch.version}</version>
    <scope>runtime</scope>
  </dependency>  

Now you're ready to start catching exceptions!
