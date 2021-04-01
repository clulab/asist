/**
 *  Authors:  Joseph Astier, Adarsh Pyarelal
 *
 *  Updated:  2021 March
 *
 *  An abstract MQTT agent
 *
 *  Based on the Eclipse Paho MQTT API: www.eclipse.org/paho/files/javadoc
 */
package org.clulab.asist

import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.slf4j.LoggerFactory
import scala.util.control.Exception._


/** base class for anything needing connection to the message bus */
abstract class AgentMqtt(
  val host: String = "",
  val port: String = "",
  val id: String = "",
  val inputTopics: List[String] = List.empty,
  val outputTopics: List[String] = List.empty) extends MqttCallback {

  private lazy val logger = LoggerFactory.getLogger(this.getClass())

  /** MQTT broker connection identities */
  val SUB_ID = "%s_subscriber".format(id)
  val PUB_ID = "%s_publisher".format(id)

  /** MQTT broker connection address */
  val uri = "tcp://%s:%s".format(host,port)

  /** MQTT quality of service */
  val qos = 2

  /** Publisher for sending messages to the publication topics
   *  @return an optional MqttClient publisher if successful, else None
   */
  val publisher: Option[MqttClient] = allCatch.opt{
    val pub = new MqttClient(uri, PUB_ID, new MemoryPersistence())
    pub.connect(new MqttConnectOptions)
    pub
  }

  /** Subscriber receives messages on the subscription topics 
   *  @return an optional MqttAsyncClient subscriber if successful, else None
   */
  val subscriber: Option[MqttAsyncClient] = allCatch.opt {
    val sub = new MqttAsyncClient(uri, SUB_ID, new MemoryPersistence())
    sub.setCallback(this)
    sub.connect(new MqttConnectOptions).waitForCompletion
    inputTopics.map(topic=> sub.subscribe(topic,qos))
    sub
  }

  /** True if publisher and subsriber are connected to the MQTT broker */
  def mqttConnected: Boolean = if (
    ((!subscriber.isEmpty && subscriber.head.isConnected) &&
    (!publisher.isEmpty && publisher.head.isConnected))
  ) {
    logger.info("Connected to MQTT broker at %s".format(uri))
    logger.info("Subscribed to: %s".format(inputTopics.mkString(", ")))
    logger.info("Publishing on: %s".format(outputTopics.mkString(", ")))
    true
  } else {
    logger.error("Could not connect to MQTT broker at %s".format(uri))
    false
  }

  /** Publish a string to all publication topics
   *  @param output string to publish
   *  @return true if the output was published to all publication topics
   */
  def publish(output: String): Boolean = publish(output.getBytes)

  /** Publish a byte array to all publication topics
   *  @param output byte array to publish
   *  @return true if the output was published to all publication topics
   */
  def publish(output: Array[Byte]): Boolean = publish(new MqttMessage(output))

  /** Publish a MQTT message to all publication topics
   *  @param output MQTT message to publish
   *  @return true if the output was published to all publication topics
   */
  def publish(output: MqttMessage): Boolean = 
    outputTopics.map(topic => publish(topic, output)).foldLeft(true)(_ && _)

  /** Publish a MQTT message to one topic
   *  @param topic Destination for MQTT message
   *  @param output MQTT message to publish
   *  @return true if the operation succeeded
   */
  def publish(topic: String, output: MqttMessage): Boolean = try {
      publisher.map(pub=>pub.publish(topic, output))
      true
    } catch {
      case t: Throwable => { 
        logger.error("Could not publish to %s".format(topic))
        false
      }
    } 

  /** Called when the connection to the server is lost.
   * @param cause The reason behind the loss of connection.
   */
  override def connectionLost(cause: Throwable): Unit = {
    logger.error("Connection to MQTT broker lost.")
  }

  /** Called when delivery for a message has been completed.
   * @param token the delivery token associated with the message.
   */
  override def deliveryComplete(token: IMqttDeliveryToken): Unit =
    logger.debug("deliveryComplete: %s" + token.getMessage)

  /** This method is called when a message arrives on the message bus
   * @param topic The message topic
   * @param mm The message
   */
  override def messageArrived(topic: String, mm: MqttMessage): Unit = try {
    messageArrived(topic, mm.toString)
  } catch {
    case t: Throwable => {
      logger.error("Problem reading message on %s".format(topic))
      logger.error(t.toString)
    }
  }

  /** This method is called when a MqttMessage is successfully read
   * @param topic name of the topic on the message was published to
   * @param message the string representation of the message
   */
  def messageArrived(topic: String, message: String): Unit
}