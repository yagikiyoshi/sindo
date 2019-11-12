#!/bin/bash

sindo_dir=/path/to/sindo-4.0
sindo_jar=$sindo_dir/jar
PATH=$PATH:$sindo_dir/script

runGaussian.sh ./  h2co-b3lyp-dz.inp
java -cp "$sindo_jar/*" Fchk2Minfo h2co-b3lyp-dz

