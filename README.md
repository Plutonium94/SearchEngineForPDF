# PDF Search Engine

This Java application **indexes** **PDF documents** in the local file system, and 
**searches** term. It uses an **ElasticSearch** search engine/database.

## Technologies Used
Java 8, Swing, [Apache PDFBox](https://pdfbox.apache.org/), ElasticSearch and
[ElasticSearch High Level Java REST Client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.5/java-rest-high.html).

## Installation

1. Download & unzip [ElasticSearch 6.5.4](https://www.elastic.co/fr/downloads/past-releases/elasticsearch-6-5-4).
2. Launch the elasticsearch script/executable from the _bin_ folder
3. Launch the application using `mvn compile exec:java`

### Execution Modes

* `mvn exec:java` : index and search
* `mvn exec:java -Dexec.args="INDEX"` : index only
* `mvn exec:java -Dexec.args="SEARCH"` : search only
* `mvn exec:java -Dexec.args="BOTH"` : index and search