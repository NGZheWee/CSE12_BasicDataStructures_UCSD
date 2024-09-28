.PHONY:  clean new 
.SUFFIXES: .java .class
.java.class:
	javac -g $<

Driver: Base.java Driver.java MyLib.java \
	SymTab.java Heap.java Tracker.java Size.java
	javac -g Driver.java
	echo 'java Driver $$*' > Driver
	chmod ug+rx Driver

clean:
	rm -f *.class Driver
        
new:
	make clean
	make