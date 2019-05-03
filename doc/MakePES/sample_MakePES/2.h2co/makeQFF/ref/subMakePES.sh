######################################################
# Batch file for a Sindo job (SMP Parallel)          #
#
#$ -S /bin/bash
#$ -V
#$ -cwd
#$ -N makeQFF
#$ -j y
#$ -pe ompi 32
#$ -q y4.q
######################################################

    if [ -e resources.info ]; then
       rm resources.info
    fi
    touch resources.info

    FILE=$(cat $PE_HOSTFILE | awk '{print $1}')
    #cat $PE_HOSTFILE

    for ihost in ${FILE[@]}; do
       echo "$ihost" >> resources.info
       echo "$ihost" >> resources.info
    done

    java RunMakePES -f makePES2.xml --input-version 2 >& makePES2.out
    mkdir pot2
    mv prop_no_1.mop pot2
    mv minfo.files   pot2

    exit 0
#
