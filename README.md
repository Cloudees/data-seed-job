# data-seed-job
The following repository contains the files of the data seed job init container. The role of the init container is to write initial data into our redis database.

In order to use our data seed job contianer, You should specify mainly 3 environment variables:
- MICROSERVICE = playlist / videos
- REDIS_HOST = Ip address or domain name of Redis database. In our case we will use Azure cache Redis 
- REDIS_PORT = in our case we will use 6379 (NON TLS PORT dor Redis)
- PASSWORD = Redis database password 
This repository is inspired from the following [link](https://github.com/kubees/data-seed-job) 
