package ninja.grimrose.sandbox.infra

import ninja.grimrose.sandbox.core.Base64Decoder

trait Base64DecodeSupport {
  def decode(input: String): String = Base64Decoder.decode(input)
}
