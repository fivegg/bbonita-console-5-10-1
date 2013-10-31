@echo off
echo "create MySQL DB start"
cd %MYSQL_HOME%\bin
rem Login in MySQL database and start to create databases;
mysql -u root <%CATALINA_HOME%\bin\scripts\createMySQLDB.sql
