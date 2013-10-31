@echo off

set migratedb.domain=default
set /p migratedb.domain=Which domain do you want to use (press enter without nothing to use default)?

set bonita.home=
set /p bonita.home=Where is your bonita.home folder?
if "%bonita.home%"=="" (set bonita.home=%BONITA_HOME%)

::Generate the classpath using jars in engine\libs folder
set classpath="..\engine\libs\*;"

java -DBONITA_HOME="%bonita.home%" -classpath %classpath% org.ow2.bonita.util.IndexTool  %migratedb.domain% 1 1
