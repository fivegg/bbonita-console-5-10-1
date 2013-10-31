@echo off
rem Set path of MySQL home and MySQL home in my.ini file
set MYSQL_HOME=%CATALINA_HOME%\external\databases\mysql-5.5.11-win32
if not exist "%MYSQL_HOME%" set MYSQL_HOME=%CATALINA_HOME%\external\databases\mysql-5.5.11-winx64
rem Install MySQL ,service and start it
for   /f  "tokens=2 delims=:" %%i   in   ( 'netstat -ano -p TCP ')   do  (
    echo %%i|findstr "3306">nul&&(set isMySQLStart=true)
)

if /i "%isMySQLStart%"=="true" ( 
    echo "MySQL is Started"
    goto exist
)
for /f "tokens=2 delims=:" %%i in ('sc query "MySQL5.5"') do (  
    if /i "%%i"==" MySQL5.5" (
	    goto exist
    ) else ( 
	    goto notexist
    )
) 

:notexist
rem If the user says yes(presses y) then continue installing a MySQL Server. else not.
set /p userWrite=A MySQL 5.5 server is about to be installed. You should not continue if a MySQL server is already installed on this machine. In order for the MySQL installation to work, the tomcat server must be launched with administration privileges. Do you want to continue[Y/n]?
if /i "%userWrite%"=="Y" (
    set MYSQL_DATA=C:\ProgramData\MySQL\MySQL Server 5.5\Data
    cd %MYSQL_HOME%
    cd bin
    xcopy  /E "%MYSQL_HOME%\data"  "%MYSQL_DATA%\"
    mysqld --install MySQL5.5 --defaults-file=%MYSQL_HOME%\my.ini
    net start MySQL5.5
    rem Auto create MySQL databases;
    if exist "%CATALINA_HOME%\bin\scripts\createMySQLDB.bat" call "%CATALINA_HOME%\bin\scripts\createMySQLDB.bat"
)
goto end

:exist
net stop MySQL5.5
net start MySQL5.5

:end

