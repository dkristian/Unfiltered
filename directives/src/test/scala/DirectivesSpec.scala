package unfiltered.directives

import org.specs._

import unfiltered.request._

object DirectivesSpecJetty
extends unfiltered.spec.jetty.Planned
with DirectivesSpec

object DirectivesSpecNetty
extends unfiltered.spec.netty.Planned
with DirectivesSpec

trait DirectivesSpec extends unfiltered.spec.Hosted {
  import unfiltered.response._
  import unfiltered.response._
  import unfiltered.directives._, Directives._

  // it's simple to define your own directives
  def contentType(tpe:String) =
    when{ case RequestContentType(`tpe`) => } orElse UnsupportedMediaType

  def intent[A,B] = Directive.Intent(unfiltered.request.Path[A]) {
    case Seg(List("accept_json", id)) =>
      for {
        _ <- POST
        _ <- contentType("application/json")
        _ <- Accepts.Json
        r <- request[Any]
      } yield Ok ~> JsonContent ~> ResponseBytes(Body.bytes(r))
  }

  val someJson = """{"a": 1}"""

  "Directives" should {
    "respond with json if accepted" in {
      val resp = http(host / "accept_json" / "123"
        <:< Map("Accept" -> "application/json")
        <:< Map("Content-Type" -> "application/json")
        << someJson as_str)
      resp must_== someJson
    }
  }
}
