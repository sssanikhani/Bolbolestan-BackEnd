#!/bin/bash

network_name="bolbolestan-net"
mysql_name="mysqldb"
dbname="bolbolestan"
project_name="bolbolestan-back"
project_image_name="bolbolestan-back:latest"

[[ $(docker network ls -f "name=bolbolestan-net" --format '{{.Name}}') == $network_name ]] ||
docker network create $network_name

[[ $(docker ps -a -f "name=$mysql_name" --format '{{.Names}}') == $mysql_name ]] || 
docker run --network $network_name --name $mysql_name -it \
    -e MYSQL_ROOT_PASSWORD="root" -e MYSQL_DATABASE=$dbname -d mysql:8

docker start $mysql_name

docker build -t $project_image_name .

[[ $(docker ps -a -f "name=$project_name" --format '{{.Names}}') == $project_name ]] || 
docker run --network $network_name --name $project_name -p 8080:8080 -it $project_image_name

docker start $project_name