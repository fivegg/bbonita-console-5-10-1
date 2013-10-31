@echo off

set execute.script=n
set /p execute.script=It's recommended to backup manually your large data repository folder before executing this migration script. Would you like to continue (y or n)?
if "%execute.script%"=="y" (
	goto StartMigrate
) else (
	goto CancelMigrate
)

:StartMigrate
set /p BONITAHOME=Where is your BONITA_HOME folder (absolute path expected)?
if "%BONITAHOME%"=="" (set BONITAHOME="%CD%\..\..\conf\bonita")
		
set MIGRATE_DOMAIN=
set /p MIGRATE_DOMAIN=Which domain do you want to use (press enter without nothing to use default)?
	
set /p LIST_OF_PROCESS=Please list processes you want to migrate (eg:Web_Purchase--1.5 or if you want to migrate all processes, you should press enter without anything):
if "%LIST_OF_PROCESS%"=="" (set LIST_OF_PROCESS="ALL")
	
set MIGRATION_CLASSPATH=
FOR %%f IN ("%CD%\lib\*.jar") DO call :LoopToCreateClasspath "%%f"
	
set MIGRATION_PATH=
set /p MIGRATION_PATH=Where is your engine libraries folder (absolute path expected)?
	
if "%MIGRATION_PATH%" == "" (
	goto MIGRATION_PATH_NOT_EXIST
) else (
	goto MIGRATION_PATH_EXIST
)
	
:MIGRATION_PATH_NOT_EXIST
FOR %%i IN ("%CD%\..\..\bonita_execution_engine\engine\libs\*.jar") DO call :LoopToCreateClasspath "%%i"
	
:MIGRATION_PATH_EXIST
FOR %%j IN ("%MIGRATION_PATH%\*.jar") DO call :LoopToCreateClasspath "%%j"
	
set /p JAAS_CONFIG=Where is your JAAS configuration file (absolute path expected)?
if "%JAAS_CONFIG%"=="" (set JAAS_CONFIG="%CD%\..\..\conf\external\security\jaas-standard.cfg")
	
echo %MIGRATION_CLASSPATH%
	
java  -DBONITA_HOME="%BONITAHOME%" -cp %MIGRATION_CLASSPATH% -Djava.security.auth.login.config=%JAAS_CONFIG% -DMIGRATE_DOMAIN=%MIGRATE_DOMAIN% org.bonitasoft.migration.forms.FormMigrationManager %LIST_OF_PROCESS%
	
echo "migration over."
Goto :EOF

:CancelMigrate
echo "migration canceled."

:LoopToCreateClasspath
Set MIGRATION_CLASSPATH=%MIGRATION_CLASSPATH%;%1
GoTo :EOF
