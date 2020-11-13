### Install Instructions

## Servers
These are built for PaperSpigot 1.16.3 which means it should also work on Spigot.

1. Put MCBody.jar inside your plugins folder
2. Get RaycastAPI.jar [here](https://www.spigotmc.org/resources/api-raycastapi-create-guns-get-towards-entity-block.77541/) and place it in your server's plugins folder
3. Get SpigotLib.jar [here](https://github.com/gyurix/SpigotLib/releases) (The latest SpigotLib.jar under assets) and place it in your server's plugins folder

## Developers

# SpigotLib
SpigotLib.jar is the original copy of SpigotLib from https://github.com/gyurix/SpigotLib/releases
SpigotLib main page can be found here: https://github.com/gyurix/SpigotLib

SpigotLib-10.2.6.jar and SpigotLib-10.2.6-sources.jar are versions build by me. They contain very minor changes that really only deal with getting them built or compatibility problems I had. They also contain the documented source code.

You are not required to do anything for install. But if you wish to use the original jar change the filename for the SpigotLib dependency in pom.xml.

Edited jars:
```
    <dependency>
      <groupId>gyurix</groupId>
      <artifactId>SpigotLib</artifactId>
      <version>10.2.6</version>
      <scope>system</scope>
      <systemPath>${pom.basedir}/vendor/SpigotLib/SpigotLib-10.2.6.jar</systemPath>
    </dependency>
```

Original jars:
```
    <dependency>
      <groupId>gyurix</groupId>
      <artifactId>SpigotLib</artifactId>
      <version>10.2.6</version>
      <scope>system</scope>
      <systemPath>${pom.basedir}/vendor/SpigotLib/SpigotLib.jar</systemPath>
    </dependency>
```

# RaycastAPI
I was not sure RaycastAPI licence. It does not seem to have a maven repo so you must obtain a copy yourself from [Spigot](https://www.spigotmc.org/resources/api-raycastapi-create-guns-get-towards-entity-block.77541/).

After that you must put the jar in vendor/RaycastAPI named: `RaycastAPI.jar` or alternatively rename the systemPath element in the pom.
