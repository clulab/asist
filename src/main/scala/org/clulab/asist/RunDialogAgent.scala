/**
 *  Authors:  Joseph Astier, Adarsh Pyarelal
 *
 *  updated:  2021 February
 *
 *  This class will test the DialogAgent in either MQTT or file mode
 */
package org.clulab.asist

object  RunDialogAgent extends App {
  /** subscribe to these MQTT topics for messages */
  val TOPIC_INPUT_OBS = "observations/chat"
  val TOPIC_INPUT_ASR = "agent/asr"

  /** publish message analysis to this MQTT topic */
  val TOPIC_OUTPUT = "agent/tomcat_chatbot"

  /** display the usage hints */
  def usage: Unit = List(
    "Usage",
    "mqtt <host> <port number>",
    "or",
    "file <input filename> <expected_results filename>",
  ).map(println)

  /** Create an agent for the user args and keep it in scope */
  val agent: Option[DialogAgent] = if(args.length == 3) args(0) match {
    case "mqtt" => Some {
      new DialogAgentMqtt(
        host = args(1),
        port = args(2),
        topicInputObs = TOPIC_INPUT_OBS,
        topicInputAsr = TOPIC_INPUT_ASR,
        topicOutput = TOPIC_OUTPUT
      )
    }
    case "file" => Some {
      new DialogAgentFile(
        inputFilename = args(1),
        outputFilename = args(2)
      )
    }
    case _ => {
      usage
      None
    }
  } else {
    usage
    None
  }
}
