#!/bin/bash

# Create group csye6225
sudo groupadd csye6225

# Create user csye6225 with primary group csye6225 and nologin shell
sudo useradd csye6225 --system --shell /usr/sbin/nologin -g csye6225