package org.clulab.asist.agents

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import com.typesafe.scalalogging.LazyLogging
import org.clulab.asist.messages._

import scala.concurrent.Future
import scala.util.{ Failure, Success }


class DialogAgentTamu (
  override val nMatches: Int = 0
) extends DialogAgent with LazyLogging {

  val test = new TamuDialogAgentMessage(
    participant_id = "Participant 21",
    text = "Five because at least I know there was one yellow victim that died so",
    extractions = Seq(
      DialogAgentMessageDataExtraction(
        label = "Sight",
        span = "was one yellow victim",
        arguments = Map.empty,
        start_offset = 20,
        end_offset = 25,
        taxonomy_matches = Seq(
          (
            "stop-triaging", "11.709686762798679"
          ),
          (
           "no-victims-spotted", "10.767969549025242"
          )
        )
      ),
      DialogAgentMessageDataExtraction(
        label = "Victim",
        span = "Victim",
        arguments = Map.empty,
        start_offset = 60,
        end_offset = 75,
        taxonomy_matches = Seq(
          (
            "stop-triaging-victim", "18.593763750341402"
          ),
          (
            "start-triaging-victim", "17.326888048081006"
          )
        )
      )
    )
  )

  TamuClientSingleRequest(writeJson(test))

}


object TamuClientSingleRequest extends LazyLogging {

  def apply(json :String): Unit = {

    implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    val responseFuture: Future[HttpResponse] = 
      Http().singleRequest(
        HttpRequest(
          method=HttpMethods.GET,
          uri = "http://localhost:8000/classify",
          entity = HttpEntity(
            ContentTypes.`application/json`,
            json
          )
        )
      )

    responseFuture
      .onComplete {
        case Success(res: HttpResponse) => 
          logger.info("Success communicating with server:")
          logger.info(res.toString)
          logger.info("Response entity databytes:")
          logger.info(res.entity.getDataBytes.toString)
        case Failure(e)   => 
          logger.error("Error communicating with server:")
          logger.error(e.toString)
      }
  }
}
