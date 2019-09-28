package ninja.grimrose.sandbox.core

import io.scalajs.nodejs.buffer.Buffer

object Base64Decoder {

  def decode(input: String): String = Buffer.from(input, "base64").toString("utf-8")

}
