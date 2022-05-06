#

#/target/qqbot-2.0.0-SNAPSHOT.jar
cd /home/raccoon_bot/
sudo kill -9 `cat pid`
nohup java -jar qqbot-2.0.0-SNAPSHOT.jar --spring.profiles.active=prod > log & echo $! > pid &