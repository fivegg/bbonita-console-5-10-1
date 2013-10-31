#!/bin/bash
echo "create MySQL DB start"
cd /usr/local/mysql
#Login in MySQL database and start to create databases;
bin/mysql -u root < $CATALINA_HOME/bin/scripts/createMySQLDB.sql

