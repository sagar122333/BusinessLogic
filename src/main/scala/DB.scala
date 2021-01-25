object DB {
  import org.bson.codecs.configuration.CodecRegistries._
  import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
  import org.mongodb.scala.bson.codecs.Macros._
  import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

  private val customCodecs = fromProviders(classOf[Job])

  private val codecRegistry = fromRegistries(customCodecs,
    DEFAULT_CODEC_REGISTRY)

  val database: MongoDatabase = MongoClient().getDatabase(generateOutBoud.dbName)
    .withCodecRegistry(codecRegistry)

  val Pdatabase: MongoDatabase = MongoClient().getDatabase("Publisher")
    .withCodecRegistry(codecRegistry)
}