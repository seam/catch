#Seam Catch JAX-RS example

This example demonstrates the use of Seam Catch to send HTTP error responses
when exceptions occur during a REST resource request.

##Prerequisite

If you are deploying to JBoss AS, you must have at least JBoss AS 6.0.0.CR1
installed. You also need to copy the file:

 src/main/resources-jbossas/default-ds.xml

to

 $JBOSSAS_HOME/server/default/deploy
 
before deploying the application.

##Building

mvn package
mvn jboss:hard-deploy
