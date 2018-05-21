import java.util.{Collections, Properties}
import java.util.concurrent.TimeUnit

import com.mattkirwan.model.Purchase
import org.apache.kafka.common.serialization.{Serde, Serdes}
import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroSerializer}
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.avro.specific.SpecificRecord
//import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.serialization.Serdes.StringSerde
import org.apache.kafka.connect.json.{JsonDeserializer, JsonSerializer}
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream.{KStream, Printed, Produced}


import com.mattkirwan.avro.Board

object Main extends App {

  val stringSerde: Serde[String] = Serdes.String()

  val config: Properties = {
    val props = new Properties
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "appid")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092")
    props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081")
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName())
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, classOf[SpecificAvroSerde[_ <: SpecificRecord]])
    props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, "org.apache.kafka.streams.errors.LogAndContinueExceptionHandler")
    props
  }


  val builder = new StreamsBuilder

  val specificAvroSerde: Serde[Board] = new SpecificAvroSerde[Board]

  val isKeySerde: Boolean = false

  specificAvroSerde.configure(Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081"), isKeySerde)

  val stream: KStream[String, Board] = builder.stream("board2058")
  stream.print(Printed.toSysOut[String, Board])

  val topology: Topology = builder.build()
  println(topology.describe())

  val kafkaStreams = new KafkaStreams(topology, config)

  kafkaStreams.start()

  Runtime.getRuntime.addShutdownHook(new Thread(() => {
    kafkaStreams.close(10, TimeUnit.SECONDS)
  }))

}
