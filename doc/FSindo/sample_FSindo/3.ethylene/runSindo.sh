#!/bin/bash

. /path/to/sindo/sindovars.sh
export POTDIR=./

sindo < ocvscf.inp   > ocvscf.out   2>&1
sindo < ncvqdpt2.inp > ncvqdpt2.out 2>&1
sindo < ocvqdpt2.inp > ocvqdpt2.out 2>&1
sindo < ncvci.inp    > ncvci.out    2>&1
sindo < ocvci.inp    > ocvci.out    2>&1

