#!/bin/bash

export SINDO_RSH=ssh
sindo_jar=/path/to/sindo-4.0/jar

java -cp "$sindo_jar/*" RunMakePES -f makePES1.xml >& makePES1.out
mkdir b3lyp_pes
mv minfo.files *pot *dipole b3lyp_pes

java -cp "$sindo_jar/*" RunMakePES -f makePES2.xml >& makePES2.out
mkdir ccsdt_pes
mv minfo.files *pot ccsdt_pes

mkdir mrpes
mv prop_no_1.mop     mrpes
cp b3lyp_pes/*pot    mrpes
cp b3lyp_pes/*dipole mrpes
cp ccsdt_pes/*pot    mrpes

exit 0
#
