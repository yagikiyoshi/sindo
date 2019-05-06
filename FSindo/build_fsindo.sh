#!/bin/bash

Version=$(cat ../VERSION)
TarFile=FSindo-${Version}.tar.gz

if [ -e $TarFile ]; then
  rm $TarFile
fi

cd ..
tar -zcvf ${TarFile} FSindo/configure FSindo/config FSindo/src/*.f90 FSindo/src/Makefile
mv ${TarFile} FSindo
cd FSindo

