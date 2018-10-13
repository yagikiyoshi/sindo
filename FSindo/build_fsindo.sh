#!/bin/bash

Version=4.0.beta
TarFile=FSindo-${Version}.tar.gz

cd ..
tar -zcvf ${TarFile} FSindo/configure FSindo/config FSindo/src
mv ${TarFile} FSindo
cd FSindo

