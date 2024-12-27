#!/usr/bin/env zsh

ARCH=$(uname -m)

LIB_PATH="libs/native/linux/$ARCH"

export DYLD_LIBRARY_PATH=$(pwd)/$LIB_PATH
export LD_LIBRARY_PATH=$(pwd)/$LIB_PATH
echo "$LD_LIBRARY_PATH"

CLASS=$1
shift 1

echo "Parameters passed to run.zsh: $@"

java -cp .:third_party/gson-2.11.0.jar:./libs/agora-recording-sdk.jar:./build -Xcheck:jni -XX:+HeapDumpOnOutOfMemoryError -Djava.library.path=$LIB_PATH $CLASS $* | grep -v "WARNING in native method: JNI call made without checking exceptions"
