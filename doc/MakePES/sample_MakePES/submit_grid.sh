#$ -S /bin/bash
#$ -V
#$ -cwd
#$ -N testgrid
#$ -j y
#$ -pe ompi 32
#$ -q y4.q

. sindovars.sh

gridlog=$(pwd)/test_grid.log
if [ -e $gridlog ]; then
  rm $gridlog
fi
touch $gridlog


cd 2.grid_h2co/

  if [ -e resources.info ]; then
   rm resources.info
  fi
  touch resources.info

  FILE=$(cat $PE_HOSTFILE | awk '{print $1}')

  for ihost in ${FILE[@]}; do
     echo "$ihost" >> resources.info
     echo "$ihost" >> resources.info
  done

  echo "----------------" >> $gridlog
  echo "Enter 2-1.1MR"    >> $gridlog
  echo "----------------" >> $gridlog
  cd 2-1.1MR
  
    cp ../resources.info ./ 
    java -cp "$sindo_jar/*" RunMakePES -f makePES.xml >& makePES.out
    for i in `ls *.pot`; do
      compare_pot.sh $i log/$i >> $gridlog
    done

  cd ..

  echo "----------------" >> $gridlog
  echo "Enter 2-2.2MR"    >> $gridlog
  echo "----------------" >> $gridlog
  cd 2-2.2MR
  
    cp ../resources.info ./
    java -cp "$sindo_jar/*" RunMakePES -f makePES.xml >& makePES.out
    for i in `ls q*q*.pot`; do
      compare_pot.sh $i log/$i >> $gridlog
    done

  cd ..
  
  echo "----------------" >> $gridlog
  echo "Enter 2-3.3MR"    >> $gridlog
  echo "----------------" >> $gridlog
  cd 2-3.3MR
    cp ../resources.info ./ 
    java -cp "$sindo_jar/*" RunMakePES -f makePES.xml >& makePES.out
    for i in `ls q*q*q*.pot`; do
      compare_pot.sh $i log/$i >> $gridlog
    done
  cd ..
  
  echo "----------------"       >> $gridlog
  echo "Enter 2-4.1MR_generic"  >> $gridlog
  echo "----------------"       >> $gridlog
  cd 2-4.1MR_generic/

    ./run.sh

    echo "Compare xyz files"      >> $gridlog
    diff makeGrid.xyz log1_genxyz >> $gridlog

    cp log2_genpot/makeGrid.dat .
    ./run.sh
    for i in `ls q*.pot`; do
      compare_pot.sh $i log2_genpot/$i >> $gridlog
    done

  cd ..
  
cd ..
  
  
