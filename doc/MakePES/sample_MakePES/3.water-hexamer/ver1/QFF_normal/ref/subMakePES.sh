######################################################
# Batch file for a Sindo job (SMP Parallel)          #
#
#$ -S /bin/bash
#$ -V
#$ -cwd
#$ -N makeQFF1
#$ -j y
#$ -pe ompi 32
#$ -q y3.q
######################################################

    if [ -e resources.info ]; then
       rm resources.info
    fi
    touch resources.info

    FILE=$(cat $PE_HOSTFILE | awk '{print $1}')
    #cat $PE_HOSTFILE

    for ihost in ${FILE[@]}; do
       echo "$ihost ppn=8 mem=3 scr=240" >> resources.info
       echo "$ihost ppn=8 mem=3 scr=240" >> resources.info
    done

    java RunMakePES >& makePES.out

    exit 0
#
