@echo on

rem Sets some variables
set BONITA_HOME="-DBONITA_HOME=%CATALINA_HOME%\bonita"
set LOG_OPTS="-Djava.util.logging.config.file=%CATALINA_HOME%\external\logging\logging.properties"
set SECURITY_OPTS="-Djava.security.auth.login.config=%CATALINA_HOME%\external\security\jaas-standard.cfg"

set CATALINA_OPTS=%CATALINA_OPTS% %LOG_OPTS% %SECURITY_OPTS% %BONITA_HOME% -Dfile.encoding=UTF-8 -Xshare:auto -Xms512m -Xmx1024m -XX:MaxPermSize=256m -XX:+HeapDumpOnOutOfMemoryError

rem install and start MySQL
if exist "%CATALINA_HOME%\bin\scripts\installMySQLAndStart.bat" call "%CATALINA_HOME%\bin\scripts\installMySQLAndStart.bat" goto mysqlHome
:mysqlHome
