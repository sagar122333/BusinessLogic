import org.mongodb.scala.MongoCollection
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object utilityMethods {
  val rule1 = Rule("country", "in", List("India"))
  val rule2 = Rule("country", "nin", List("India"))
  val rule3 = Rule("category", "in", List("Recruitment", "Engineering"))

  val publisher1 = Publisher(1, "Monster", true, "outBound_1")
  val publisher2 = Publisher(2, "StackOverFlow", true, "outBound_2")
  val publisher3 = Publisher(3, "Indeed", true, "outBound_3")

  val jobGroups = List(
    JobGroup( 1, List(rule1), 1, List(publisher2, publisher3) ),
    JobGroup( 2, List(rule2), 2, List(publisher1, publisher2) ),
    JobGroup( 3, List(rule3), 3, List(publisher1, publisher2, publisher3) ),
  )

  def operatorIn(job: Job, group: JobGroup): Boolean ={
    group.rules(0).field match{
      case "title" if(group.rules(0).value.exists(a => a == job.title)) => return true
      case "company" if(group.rules(0).value.exists(a => a == job. company)) => return true
      case "city" if(group.rules(0).value.exists(a => a == job.city)) => return true
      case "state" if(group.rules(0).value.exists(a => a == job.state)) => return true
      case "country" if(group.rules(0).value.exists(a => a == job.country)) => return true
      case "description" if(group.rules(0).value.exists(a => a == job.description)) => return true
      case "referencenumber" if(group.rules(0).value.exists(a => a == job.referencenumber)) => return true
      case "date" if(group.rules(0).value.exists(a => a == job.date)) => return true
      case "category" if(group.rules(0).value.exists(a => a == job.category)) => return true
      case "url" if(group.rules(0).value.exists(a => a == job.url)) => return true
      case _ => return false
    }
  }

  def operatorNin(job: Job, group: JobGroup): Boolean ={
    group.rules(0).field match{
      case "title" if(group.rules(0).value.exists(a => a == job.title)) => return false
      case "company" if(group.rules(0).value.exists(a => a == job. company)) => return false
      case "city" if(group.rules(0).value.exists(a => a == job.city)) => return false
      case "state" if(group.rules(0).value.exists(a => a == job.state)) => return false
      case "country" if(group.rules(0).value.exists(a => a == job.country)) => return false
      case "description" if(group.rules(0).value.exists(a => a == job.description)) => return false
      case "referencenumber" if(group.rules(0).value.exists(a => a == job.referencenumber)) => return false
      case "date" if(group.rules(0).value.exists(a => a == job.date)) => return false
      case "category" if(group.rules(0).value.exists(a => a == job.category)) => return false
      case "url" if(group.rules(0).value.exists(a => a == job.url)) => return false
      case _ => return true
    }
  }

  def methodSelector(job: Job, group: JobGroup): Option[JobGroup] ={
    if(group.rules(0).operator == "in") {
      if(operatorIn(job, group)) {
        return Some(group)
      }
    }
    else if(group.rules(0).operator == "nin") {
      if (operatorNin(job, group)) {
        return Some(group)
      }
    }
    None
  }

  def mapjob_jobgrp(job: Job): Unit = {
    val eligibleList = for{
      jobgrp <- jobGroups
    }yield methodSelector(job, jobgrp)

    val eligibleListP = eligibleList.filter(x => x != None)
    val eligibleListPure = eligibleListP.map(x => x.get)

    if(eligibleListPure.size == 1) {
      val JobC: MongoCollection[Job] = DB.database.getCollection("JobGroup_" + eligibleListPure.head.id)

      Await.result(JobC.insertOne(job).toFuture, 10 seconds)
    }

    else{
      val eligibleListwithPriority = filterWithPriority(eligibleListPure)

      val JobC: MongoCollection[Job] = DB.database.getCollection("JobGroup_" + eligibleListwithPriority.head.id)

      Await.result(JobC.insertOne(job).toFuture, 10 seconds)
    }
  }

  def filterWithPriority(value: List[JobGroup]): List[JobGroup] ={
    var maxPriority = 0
    value.foreach(a => {if(a.priority > maxPriority) maxPriority = a.priority})
    val grpList = for{
      v <- value
      if(v.priority == maxPriority)
    }yield v
    grpList
  }


  def buildOutBound(group: JobGroup): Unit ={
    val jobSeq = Await.result(DB.database.getCollection("JobGroup_" + group.id).find().toFuture(), 10 seconds)

    group.sponsoredPublishers.foreach{ x => {
      Await.result(DB.Pdatabase.getCollection(x.name).insertMany(jobSeq).toFuture(), 10 seconds)}
    }
  }
}
