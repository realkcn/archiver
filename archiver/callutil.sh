#!/bin/sh
eval mvn compile exec:java -Dexec.mainClass=\"org.kbs.archiver.util.ArchiverTools\" -Dexec.args="\"$*\"" -Ponlinedb
