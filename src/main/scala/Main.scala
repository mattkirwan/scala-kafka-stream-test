import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.common.serialization.Serdes
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

  builder.stream[String, String]("testing")
    .mapValues(v => v.toUpperCase)
    .to("testing-out")


//  val topology: Topology = builder.build()
//  println(topology.describe())

  val kafkaStreams = new KafkaStreams(builder.build(), config)

  kafkaStreams.start()

  Runtime.getRuntime.addShutdownHook(new Thread(() => {
    kafkaStreams.close(10, TimeUnit.SECONDS)
  }))

}
