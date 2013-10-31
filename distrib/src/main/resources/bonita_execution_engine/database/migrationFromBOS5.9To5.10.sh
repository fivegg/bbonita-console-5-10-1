#!/bin/bash

echo -n "It's recommended to backup manually your history and journal databases before executing this migration script. Would you like to continue (y or n)? "
read -e execute_script
if [ "$execute_script" == "y" ]; then
	echo -n "Where is your bonita.home folder (absolute path expected)? "
	read -e BONITA_HOME
	if [ "$BONITA_HOME" = "" ]
	then 
	    DIR="$( cd "$( dirname "$0" )" && pwd )"
		BONITA_HOME=$DIR/../../conf/bonita
	fi

	echo -n "Which domain to you want to use (press enter without nothing to use default)? "
	read -e migratedb_domain
	if [ a$migratedb_domain == a ]; then
		export migratedb_domain="default"
	fi

	echo -n "What is your database type (h2, mysql, oracle, postgresql, sqlserver)? "
	read -e migratedb_database
	if [ a$migratedb_database == a ]; then
		export migratedb_database="h2"
	fi

	MIGRATION_CLASSPATH=../engine/libs
	for jar in $(ls ../engine/libs/*.jar); do
		MIGRATION_CLASSPATH=$MIGRATION_CLASSPATH:$jar
	done

	java -DBONITA_HOME=$BONITA_HOME -cp $MIGRATION_CLASSPATH org.ow2.bonita.util.DbMigration $migratedb_domain $migratedb_database 1
	exitValue=$?
	if [ $exitValue != 0 ]; then
		echo "migration failed due to migration exception."
		exit $exitValue 
	fi
	java -DBONITA_HOME=$BONITA_HOME -cp $MIGRATION_CLASSPATH org.ow2.bonita.util.IndexTool $migratedb_domain 1 1
else
	echo "migration canceled."
fi
