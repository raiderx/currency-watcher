#!/bin/bash

FILE='/var/run/tomcat/tomcat.pid'

if [ -f $FILE ];
then
    echo "$FILE exists"
    PID=$( head -n 1 $FILE )
    if
        ps -p $PID -o comm= > /dev/null
        then
            echo "Tomcat is running"
        else
            echo "Tomcat is not running"
            /usr/sbin/service tomcat start
        fi
else
    echo "$FILE does not exist"
    echo "Tomcat is not running"
    /usr/sbin/service tomcat start
fi
