#!/bin/bash
mvn clean
mvn package
mvn exec:java -pl xjplay