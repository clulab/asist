/**
 *  Authors:  Joseph Astier, Adarsh Pyarelal
 *
 *  updated:  2021 February
 *
 *  This object will run the DialogAgent in either message bus or 
 *  file mode depending on user inputs
 *
 */
package org.clulab.asist

import java.io.File

object  RunDialogAgent extends App {
  
  val appName ="DialogAgent"

  /** Show the usage hints */
  def usage: Unit = List(
    "",
    "To use %s on the message bus:".format(appName),
    "%s mqtt [host] [port]".format(appName),
    "",
    "To use %s on a file or the first level of a directory:".format(appName),
    "%s file [inputFileOrDir] [outputFile]".format(appName),
    ""
  ).map(println)


  /** Create an agent based on the user args */
  val agent: DialogAgent = args match {
    case Array("mqtt", host: String, port: String) => 
      new DialogAgentMqtt(host, port)
    case Array("file", inputFile: String, outputFile: String) => 
      new DialogAgentFile(inputFile, outputFile)
    case _ => {
      new DialogAgentMqtt(MqttAgentDefaults.HOST, MqttAgentDefaults.PORT)
    }
  }
}
