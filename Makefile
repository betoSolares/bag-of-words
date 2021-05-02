.PHONY: build clean help lint
.DEFAULT_GOAL := help

build: clean pom.xml src/main/java/com/ai/App.java
	mvn package

clean:
	rm -rfv ./target
help:
	@echo "-------------------------HELP-------------------------"
	@echo "To create the jar file type make build"
	@echo "To remove build files type make clean"
	@echo "To lint the source code type make lint"
	@echo "To run the program type make run ARGS=\"your arguments\""
	@echo "To see this message again type make help or just make"
	@echo "------------------------------------------------------"

lint: pom.xml
	mvn com.coveo:fmt-maven-plugin:format

run: target/bow-1.0.jar
	java -jar target/bow-1.0.jar $(ARGS)
