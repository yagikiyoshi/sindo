
#    SINDO=/path/to/FSindo/bin/sindo
    SINDO=/Users/kyagi/Work/devel/sindo/sindo.master/FSindo/bin/sindo

    ${SINDO} < ocvscf.inp > ocvscf.out   2>&1
    ${SINDO} < ocvqdpt2.inp > ocvqdpt2.out 2>&1
    ${SINDO} < ocvci.inp  > ocvci.out    2>&1

