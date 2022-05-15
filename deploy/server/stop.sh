#!/bin/sh
cd /home/raccoon_bot/
sudo touch pause

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