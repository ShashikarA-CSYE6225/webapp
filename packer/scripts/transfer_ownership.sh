#!/bin/bash

#Move jar file to opt from tmp
sudo mv /tmp/webapp-0.0.1-SNAPSHOT.jar /opt/

# Set ownership of the copied JAR file
sudo chown -R csye6225:csye6225 /opt/webapp-0.0.1-SNAPSHOT.jar

