## digi-OARS

### cordapp
This folder contains the source code of digi-OARS cordapp. CorDapps are distributed applications that run on the Corda platform. The goal of a CorDapp is to allow nodes to reach agreement on updates to the ledger. They achieve this goal by defining flows that Corda node owners can invoke over RPC.

CorDapps take the form of a set of JAR files containing class definitions written in Java and/or Kotlin.
These class definitions will commonly include the following elements:

- Flows: Define a routine for the node to run, usually to update the ledger. They subclass FlowLogic
- States: Define the facts over which agreement is reached. They implement the ContractState interface
- Contracts, defining what constitutes a valid ledger update. They implement the Contract interface
Services, providing long-lived utilities within the node. They subclass SingletonSerializationToken
Serialisation whitelists, restricting what types your node will receive off the wire. They implement the SerializationWhitelist interface

But the CorDapp JAR can also include other class definitions. These may include:

- APIs and static web content which usually locates in `/clients`: These are served by Corda’s built-in webserver. This webserver is not production-ready, and should be used for testing purposes only.

- frontend: build using React JS in this POC is located in `/frontend`.

### deployment
This folder contains deployment infrastructure files and orchestration files for cloud deployment. 
1. ansible playbooks can be used to orchestrate deployments, contact admin to add your ssh to current POC deployment.
For instance, if you want to clean up the data in the current deployment, follow these steps (you need to install ansible on cmd line first):
```
ansible-playbook 06-stop-client.yml
ansible-playbook 101-clean-dbs.yml
ansible-playbook 03-start-nodes.yml
ansible-playbook 05-start-clients.yml
(note that it may take a couple of minutes for all the nodes to start up)
```
2. terraform can be used to deploy different resources on Azure or any cloud services with desired configuration. The current POC design involves 3 load balancer instance, 9 corda node instances, 9 UI instances and 1 keycloak instance on MS Azure.

To bootstrap locally, follow the following steps:

1. build project using `./gradlew clean build`
2. run `./gradlew deployNodes`
3. run `build/nodes/runnodes` and wait for all the nodes to startup correctly
4. to start the UI of respective node, follow the pattern in the /client/build.gradle file



### keycloak
Keycloak is an open source software product to allow single sign-on with Identity and Access Management aimed at modern applications and services. As of March 2018 this JBoss community project is under the stewardship of Red Hat who use it as the upstream project for their RH-SSO product. For more information, pleaser refer to : https://www.keycloak.org/.

Keycloak is deployed using docker-compose.yml for the current POC. 


### postman
This folder contains all OARS API collection and their runtime environment. 
- The Smoke Testing json file contains all the api tests and can be run at once by postman collection runner to test API changes. Tests needs to be kept updated with relevant API changes.

- OARS.postman_collection.json contains all the API calls whose detailed definition can be found at /cordapp/client/Controller.java.

- dev_api.postman_environment.json is the runtime configuration for OARS.postman_collection.json.

- dev_keycloak.postman_environment.json is the runtime configuration for dev keycloak related API calls.

- prod_keycloak.postman_environment.json is the runtime configuration for prod keycloak related API calls.

### ux
This folder contains adobe xd ux design for the first iteration of the OARS system. The POC OARS might look different from what was originally designed. Please refer to the current UI at http://oars.eastus.cloudapp.azure.com/.

