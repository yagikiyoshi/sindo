# How to build JSindo

Run a configure script,

    ./configure

You will be prompted to enter a path to JAMA and Java3D,

    Enter the path to JAMA [default=system]:
    Enter the path to Java3D [default=system]:

Empty enter is OK if you have installed jar files to your system folder (e.g., `${HOME}/Library/Java/Extensions`). Otherwise, enter the folder where jar files are located. For example, I have

    /home/yagi/lib/Jama/Jama-1.0.2.jar  
    /home/yagi/lib/jogamp-java3d/j3dcore.jar  
    /home/yagi/lib/jogamp-java3d/j3dutils.jar  
    /home/yagi/lib/jogamp-java3d/vecmath.jar  

so I set as follow,

    Enter the path to JAMA [default=system]: /home/yagi/lib/Jama
    Enter the path to Java3D [default=system]: /home/yagi/lib/jogamp-java3d

Then, `build_JSindo.xml` will be created in the folder. Run ant using this file,

    $ ant -f build_JSindo.xml

This command compiles all source codes in `src`, creates class files in `bin`, and creates a jar file, `JSindo-$version.jar`.

