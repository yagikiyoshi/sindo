#!/bin/bash

# JogAmp
#
wget https://jogamp.org/deployment/v2.4.0-rc-20210111/archive/jogamp-all-platforms.7z
unar jogamp-all-platforms.7z

# mac osx
#brew install unar
# ubuntu
#apt-get install unar
# centos
#yum -y install unar

# Java3D
#
wget https://jogamp.org/deployment/java3d/1.6.2/j3dcore.jar
wget https://jogamp.org/deployment/java3d/1.6.2/j3dutils.jar
wget https://jogamp.org/deployment/java3d/1.6.2/vecmath.jar

# JAMA
wget https://math.nist.gov/javanumerics/jama/Jama-1.0.3.jar

