#!/bin/bash

# download external jars
echo ""
echo " ------  Download external jar files  ------ "
if [ ! -e external_jars/Jama-1.0.3.jar ]; then
  cd external_jars/
  ./download_jars.sh
  cd ..
fi

# create build file for ant

echo ""
echo " ------ Creating a build file for ant ------ "

xmlfile='build_JSindo.xml'
if [ -e ${xmlfile} ]; then
  rm ${xmlfile}
fi

ver=$(cat ../VERSION)
sed -e "s#VERSION#${ver}#"  build/build_JSindo.tmp  > ${xmlfile}

echo ""
echo " Successfully created ${xmlfile}. "
echo ""
echo " ------------------------------------------- "

# now build JSINDO
ant -f ${xmlfile}

# create a fat jar
./create_fat.sh
