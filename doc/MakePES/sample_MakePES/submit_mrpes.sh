#$ -S /bin/bash
#$ -V
#$ -cwd
#$ -N testmrpes
#$ -j y
#$ -pe ompi 32
#$ -q y4.q

. sindovars.sh

mrpeslog=$(pwd)/test_mrpes.log
if [ -e $mrpeslog ]; then
  rm $mrpeslog
fi
touch $mrpeslog

cd 3.mrpes_h2co/
  if [ -e resources.info ]; then
   rm resources.info
  fi
  touch resources.info

  FILE=$(cat $PE_HOSTFILE | awk '{print $1}')

  for ihost in ${FILE[@]}; do
     echo "$ihost" >> resources.info
     echo "$ihost" >> resources.info
  done

  ./run.sh

  echo "Compare mrpes"      >> $mrpeslog
  compare_mop.sh pes_mrpes/prop_no_1.mop log/pes_mrpes/prop_no_1.mop >> $mrpeslog
  for i in `ls pes_mrpes/*.pot`; do
    compare_pot.sh $i log/$i >> $mrpeslog
  done

cd ..
