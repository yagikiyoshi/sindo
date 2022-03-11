#!/bin/bash


version=$(cat VERSION)_$(date +%y%m%d)
sindo=sindo-$version
doc=doc

backup=.bak

releaseDir=$(pwd)/../release/$(date +%F)
if [ -e $releaseDir ]; then
  rm -rf $releaseDir
fi
sindoDir=$releaseDir/$sindo
docDir=$releaseDir/$sindo/$doc

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
cp -a sindovars.sh $sindoDir

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
cp -a *.pdf  $docDir/JSindo

tar -zcf sample_JSindo.tar.gz sample_JSindo
mv sample_JSindo.tar.gz $docDir/JSindo

cd ../FSindo
cp -a *.pdf  $docDir/FSindo
tar -zcf sample_FSindo.tar.gz sample_FSindo
mv sample_FSindo.tar.gz $docDir/FSindo

cd ../MakePES
cp -a *.pdf  $docDir/MakePES
tar -zcf sample_MakePES.tar.gz sample_MakePES
mv sample_MakePES.tar.gz $docDir/MakePES

cd ../GENESIS
cp -a *.pdf  $docDir/GENESIS
tar -zcf sample_sindo_genesis.tar.gz --exclude *wako.sh sample_sindo_genesis
mv sample_sindo_genesis.tar.gz $docDir/GENESIS

cd ../lecture_notes
cp -a *.pdf $docDir/lecture_notes


# create archive
cd $releaseDir
#tar -zcf $sindo.tar.gz $sindo
#tar -zcf $doc.tar.gz $doc
zip -r $sindo.zip $sindo

echo ""
echo "Release version made in ../release/$(date +%F)."
echo ""
