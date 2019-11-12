######################################################
# Batch file for a Sindo job (SMP Parallel)          #
#   Written by K.Yagi                                #
#   Last modified  :   2011/06/21                    #
#
#$ -S /bin/bash
#$ -V
#$ -cwd
#$ -N mrpes
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

    java RunMakePES -f makePES1.xml >& makePES1.out
    mkdir b3lyp_pes
    mv minfo.files *pot *dipole b3lyp_pes

    java RunMakePES -f makePES2.xml >& makePES2.out
    mkdir ccsdt_pes
    mv minfo.files *pot ccsdt_pes

    mkdir mrpes
    mv prop_no_1.mop     mrpes
    cp b3lyp_pes/*pot    mrpes
    cp b3lyp_pes/*dipole mrpes
    cp ccsdt_pes/*pot    mrpes

    exit 0
#
