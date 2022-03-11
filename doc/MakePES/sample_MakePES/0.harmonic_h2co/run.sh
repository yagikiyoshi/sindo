#!/bin/bash

. /path/to/sindo/sindovars.sh
runGaussian.sh ./  h2co-b3lyp-dz.inp
java Fchk2Minfo h2co-b3lyp-dz

