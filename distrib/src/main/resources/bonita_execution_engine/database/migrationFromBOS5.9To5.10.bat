@echo off

set execute.script=n
set /p execute.script=It's recommended to backup manually your history and journal databases before executing this migration script. Would you like to continue (y or n)?
if "%execute.script%"=="y" (
	goto StartMigrate
) else (
	goto CancelMigrate
)

:StartMigrate
set bonita.home=
set /p bonita.home=Where is your bonita.home folder (absolute path expected)?
if "%bonita.home%"=="" (
	set bonita.home=%CD%\..\..\conf\bonita
)

set migratedb.domain=default
set /p migratedb.domain=Which domain do you want to use (press enter without anything to use default)?
set /p migratedb.database=What is your database type (h2, mysql, oracle, postgresql, sqlserver)?

::Generate the classpath using jars in engine\libs folder
set classpath="..\engine\libs\*;"


java -DBONITA_HOME="%bonita.home%" -classpath %classpath% org.ow2.bonita.util.DbMigration  %migratedb.domain% %migratedb.database% 1
java -DBONITA_HOME="%bonita.home%" -classpath %classpath% org.ow2.bonita.util.IndexTool  %migratedb.domain% 1 1

echo "migration over."
Goto :EOF

:CancelMigrate
echo "migration canceled."
