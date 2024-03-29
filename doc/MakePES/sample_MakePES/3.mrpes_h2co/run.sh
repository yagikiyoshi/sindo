#!/bin/bash

. /path/to/sindo/sindovars.sh

b3lypdir=pes_b3lyp
ccsdtdir=pes_ccsdt
mrpesdir=pes_mrpes

java RunMakePES -f makePES1.xml >& makePES1.out
mkdir $b3lypdir
mv minfo.files *pot *dipole $b3lypdir

sleep 30 

java RunMakePES -f makePES2.xml >& makePES2.out
mkdir $ccsdtdir
mv minfo.files *pot $ccsdtdir

mkdir $mrpesdir
mv prop_no_1.mop     $mrpesdir
cp $b3lypdir/*pot    $mrpesdir
cp $b3lypdir/*dipole $mrpesdir
cp $ccsdtdir/*pot    $mrpesdir

exit 0
#
