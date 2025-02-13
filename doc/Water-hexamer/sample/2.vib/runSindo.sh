
#    SINDO=/path/to/FSindo/bin/sindo
    SINDO=/Users/kyagi/Work/devel/sindo/sindo.master/FSindo/bin/sindo

    # VQDPT2 and VCI based on normal coordinates
    ${SINDO} < ncvqdpt2.inp > ncvqdpt2.out 2>&1
    ${SINDO} < ncvci.inp  > ncvci.out    2>&1

    # VQDPT2 and VCI based on optimized coordinates
    ${SINDO} < ocvscf.inp > ocvscf.out   2>&1
    ${SINDO} < ocvqdpt2.inp > ocvqdpt2.out 2>&1
    ${SINDO} < ocvci.inp  > ocvci.out    2>&1

