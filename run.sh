#!/bin/bash
JARPATH="./target/PSI_jFuzzyLogic-1.0.1-jar-with-dependencies.jar"

CONTROLLER_PATH="./FCL/controller.fcl"


if [ "$1" = "-n" ]; then
  java -jar $JARPATH $CONTROLLER_PATH;
else
  mvn clean package
  java -jar $JARPATH $CONTROLLER_PATH;
fi