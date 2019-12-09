#!/bin/bash

mop1=$1
mop2=$2

echo "Compare mopfile"
echo ""
echo "$mop1                   $mop2"
echo "------------------------------------------------------------"
tmp1=a1
tmp2=b1
ttmp1=aa1
ttmp2=bb1

grep -v -e "SCALING" -e "DALTON" $mop1 > $tmp1
grep -v -e "SCALING" -e "DALTON" $mop2 > $tmp2
cat   $tmp1 |awk '{print $1}' > $ttmp1
paste $ttmp1 $tmp2 > $ttmp2

cat $ttmp2 |awk 'BEGIN {ne = 0} { if ($1/$2 > 1.01 || $1/$2 < 0.99) print $0 ne++} END { print "Number of error is " ne}'

echo "------------------------------------------------------------"
rm $tmp1 $tmp2 $ttmp1 $ttmp2
