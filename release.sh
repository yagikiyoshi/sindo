#!/bin/bash


version=$(cat VERSION)_$(date +%y%m%d)
sindo=sindo-$version
doc=doc-$version

backup=.bak

releaseDir=$(pwd)/../release/$(date +%F)
if [ -e $releaseDir ]; then
  rm -rf $releaseDir
fi
sindoDir=$releaseDir/$sindo
docDir=$releaseDir/$doc

echo "Creating a release version [ "$(date +%F)" ]"
mkdir -p $sindoDir
mkdir $sindoDir/FSindo
mkdir $sindoDir/JSindo
mkdir -p $docDir
mkdir $docDir/JSindo
mkdir $docDir/FSindo
mkdir $docDir/MakePES
mkdir $docDir/GENESIS
mkdir $docDir/lecture_notes

# copy license and readme
cp -a LICENSE $sindoDir
cp -a README.md $sindoDir

# copy environment variables
sed -e "s/version/$(cat VERSION)/" sindovars.sh > $sindoDir/sindovars.sh

# JSindo
echo " - copying JSindo"
cd JSindo

VersionInfo=src/sys/VersionInfo.java
sed -i $backup -e "s/development/$(date +%m%d)/"  $VersionInfo

./build.sh >& /dev/null
cp -a jar  $sindoDir/JSindo
cp -a src  $sindoDir/JSindo
cp -a test $sindoDir/JSindo

aa=$(grep external_license= build.sh)
cp -a ${aa#*=} $sindoDir/JSindo

mv ${VersionInfo}$backup $VersionInfo

cd ..

# FSindo
echo " - copying FSindo"
cd FSindo

tools=src/tools.f90
sed -i $backup -e "s/DEVEL/$(date +%m%d) /" $tools

tarball=fsindo.tar.gz
tar --exclude *.o --exclude *.mod --exclude make.inc -zcf $tarball configure config src util
tar -zxf $tarball -C $sindoDir/FSindo
rm $tarball

mv ${tools}$backup $tools
cd ..

# script
cp -a script $sindoDir
rm $sindoDir/script/runGaussian.sh

# doc
echo " - copying doc"
cd doc/JSindo
cp -a *.pdf         $docDir/JSindo
cp -a sample_JSindo $docDir/JSindo

cd ../FSindo
cp -a *.pdf         $docDir/FSindo
cp -a sample_FSindo $docDir/FSindo

cd ../MakePES
cp -a *.pdf          $docDir/MakePES
cp -a sample_MakePES $docDir/MakePES

cd ../GENESIS
cp -a *.pdf  $docDir/GENESIS
tar -zcf sample_sindo_genesis.tar.gz --exclude *wako.sh sample_sindo_genesis
tar -zxf sample_sindo_genesis.tar.gz -C $docDir/GENESIS
rm sample_sindo_genesis.tar.gz

cd ../lecture_notes
cp -a *.pdf $docDir/lecture_notes


# create archive
cd $releaseDir
zip -r $sindo.zip $sindo
zip -r $doc.zip   $doc

echo ""
echo "Release version made in ../release/$(date +%F)."
echo ""
