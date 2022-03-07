# How to build JSindo

Jar files of JSindo is created by the following script.  

    $ ./build.sh

- The build of JSindo requires the Java development kit (JDK) to compile the code.  
  Also, these unix commands are needed.
    - wget : to download files from the Internet
    - unar : to extract 7z files
    - ant  : to compile java source codes, and to build jar files.

  They are available via brew (mac), apt (ubuntu), and yum (centos). 

- Exernal jars will be downloaded to `external_jars` directory.
    - jogamp (v2.4.0) : https://jogamp.org
    - java3d (v1.6.2) : https://jogamp.org
    - jama   (v1.0.3) : https://math.nist.gov/javanumerics/jama/

- Then, `build_JSindo.xml` is created in the current directory, which is used to build  
  JSindo-$version.jar

      $ ant -f build_JSindo.xml

  This command compiles source codes in `src` and `test`, creates class files in `bin`, and creates a jar file, `jar/JSindo-$version.jar`.

- The jar files of JSindo and external programs are archived to one jar file, `jar/JSindo-$version_fat.jar`.

- Finally, all external licenses are gathered in `external_license`.

- To clean up the build,

      $ ant -f build_JSindo.xml clean

  To clean up everthing (downloaded jars will be also removed).

      $ ./cleanup.sh


