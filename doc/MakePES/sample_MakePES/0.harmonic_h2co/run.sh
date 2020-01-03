#!/bin/bash

. ../sindovars.sh
runGaussian.sh ./  h2co-b3lyp-dz.inp
java -cp "$sindo_jar/*" Fchk2Minfo h2co-b3lyp-dz

