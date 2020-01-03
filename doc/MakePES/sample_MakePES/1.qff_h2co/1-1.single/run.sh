#!/bin/bash

. ../../sindovars.sh
unset SINDO_RSH
java -cp "$sindo_jar/*" RunMakePES -f makePES.xml >& makePES.out

