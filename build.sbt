import java.util.jar.Attributes

import sbt.Keys.libraryDependencies

name := "jlevtree"

organization := "org.oggio88"

version := "1.0"

resolvers += Resolver.mavenLocal

def artifactNameGenerator(test : Boolean = false) =
    (sv: ScalaVersion, module: ModuleID, artifact: Artifact) => {
        String.join("-", (artifact.name ::
            (artifact.classifier match {
                case Some(s) => List(s)
                case None => Nil
            }) ::: module.revision :: Nil).toArray : _*) + "." + artifact.extension
    }
artifactName := artifactNameGenerator()

lazy val levtreeTestUtils = (project in file("test")).settings(
    organization := (organization in LocalRootProject).value,
    name := "jlevtree-test",
    version := (version in LocalRootProject).value,
    mappings in packageBin in Compile ++= (mappings in (LocalRootProject, Compile, packageBin)).value,
    libraryDependencies += "junit" % "junit" % "4.12" % Test,
    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % Test,
    libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.6" % Test,
    artifactName := artifactNameGenerator(),
    publishArtifact := false
).dependsOn(LocalRootProject)

lazy val levtreeBenchmark = (project in file("benchmark")).settings(
    organization := (organization in LocalRootProject).value,
    name := "jlevtree-benchmark",
    version := (version in LocalRootProject).value,
    mainClass := Some("com.jlevtree.benchmark.Benchmark"),
    mappings in packageBin in Compile ++= (mappings in (LocalRootProject, Compile, packageBin)).value,
    mappings in packageBin in Compile ++= (mappings in (levtreeTestUtils, Compile, packageBin)).value,
    resourceDirectory := (resourceDirectory in (LocalRootProject, Test)).value,
    artifactName := artifactNameGenerator(),
    publishArtifact := false
).dependsOn(LocalRootProject).dependsOn(levtreeTestUtils)
