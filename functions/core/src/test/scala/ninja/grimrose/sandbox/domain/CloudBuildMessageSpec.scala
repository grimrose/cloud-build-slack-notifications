package ninja.grimrose.sandbox.domain

import ninja.grimrose.sandbox.BaseSpecSupport
import org.scalatest.FlatSpec

class CloudBuildMessageSpec extends FlatSpec with BaseSpecSupport {

  import io.circe.syntax._

  it should "asJson" in {
    val message = CloudBuildMessage.build(
      buildId = "test",
      logUrl = "https://console.cloud.google.com",
      status = "SUCCESS",
      badStatus = false,
      startTime = "2014-10-02T15:01:23.045123456Z",
      finishTime = "2014-10-02T15:01:23.045123456Z",
      timeout = "3.5s"
    )

    debug(message.asJson.spaces2)

    val expected =
      """|{
         |  "text" : "Build `test`",
         |  "attachments" : [
         |    {
         |      "title" : "Build logs",
         |      "title_link" : "https://console.cloud.google.com",
         |      "color" : "good",
         |      "fields" : [
         |        {
         |          "title" : "Status",
         |          "value" : "SUCCESS",
         |          "short" : false
         |        },
         |        {
         |          "title" : "StartTime",
         |          "value" : "2014-10-02T15:01:23.045123456Z",
         |          "short" : true
         |        },
         |        {
         |          "title" : "FinishTime",
         |          "value" : "2014-10-02T15:01:23.045123456Z",
         |          "short" : true
         |        },
         |        {
         |          "title" : "timeout",
         |          "value" : "3.5s",
         |          "short" : true
         |        }
         |      ]
         |    }
         |  ]
         |}""".stripMargin
    assert(message.asJson.spaces2 === expected)
  }
}
