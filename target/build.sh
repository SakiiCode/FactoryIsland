#!/bin/bash
mv factoryisland-$1-jar-with-dependencies.jar factoryisland-$1.jar
rm -r export/$1

java -jar packr.jar \
     --platform windows64 \
     --jdk openjdk/jre_win64.zip \
     --executable launcher \
     --classpath factoryisland-$1.jar \
     --mainclass ml.sakii.factoryisland.Main \
     --vmargs Xmx4G \
     --useZgcIfSupportedOs \
     --output export/$1/FI_${1}_windows64

     
java -jar packr.jar \
     --platform linux64 \
     --jdk openjdk/jre_linux64.tar.gz \
     --executable launcher \
     --classpath factoryisland-$1.jar \
     --mainclass ml.sakii.factoryisland.Main \
     --vmargs Xmx4G Dsun.java2d.opengl=True \
     --useZgcIfSupportedOs \
     --output export/$1/FI_${1}_linux64
     
java -jar packr.jar \
     --platform mac \
     --jdk openjdk/jre_mac.tar.gz \
     --executable launcher \
     --classpath factoryisland-$1.jar \
     --mainclass ml.sakii.factoryisland.Main \
     --vmargs Xmx4G \
     --useZgcIfSupportedOs \
     --output export/$1/FI_${1}_mac
