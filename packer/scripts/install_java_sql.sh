#!/bin/bash

# Install MySQL and start service
sudo dnf install -y mysql mysql-server
systemctl start mysqld

# Wait for MySQL service to start
sleep 5

# Set MySQL root password
sudo mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'Sqlpassword1.';"

echo "MySQL installation and root password setup completed."

# Install OpenJDK 17
sudo dnf -y install java-17-openjdk java-17-openjdk-devel

# Set up environment variables for Java
cat > /etc/profile.d/java.sh <<'EOF'
export JAVA_HOME=$(dirname $(dirname $(readlink $(readlink $(which java)))))
export PATH=$PATH:$JAVA_HOME/bin
EOF

# Source the profile script to apply changes
source /etc/profile.d/java.sh

# Verify Java installation
java --version

echo "Java installation completed."
