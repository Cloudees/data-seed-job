# data-seed-job
The following repository contains the files of the data seed job init container. The role of the init container is to write initial data into our Redis database.

In order to use our data seed job container, we should specify mainly 3 environment variables:
- MICROSERVICE = playlist/videos.
- REDIS_HOST = IP address or domain name of Redis database. In our case, we will use Azure Cache for Redis.
- REDIS_PORT = In our case, we will use 6379 (NON TLS PORT for Redis).
- PASSWORD = Redis database password.