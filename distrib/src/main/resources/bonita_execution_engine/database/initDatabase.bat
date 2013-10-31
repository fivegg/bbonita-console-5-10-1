@echo off

set initdb.domain=default
set /p initdb.domain=Which domain do you want to use (press enter without nothing to use default)?

set bonita.home=
set /p bonita.home=Where is your BONITA_HOME folder?
if "%bonita.home%"=="" (set bonita.home=%BONITA_HOME%)

set initdb.hibernate.configuration=hibernate-configuration:core hibernate-configuration:history
echo Which hibernate configuration to use to generate database (press enter without nothing to use default)?
set /p initdb.hibernate.configuration=Default is 'hibernate-configuration:core hibernate-configuration:history' to init both databases: 

::Generate the classpath using jars in engine\libs folder
set classpath=
FOR %%f IN ("%CD%\..\engine\libs\*.jar") DO call :LoopToCreateClasspath "%%f"
FOR %%f IN ("%CD%\script\*.sql") DO call :LoopToCreateClasspath "%%f"

java -DBONITA_HOME="%bonita.home%" -classpath %classpath% org.ow2.bonita.util.DbTool  %initdb.domain% %initdb.hibernate.configuration%
java -DBONITA_HOME="%bonita.home%" -classpath %classpath% org.ow2.bonita.util.IndexTool  %initdb.domain% 1 1



:LoopToCreateClasspath
Set classpath=%classpath%;%1
GoTo :EOF