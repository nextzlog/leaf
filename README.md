Leaf: Awesome Java Swing Library for Text Editors
====

![image](https://img.shields.io/badge/Gradle-6-red.svg)
![image](https://img.shields.io/badge/OpenJDK-SE8-red.svg)
![image](https://img.shields.io/badge/license-LGPL3-darkblue.svg)

Leaf is a Java Swing library for creating a rich text editor.

## Features

## Documents

- [Javadoc](https://nextzlog.github.io/leaf/doc/index.html)

## Sample Codes

## Maven

If you want to use the latest build, configure the `build.gradle` as follows:

```Groovy:build.gradle
repositories.maven {
  url('https://nextzlog.github.io/leaf/mvn/')
}

dependencies {
  implementation('leaf:leaf:+')
}
```

## Build

[Gradle](https://gradle.org/) retrieves dependent libraries, runs tests, and generates a JAR file automatically.

```shell
$ gradle build javadoc publish
```

## Contribution

Feel free to contact [@nextzlog](https://twitter.com/nextzlog) on Twitter.

## License

### Author

[無線部開発班](https://pafelog.net)

### Clauses

- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License(LGPL) as published by the Free Software Foundation (FSF), either version 3 of the License, or (at your option) any later version.

- This program is distributed in the hope that it will be useful, but **without any warranty**; without even the implied warranty of **merchantability or fitness for a particular purpose**.
See the GNU Lesser General Public License for more details.

- You should have received a copy of the GNU General Public License and GNU Lesser General Public License along with this program.
If not, see <http://www.gnu.org/licenses/>.
