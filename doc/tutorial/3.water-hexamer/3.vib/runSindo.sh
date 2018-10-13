######################################################
# Batch file for a Sindo job (SMP Parallel)          #
#   Written by K.Yagi                                #
#   Last modified  :   2011/06/21                    #
#
#$ -S /bin/bash
#$ -V
#$ -cwd
#$ -N sindo
#$ -j y
#$ -pe ompi 1
#$ -q y2.q
######################################################

#    SINDO=/path/to/FSindo/bin/sindo
#    SINDO=/Users/kyagi/Work/devel/sindo/sindo.master/FSindo/bin/sindo
    SINDO=/home/yagi/devel/sindo/sindo.master/FSindo/bin/sindo

    ${SINDO} < ncvqdpt2.inp > ncvqdpt2.out 2>&1
    ${SINDO} < ocvqdpt2.inp > ocvqdpt2.out 2>&1
    ${SINDO} < ncvci.inp  > ncvci.out    2>&1
    ${SINDO} < ocvci.inp  > ocvci.out    2>&1

