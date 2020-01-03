#!/bin/bash


cd 2.grid_h2co
  rm resources.info
  cd 2-1.1MR
  rm -r makePES.out *.pot *.dipole minfo.files
  cd ..
  cd 2-2.2MR
  rm -r makePES.out q*q*.pot q*q*.dipole minfo.files
  cd ..
  cd 2-3.3MR
  rm -r makePES.out q*q*q*.pot q*q*q*.dipole q?q4.pot q?q4.dipole minfo.files
  cd ..
  cd 2-4.1MR_generic
  rm -r makePES.out *.pot *.dipole makeGrid.*
  cd ..
cd ..
