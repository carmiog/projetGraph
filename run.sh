#!/bin/bash
mkdir -p bin
JAVA=java
MAINCLASS="fr.antoinemarendet.graphs.BrujinGraph"
CLASSPATH="bin"
JAVAOPTIONS="-cp $CLASSPATH"
./compile.sh
time $JAVA $JAVAOPTIONS $MAINCLASS $1
