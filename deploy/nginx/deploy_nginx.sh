#!/bin/sh

scp ./config/ngx.react.conf ubuntu@106.53.108.169:/home/raccoon_bot/
ssh ubuntu@106.53.108.169 "sudo mv /home/raccoon_bot/ngx.react.conf /etc/nginx/sites-enabled && sudo nginx -s reload"