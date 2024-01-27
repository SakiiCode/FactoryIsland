# Factory Island

## Getting the source

1. This project was written in Eclipse 2021-06 and JDK 16. It's better to use an eclipse integrated JDK, so it doesn't interfere with anything installed system-wide.
2. Eclipse -> Import... -> Existing Maven Project
3. Set JRE System Library to JavaSE-16 in the build path
4. Add `-Xmx4G -XX:+UseZGC -Dsun.java2d.d3d=false` (Windows) `-Xmx4G -XX:+UseZGC -Dsun.java2d.opengl=True` (Linux) to VM arguments in the Run Configuration

## Building on Linux

1. Download platform-specific JRE 17 packages ([Eclipse Temurin](https://adoptium.net/temurin/releases/) for example) into `/target/openjdk`. Filenames need to be `jdk_win64.zip`, `jdk_mac.tar.gz`, `jdk_linux64.tar.gz`.
2. Right click pom.xml -> Run As -> Maven install
3. Exported packages will be in /target/export

## Thanks

- [JavaTutorials101](https://www.youtube.com/user/JavaTutorials101) for kick-starting this project with his tutorial series
- [MeanRollerCoding](https://www.youtube.com/watch?v=LBSaqhSs6Q4&list=PLgRPwj3No0VLXFoqYnL2aYhczXB2qwKvp&index=5) for the 3D to 2D projection and sphere generation algorithms
- [Jorge Rodriguez](https://www.youtube.com/watch?v=zZM2uUkEoFw&list=PLW3Zl3wyJwWOpdhYedlD-yCB7WQoHf-My&index=13) for the mouse control and the awesome "Math/Code for Game Developers" series
- [submissive (cubic.org)](https://www.cubic.org/docs/3dclip.htm) for the 3D clipping algorithm
- [triszt4n](https://musescore.com/triszt4n) for the main menu soundtrack
- [hendriks73](https://github.com/hendriks73/ffsampledsp) for the FFSampledSP library
- [Bresenham](https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm) for the line drawing algorithm
- [mikolalysenko (0fps.net)](https://0fps.net/2013/07/03/ambient-occlusion-for-minecraft-like-worlds/) for the ambient occlusion
- [Squareys](https://blog.squareys.de/sphere-cone-intersection/) for the sphere-cone intersection algorithm
- [Leo Ono](https://www.youtube.com/c/LeoOno), [FuzzyCat](https://www.youtube.com/channel/UCxosPk3zlNp98CS1YGCzGww), [ThinMatrix](https://www.youtube.com/c/ThinMatrix) and [Gameplicit](https://www.twitch.tv/gameplicit) for inspiration
- Countless anonymous StackExchange, Reddit and other users for guidance in the right direction
