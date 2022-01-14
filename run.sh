#!/bin/bash
JARPATH="./target/PSI_jFuzzyLogic-1.0.1-jar-with-dependencies.jar"

if [ "$1" = "-nc" ]; then
  java -jar $JARPATH $2 $3 $4;
else
  mvn clean package
  java -jar $JARPATH $1 $2 $3;
fi