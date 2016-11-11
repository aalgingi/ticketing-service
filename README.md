# Ticketing Service

A simple ticket service REST API that facilitates the discovery, temporary hold, and final reservation of seats within a high-demand performance venue. The service allow the API consumer to:

 + Put the best seats available on hold for 60 minutes
 + Confirm a hold using the hold id and email address

The service is build using spring boot, swagger, embedded Tomcat server, and an embedded H2 database for testing mode only. H2 database can simply be replace with a another persistence layer.

# Assumptions

 + This is for a single venue with a capacity of 297 seats (9 rows x 33 columns) as proposed in the problem description
 + Holds expire after 30 minutes
 + The venue rows are label A - I, A being in front of the stage
 + Like a movie theater, the best seats are as close to the middle of the venue as possible


# Requirements
+ Java 8
+ Maven 3

# Running
To run the test cases and package the app use the following maven command at the root directory of your project

mvn clean install

To start-up the app using the embedded tomcat server, simple run the jar file locates in the target directory using the command below at the project root directory. One the embedded tomcat server is running, visit http://localhost:8080/ticketing-service/api/v1/swagger-ui.html in your browser to view the swagger documentation and test the API. NOTE: Make sure nothing else is running on port 8080 since this is the port tomcat uses by default

java -jar ./target/