#!/bin/bash

. /path/to/sindo/sindovars.sh

sindo < vscf.inp   > vscf.out   2>&1
sindo < vmp2.inp   > vmp2.out   2>&1
sindo < vqdpt2.inp > vqdpt2.out 2>&1
sindo < vci.inp    > vci.out    2>&1

