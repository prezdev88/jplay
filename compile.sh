cd jplay-common
mvn clean install
cd ..
cd jplay
mvn clean install
cd ..
cd xjplay
mvn clean compile assembly:single
