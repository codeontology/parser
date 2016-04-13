# CodeOntology

### Extract RDF triples from Java source code
CodeOntology is an extraction tool that parses Java source code to generate RDF triples. It actually supports both maven and gradle projects. For more details see [codeontology.org](http://codeontology.org/).

### Set up
To set up the project, you have to clone this repository and build the tool:
```bash
$ git clone https://bitbucket.org/atzori/codeontology
$ cd codeontology
$ mvn package -DskipTests
```

Now, you can run the tool on any java project:
```bash
$ ./codeontology -i <input_folder> -o <output_file>
```

For a complete list of all command line options, just type:
```bash
$ ./codeontology --help
```

### Use case
Let's use the tool to extract RDF triples from the JDK source code.

First, be sure to have the latest version of java:
```bash
$ java -version
java version "1.8.0_77"
Java(TM) SE Runtime Environment (build 1.8.0_77-b03)
Java HotSpot(TM) 64-Bit Server VM (build 25.77-b03, mixed mode)
```

If you don't have it, you can install it by running the following commands:
```bash
$ sudo add-apt-repository ppa:webupd8team/java
$ sudo apt-get update
$ sudo apt-get install oracle-java8-installer
$ sudo apt-get install oracle-java8-set-default
```

Now, we need to get the JDK source code. It is available on github:
```bash
$ git clone https://github.com/jdk-mirror/openjdk8
```
You are ready to extract the triples. Just type:
```bash
./codeontology -i openjdk8 -o jdk.nt
```
This will run the tool on the openjdk8 directory and save the extracted RDF triples to the file jdk.nt.
