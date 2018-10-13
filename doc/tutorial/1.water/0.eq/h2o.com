%NprocShared=4
%chk=h2o.chk
%mem=1000MB
#p B3LYP/cc-pVDZ opt freq

H2O B3LYP/cc-pVDZ

0 1
 O           0.00   0.00   0.00
 H          -1.00   0.00  -0.60
 H           1.00   0.00  -0.60

