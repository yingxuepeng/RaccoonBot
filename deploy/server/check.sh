#!/bin/sh
cd /home/raccoon_bot/
FILE=/home/raccoon_bot/pid
PAUSE_FILE=/home/raccoon_bot/pause
if test -f "$FILE"; then
  PID=`cat pid`
  if ps -p $PID > /dev/null
  then
    echo 'running'
    # running, do nothing
  else
    if test -f "$PAUSE_FILE"
    then
      echo 'pause'
    else
      rm -f pid
      touch check
      sudo nohup java -jar /home/raccoon_bot/qqbot-2.0.0-SNAPSHOT.jar --spring.profiles.active=prod > /dev/null & echo $! > /home/raccoon_bot/pid &
    fi
  fi
fi
