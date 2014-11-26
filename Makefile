JC=javac
JFLAGS=
SRC=BrujinGraph.java
BINDIR=bin
SRCDIR=src
all:
	mkdir -p bin
	$(JC) $(JFLAGS) -d $(BINDIR) -sourcepath $(SRCDIR) $(SRC)
