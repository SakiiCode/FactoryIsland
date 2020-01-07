#!/bin/bash
rm -r export/$1
java -jar packr.jar \
     --platform windows64 \
     --jdk openjdk/jdk13_win64.zip \
     --executable factoryisland-$1 \
     --classpath factoryisland-$1.jar \
     --removelibs factoryisland-$1.jar \
     --mainclass ml.sakii.factoryisland.Main \
     --vmargs Xmx1G \
     --output export/$1/windows64
     
java -jar packr.jar \
     --platform windows32 \
     --jdk openjdk/jdk13_win32.zip \
     --executable factoryisland-$1 \
     --classpath factoryisland-$1.jar \
     --removelibs factoryisland-$1.jar \
     --mainclass ml.sakii.factoryisland.Main \
     --vmargs Xmx1G \
     --output export/$1/windows32
     
     
     
java -jar packr.jar \
     --platform linux64 \
     --jdk openjdk/jdk13_linux64.tar.gz \
     --executable factoryisland-$1 \
     --classpath factoryisland-$1.jar \
     --removelibs factoryisland-$1.jar \
     --mainclass ml.sakii.factoryisland.Main \
     --vmargs Xmx1G \
     --output export/$1/linux64
