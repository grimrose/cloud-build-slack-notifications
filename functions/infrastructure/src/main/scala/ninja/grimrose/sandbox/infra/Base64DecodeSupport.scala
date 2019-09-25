package ninja.grimrose.sandbox.infra

import java.util.Base64

trait Base64DecodeSupport {
  def decode(input: String) = new String(Base64.getDecoder.decode(input))
}
