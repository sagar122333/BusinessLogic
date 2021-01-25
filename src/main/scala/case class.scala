case class Client(
                   id: String,
                   name: String,
                   inboundFeedUrl: String,
                   jobGroups: List[JobGroup]
                 )
case class JobGroup(
                     id: Int,
                     rules: List[Rule],
                     priority: Int,
                     sponsoredPublishers: List[Publisher]
                   )
case class Publisher(
                      id: Int,
                      name: String,
                      isActive: Boolean,
                      outboundFileName: String
                    )
case class Job(
                title: String,
                company: String,
                city: String,
                state: String,
                country: String,
                description: String,
                referencenumber: Int,
                date: String,
                category: String,
                url: String
              )
case class Rule(
                 field: String,
                 operator: String,
                 value: List[String]
               )


