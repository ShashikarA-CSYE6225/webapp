#!/bin/bash

sudo chown -R csye6225:csye6225 /tmp/webapp.service
sudo mv /tmp/webapp.service /etc/systemd/system/webapp.service
sudo systemctl daemon-reload
sudo systemctl enable csye6225.service

