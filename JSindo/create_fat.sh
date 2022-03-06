#!/bin/bash

# create manifest (manifest.mf)
if [ -e manifest.mf ]; then
  rm manifest.mf
fi
echo "Main-Class: JSindo" >> manifest.mf

# create fat-jar
mkdir tmp
cd tmp

cp       ../external_jars/jogamp-all-platforms/gluegen.LICENSE.txt .
jar -xvf ../external_jars/jogamp-all-platforms/jar/gluegen-rt.jar
jar -xvf ../external_jars/jogamp-all-platforms/jar/gluegen.jar
jar -xvf ../external_jars/jogamp-all-platforms/jar/gluegen-rt-natives-linux-aarch64.jar
jar -xvf ../external_jars/jogamp-all-platforms/jar/gluegen-rt-natives-linux-amd64.jar
jar -xvf ../external_jars/jogamp-all-platforms/jar/gluegen-rt-natives-linux-armv6hf.jar
jar -xvf ../external_jars/jogamp-all-platforms/jar/gluegen-rt-natives-linux-i586.jar
jar -xvf ../external_jars/jogamp-all-platforms/jar/gluegen-rt-natives-macosx-universal.jar
jar -xvf ../external_jars/jogamp-all-platforms/jar/gluegen-rt-natives-windows-amd64.jar
jar -xvf ../external_jars/jogamp-all-platforms/jar/gluegen-rt-natives-windows-i586.jar

cp       ../external_jars/jogamp-all-platforms/jogl.LICENSE.txt .
cp       ../external_jars/jogamp-all-platforms/jogl.README.txt  .
jar -xvf ../external_jars/jogamp-all-platforms/jar/jogl-all.jar
jar -xvf ../external_jars/jogamp-all-platforms/jar/jogl-all-natives-linux-aarch64.jar
jar -xvf ../external_jars/jogamp-all-platforms/jar/jogl-all-natives-linux-amd64.jar
jar -xvf ../external_jars/jogamp-all-platforms/jar/jogl-all-natives-linux-armv6hf.jar
jar -xvf ../external_jars/jogamp-all-platforms/jar/jogl-all-natives-linux-i586.jar
jar -xvf ../external_jars/jogamp-all-platforms/jar/jogl-all-natives-macosx-universal.jar
jar -xvf ../external_jars/jogamp-all-platforms/jar/jogl-all-natives-windows-amd64.jar
jar -xvf ../external_jars/jogamp-all-platforms/jar/jogl-all-natives-windows-i586.jar

jar -xvf ../external_jars/j3dcore.jar
jar -xvf ../external_jars/j3dutils.jar
jar -xvf ../external_jars/vecmath.jar
mv COPYRIGHT.txt java3d.COPYRIGHT.txt
mv LICENSE.txt   java3d.LICENSE.txt

jar -xvf ../external_jars/Jama-1.0.3.jar
cp       ../external_jars/Jama-1.0.3.txt .

jar -xvf ../jar/JSindo-4.0.beta.jar
cp       ../../LICENSE   ./sindo.LICENSE.txt
cp       ../../README.md ./sindo.REAMD.md

jar -cvfm JSindo-4.0.beta_fat.jar ../manifest.mf *
mv JSindo-4.0.beta_fat.jar ../jar

cd ../
rm manifest.mf
rm -r tmp
