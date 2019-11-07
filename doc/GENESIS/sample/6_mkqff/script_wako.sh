######################################################
# Batch file for running Genesis                     #
#                                    (SMP Parallel)  #
#   Written by K.Yagi                                #
#   Last modified  :   2015/08/31                    #
#
#$ -S /bin/bash
#$ -V
#$ -cwd
#$ -N qmmm_qff
#$ -j y
#$ -pe ompi 32
#$ -q y2.q
######################################################


# Run MakePES in generic mode and generate makeQFF.xyz
#
java RunMakePES -f makePES.xml >& makePES.out1

# Run GENESIS
 
# Path to atdyn
GENESIS=/home/yagi/devel/genesis/genesis.gat_beluga/bin/atdyn

# QM_NUM_THREADS is for QM jobs, while OMP_NUM_THREADS is for atdyn.
# They are usually set to be equal.
#
export  QM_NUM_THREADS=8
export OMP_NUM_THREADS=8

# Open MPI
#
mpirun -np 4 --map-by node:pe=${QM_NUM_THREADS} $GENESIS qmmm_mkqff.inp >& qmmm_mkqff.out

# Intel MPI
#
#mpirun -np 4 -ppn 2 $GENESIS qmmm_vib.inp >& qmmm_vib.out

# Run MakePES again to generate prop_no_1.mop
#
java RunMakePES -f makePES.xml >& makePES.out2

exit 0

