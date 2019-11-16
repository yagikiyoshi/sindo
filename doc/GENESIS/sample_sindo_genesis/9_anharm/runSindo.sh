    SINDO=/path/to/FSindo/bin/sindo
    export POTDIR=../8_mrpes

    ${SINDO} < vmp2.inp   > vmp2.out   2>&1
    ${SINDO} < vqdpt2.inp > vqdpt2.out 2>&1
    ${SINDO} < vci.inp    > vci.out    2>&1

