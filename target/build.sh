#!/bin/bash
rm -r export/$1
java -jar packr.jar \
     --platform windows64 \
     --jdk openjdk/jdk13_win64.zip \
     --executable launch-this \
     --classpath factoryisland-$1.jar \
     --removelibs factoryisland-$1.jar \
     --mainclass ml.sakii.factoryisland.Main \
     --vmargs Xmx1G \
     --output export/$1/windows64
     
java -jar packr.jar \
     --platform windows32 \
     --jdk openjdk/jdk13_win32.zip \
     --executable launch-this \
     --classpath factoryisland-$1.jar \
     --removelibs factoryisland-$1.jar \
     --mainclass ml.sakii.factoryisland.Main \
     --vmargs Xmx1G \
     --output export/$1/windows32
     
     
     
java -jar packr.jar \
     --platform linux64 \
     --jdk openjdk/jdk13_linux64.tar.gz \
     --executable launch-this \
     --classpath factoryisland-$1.jar \
     --removelibs factoryisland-$1.jar \
     --mainclass ml.sakii.factoryisland.Main \
     --vmargs Xmx1G \
     --output export/$1/linux64
     
java -jar packr.jar \
     --platform mac \
     --jdk openjdk/jdk13_mac.tar.gz \
     --executable launch-this \
     --classpath factoryisland-$1.jar \
     --removelibs factoryisland-$1.jar \
     --mainclass ml.sakii.factoryisland.Main \
     --vmargs Xmx1G \
     --output export/$1/mac
     
rm start.sh
rm start.bat
echo "java -jar -Xmx1G factoryisland-$1.jar" >> start.sh
chmod 777 start.sh
echo "java -jar -Xmx1G factoryisland-$1.jar" >> start.bat
mkdir export/$1/universal
cp {start.bat,start.sh,factoryisland-$1.jar} export/$1/universal
