#!/bin/bash

export sindo_dir=/path/to/sindo
export CLASSPATH=${CLASSPATH}:$sindo_dir/JSindo/jar/JSindo-version_fat.jar
export PATH=$PATH:$sindo_dir/script:$sindo_dir/FSindo/bin
export SINDO_RSH=ssh
