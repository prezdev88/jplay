#!/bin/bash
mvn clean
mvn package
cd xjplay
mvn exec:java