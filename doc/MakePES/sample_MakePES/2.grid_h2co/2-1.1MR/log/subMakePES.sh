######################################################
# Batch file for a Sindo job (SMP Parallel)          #
#   Written by K.Yagi                                #
#   Last modified  :   2011/06/21                    #
#
#$ -S /bin/bash
#$ -V
#$ -cwd
#$ -N mkpes1
#$ -j y
#$ -pe ompi 64
#$ -q y2.q
######################################################

    if [ -e resources.info ]; then
       rm resources.info
    fi
    touch resources.info

    FILE=$(cat $PE_HOSTFILE | awk '{print $1}')
    #cat $PE_HOSTFILE

    num=0
    for ihost in ${FILE[@]}; do
       echo "$ihost" >> resources.info
       echo "$ihost" >> resources.info
    done

    . ../../sindovars.sh
    java -cp "$sindo_jar/*" RunMakePES -f makePES.xml >& makePES.out

    exit 0
#
