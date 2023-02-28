#!/bin/bash

if [ $(docker ps | grep -c "nginx-dev") -eq 0 ]; then
  echo "Starting nginx-dev..."
  docker-compose up -d nginx-dev
else
  echo "running nginx"
fi

# Blue 를 기준으로 현재 떠있는 컨테이너를 체크한다.
RUNNING_BLUE_CONTAINER=$(docker ps | grep blue)

DEFAULT_CONF="/home/ec2-user/app/nginx/default.conf"
BLUE_PORT=8080
GREEN_PORT=8081
BLUE_CONTAINER_NAME="dadok-dev-blue"
GREEN_CONTAINER_NAME="dadok-green-blue"


# 컨테이너 스위칭
# BLUE 컨테이너가 동작중이지 않다면,
if [ -z "$RUNNING_BLUE_CONTAINER" ]; then

  echo "blue up"
  START_CONTAINER="dadok-dev-blue" # 시작할 컨테이너
  STARTED_CONTAINER="dadok-dev-green" # 시동중인 턴테이너
  START_PORT=8080
  docker-compose up -d dadok-dev-blue

  sed -i "s/${GREEN_CONTAINER_NAME}:${GREEN_PORT}/${BLUE_CONTAINER_NAME}:${BLUE_PORT}/g" $DEFAULT_CONF

  docker-compose exec nginx-dev service nginx reload

else # BLUE 컨테이너가 동작중이라면

  echo "green up"
  START_CONTAINER="dadok-dev-green" # 시작할 컨테이너
  STARTED_CONTAINER="dadok-dev-blue" # 시동중인 턴테이너
  START_PORT=8081

  docker-compose up -d dadok-dev-green

  sed -i "s/${BLUE_CONTAINER_NAME}:${BLUE_PORT}/${GREEN_CONTAINER_NAME}:${GREEN_PORT}/g" $DEFAULT_CONF
  docker-compose exec nginx-dev service nginx reload

fi

echo "start container : $START_CONTAINER"
echo "started container : $STARTED_CONTAINER"

# 새로운 컨테이너가 제대로 떴는지 확인
count=0
while [ $count -lt 10 ]; do
    echo "$START_CONTAINER health check...."

    HEALTH=$(docker-compose exec nginx-dev curl http://$START_CONTAINER:$START_PORT)
    if [ -n "$HEALTH" ]; then
        break
    fi
    sleep 3
    count=$((count+1))
done

if [ $count -ge 11 ]; then
    echo "Error: Health check failed after 10 retries. deploy fail. count : $count"
    count=0
    exit 1
fi

# 이전 컨테이너 종료
if [ $(docker ps | grep -c "$STARTED_CONTAINER") -eq 1 ]; then
  docker-compose stop "$STARTED_CONTAINER"
else
  echo "$STARTED_CONTAINER is not running."
fi
## end