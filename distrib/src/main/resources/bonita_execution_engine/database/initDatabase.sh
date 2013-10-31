#!/bin/bash

echo -n "Which domain to you want to use (press enter without nothing to use default)? "
read -e initdb_domain
if [ a$initdb_domain == a ]; then
	export initdb_domain="default"
fi

echo -n "Where is your BONITA_HOME folder? "
read -e BONITA_HOME

echo -n "Which hibernate configuration to use to generate database (press enter without nothing to use default)?
Default is 'hibernate-configuration:core hibernate-configuration:history' to init both databases: "
read -e initdb_hibernate_configuration
if [ a$initdb_hibernate_configuration == a ]; then
	export initdb_hibernate_configuration="hibernate-configuration:core hibernate-configuration:history"
fi

INIT_CLASSPATH=../engine/libs
for jar in $(ls ../engine/libs/*.jar); do
	INIT_CLASSPATH=$INIT_CLASSPATH:$jar
done
INIT_CLASSPATH=$INIT_CLASSPATH:./
for sql in $(ls ./script/*.sql); do
	INIT_CLASSPATH=$INIT_CLASSPATH:$sql
done

java -DBONITA_HOME=$BONITA_HOME -cp $INIT_CLASSPATH org.ow2.bonita.util.DbTool $initdb_domain $initdb_hibernate_configuration
java -DBONITA_HOME=$BONITA_HOME -cp $INIT_CLASSPATH org.ow2.bonita.util.IndexTool $initdb_domain 1 1