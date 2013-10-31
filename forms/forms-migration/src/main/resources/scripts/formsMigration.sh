#!/bin/bash

echo -n "It's recommended to backup manually your large data repository folder before executing this migration script. Would you like to continue (y or n)? "
read -e execute_script
if [ "$execute_script" == "y" ]
then
	DIR="$( cd "$( dirname "$0" )" && pwd )"

	echo -n "Where is your BONITA_HOME folder (absolute path expected)? "
	read -e BONITA_HOME
	if [ "$BONITA_HOME" = "" ]
	then 
		BONITA_HOME=$DIR/../../conf/bonita
	fi
	
	echo -n "Which domain do you want to use (press enter without anything to use default)? "
	read -e MIGRATE_DOMAIN
	
	echo -n "Please list processes you want to migrate (eg:Web_Purchase--1.5 or if you want to migrate all processes, you should press enter without anything):"
	read -e LIST_OF_PROCESS
	if [ "$LIST_OF_PROCESS" = "" ]
	then 
		LIST_OF_PROCESS="ALL"
	fi
	
	MIGRATION_CLASSPATH=""
	for jar in $(ls $DIR/lib/*.jar); do
		echo $jar
		MIGRATION_CLASSPATH=$MIGRATION_CLASSPATH:"$jar"
	
	done
	
	echo -n "Where is your engine libraries folder (absolute path expected)? "
	read -e MIGRATION_PATH
	if [ "$MIGRATION_PATH" = "" ]
	then
		for jar in $(ls $DIR/../../bonita_execution_engine/engine/libs/*.jar); do
			echo $jar
			MIGRATION_CLASSPATH=$MIGRATION_CLASSPATH:"$jar"
		
		done
	else
		for jar in $(ls $MIGRATION_PATH/*.jar); do
			echo $jar
			MIGRATION_CLASSPATH=$MIGRATION_CLASSPATH:"$jar"
		
		done
	fi
	
	echo -n "Where is your JAAS configuration file (absolute path expected)? "
	read -e JAAS_PATH
	if [ "$JAAS_PATH" = "" ]
	then
		JAAS_PATH=$DIR/../../conf/external/security/jaas-standard.cfg
		
	fi
	
	java  -DBONITA_HOME=$BONITA_HOME -cp $MIGRATION_CLASSPATH -Djava.security.auth.login.config=$JAAS_PATH -DMIGRATE_DOMAIN=$MIGRATE_DOMAIN org.bonitasoft.migration.forms.FormMigrationManager $LIST_OF_PROCESS
	
	echo "migration over."
else
	echo "migration canceled."
fi
