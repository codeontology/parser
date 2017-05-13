
## NOTE: this Dockerfile has not been tested


######
#IMPORTANT: 
#1. http://askubuntu.com/questions/755853/how-to-install-jdk-sources
#
#	sudo apt-get install openjdk-8-source
#	# apt-get puts it under the relevant JDK location as src.zip: /usr/lib/jvm/java-8-openjdk-amd64/src.zip
#
#
#2. http://stackoverflow.com/a/8693261
#
## An exception is for many (if not all) of the com.sun.* classes and others that are only available under the SCSL or the JRL licenses - #which is available through a separate download from Oracle, after accepting one of these licenses.
#
#3. Apparentemente lo zip contiene anche classi com.sun.* ma non funziona con codeontology (crash quasi subito senza messaggio di spiegazione)
#
####

FROM ubuntu:16 # check if it works with FROM openjdk:8
# install Oracle JDK 8
RUN apt-get update && \
	apt-get install -y software-properties-common && \

RUN git clone https://github.com/codeontology/openjdk8.git
RUN dpkg -iR openjdk8/amd64
RUN apt-get -f install && \
	apt-get install -y maven gradle

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app


ONBUILD COPY . /usr/src/app
ONBUILD RUN mvn package -DskipTests

CMD [ "./codeontology" ]

# TIMES TO EXTRACT TRIPLES on JDK8
#Triples extracted successfully in 2 h 34 min 34 s 321 ms.
#real	156m21.239s

