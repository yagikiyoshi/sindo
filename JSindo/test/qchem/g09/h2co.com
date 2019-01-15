%chk=test/qchem/g09/h2co.chk
%mem=24GB
%LindaWorker=diva01, diva02
%Nprocshared=8
#P MP2(FULL)/GEN 6D 10F NOSYMMETRY MAXDISK=250GB

Frequency at MP2/aug-cc-pVTZ

0   1  
C        0.000000     0.000000    -0.534156   H
O       -0.000000    -0.000000     0.678750   H
H        0.000000     0.935861    -1.112535   L
H       -0.000000    -0.935861    -1.112535   L 

C O H 0
aug-cc-pVDZ
****
1 0
S   1 1.00
.0987000000D-01   .1000000000D+01
P   1 1.00
.0857000000D-01   .1000000000D+01
****

