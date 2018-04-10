import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.{KStream, KStreamBuilder, Printed}
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, StreamsConfig}

object Main extends App {

  val streamConfig: Properties = {
    val props = new Properties
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "appid")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092,localhost:29092,localhost:39092")
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass())
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass())
    props
  }

  val builder = new StreamsBuilder

  val source: KStream[String, String] = builder.stream("testing")

  source.print(Printed.toSysOut[String, String]())

  val streams = new KafkaStreams(builder.build(), streamConfig)

  streams.start()

  Runtime.getRuntime.addShutdownHook(new Thread(() => {
    streams.close(10, TimeUnit.SECONDS)
  }))
}
