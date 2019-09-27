package ninja.grimrose.sandbox.core

import java.util.Base64

object Base64Decoder {

  def decode(input: String): String = new String(Base64.getDecoder.decode(input))

}
