package ninja.grimrose.sandbox.infra.gcp

import ninja.grimrose.sandbox.BaseSpecSupport
import org.scalatest.FlatSpec
import typings.node.Buffer
import typings.node.nodeStrings.base64

class GoogleCloudKmsIOSpec extends FlatSpec with BaseSpecSupport {

  it should "be converted" in {
    val source = "abcde12345"

    val input  = Buffer.from(source).toString("base64")
    val actual = Buffer.from(input, base64).toString("utf8")

    assert(input === "YWJjZGUxMjM0NQ==")
    assert(actual === source)
  }
}
