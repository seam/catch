#Seam Catch JAX-RS example

This example demonstrates the use of Seam Catch to send HTTP error responses
when exceptions occur during a REST resource request.

##Prerequisite

If you are deploying to JBoss AS, you must have at least JBoss AS 6.0.0.CR1
installed. You should also set the JBOSS_HOME environment variable to your
JBoss AS installation.

##Build and deploy

To package and deploy to JBoss AS, run the following command:

 mvn clean package jboss:hard-deploy -Pjbossas 

At the moment, Seam 3 is not working on GlassFish due to an incompatibility
with several extension points used by Seam Solder.
