# jplay
Java music player

# Compile and package (generate jars)
In root directory:

```bash
mvn package
```

# Run (and compile) the project with maven
In root directory:

```bash
mvn install
```

# Run (only run) with java
You must compile first

```bash
java -jar -Dawt.useSystemAAFontSettings=on -Dsun.java2d.opengl=true xjplay/target/xjplay-1.0-SNAPSHOT-jar-with-dependencies.jar
```