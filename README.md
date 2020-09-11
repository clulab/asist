# asist
Code for the DARPA ASIST program


## Running the Code
There are three potential configurations of the extraction script that can be run.

### Extractions Without Metadata
If the program is passed only a transcript generated by Zoom, it will output detected events as JSON. These JSON will have NULL values for the experimental id and trial id fields. These values can only be filled if an additional metadata file is passed as an argument. The extractions will be sent to STDOUT as JSON seperated by newline characters. The timestamps in the extraction JSONs are from the timestamps in the Zoom transcripts. Specifically, the JSON has the timestamp from the beginning of the player utterance where the event was found.

### Extractions with Metadata
If the program is passed a Zoom transcript and a metadata file, it will fill in the experimental id and trial id fields using information from the metadata file. Otherwise the output is the same as the extractions without metadata configuration.

### Testing Extraction Rules
If the program is passed a Zoom transcript, a metadata file, and an annotated transcript, it will score the performance of the extraction rules. The program will extract events and then attempt to find matching events in the gold labels. After aligning the extractions it will generate some summary statistics of the system's performance.
