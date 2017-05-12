# CodeOntology

### RDF-ization of source code
CodeOntology is an extraction tool that parses Java source code to generate RDF triples. It actually supports both maven and gradle projects. For more details see [codeontology.org](http://codeontology.org/).

### Set up
First, check dependencies in the Dockerfile.

To set up codeontology, you have to clone the repository and build the tool:
```bash
$ git clone https://bitbucket.org/semanticweb/codeontology-parser
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

### Use cases
#### JDK
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


Now, we need the JDK source code. It is available on github:
```bash
$ git clone https://github.com/codeontology/openjdk8.git
```

You are ready to extract the triples. Just type:
```bash
$ ./codeontology -i openjdk8/ -o jdk8.nt
```

It will run the tool on the openjdk8 directory and save the extracted RDF triples to the file `jdk8.nt`.
Be aware that this may take 2 hour and a half! 

#### Maven Repository
Let's suppose you want to use the tool to extract RDF triples from a generic repository.
Here the spoon maven repository is used as an example to show how it works.

First, you have to clone the repository:

```bash
$ git clone https://github.com/INRIA/spoon
```

The repository contains tests that cause some troubles when building the abstract syntax tree. The -f switch is added to solve this issue and get rid of the tests. Moreover, the --dependencies switch is here used to parse all of the dependencies of the repository. The -v switch tells CodeOntology to verbosely print out all files processed.

```bash
$ ./codeontology -i spoon -o spoon.nt -vf --dependencies
```

Another interesting repository that can be used as example is Apache [Commons Math](https://github.com/apache/commons-math) (it will take less than 2 minutes to build the triples).

#### Jar files
CodeOntology can also process jar files:

```bash
$ ./codeontology --jar <path_to_jar>
```

In the following example, a jar file is downloaded to show how it works.

```bash
$ wget -O weka.zip http://downloads.sourceforge.net/project/weka/weka-3-8/3.8.0/weka-3-8-0.zip?r=https%3A%2F%2Fsourceforge.net%2Fprojects%2Fweka%2F&ts=1463402758&use_mirror=kent
$ unzip -j weka.zip "weka-3-8-0/weka.jar" -d .
$ ./codeontology --jar weka.jar -v
```
