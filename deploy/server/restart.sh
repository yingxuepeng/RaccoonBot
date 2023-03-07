#!/bin/sh
cd /home/raccoon_bot/
FILE=/home/raccoon_bot/pid
if test -f "$FILE"; then
  echo "$FILE exists."
  PID=`cat pid`
  if ps -p $PID > /dev/null
  then
    echo "$PID is running"
    sudo kill $PID
  fi
  sudo rm -f pid
fi
sudo rm -f pause
sleep 3s
screen
sudo nohup java -jar /home/raccoon_bot/qqbot-2.0.0-SNAPSHOT.jar --spring.profiles.active=prod > /dev/null & echo $! > /home/raccoon_bot/pid &
sleep 1s
echo "`cat pid`"