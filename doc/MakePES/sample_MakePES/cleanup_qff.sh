#!/bin/bash

cd 1.qff_h2co/
  cd 1-1.single/
  rm -r makePES.out prop_no_1.mop minfo.files
  cd ..

  cd 1-2.parallel
  rm -r makePES.out prop_no_1.mop minfo.files
  cd ..

  cd 1-3.dryrun
  rm -r makePES.out prop_no_1.mop minfo.files
  cp log1_dryrun_true/makePES.xml .
  cd ..

  cd 1-4.generic
  rm -r makePES.out prop_no_1.mop minfo.files makeQFF.xyz*
  cd ..
cd ..
