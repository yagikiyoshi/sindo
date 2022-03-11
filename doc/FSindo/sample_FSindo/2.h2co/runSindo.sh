#!/bin/bash

. /path/to/sindo/sindovars.sh
export POTDIR=./pes_mrpes

sindo < vqdpt2.inp > vqdpt2.out 2>&1
sindo < vci.inp    > vci.out    2>&1
