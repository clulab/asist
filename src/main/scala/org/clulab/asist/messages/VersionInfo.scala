package org.clulab.asist.messages

import org.clulab.asist.agents.DialogAgent


/**
 *  Authors:  Joseph Astier, Adarsh Pyarelal, Rebecca Sharp
 *
 *  Updated:  2021 June
 *
 *  Testbed version info, based on:
 *
 *  https://gitlab.asist.aptima.com/asist/testbed/-/blob/develop/MessageSpecs/Agent/versioninfo/agent_versioninfo.md
 *
 *  This message is generated by the Dialog Agent whenever there is a trial 
 *  start message published on the message bus
 */

/** Configuration settings */
case class VersionInfoDataConfig(
  name: String = "Not Set",
  value: String = "Not Set"
)

/** channel on the Message Bus */
case class VersionInfoDataMessageChannel(
  topic: String = "Not Set",
  message_type: String = "Not Set",
  sub_type: String = "Not Set"
)

/** Part of the Info class */
case class VersionInfoData(
  agent_name: String = "Not Set", // "tomcat_textAnalyzer"
  owner: String = "Not Set", // "University of Arizona"
  version: String = "Not Set", // "2.0.0"
  source: Seq[String] = List(), // "https://gitlab.asist.aptima.com:5050/asist/testbed/uaz_dialog_agent:2.0.0"
  dependencies: Seq[String] = List(),
  config: Seq[VersionInfoDataConfig] = List(),
  publishes: Seq[VersionInfoDataMessageChannel] = List(),
  subscribes: Seq[VersionInfoDataMessageChannel] = List()
)

/** Contains the full analysis data of one chat message */
case class VersionInfo (
  header: CommonHeader,
  msg: CommonMsg,
  data: VersionInfoData
) 

case class VersionInfoMetadata(
  topic: String,
  header: CommonHeader,
  msg: CommonMsg,
  data: VersionInfoData
) 

object VersionInfoMetadata {
  def apply(da: DialogAgent, timestamp: String): VersionInfoMetadata = {
    val vi = VersionInfo(da, timestamp)
    VersionInfoMetadata(
      topic = da.topicPubVersionInfo,
      header = vi.header,
      msg = vi.msg,
      data = vi.data
    )
  }
}


// Return a VersionInfo populated with the current DialogAgent testbed configuration
object VersionInfo {
  def apply(da: DialogAgent, timestamp: String): VersionInfo = {
    VersionInfo(
      da.commonHeader(timestamp),
      da.commonMsg(timestamp),
      VersionInfoData(
        agent_name = "tomcat_textAnalyzer",
        owner = "University of Arizona",
        version = da.dialogAgentVersion,
        source = List(
          "https://gitlab.asist.aptima.com:5050/asist/testbed/uaz_dialog_agent:2.0.0"
        ),
        dependencies = List(),
        config = List(),
        publishes = List(
          VersionInfoDataMessageChannel(
            topic = da.topicPubDialogAgent,
            message_type = da.dialogAgentMessageType,
            sub_type = da.dialogAgentSubType
          )
          // should we include the trial version info publication channel?
        ),
        subscribes = List(
          VersionInfoDataMessageChannel(
            topic = da.topicSubTrial,
            message_type = "agent/versioninfo",
            sub_type = "start"
          ),
          VersionInfoDataMessageChannel(
            topic = da.topicSubChat,
            message_type = "chat",
            sub_type = "Event:Chat"
          ),
          VersionInfoDataMessageChannel(
            topic = da.topicSubUazAsr,
            message_type = "observation",
            sub_type = "asr"
          ),
          VersionInfoDataMessageChannel(
            topic = da.topicSubAptimaAsr,
            message_type = "observation",
            sub_type = "asr"
          )
          // should we incude the trial version info subscription channel?
        )
      )
    )
  }
}

