#!/bin/bash

# create manifest (manifest.mf)
if [ -e manifest.mf ]; then
  rm manifest.mf
fi
echo "Main-Class: JSindo" >> manifest.mf

mkdir tmp
cd tmp

jar -xvf ../../../external_jars/jogamp-v2.4.0-rc-20210111/jogamp-fat.jar
cp       ../../../external_jars/jogamp-v2.4.0-rc-20210111/jogamp-all-platforms/*txt .

jar -xvf ../../../external_jars/jogamp-java3d-1.6.2/j3dcore.jar
jar -xvf ../../../external_jars/jogamp-java3d-1.6.2/j3dutils.jar
jar -xvf ../../../external_jars/jogamp-java3d-1.6.2/vecmath.jar
rm COPYRIGHT.txt
rm LICENSE.txt

jar -xvf ../../../external_jars/Jama-1.0.3/Jama-1.0.3.jar
cp       ../../../external_jars/Jama-1.0.3.txt .

jar -xvf ../jar/JSindo-4.0.beta.jar
cp       ../../LICENSE   ./sindo.LICENSE.txt
cp       ../../README.md ./sindo.REAMD.md

jar -cvfm JSindo-4.0.beta_fat.jar ../manifest.mf *
mv JSindo-4.0.beta_fat.jar ../jar

cd ../
rm manifest.mf
rm -r tmp
