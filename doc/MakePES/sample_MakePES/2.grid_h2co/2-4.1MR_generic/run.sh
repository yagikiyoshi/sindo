#!/bin/bash

sindo_jar=/path/to/sindo-4.0/jar
java -cp "$sindo_jar/*" RunMakePES -f makePES.xml >& makePES.out

