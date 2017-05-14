# CodeOntology

### RDF-ization of source code
CodeOntology is an extraction tool that parses Java source code to generate RDF triples. It supports both maven and gradle projects. For more details see [codeontology.org](http://codeontology.org/).

### Set up
First, check dependencies in the Dockerfile.

To set up codeontology, you have to clone the repository and build the tool:
```bash
$ git clone https://github.com/codeontology/parser
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
Let's use the tool to extract RDF triples from the OpenJDK 8 source code.

First, you need the OpenJDK 8 source code. It is available on github:
```bash
$ git clone https://github.com/codeontology/openjdk8.git
```

Now, you have to install OpenJDK 8:
```bash
$ sudo dpkg -iR openjdk8/amd64
```

The above command should install OpenJDK 8. If you get dependecy errors, just type:
```bash
$ sudo apt-get -f install
```

Set the newly installed version of Java as the default version:
```bash
$ sudo update-java-alternatives -s java-1.8.0-openjdk-amd64
```

If you get the following error, just ignore it:
```bash
update-java-alternatives: plugin alternative does not exist: /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/amd64/IcedTeaPlugin.so
```

To verify that everything has worked, check that your java version is correct:
```bash
$ java -version
openjdk version "1.8.0_121"
OpenJDK Runtime Environment (build 1.8.0_121-8u121-b13-4-b13)
OpenJDK 64-Bit Server VM (build 25.121-b13, mixed mode)
```

Finally, you are ready to serialize the OpenJDK source code into RDF triples. Just type:
```bash
$ ./codeontology -i openjdk8/ -o openjdk8.nt
```

This command  will run the tool on the openjdk8 directory and save the extracted RDF triples to the file `openjdk8.nt`.
Be aware that this may take 2 hour and a half!

To annotate source code comments, see [CommentLinker](https://github.com/codeontology/commentlinker).

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
