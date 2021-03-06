all:
	javac -cp libs/qtjambi-4.5.2_01.jar:libs/qtjambi-linux64-gcc-4.5.2_01.jar:libs/vecmath.jar:. */*java

runmac:
	java -XstartOnFirstThread -d32 -cp libs/mac/qtjambi-4.5.2_01.jar:libs/mac/qtjambi-macosx-gcc-4.5.2_01.jar:libs/vecmath.jar:. clientQT.ClientQT

run:
	java -cp libs/qtjambi-4.5.2_01.jar:libs/qtjambi-linux64-gcc-4.5.2_01.jar:libs/vecmath.jar:. clientQT.ClientQT

client:
	java -cp libs/linux/qtjambi-4.5.2_01.jar:libs/linux/qtjambi-linux64-gcc-4.5.2_01.jar:libs/vecmath.jar:. clientQT.ClientQT --connect localhost

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
	ls games/*.java > games/gameslist.txt
	jar cfvm cardgame.jar ports/MANIFEST.STARTQTCLIENT */*.class games/gameslist.txt
	rm games/gameslist.txt 

macport:
	ls games/*.java > games/gameslist.txt
	jar cfv ports/CardGame.app/cardgame.jar */*.class games/gameslist.txt
	cp libs/*jar libs/mac/*jar ports/CardGame.app/
	rm games/gameslist.txt 
