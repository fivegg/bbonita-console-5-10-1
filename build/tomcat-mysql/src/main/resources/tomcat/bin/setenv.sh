#!/bin/sh


# Sets some variables
BONITA_HOME="-DBONITA_HOME=$CATALINA_HOME/bonita"
LOG_OPTS="-Djava.util.logging.config.file=$CATALINA_HOME/external/logging/logging.properties"
SECURITY_OPTS="-Djava.security.auth.login.config=$CATALINA_HOME/external/security/jaas-standard.cfg"

CATALINA_OPTS="$CATALINA_OPTS $BONITA_HOME $LOG_OPTS -Dfile.encoding=UTF-8 -Xshare:auto -Xms512m -Xmx1024m -XX:MaxPermSize=256m -XX:+HeapDumpOnOutOfMemoryError"
export CATALINA_OPTS
#install and start MySQL
if [ -r "$CATALINA_HOME"/bin/scripts/installMySQLAndStart.sh ]; then
  . "$CATALINA_HOME"/bin/scripts/installMySQLAndStart.sh
fi
