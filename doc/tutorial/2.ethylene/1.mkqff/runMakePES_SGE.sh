#!/bin/bash
# Force bash
#$ -S /bin/bash
#$ -V
#$ -cwd
#$ -N mkqff
#$ -j y
#$ -pe ompi 12
#$ -q kushana.q

#----------------------------------------------------------
trap 'echo "run a clean-up program here..."' 1 2 3 15

if [ -e resources.info ]; then
   rm resources.info
fi
touch resources.info

FILE=$(cat $PE_HOSTFILE)

num=0
for ihost in ${FILE[@]}; do
   i=`expr "$num" % 4`
   if [ "$i" == 0 ]; then
      echo "$ihost ppn=4 mem=1 scr=50" >> resources.info
      echo "$ihost ppn=4 mem=1 scr=50" >> resources.info
      echo "$ihost ppn=4 mem=1 scr=50" >> resources.info
   fi
   num=`expr $num + 1`
done

java RunMakePES > makePES.out

