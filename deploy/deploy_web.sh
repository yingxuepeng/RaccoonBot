#!/bin/sh
#/target/qqbot-2.0.0-SNAPSHOT.jar
ssh ubuntu@106.53.108.169 "sudo rm -rf /home/raccoon_bot/frontend/raccoon-forum"
scp -r ../web/raccoon-forum/build/ ubuntu@106.53.108.169:/home/raccoon_bot/frontend/raccoon-forum
ssh ubuntu@106.53.108.169 "sudo nginx -s reload"