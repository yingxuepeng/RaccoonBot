#

#/target/qqbot-2.0.0-SNAPSHOT.jar
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
sleep 3s
sudo nohup java -jar qqbot-2.0.0-SNAPSHOT.jar --spring.profiles.active=prod > /dev/null & echo $! > pid &