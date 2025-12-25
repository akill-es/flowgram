# flowgram
Workflow automation for Telegram


# Quickstart

## Dev Local (docker compose)

````
```shell
cd infra/dev
``

```shell
docker-compose -f kafka-compose.yaml -f postgres.yaml up -d
```

* Postgres
- Username: dev
- Password: dev_pass
- Port: 5432 

* Kafka
- Port: 9092 
