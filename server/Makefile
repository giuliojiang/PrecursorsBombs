SOURCE_DIR	:= src
OUTPUT_DIR	:= bin 

# Tools

FIND	:= find
RM	:= rm -rf
MKDIR	:= mkdir -p
JAVA	:= java
JAVAC	:= javac

JFLAGS	:= -sourcepath $(SOURCE_DIR) -d $(OUTPUT_DIR)

# the make rules

all: rules

# runs the antlr build script then attempts to compile all .java files within src
rules:
	$(FIND) $(SOURCE_DIR) -name '*.java' > $@
	- $(MKDIR) $(OUTPUT_DIR)
	$(JAVAC) $(CLASSPATHS) $(JFLAGS) @$@
	- rm rules

clean: cleanbin
	- rm rules

cleanbin:
	rm -rf $(OUTPUT_DIR)

run: rules
	./start_server.sh

test: clean rules
	java -cp bin:jar/lib/ext/mysql-connector.jar net.precursorsbombs.testsuite.RunTests

.PHONY: clean
