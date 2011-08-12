all:
	javac -cp qtjambi452/qtjambi-4.5.2_01.jar */*java

run:
	java -cp qtjambi452/qtjambi-4.5.2_01.jar:qtjambi452/qtjambi-linux64-gcc-4.5.2_01.jar:. clientQT.Client

rs:
	java -cp qtjambi452/qtjambi-4.5.2_01.jar:qtjambi452/qtjambi-linux64-gcc-4.5.2_01.jar:. server.ServerMain

gitaddall:
	git add \
	*/*.java 

loc:
	wc -l */*.java
