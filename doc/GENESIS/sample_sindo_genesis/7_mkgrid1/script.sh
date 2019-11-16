# Run MakePES in generic mode and generate makeQFF.xyz
#
java RunMakePES -f makePES.xml >& makePES.out1

# Run GENESIS
 
# Path to atdyn
GENESIS=/home/user/path/to/genesis/bin/atdyn

# QM_NUM_THREADS is for QM jobs, while OMP_NUM_THREADS is for atdyn.
# They are usually set to be equal.
#
export  QM_NUM_THREADS=8
export OMP_NUM_THREADS=8

# Open MPI
#
mpirun -np 4 --map-by node:pe=${QM_NUM_THREADS} $GENESIS qmmm_mkgrid.inp >& qmmm_mkgrid.out

# Intel MPI
#
#mpirun -np 4 -ppn 2 $GENESIS qmmm_mkgrid.inp >& qmmm_mkgrid.out

# Run MakePES again to generate prop_no_1.mop
#
java RunMakePES -f makePES.xml >& makePES.out2

exit 0

