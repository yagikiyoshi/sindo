######################################################
# Batch file for a Sindo job (SMP Parallel)          #
#
#$ -S /bin/bash
#$ -V
#$ -cwd
#$ -N makeHybrid
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

    for ihost in ${FILE[@]}; do
       echo "$ihost ppn=8 mem=4 scr=125" >> resources.info
       echo "$ihost ppn=8 mem=4 scr=125" >> resources.info
    done

    java RunMakePES -f makePES.xml >& makePES.out
    mkdir pot1
    mv prop_no_1.mop pot1
    mv *pot *diople  pot1
    rm -r minfo.files

    java RunMakePES -f makePES2.xml --input-version 2 >& makePES2.out

    exit 0
#