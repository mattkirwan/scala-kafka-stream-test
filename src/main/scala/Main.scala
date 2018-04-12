import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.{KStream, Printed}
import org.apache.kafka.streams._

object Main extends App {

  val config: Properties = {
    val props = new Properties
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "appid")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092,localhost:29092,localhost:39092")
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass())
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass())
    props
  }

  val builder = new StreamsBuilder

  val topology: Topology = builder.build()

  val sourceStream: KStream[String, String] = builder.stream("testing")

  val uppercaseStream: KStream[String, String] = sourceStream.mapValues { v => v.toUpperCase }

  uppercaseStream.print(Printed.toSysOut[String, String]())

  uppercaseStream.to("testing-out")

  println(topology.describe())

  val kafkaStreams = new KafkaStreams(builder.build(), config)

  kafkaStreams.start()

  Runtime.getRuntime.addShutdownHook(new Thread(() => {
    kafkaStreams.close(10, TimeUnit.SECONDS)
  }))

}
