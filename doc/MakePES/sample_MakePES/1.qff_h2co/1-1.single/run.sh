#!/bin/bash

. /path/to/sindo/sindovars.sh
unset SINDO_RSH
java RunMakePES -f makePES.xml >& makePES.out

