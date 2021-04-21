# tomcat-text

This repository contains natural language text processing code for the DARPA Artificial Social Intelligence for Successful Teams (ASIST) program. See the main ToMCAT project page for more information: https://ml4ai.github.io/tomcat.


# Web App

The repo includes a webapp you can use to debug ODIN rules. When you input a piece of text, it will run the text through the system and visually display any mentions extracted by the system. The app will reload if it detects any changes to the rule files, so you can easily jump back and forth between writing rules and testing them. It also includes a syntax parse and specifies which rule generated each mention. 

To open the webapp run the following command from the top level directory:
```
sbt webapp/run
```
Then navigate to the specified port using your web browser.



# Dialog Agent

The repo also includes a Dialog Agent application that will ingest text and output extracted events of interest for a particular domain.

Sources of Dialog Agent input text are files, the MQTT message bus, and interactively from a terminal.

In all cases, a final optional argument of "-m n" can be used to control the number of taxonomy matches, where n can range from 0 to 5, and defaults to 0.



## Stdin mode

```
  sbt "runMain org.clulab.asist.RunDialogAgent stdin"
```

In this mode, the Dialog Agent will prompt the user for text, and return the extractions directly.  


An example of the agent running in stdin mode, with the number of taxonomy matches at the default setting of zero

```

Dialog Agent stdin extractor running.
Enter plaintext for extraction, [CTRL-D] to exit.

> I see a green victim!
{"label":"Self","span":"I","arguments":{},"start_offset":0,"end_offset":1,"taxonomy_matches":[]}
{"label":"Victim","span":"victim","arguments":{},"start_offset":14,"end_offset":20,"taxonomy_matches":[]}

> There is rubble here.
{"label":"Rubble","span":"rubble","arguments":{},"start_offset":9,"end_offset":15,"taxonomy_matches":[]}
{"label":"Deictic","span":"here","arguments":{},"start_offset":16,"end_offset":20,"taxonomy_matches":[]}

>

```


To exit the program, press [CTRL+D].  It will take several seconds for sbt to gracefully shut down the agent.



## File mode

To run the Dialog Agent with files, the user specifies the input and output filenames, and optionally the number of taxonomy matches to return.  

```
  sbt "runMain org.clulab.asist.RunDialogAgent file inputfile outputfile"
```

  Supported input file types are WebVtt(.vtt), and TomCAT metadata (.metadata).  A directory can be specified as input.  Directories are traversed one level deep, and only the .vtt and .metadata files are processed.  Input files are processed in alphabetical order.

  The ouput from the file(s) written to a singe output file in the order of processing.  The output is in [chat_analysis_message][1] Json format.
   

### MQTT mode

To run the Dialog Agent on the MQTT Message Bus, specify the mqtt run mode, then the host and port that the MQTT message broker is running on.

```
  sbt "runMain org.clulab.asist.RunDialogAgent mqtt hostname port"
```

To connect to a broker on localhost at the MQTT default port (1883), the agent can be started as follows:

```
    sbt "runMain org.clulab.asist.RunDialogAgent mqtt localhost 1883"
```

Once connected to the Message Bus broker, the Dialog Agent subscribes to three Message Bus topics:


  * "minecraft/chat"
  * "agent/asr/final"
  * "status/asistdataingester/userspeech"







When run on the message bus, the agent will run the same way it does with .metadata file input, writing the analysis to the 'agent/dialog' topic.


### Chat messages

Message bus topic: `observations/chat`

This topic represents text chat messages that players send to each other in Minecraft.

Message received on this topic are expected to have the following json format:

```json
{
  "header": {
    "timestamp": "2019-12-26T12:47:23.1234Z",
    "message_type": "chat",
    "version": "0.4"
  },
  "msg": {
    "experiment_id":"123e4567-e89b-12d3-a456-426655440000",
    "trial_id": "123e4567-e89b-12d3-a456-426655440000",
    "timestamp": "2019-12-26T14:05:02.1412Z",
    "source": "simulator",
    "sub_type": "chat",
    "version": "0.5",
    "replay_root_id": "123e4567-e89b-12d3-a456-426655440000",
    "replay_id": "876e4567-ab65-cfe7-b208-426305dc1234",
  },
  "data": {
    "mission_timer": "8 : 36",
    "sender": "Miner9",
    "addressees": [
      "Player123"
    ],
    "text": "I'm in room 210"
  }
}
```

### UAZ ASR messages

Message bus topic: `agent/asr`

This topic corresponds to utterances by dialogue participants that are
automatically transcribed in real-time using an automatic speech recognition
(ASR) service like Google Cloud Speech. Messages received on this topic are
expected to have the following format:

```json
{
  "data": {
    "text": "I am going to save a green victim.",
    "asr_system": "Google",
    "is_final": true,
    "participant_id": "participant_1"
  },
  "header": {
    "timestamp": "2021-01-19T23:27:58.633076Z",
    "message_type": "observation",
    "version": "0.1"
  },
  "msg": {
    "experiment_id":"123e4567-e89b-12d3-a456-426655440000",
    "trial_id": "123e4567-e89b-12d3-a456-426655440000",
    "timestamp": "2019-12-26T14:05:02.1412Z",
    "source": "tomcat_asr_agent",
    "sub_type": "asr",
    "version": "0.1",
    "replay_root_id": "123e4567-e89b-12d3-a456-426655440000",
    "replay_id": "876e4567-ab65-cfe7-b208-426305dc1234",
  }
}
```

### Aptima ASR messages

Message bus topic: `status/asistdataingester/userspeech`

This topic corresponds to utterances by dialogue participants that are
automatically transcribed in real-time using an automatic speech recognition
(ASR) service like Google Cloud Speech. Messages received on this topic are
expected to have the following format:

```json
{
  "msg": {
    "experiment_id":"123e4567-e89b-12d3-a456-426655440000",
    "trial_id": "123e4567-e89b-12d3-a456-426655440000",
    "timestamp": "2019-12-26T14:05:02.1412Z",
    "source": "tomcat_asr_agent",
    "sub_type": "asr",
    "version": "0.1",
    "replay_root_id": "123e4567-e89b-12d3-a456-426655440000",
    "replay_id": "876e4567-ab65-cfe7-b208-426305dc1234",
  },
  "data": {
    "text": "You want me to share my screen?",
    "playername": "intermonk"
  },
  "header": {
    "timestamp": "2021-01-19T23:27:58.633076Z",
    "message_type": "observation",
    "version": "0.1"
  }
}
```


### Output 

Message bus topic: `agent/dialog`

The Dialog Agent will publish its analysis to the message bus in [chat_analysis_message][1] format.

[1]: https://github.com/clulab/tomcat-text/blob/master/message_specs/chat_analysis_message.md
