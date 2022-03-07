#!/bin/bash

version=$(cat ../VERSION)
xmlfile=build_JSindo.xml
external_jars=external_jars
external_license=external_license

# download external jars
#
echo ""
echo "-----------------------------------------------------"
echo "1. Download external jar files"
if [ ! -e ${external_jars} ]; then
  mkdir ${external_jars}
  cd ${external_jars}/
  # JogAmp
  #
  wget https://jogamp.org/deployment/v2.4.0-rc-20210111/archive/jogamp-all-platforms.7z
  unar jogamp-all-platforms.7z

  # Java3D
  #
  wget https://jogamp.org/deployment/java3d/1.6.2/j3dcore.jar
  wget https://jogamp.org/deployment/java3d/1.6.2/j3dutils.jar
  wget https://jogamp.org/deployment/java3d/1.6.2/vecmath.jar

  # JAMA
  wget https://math.nist.gov/javanumerics/jama/Jama-1.0.3.jar

  cd ..

else
  echo ""
  echo "  - External jars already exist in ${external_jars}. "

fi

# create build file for ant
#
echo ""
echo "-----------------------------------------------------"
echo "2. Creating JSindo"

if [ -e ${xmlfile} ]; then
  rm ${xmlfile}
fi

sed -e "s#VERSION#${version}#"  build/build_JSindo.tmp  > ${xmlfile}

echo ""
echo "  - Successfully created ${xmlfile}. "

# now build JSINDO
#
echo ""
echo "  - Now build JSindo."
echo "    ant -f ${xmlfile}"

ant -f ${xmlfile}

# create fat-jar
#
echo ""
echo "-----------------------------------------------------"
echo "3. Create fat jar"

if [ -e manifest.mf ]; then
  rm manifest.mf
fi
echo "Main-Class: JSindo" >> manifest.mf

TMP=$(mktemp -d tmp.XXXX)
cd $TMP

cp      ../${external_jars}/jogamp-all-platforms/gluegen.LICENSE.txt .
jar -xf ../${external_jars}/jogamp-all-platforms/jar/gluegen-rt.jar
jar -xf ../${external_jars}/jogamp-all-platforms/jar/gluegen.jar
jar -xf ../${external_jars}/jogamp-all-platforms/jar/gluegen-rt-natives-linux-aarch64.jar
jar -xf ../${external_jars}/jogamp-all-platforms/jar/gluegen-rt-natives-linux-amd64.jar
jar -xf ../${external_jars}/jogamp-all-platforms/jar/gluegen-rt-natives-linux-armv6hf.jar
jar -xf ../${external_jars}/jogamp-all-platforms/jar/gluegen-rt-natives-linux-i586.jar
jar -xf ../${external_jars}/jogamp-all-platforms/jar/gluegen-rt-natives-macosx-universal.jar
jar -xf ../${external_jars}/jogamp-all-platforms/jar/gluegen-rt-natives-windows-amd64.jar
jar -xf ../${external_jars}/jogamp-all-platforms/jar/gluegen-rt-natives-windows-i586.jar

cp      ../${external_jars}/jogamp-all-platforms/jogl.LICENSE.txt    .
cp      ../${external_jars}/jogamp-all-platforms/jogl.README.txt     .
jar -xf ../${external_jars}/jogamp-all-platforms/jar/jogl-all.jar
jar -xf ../${external_jars}/jogamp-all-platforms/jar/jogl-all-natives-linux-aarch64.jar
jar -xf ../${external_jars}/jogamp-all-platforms/jar/jogl-all-natives-linux-amd64.jar
jar -xf ../${external_jars}/jogamp-all-platforms/jar/jogl-all-natives-linux-armv6hf.jar
jar -xf ../${external_jars}/jogamp-all-platforms/jar/jogl-all-natives-linux-i586.jar
jar -xf ../${external_jars}/jogamp-all-platforms/jar/jogl-all-natives-macosx-universal.jar
jar -xf ../${external_jars}/jogamp-all-platforms/jar/jogl-all-natives-windows-amd64.jar
jar -xf ../${external_jars}/jogamp-all-platforms/jar/jogl-all-natives-windows-i586.jar

jar -xf ../${external_jars}/j3dcore.jar
jar -xf ../${external_jars}/j3dutils.jar
jar -xf ../${external_jars}/vecmath.jar
mv COPYRIGHT.txt java3d.COPYRIGHT.txt
mv LICENSE.txt   java3d.LICENSE.txt

cp      ../build/Jama-1.0.3.txt .
jar -xf ../${external_jars}/Jama-1.0.3.jar

jar -xf ../jar/JSindo-${version}.jar
cp ../../LICENSE   sindo.LICENSE.txt
cp ../../README.md sindo.README.md

jar -cfm JSindo-${version}_fat.jar ../manifest.mf *
mv JSindo-${version}_fat.jar ../jar

echo ""
echo "  - created jar/JSindo-${version}_fat.jar."

cd ../

# copy license files
#
echo ""
echo "-----------------------------------------------------"
echo "4. Copy license files"

if [ ! -e ${external_license} ]; then
  mkdir ${external_license}
  cp ${external_jars}/jogamp-all-platforms/gluegen.LICENSE.txt ${external_license}
  cp ${external_jars}/jogamp-all-platforms/jogl.LICENSE.txt    ${external_license}
  cp ${external_jars}/jogamp-all-platforms/jogl.README.txt     ${external_license}
  cp $TMP/java3d.*.txt                                         ${external_license}
  cp build/Jama-1.0.3.txt                                      ${external_license}

echo ""
  echo "  - License files of external programs are stored in ${external_license}."

else
  echo ""
  echo "  - License files already exist in ${external_license}."

fi

rm manifest.mf
rm -r $TMP

echo ""
echo "-----------------------------------------------------"
echo "End."
