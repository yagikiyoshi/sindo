#!/bin/bash
# Force bash
#$ -S /bin/bash
#$ -V
#$ -cwd
#$ -N RunMakePES
#$ -j y
#$ -pe orte 16
#$ -q all.q@compute-0-7.local

#----------------------------------------------------------
trap 'echo "run a clean-up program here..."' 1 2 3 15

if [ -e resources.info ]; then
   rm resources.info
fi
touch resources.info

FILE=$(cat $PE_HOSTFILE)
#cat $PE_HOSTFILE
NODELIST=""

num=0
for ihost in ${FILE[@]}; do
   i=`expr "$num" % 4`
   if [ "$i" == 0 ]; then
      NODELIST="$NODELIST $ihost"
      echo "$ihost ppn=2 mem=6 scr=60" >> resources.info
      echo "$ihost ppn=2 mem=6 scr=60" >> resources.info
      echo "$ihost ppn=2 mem=6 scr=60" >> resources.info
      echo "$ihost ppn=2 mem=6 scr=60" >> resources.info
      echo "$ihost ppn=2 mem=6 scr=60" >> resources.info
      echo "$ihost ppn=2 mem=6 scr=60" >> resources.info
      echo "$ihost ppn=2 mem=6 scr=60" >> resources.info
      echo "$ihost ppn=2 mem=6 scr=60" >> resources.info
   fi
   num=`expr $num + 1`
done

java RunMakePES > makePES.out

