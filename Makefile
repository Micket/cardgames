all:
	javac -cp qtjambi452/qtjambi-4.5.2_01.jar */*java

run:
	java -cp qtjambi452/qtjambi-4.5.2_01.jar:qtjambi452/qtjambi-linux64-gcc-4.5.2_01.jar:libs/vecmath.jar:. clientQT.ClientQT

client:
	java -cp qtjambi452/qtjambi-4.5.2_01.jar:qtjambi452/qtjambi-linux64-gcc-4.5.2_01.jar:libs/vecmath.jar:. clientQT.ClientQT --connect localhost

rs:
	java -cp qtjambi452/qtjambi-4.5.2_01.jar:qtjambi452/qtjambi-linux64-gcc-4.5.2_01.jar:libs/vecmath.jar:. server.ServerMain

gitaddall:
	git add \
	*/*.java 

loc:
	wc -l */*.java

todo:
	grep -r TODO *
