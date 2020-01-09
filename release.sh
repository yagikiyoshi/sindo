#!/bin/bash

# NOTE: Don't forget to update:
#  o JSindo/src/VersionInfo.java
#  o FSindo/src/tools.f90
#

version=$(cat VERSION)
sindo=sindo-$version
doc=doc-$version

releaseDir=$(pwd)/../release/$(date +%F)
if [ -e $releaseDir ]; then
  rm -rf $releaseDir
fi
sindoDir=$releaseDir/$sindo
docDir=$releaseDir/$doc

echo "Creating a release version [ "$(date +%F)" ]"
mkdir -p $sindoDir
mkdir $sindoDir/jar
mkdir $sindoDir/FSindo
mkdir -p $docDir
mkdir $docDir/JSindo
mkdir $docDir/FSindo
mkdir $docDir/MakePES
mkdir $docDir/GENESIS
mkdir $docDir/lecture_notes

# copy license and readme
cp LICENSE $sindoDir
cp README.md $sindoDir

# JSindo
cd JSindo
ant -f build_JSindo.xml  >& /dev/null
cp jar/JSindo*.jar $sindoDir/jar
cd ..

# FSindo
cd FSindo
tarball=fsindo.tar.gz
tar --exclude *.o --exclude *.mod --exclude *.inc -zcf $tarball configure config src util
tar -zxf $tarball -C $sindoDir/FSindo
rm $tarball
cd ..

# script
cp -r script $sindoDir
rm $sindoDir/script/runGaussian.sh

# doc
cd doc/JSindo
cp *.pdf  $docDir/JSindo

tar -zcf sample_JSindo.tar.gz sample_JSindo
mv sample_JSindo.tar.gz $docDir/JSindo

cd ../FSindo
cp *.pdf  $docDir/FSindo
tar -zcf sample_FSindo.tar.gz sample_FSindo
mv sample_FSindo.tar.gz $docDir/FSindo

cd ../MakePES
cp *.pdf  $docDir/MakePES
tar -zcf sample_MakePES.tar.gz sample_MakePES
mv sample_MakePES.tar.gz $docDir/MakePES

cd ../GENESIS
cp *.pdf  $docDir/GENESIS
tar -zcf sample_sindo_genesis.tar.gz --exclude *wako.sh sample_sindo_genesis
mv sample_sindo_genesis.tar.gz $docDir/GENESIS

cd ../lecture_notes
cp *.pdf $docDir/lecture_notes


# create archive
cd $releaseDir
tar -zcvf $sindo.tar.gz $sindo
tar -zcvf $doc.tar.gz $doc

echo ""
echo "Release version made in ../release/$(date +%F)."
echo ""
