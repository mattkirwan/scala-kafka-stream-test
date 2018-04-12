This is my playground for messing with Scala and Kafka Streams.

# Usage

## Create a topic

`docker-compose up -d app-`

`docker exec -it kafkastreamtest_kafka-1_1 bash`

`kafka-topics --zookeeper localhost:22181,localhost:32181,localhost:42181 --create --topic testing --if-not-exists --replication-factor 3 --partitions 3`

## Run the app

`docker exec -it kafkastreamtest_app-scala_1 bash`

`sbt run`

## Create some message

`docker exec -it kafkastreamtest_kafka-1_1 bash`

`kafka-console-producer --broker-list localhost:19092,localhost:29092,localhost:39092 --topic testing --property="parse.key=true" --property="key.separator=:"`

```
>hello:world
>
```

and the KStreams app will print:

```
[KSTREAM-SOURCE-0000000000]: hello, world
```

and also output so topic `testing-out`:

`kafka-console-consumer --bootstrap-server localhost:19092,localhost:29092,localhost:39092 --topic testing-out --from-beginning`

