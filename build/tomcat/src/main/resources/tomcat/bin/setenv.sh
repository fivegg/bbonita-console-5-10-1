#!/bin/sh

# Sets some variables
BONITA_HOME="-DBONITA_HOME=$CATALINA_HOME/bonita"
SECURITY_OPTS="-Djava.security.auth.login.config=$CATALINA_HOME/external/security/jaas-standard.cfg"

CATALINA_OPTS="$CATALINA_OPTS $BONITA_HOME $SECURITY_OPTS -Dfile.encoding=UTF-8 -Xshare:auto -Xms512m -Xmx1024m -XX:MaxPermSize=256m -XX:+HeapDumpOnOutOfMemoryError"
export CATALINA_OPTS
