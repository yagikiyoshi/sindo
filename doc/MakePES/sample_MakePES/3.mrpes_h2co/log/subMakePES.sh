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

    b3lypdir=pes_b3lyp
    ccsdtdir=pes_ccsdt
    mrpesdir=pes_mrpes

    java RunMakePES -f makePES1.xml >& makePES1.out
    mkdir $b3lypdir
    mv minfo.files *pot *dipole $b3lypdir

    sleep 30 

    java RunMakePES -f makePES2.xml >& makePES2.out
    mkdir $ccsdtdir
    mv minfo.files *pot $ccsdtdir

    mkdir $mrpesdir
    mv prop_no_1.mop     $mrpesdir
    cp $b3lypdir/*pot    $mrpesdir
    cp $b3lypdir/*dipole $mrpesdir
    cp $ccsdtdir/*pot    $mrpesdir

    exit 0
#
