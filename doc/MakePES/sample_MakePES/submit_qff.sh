#$ -S /bin/bash
#$ -V
#$ -cwd
#$ -N testqff
#$ -j y
#$ -pe ompi 16
#$ -q y2.q

. sindovars.sh

qfflog=$(pwd)/test_qff.log
if [ -e $qfflog ]; then
  rm $qfflog
fi
touch $qfflog

cd 1.qff_h2co/
  echo "----------------" >> $qfflog
  echo "Enter 1-1.single" >> $qfflog
  echo "----------------" >> $qfflog
  cd 1-1.single/
    ./run.sh
    compare_mop.sh prop_no_1.mop log/prop_no_1.mop >> $qfflog
  cd ..
  echo "" >> $qfflog
  echo "" >> $qfflog

  echo "------------------" >> $qfflog
  echo "Enter 1-2.parallel" >> $qfflog
  echo "------------------" >> $qfflog
  cd 1-2.parallel
    if [ -e resources.info ]; then
       rm resources.info
    fi
    touch resources.info

    FILE=$(cat $PE_HOSTFILE | awk '{print $1}')

    num=0
    for ihost in ${FILE[@]}; do
      echo "$ihost" >> resources.info
      echo "$ihost" >> resources.info
    done

    ./run.sh
    compare_mop.sh prop_no_1.mop log/prop_no_1.mop >> $qfflog
  cd ..
  echo "" >> $qfflog
  echo "" >> $qfflog

  echo "----------------" >> $qfflog
  echo "Enter 1-3.dryrun" >> $qfflog
  echo "----------------" >> $qfflog
  cd 1-3.dryrun
    cp log1_dryrun_true/makePES.xml .
    ./run.sh

    echo "Compare input files" >> $qfflog
    for i in `ls minfo.files/*.inp`; do 
      echo $i                              >> $qfflog
      diff $i log1_dryrun_true/minfo.files >> $qfflog
    done

    cp log2_dryrun_false/makePES.xml .
    cp log2_dryrun_false/minfo.files/*.minfo minfo.files
    ./run.sh

    compare_mop.sh prop_no_1.mop log2_dryrun_false/prop_no_1.mop >> $qfflog

  cd ..
  echo "" >> $qfflog
  echo "" >> $qfflog

  echo "-----------------" >> $qfflog
  echo "Enter 1-4.generic" >> $qfflog
  echo "-----------------" >> $qfflog
  cd 1-4.generic
    ./run.sh

    echo "Compare xyz files"   >> $qfflog
    diff makeQFF.xyz log1_genxyz >> $qfflog

    cp -r log2_genmop/minfo.files .
    ./run.sh

    compare_mop.sh prop_no_1.mop log2_genmop/prop_no_1.mop >> $qfflog

  cd ..
  echo "" >> $qfflog
  echo "" >> $qfflog
cd ..

