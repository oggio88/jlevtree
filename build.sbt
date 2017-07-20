name := "jlevtree"

organization := "org.oggio88"

version := "1.0"

scalaVersion := "2.12.2"

resolvers += Resolver.mavenLocal

mainClass in assembly := Some("com.jlevtree.TestClass")

libraryDependencies += "junit" % "junit" % "4.12" % "test"
libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"
libraryDependencies += "org.tukaani" % "xz" % "1.6" % "test"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.9.0.pr3"

crossPaths := false

enablePlugins(JavaAppPackaging)
