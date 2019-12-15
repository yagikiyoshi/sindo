#!/bin/bash

mop1=$1
mop2=$2

echo "Compare potfile"
echo ""
echo "$mop1                   $mop2"
echo "------------------------------------------------------------"
tmp1=$(mktemp a.XXXX)
tmp2=$(mktemp b.XXXX)
ttmp1=$(mktemp c.XXXX)

cat   $mop1 |awk '{if (NR > 4) print $0}' > $tmp1
cat   $mop2 |awk '{if (NR > 4) print $NF}' > $tmp2
paste $tmp1 $tmp2 > $ttmp1

cat $ttmp1 |awk 'BEGIN {ne = 1} { if ($NF != 0.0 && ($(NF-1)/$NF > 1.01 || $(NF-1)/$NF < 0.99)) print ne++ $0 } END { print "Number of error is " ne-1}'

echo "------------------------------------------------------------"
rm $tmp1 $tmp2 $ttmp1 
