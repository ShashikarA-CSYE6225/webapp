#!/bin/bash

# Install required packages
yum install -y wget

# Navigate to the /opt directory
cd /opt

# Download Apache Maven binary
wget https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz

# Unzip the downloaded file
sudo tar xzf apache-maven-3.9.5-bin.tar.gz

# Rename the folder and create a symbolic link
sudo ln -s apache-maven-3.9.5 maven

# Define environment variables for Maven
cat > /etc/profile.d/maven.sh <<'EOF'
export M2_HOME=/opt/maven
export PATH=$PATH:/opt/apache-maven-3.9.5/bin
EOF

# Apply the changes
source /etc/profile.d/maven.sh

# Check Apache Maven version
mvn --version

# Navigate to root
cd

echo "maven installation completed."

