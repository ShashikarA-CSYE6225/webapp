#!/bin/bash

# Install MySQL and start service
sudo dnf install -y mysql mysql-server
sudo systemctl start mysqld

# Wait for MySQL service to start
sleep 5

# Set MySQL root password
sudo mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';"

echo "MySQL installation and root password setup completed."

# Install OpenJDK 17
sudo dnf install java-17-openjdk-devel.x86_64 -y

# Verify Java installation
java --version

echo "Java installation completed."
