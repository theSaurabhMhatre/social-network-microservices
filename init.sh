#!/bin/bash
# script to check if dependant services are started

# command line inputs to be specified in the following sequence
# service-name dbcheck (true/false) dependant-service-host-1 dependant-service-host-2
inputs=("$@")
# upstream services status
upstream=1
# database service status
dbstatus=1
# service not started
down=""

# checks if the database-service is up
function database() {
  local retries=10
  local response=0
  response=$(curl -I database-service:5432 2>&1 | grep 52 | wc -l)
  while [ $response -ne 1 ] && [ $retries -gt 0 ]
  do
    echo "database-service not yet started, sleeping for 10 seconds"
    dbstatus=1
    sleep 10
    response=$(curl -I database-service:5432 2>&1 | grep 52 | wc -l)
    ((retries--))
  done
  if [ $response -eq 1 ]
    then
      echo "database-service is up"
      dbstatus=0
    else
      echo "Could not start $1"
      exit 1
  fi
}

# helper to check if upstream services are up
function up() {
  local hosts=("$@")
  for host in "${hosts[@]}"
  do
    local response=$(curl -I $host/actuator/health 2>/dev/null | head -n 1 | cut -d$' ' -f2)
    if [[ -z $response || $response -ne 200 ]]
    then
      upstream=1
      down=$host
      return
    fi
  done
  echo "Necessary services are up"
  upstream=0
}

# checks if requested upstream services are up
function status() {
  local retries=10
  local hosts=("$@")
  up ${hosts[@]}
  while [ $upstream -ne 0 ] && [ $retries -gt 0 ]
  do
    echo "$down not yet started, sleeping for 10 seconds"
    sleep 10
    up ${hosts[@]}
    ((retries--))
  done
}

# entry point
function init() {
  local inputs=("$@")
  local service=${inputs[0]}
  local dbcheck=${inputs[1]}
  local hosts=("${inputs[@]:2}")

  echo "Attempting to start $service"

  if [ $dbcheck == "true" ]
    then
      echo "Database status check requested, checking database-service status"
      database $service
    else
      echo "Database status check not requested, skipping database-service status check"
  fi

  if [ ${#hosts[@]} -gt 0 ]
  then
    status ${hosts[@]}
  else
    upstream=0
  fi

  if [[ $upstream -eq 0 && ( ( $dbcheck == "true" && $dbstatus -eq 0 ) || $dbcheck == "false" ) ]]
    then
      echo "Starting $service"
      java -jar $service.jar
    else
      echo "Could not start $service"
  fi
}

init ${inputs[@]}
