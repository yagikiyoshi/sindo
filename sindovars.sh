
#export sindo_dir=/home1/yagi/devel/sindo
export sindo_dir=/path/to/sindo-4.0
export sindo_jar=$sindo_dir/JSindo/jar
export CLASSPATH=${CLASSPATH}:${sindo_jar}/JSindo-4.0.beta.jar
export CLASSPATH=${CLASSPATH}:${sindo_jar}/ext/Jama-1.0.3.jar

export PATH=$PATH:$sindo_dir/script:$sindo_dir/FSindo/bin
export SINDO_RSH=ssh
