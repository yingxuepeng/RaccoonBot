#

#/target/qqbot-2.0.0-SNAPSHOT.jar
cd /home/raccoon_bot/
sudo kill `cat pid`
sudo nohup java -jar qqbot-2.0.0-SNAPSHOT.jar --spring.profiles.active=prod > /dev/null & echo $! > pid &