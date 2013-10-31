#!/bin/bash

echo -n "Which domain to you want to use (press enter without nothing to use default)? "
read -e migratedb_domain
if [ a$migratedb_domain == a ]; then
	export migratedb_domain="default"
fi

echo -n "Where is your bonita.home folder? "
read -e BONITA_HOME

echo -n "What is your database type (h2, mysql, oracle, postgresql, sqlserver)? "
read -e migratedb_database
if [ a$migratedb_database == a ]; then
	export migratedb_database="h2"
fi


MIGRATION_CLASSPATH=../engine/libs
for jar in $(ls ../engine/libs/*.jar); do
	MIGRATION_CLASSPATH=$MIGRATION_CLASSPATH:$jar
done

java -DBONITA_HOME=$BONITA_HOME -cp $MIGRATION_CLASSPATH org.ow2.bonita.util.IndexTool $migratedb_domain 1 1
