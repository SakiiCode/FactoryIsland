# Factory Island

## Getting the source

1. This project was written in Eclipse 2021-06 and JDK 16. It's better to use an eclipse integrated JDK, so it doesn't interfere with anything installed system-wide.
2. Eclipse -> Import... -> Existing Maven Project
3. Set JRE System Library to JavaSE-16 in the build path
4. Add `-XX:+UseZGC -Dsun.java2d.opengl=true` to VM arguments in the Run Configuration

## Building on Linux

1. Download platform-specific JRE 16 runtimes ([AdoptOpenJDK with OpenJ9](https://adoptopenjdk.net/?variant=openjdk16&jvmVariant=openj9) recommended) into `/target/openjdk`. Filenames need to be `jdk16_win64.zip`, `jdk16_mac.tar.gz`, `jdk16_linux64.tar.gz`.
2. Right click pom.xml -> Run As -> Maven install
3. Exported packages will be in /target/export
