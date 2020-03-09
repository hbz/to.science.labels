#!/bin/bash

cp target/universal/etikett-0.1.0-SNAPSHOT.zip  /tmp
cd /tmp 
unzip etikett-0.1.0-SNAPSHOT.zip
cp /opt/etikett/conf/application.conf /tmp/etikett-0.1.0-SNAPSHOT/conf
sudo service etikett stop
rm -rf /opt/etikett/*
mv /tmp/etikett-0.1.0-SNAPSHOT/* /opt/etikett/
sudo service etikett start

