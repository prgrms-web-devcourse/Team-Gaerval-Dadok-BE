#!/bin/bash


if [ $(docker ps | grep -c "nginx-dev") -eq 0 ]; then
  echo "Starting nginx-dev..."
  docker-compose up -d nginx-dev
else

  echo "-------------------------------------------"
  echo "nginx is already running"
  echo "-------------------------------------------"
fi

echo
echo

if [ $(docker ps | grep -c "redis-dev") -eq 0 ]; then
  echo "Starting redis-dev..."
  docker-compose up -d redis-dev
else
  echo "-------------------------------------------"
  echo "redis is already running"
  echo "-------------------------------------------"
fi

echo
echo "start blue-green deploy!"
echo



# Blue 를 기준으로 현재 떠있는 컨테이너를 체크한다.
RUNNING_BLUE_CONTAINER=$(docker ps | grep blue)

DEFAULT_CONF="/home/app/nginx/default.conf"
BLUE_CONTAINER_NAME="dadok-dev-blue"
GREEN_CONTAINER_NAME="dadok-dev-green"
PORT=8080

# 컨테이너 스위칭
# BLUE 컨테이너가 동작중이지 않다면,
if [ -z "$RUNNING_BLUE_CONTAINER" ]; then

  echo "blue up"
  START_CONTAINER="dadok-dev-blue" # 시작할 컨테이너
  STARTED_CONTAINER="dadok-dev-green" # 시동중인 턴테이너
  docker-compose up -d dadok-dev-blue

  sed -i "s/${GREEN_CONTAINER_NAME}/${BLUE_CONTAINER_NAME}/g" $DEFAULT_CONF

  docker exec nginx-dev service nginx reload

else # BLUE 컨테이너가 동작중이라면

  echo "green up"
  START_CONTAINER="dadok-dev-green" # 시작할 컨테이너
  STARTED_CONTAINER="dadok-dev-blue" # 시동중인 턴테이너

  docker-compose up -d dadok-dev-green

  sed -i "s/${BLUE_CONTAINER_NAME}/${GREEN_CONTAINER_NAME}/g" $DEFAULT_CONF

  docker exec nginx-dev service nginx reload

fi

echo "start container : $START_CONTAINER"
echo "started container : $STARTED_CONTAINER"

# 새로운 컨테이너가 제대로 떴는지 확인
count=0
while [ $count -lt 10 ]; do
    echo "$START_CONTAINER health check...."

    HEALTH=$(docker exec nginx-dev curl http://$START_CONTAINER:8080)
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

echo "-------------------------------------------"
echo "nginx reload"
docker exec nginx-dev service nginx reload
echo "-------------------------------------------"

# 이전 컨테이너 종료
if [ $(docker ps | grep -c "$STARTED_CONTAINER") -eq 1 ]; then
  docker-compose stop "$STARTED_CONTAINER"
  docker rm $STARTED_CONTAINER
else
  echo "$STARTED_CONTAINER is not running."
fi
## end
