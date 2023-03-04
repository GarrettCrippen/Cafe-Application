REM ##!/bin/bash
REM #DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
REM #
REM #export JAVA_HOME=/usr/csshare/pkgs/jdk1.7.0_17
REM #export PATH=$JAVA_HOME/bin:$PATH
REM
REM # compile the java program
REM #javac -d $DIR/../classes $DIR/../src/Cafe.java
javac -d "C:\Users\playt\Desktop\Spring 2022\CS166\Projects\phase3\project-1\project\java\scripts" "C:\Users\playt\Desktop\Spring 2022\CS166\Projects\phase3\project-1\project\java\src\Cafe.java"

REM #run the java program
REM #Use your database name, port number and login
REM #java -cp $DIR/../classes:$DIR/../lib/pg73jdbc3.jar Cafe $USER"_DB" $PGPORT $USER

java Cafe postgres 5432 postgres


