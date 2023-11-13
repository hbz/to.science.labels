name := """etikett"""
  
version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"

version := "0.1.0-SNAPSHOT"


libraryDependencies ++= Seq(
  cache,ws,javaWs,javaJdbc,
  "org.eclipse.rdf4j" % "rdf4j-runtime" % "2.2.2" exclude("org.apache.lucene" , "lucene-core") ,
  "com.github.jsonld-java" % "jsonld-java" % "0.11.1",
  "com.github.rjeschke" % "txtmark" % "0.13",
  "com.fasterxml.jackson.core" %"jackson-core" %"2.7.6",
  "com.fasterxml.jackson.core" %"jackson-databind" %"2.7.6",
  "com.fasterxml.jackson.dataformat" %"jackson-dataformat-xml" %"2.6.3",
  "mysql" % "mysql-connector-java" % "8.0.23",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.7.6"
)

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

resolvers := Seq(Resolver.mavenLocal,
"Maven Central Server" at "https://repo1.maven.org/maven2",
"edoweb releases" at "https://edoweb.github.com/releases",
"hypnoticocelot" at "https://oss.sonatype.org/content/repositories/releases/",
"aduna" at "https://maven.ontotext.com/content/repositories/aduna/",
"Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
"Play2war plugins release" at "https://repository-play-war.forge.cloudbees.com/release/",
"Duraspace releases" at "https://m2.duraspace.org/content/repositories/thirdparty"
)
