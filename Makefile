all:
	javac -cp libs/qtjambi-4.5.2_01.jar:libs/qtjambi-linux64-gcc-4.5.2_01.jar:libs/vecmath.jar:. */*java

run:
	java -cp libs/qtjambi-4.5.2_01.jar:libs/qtjambi-linux64-gcc-4.5.2_01.jar:libs/vecmath.jar:. clientQT.ClientQT

client:
	java -cp libs/qtjambi-4.5.2_01.jar:libs/qtjambi-linux64-gcc-4.5.2_01.jar:libs/vecmath.jar:. clientQT.ClientQT --connect localhost

rs:
	java -cp libs/qtjambi-4.5.2_01.jar:libs/qtjambi-linux64-gcc-4.5.2_01.jar:libs/vecmath.jar:. server.ServerMain

gitaddall:
	git add \
	*/*.java 

loc:
	wc -l */*.java

todo:
	grep -r TODO *

jars:
	jar cfvm startCardgame.jar MANIFEST.STARTQTCLIENT */*.class
