#!/bin/bash

sudo dnf install java-17-openjdk-devel.x86_64 -y

sudo dnf install mysql mysql-server -y

sudo systemctl enable mysqld
sudo systemctl start mysqld

sudo mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';"