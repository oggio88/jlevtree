name := "jlevtree"

organization := "org.oggio88"

version := "1.0"

resolvers += Resolver.mavenLocal

libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.6" % "test"

artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
  artifact.name + "-" + module.revision + "." + artifact.extension
}

crossPaths := false

enablePlugins(JavaAppPackaging)
