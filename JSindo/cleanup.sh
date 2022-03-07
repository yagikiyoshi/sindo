#!/bin/bash

rm -r bin jar build_JSindo.xml

aa=$(grep external_jars= build.sh)
external_jars=${aa#*=}
aa=$(grep external_license= build.sh)
external_license=${aa#*=}
rm -r ${external_jars} ${external_license}
