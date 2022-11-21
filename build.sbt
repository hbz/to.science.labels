import play.Project._

name := """etikett"""

version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  cache,javaJdbc,javaEbean,
  "org.eclipse.rdf4j" % "rdf4j-runtime" % "2.0.1"  exclude("com.fasterxml.jackson.core","jackson-databind") exclude("com.fasterxml.jackson.core","jackson-core") exclude("com.fasterxml.jackson.core","jackson-annotations"),
  "mysql" % "mysql-connector-java" % "5.1.18",
  "com.github.jsonld-java" % "jsonld-java" % "0.8.3",
  "com.github.rjeschke" % "txtmark" % "0.13" exclude("com.fasterxml.jackson.core","jackson-databind") exclude("com.fasterxml.jackson.core","jackson-core") exclude("com.fasterxml.jackson.core","jackson-annotations"),
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.7.6",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.7.6",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.7.6"
  )

resolvers := Seq(Resolver.mavenLocal,
"Maven Central Server" at "https://repo1.maven.org/maven2",
"aduna" at "https://maven.ontotext.com/content/repositories/aduna/" ,
"Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
"Duraspace releases" at "https://m2.duraspace.org/content/repositories/thirdparty"
)

playJavaSettings
