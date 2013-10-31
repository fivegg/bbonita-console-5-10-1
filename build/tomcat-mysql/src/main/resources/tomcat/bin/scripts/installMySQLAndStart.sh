#!/bin/sh
#Set path of MySQL home 
MYSQL_HOME="$CATALINA_HOME/external/databases/mysql-5.5.11-linux2.6-i686"
if [ ! -d "$MYSQL_HOME" ]; then
    MYSQL_HOME="$CATALINA_HOME/external/databases/mysql-5.5.11-linux2.6-x86_64"
fi
isMySQLStart=`(netstat -an | grep 3306)`
needToStartMySQL="true"
cd /usr/local
#Install MySQL and start
if [ ! -d "/usr/local/mysql" ] && [ "$isMySQLStart" = "" ] ; then
    # If the user says yes(press y) then continue installing a MySQL Server. else not. 
    echo -n "A MySQL 5.5 server is about to be installed. You should not continue if a MySQL server is already installed on this machine. In order for the MySQL installation to work, the tomcat server must be launched with administration privileges. On linux the library libaio-dev needs to be installed before the the tomcat server is launched for the first time as it's required by MySQL. Do you want to continue[Y/n]?\n"
    stty -echo
    read  userWrite
    stty echo
    if [ $userWrite = "Y" ] || [ $userWrite = "y" ] ; then 
    	#Add a group and a user 
    	groupadd mysql
    	useradd -r -g mysql mysql
    	ln -s $MYSQL_HOME mysql
    	cd mysql
    	chown -R mysql .
    	chgrp -R mysql .
    	chmod +x -R $MYSQL_HOME
    	scripts/mysql_install_db --user=mysql --datadir=/var/lib/mysql/data
    	chown -R root .
    	chown -R mysql /var/lib/mysql/data
    	$MYSQL_HOME/support-files/mysql.server stop 
    	$MYSQL_HOME/support-files/mysql.server start
    	needToStartMySQL="false"
    	#Auto create MySQL databases;
    	if [ -r "$CATALINA_HOME"/bin/scripts/createMySQLDB.sh ]; then
      		. "$CATALINA_HOME"/bin/scripts/createMySQLDB.sh
    	fi
    fi
fi
#Just to start MySQL
if [ "$needToStartMySQL" = "true" ] ; then 
    $MYSQL_HOME/support-files/mysql.server stop 
    $MYSQL_HOME/support-files/mysql.server start
	
fi

