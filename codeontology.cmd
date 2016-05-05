set CODEONTOLOGY_LINE_ARGS=%*

java -cp target/codeontology-1.0-SNAPSHOT-jar-with-dependencies.jar -d64 -Xms2G -Xmx4G org.codeontology.CodeOntology %CODEONTOLOGY_LINE_ARGS%