#!/bin/bash

mvn install:install-file -Dfile=./src/libs/jFuzzyLogic_1_2_1.jar -DgroupId=net.sourceforge.jFuzzyLogic -DartifactId=jFuzzyLogic -Dversion=1.2.1 -Dpackaging=jar
