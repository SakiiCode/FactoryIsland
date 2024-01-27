#!/bin/bash
mv factoryisland-$1-jar-with-dependencies.jar factoryisland-$1.jar
rm -r export/$1

java -jar packr.jar \
     --platform windows64 \
     --jdk openjdk/jdk_win64.zip \
     --executable launcher \
     --classpath factoryisland-$1.jar \
     --mainclass ml.sakii.factoryisland.Main \
     --vmargs Xmx4G Dsun.java2d.d3d=false \
     --useZgcIfSupportedOs \
     --output export/$1/windows64

     
java -jar packr.jar \
     --platform linux64 \
     --jdk openjdk/jdk_linux64.tar.gz \
     --executable launcher \
     --classpath factoryisland-$1.jar \
     --mainclass ml.sakii.factoryisland.Main \
     --vmargs Xmx4G Dsun.java2d.opengl=True \
     --useZgcIfSupportedOs \
     --output export/$1/linux64
     
java -jar packr.jar \
     --platform mac \
     --jdk openjdk/jdk_mac.tar.gz \
     --executable launcher \
     --classpath factoryisland-$1.jar \
     --mainclass ml.sakii.factoryisland.Main \
     --vmargs Xmx4G \
     --useZgcIfSupportedOs \
     --output export/$1/mac
