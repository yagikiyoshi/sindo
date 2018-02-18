%NprocShared=8
%chk=h2o-freq.chk
%mem=1000MB
#p B3LYP/cc-pVDZ opt freq=raman

H2O MP2(Full)/cc-pVTZ (Freq)

0 1
 O           0.0000000000   0.0000000000   0.6803027768
 H          -0.9358605394   0.0000000000  -0.6109821947
 H           0.9358605394   0.0000000000  -0.6109821947

