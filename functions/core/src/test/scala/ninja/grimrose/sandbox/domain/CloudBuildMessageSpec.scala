package ninja.grimrose.sandbox.domain

import ninja.grimrose.sandbox.BaseSpecSupport
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.funspec.AnyFunSpec

class CloudBuildMessageSpec extends AnyFunSpec with TableDrivenPropertyChecks with BaseSpecSupport {

  import io.circe.syntax._

  it("asJson") {
    val message = CloudBuildMessage.build(
      buildId = "test",
      logUrl = "https://example.com",
      status = "SUCCESS",
      badStatus = false,
      startTime = Some("2014-10-02T15:01:23.045123456Z"),
      finishTime = Some("2014-10-02T15:01:23.045123456Z"),
      timeout = "3.5s"
    )

    val expected =
      """|{
         |  "text" : "Build `test`",
         |  "attachments" : [
         |    {
         |      "title" : "Build logs",
         |      "title_link" : "https://example.com",
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

  it("parsed") {
    val fixtures = Table(
      ("rawJson", "expected"),
      (
        """|{
           |  "id" : "c473aeb6-4f29-4ff9-b8bb-68c7bc1f1460",
           |  "projectId" : "example",
           |  "status" : "QUEUED",
           |  "timeout" : "1200s",
           |  "logUrl" : "https://example.com"
           |}""".stripMargin,
        true
      ),
      (
        """|{
           |  "id" : "a87d2d8a-5455-4585-a995-6e2a6fb38d39",
           |  "projectId" : "example",
           |  "status" : "SUCCESS",
           |  "startTime" : "2019-09-25T16:34:24.958573388Z",
           |  "finishTime" : "2019-09-25T16:36:02.353865Z",
           |  "timeout" : "1200s",
           |  "logUrl" : "https://example.com"
           |}""".stripMargin,
        false
      )
    )

    forAll(fixtures) { (rawJson, expected) =>
      val actual = CloudBuildMessage.parse(rawJson)
      assert(actual.isLeft === expected)
    }
  }
}
