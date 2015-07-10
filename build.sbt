import play.Project._

name := """etikett"""

version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  cache,javaJdbc,
   "org.openrdf.sesame" % "sesame-repository-api" % "2.7.10" ,
  "org.openrdf.sesame" % "sesame-core" % "2.7.10",
  "org.openrdf.sesame" % "sesame-rio" % "2.7.10",
  "org.openrdf.sesame" % "sesame-sail" % "2.7.10",
  "org.openrdf.sesame" % "sesame" % "2.7.10",
  "org.openrdf.sesame" % "sesame-http-client" % "2.7.10",
  "org.openrdf.sesame" % "sesame-rio-ntriples" % "2.7.10",
  "org.openrdf.sesame" % "sesame-rio-api" % "2.7.10",
  "org.openrdf.sesame" % "sesame-rio-rdfxml" % "2.7.10",
  "org.openrdf.sesame" % "sesame-rio-n3" % "2.7.10",
  "org.openrdf.sesame" % "sesame-rio-turtle" % "2.7.10",
  "org.openrdf.sesame" % "sesame-queryresultio-api" % "2.7.10",
  "org.openrdf.sesame" % "sesame-queryresultio" % "2.7.10",
  "org.openrdf.sesame" % "sesame-query" % "2.7.10",
  "org.openrdf.sesame" % "sesame-model" % "2.7.10",
  "org.openrdf.sesame" % "sesame-http-protocol" % "2.7.10",
  "org.openrdf.sesame" % "sesame-http" % "2.7.10",
  "org.openrdf.sesame" % "sesame-repository-sail" % "2.7.10",
  "org.openrdf.sesame" % "sesame-sail-memory" % "2.7.10",
  "org.openrdf.sesame" % "sesame-sail-nativerdf" % "2.7.10"
  )

playJavaSettings