all:
	javac -cp qtjambi452/qtjambi-4.5.2_01.jar */*java

run:
	java -cp qtjambi452/qtjambi-4.5.2_01.jar:qtjambi452/qtjambi-linux64-gcc-4.5.2_01.jar:. clientQT.Client

gitaddall:
	git add \
	*/*.java 

