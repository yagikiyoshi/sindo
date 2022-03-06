# How to build JSindo

Run a install script,

    ./install.sh

Exernal jars will be downloaded to "external_jars", if they don't exist

    o jogamp (v2.4.0)
    o java3d (v1.6.2)
    o jama   (v1.0.3)

Then, `build_JSindo.xml` will be created in the folder. Run ant using this file,

    $ ant -f build_JSindo.xml

This command compiles all source codes in `src`, creates class files in `bin`, creates a jar file, `jar/JSindo-$version.jar`.

Finaly, a fat jar can be created by

    $ ./create_fat.sh

To clean up the build,

    $ ant -f build_JSindo.xml clean
