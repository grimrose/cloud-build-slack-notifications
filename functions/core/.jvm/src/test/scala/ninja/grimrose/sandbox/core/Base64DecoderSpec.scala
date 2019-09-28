package ninja.grimrose.sandbox.core

import java.nio.charset.StandardCharsets
import java.util.Base64

import ninja.grimrose.sandbox.BaseSpecSupport
import org.scalatest.FlatSpec

class Base64DecoderSpec extends FlatSpec with BaseSpecSupport {

  it should "be parsed" in {
    val source = "{\"xyz\":890}"

    val input = new String(Base64.getEncoder.encode(source.getBytes(StandardCharsets.UTF_8)))

    val actual = Base64Decoder.decode(input)

    assert(actual == source)
  }
}
