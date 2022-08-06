#!/bin/sh

scp ./ssl/1_www.primeoj.com_bundle.crt ubuntu@106.53.108.169:/home/raccoon_bot/
scp ./ssl/2_www.primeoj.com.key ubuntu@106.53.108.169:/home/raccoon_bot/
ssh ubuntu@106.53.108.169 "sudo mv /home/raccoon_bot/1_www.primeoj.com_bundle.crt /etc/nginx/ssl/"
ssh ubuntu@106.53.108.169 "sudo mv /home/raccoon_bot/2_www.primeoj.com.key /etc/nginx/ssl/"


scp ./ssl/1_forum.primeoj.com_bundle.crt ubuntu@106.53.108.169:/home/raccoon_bot/
scp ./ssl/2_forum.primeoj.com.key ubuntu@106.53.108.169:/home/raccoon_bot/
ssh ubuntu@106.53.108.169 "sudo mv /home/raccoon_bot/1_forum.primeoj.com_bundle.crt /etc/nginx/ssl/"
ssh ubuntu@106.53.108.169 "sudo mv /home/raccoon_bot/2_forum.primeoj.com.key /etc/nginx/ssl/"
#ssh ubuntu@106.53.108.169 "sudo nginx -s reload"