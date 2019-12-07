package ninja.grimrose.sandbox.core

import io.scalajs.nodejs.buffer.Buffer
import ninja.grimrose.sandbox.BaseSpecSupport
import org.scalatest.flatspec.AnyFlatSpec

class Base64DecoderSpec extends AnyFlatSpec with BaseSpecSupport {

  it should "be decoded" in {
    val source = "{\"abcde\":123450}"

    val input = Buffer.from(source, "utf-8").toString("base64")

    val actual = Base64Decoder.decode(input)

    assert(actual == source)
  }
}
