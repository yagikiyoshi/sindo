# Copy the QFF and 1MR-Grid data
cp ../6_mkqff/prop_no_1.mop .
cp ../7_mkgrid1/*pot ./
cp ../7_mkgrid1/*dipole ./

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
mpirun -np 4 --map-by node:pe=${QM_NUM_THREADS} $GENESIS qmmm_mrpes.inp >& qmmm_mrpes.out

# Intel MPI
#
#mpirun -np 4 -ppn 2 $GENESIS qmmm_mrpes.inp >& qmmm_mrpes.out

# Run MakePES again to generate prop_no_1.mop
#
java RunMakePES -f makePES.xml >& makePES.out2

exit 0

