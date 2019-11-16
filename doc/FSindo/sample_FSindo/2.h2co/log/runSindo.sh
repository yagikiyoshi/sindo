    SINDO=/Users/kyagi/Work/devel/sindo/sindo.master/FSindo/bin/sindo
    export POTDIR=./pes_mrpes

    ${SINDO} < vqdpt2.inp > vqdpt2.out 2>&1
    ${SINDO} < vci.inp    > vci.out    2>&1
