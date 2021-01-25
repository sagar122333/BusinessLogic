import scala.io.Source
import org.json4s._
import org.json4s.native.JsonMethods
import org.json4s.native.Serialization.{read, write}

object generateOutBoud extends App {

  implicit val formats = DefaultFormats

  def getEntity[T](path: String)(implicit m: Manifest[T]): T = {
    val entitySrc = Source.fromFile(path)
    val entityStr = try JsonMethods.parse(entitySrc.mkString) finally entitySrc.close()
    entityStr.extract[T]
  }

  lazy val element = getEntity[List[Job]]("src/main/scala/InboundFeed.JSON")
  val dbName = element(0).company

  for{
    job <- element
  }yield utilityMethods.mapjob_jobgrp(job)

  for {
    jobgrp <- utilityMethods.jobGroups
  } yield utilityMethods.buildOutBound(jobgrp)
}



