package ninja.grimrose.sandbox.infra.gcp

import ninja.grimrose.sandbox.BaseSpecSupport
import typings.node.Buffer
import typings.node.nodeStrings.base64
import org.scalatest.flatspec.AnyFlatSpec

class GoogleCloudKmsIOSpec extends AnyFlatSpec with BaseSpecSupport {

  it should "be converted" in {
    val source = "abcde12345"

    val input  = Buffer.from(source).toString("base64")
    val actual = Buffer.from(input, base64).toString("utf8")

    assert(input === "YWJjZGUxMjM0NQ==")
    assert(actual === source)
  }
}
