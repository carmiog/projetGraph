#!/bin/bash
BINDIR=bin
JAVAC=javac
JCOPTIONS="-d $BINDIR -sourcepath src"
MAINJAVAFILE="src/fr/antoinemarendet/graphs/BrujinGraph.java"
mkdir -p $BINDIR
$JAVAC $JCOPTIONS $MAINJAVAFILE
