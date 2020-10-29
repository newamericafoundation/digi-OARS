# OARS APIs

In this project, you can find all Restful APIs built with Spring in **cordapp/clients**. If you want to inspect individual API, follow the steps below to generate swagger.html.

## Build Project

Navigate to **cordapp/** directory, and do **./gradlew clean build**

## Deploy Nodes 

Deploy corda nodes in build.gradlew using **./gradlew deployNodes**.

## Start Nodes

Start nodes with **build/nodes/runnodes**. Make sure all nodes are successfully running.

## Start Webserver

Navigate to **cordapp/clients/build/libs** and start the webserver jar file there using correct parameters (rpc port, rpc username, rpc host etc). For example, ``CONFIG_RPC_HOST=localhost;SERVER_PORT=10050;CONFIG_RPC_PORT=10006;CONFIG_RPC_USERNAME=user1;CONFIG_RPC_PASSWORD=test``

# Checkout your swaggers!

Now you can check all the API definitions in **localhost:port_number/swagger-ui.html** You can find an example in this folder: OARS-Swagger UI-09-18-2020.pdf.