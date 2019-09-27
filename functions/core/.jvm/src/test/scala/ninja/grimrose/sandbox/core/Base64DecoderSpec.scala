package ninja.grimrose.sandbox.core

import io.scalajs.nodejs.buffer.Buffer
import ninja.grimrose.sandbox.BaseSpecSupport
import org.scalatest.FlatSpec

class Base64DecoderSpec extends FlatSpec with BaseSpecSupport {

  it should "be parsed" in {
    val source = "{\"xyz\":890}"

    val input = Buffer.from(source, "utf-8").toString("base64")

    val actual = Base64Decoder.decode(input)

    assert(actual == source)
  }
}
