#!/bin/bash

# 도커가 이미 설치되어있는지 확인
if command -v docker &> /dev/null
then
    echo "Docker already installed"
else # 도커가 설치되지 않았다면
    # 도커 설치 스크립트
    sudo amazon-linux-extras install docker
    sudo service docker start
    # 현재 사용자를 docker 그룹에 추가하여 sudo를 사용하지 않고 도커 명령을 사용할 수 있도록 설정
    sudo usermod -aG docker $USER
    # 도커 설치 완료 메시지 출력
    echo "Docker installed successfully"
fi


# Blue 를 기준으로 현재 떠있는 컨테이너를 체크한다.
RUNNING_BLUE_CONTAINER=$(docker ps | grep blue)

DEFAULT_CONF="/home/ec2-user/app/nginx/default.conf"
BLUE="blue"
GREEN="green"
BLUE_PORT=8080
GREEN_PORT=8081

# 컨테이너 스위칭
# BLUE 컨테이너가 동작중이지 않다면,
if [ -z "$RUNNING_BLUE_CONTAINER"]; then

  echo "blue up"
  START_CONTAINER = "blue-dev" # 시작할 컨테이너
  STARTED_CONTAINER = "green-dev" # 시동중인 턴테이너
  START_PORT=8080
  docker-compose up -d blue-dev

  sed -i "s/${GREEN}/${GREEN_PORT}/g; s/${BLUE}/${BLUE_PORT}/g" $DEFAULT_CONF
  docker-compose exec nginx-dev service nginx reload

else # BLUE 컨테이너가 동작중이라면

  echo "green up"
  START_CONTAINER = "green-dev" # 시작할 컨테이너
  STARTED_CONTAINER = "blue-dev" # 시동중인 턴테이너
  START_PORT=8081

  docker-compose up -d green-dev


  sed -i "s/${BLUE}/${BLUE_PORT}/g; s/${GREEN}/${GREEN_PORT}/g" $DEFAULT_CONF
  docker-compose exec nginx-dev service nginx reload

fi

# 새로운 컨테이너가 제대로 떴는지 확인
count=0
while [ $count lt 10]; do
    echo "$START_CONTAINER health check...."
    HEALTH=$(docker-compose exec nginx-dev curl http://$STARTED_CONTAINER:$START_PORT)
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
if docker-compose ps | grep -q "$STARTED_CONTAINER"; then
  docker-compose stop "$STARTED_CONTAINER"
else
  echo "$STARTED_CONTAINER is not running."
fi
## end