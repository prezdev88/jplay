cd jlog
mvn clean install
cd ..
cd jplay
mvn clean install
cd ..
cd xjplay
mvn clean compile assembly:single 
java -jar target/xjplay-1.0-SNAPSHOT-jar-with-dependencies.jar
