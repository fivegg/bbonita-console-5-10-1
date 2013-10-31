@echo on
set BONITATARGETVERSION=5.5-SNAPSHOT

set WORKSPACEPATH=C:\Works\WorkspacesBonita
set PATHTOPROJECT=%WORKSPACEPATH%\BOS-console
set MAVENREPOPATH=C:\Users\nicolas\.m2\repository
set BONITA_HOME="C:\Works\BONITA_HOME_BOS"

rem GWT / GWTx
rem gwt
set CLASSPATH="%MAVENREPOPATH%\com\google\gwt\gwt-user\2.1.0\gwt-user-2.1.0.jar";"%MAVENREPOPATH%\com\google\gwt\gwt-dev\2.1.0\gwt-dev-2.1.0.jar"
set CLASSPATH=%CLASSPATH%;"%MAVENREPOPATH%\com\google\code\gwtx\gwtx\1.5.3\gwtx-1.5.3.jar"

rem SECURITY
rem security-model
set CLASSPATH=%CLASSPATH%;"%PATHTOPROJECT%\security\security-model\src\main\java"

rem security-rpc
set CLASSPATH=%CLASSPATH%;"%PATHTOPROJECT%\security\security-rpc\src\main\java"

rem csecurity-server
set CLASSPATH=%CLASSPATH%;"%PATHTOPROJECT%\security\security-server\src\main\java"

rem security-view
set CLASSPATH=%CLASSPATH%;"%PATHTOPROJECT%\security\security-view\src\main\java"

rem security-war
set CLASSPATH=%CLASSPATH%;"%PATHTOPROJECT%\security\security-war\src\main\java"

rem FORMS
rem forms-model
set CLASSPATH=%CLASSPATH%;"%PATHTOPROJECT%\forms\forms-model\src\main\java"

rem forms-rpc
set CLASSPATH=%CLASSPATH%;"%PATHTOPROJECT%\forms\forms-rpc\src\main\java"

rem forms-server
set CLASSPATH=%CLASSPATH%;"%PATHTOPROJECT%\forms\forms-server\src\main\java"

rem forms-view
set CLASSPATH=%CLASSPATH%;"%PATHTOPROJECT%\forms\forms-view\src\main\java"
set CLASSPATH=%CLASSPATH%;"%PATHTOPROJECT%\forms\forms-view\src\main\resources"

rem forms-application
set CLASSPATH=%CLASSPATH%;"%PATHTOPROJECT%\forms\forms-application\src\main\java"

rem CONSOLE
rem console-model
set CLASSPATH=%CLASSPATH%;"%PATHTOPROJECT%\console\console-model\src\main\java"

rem console-rpc
set CLASSPATH=%CLASSPATH%;"%PATHTOPROJECT%\console\console-rpc\src\main\java"

rem console-server
set CLASSPATH=%CLASSPATH%;"%PATHTOPROJECT%\console\console-server\src\main\java"

rem console-view
set CLASSPATH=%CLASSPATH%;"%PATHTOPROJECT%\console\console-view\src\main\java"
set CLASSPATH=%CLASSPATH%;"%PATHTOPROJECT%\console\console-view\src\main\resources"

rem console-war
set CLASSPATH=%CLASSPATH%;"%PATHTOPROJECT%\console\console-war\src\main\java"

java -Xmx512m -XX:MaxPermSize=256m -cp %CLASSPATH% -Dfile.encoding=UTF-8 -DBONITA_HOME=%BONITA_HOME% -Djava.util.logging.config.file=%BONITA_HOME%/external/logging/logging.properties -Djava.security.auth.login.config=%BONITA_HOME%\external\security\jaas-standard.cfg -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y -Dcatalina.base="%PATHTOPROJECT%\build\console\target\tomcat" com.google.gwt.dev.DevMode -gen "%PATHTOPROJECT%\build\console\target\.generated" -logLevel INFO -war "%PATHTOPROJECT%\build\console\target\console-all-in-one-%BONITATARGETVERSION%" -port 8888 -startupUrl console/BonitaConsole.html org.bonitasoft.console.devBonitaConsoleFF