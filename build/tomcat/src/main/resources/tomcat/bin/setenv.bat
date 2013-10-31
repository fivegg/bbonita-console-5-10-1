@echo on

rem Sets some variables
set BONITA_HOME="-DBONITA_HOME=%CATALINA_HOME%\bonita"
set SECURITY_OPTS="-Djava.security.auth.login.config=%CATALINA_HOME%\external\security\jaas-standard.cfg"

set CATALINA_OPTS=%CATALINA_OPTS% %SECURITY_OPTS% %BONITA_HOME% -Dfile.encoding=UTF-8 -Xshare:auto -Xms512m -Xmx1024m -XX:MaxPermSize=256m -XX:+HeapDumpOnOutOfMemoryError
