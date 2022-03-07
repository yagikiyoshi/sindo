# How to build JSindo

## Prerequisite
  The build of JSindo requires the Java development kit (JDK) to compile the code.  
  Also, these unix commands are needed.
    - wget : to download files from the Internet
    - unar : to extract 7z files
    - ant  : to compile java source codes, and to build jar files.

  They are available via brew (mac), apt (ubuntu), or yum (centos). 


## Build JSindo
Jar files of JSindo is created by the following script.  

    $ ./build.sh

This command does the following.

1. Exernal jars will be downloaded to `external_jars` directory.
    - jogamp (v2.4.0) : https://jogamp.org
    - java3d (v1.6.2) : https://jogamp.org
    - jama   (v1.0.3) : https://math.nist.gov/javanumerics/jama/

1. `build_JSindo.xml` is created in the current directory, which builds 
  `JSindo-$version.jar` by the following command,

      $ ant -f build_JSindo.xml

  This command compiles the source codes in `src` and `test`, creates 
  class files in `bin`, and creates the jar file in `jar`.

1. The jar files of JSindo and external programs are archived to 
  one executable jar file, `jar/JSindo-$version_fat.jar`.

1. Finally, all external licenses are gathered in `external_license`.

## Clean up
To clean up the build,

    $ ant -f build_JSindo.xml clean

To clean up everthing (downloaded files will be also removed).

    $ ./cleanup.sh


