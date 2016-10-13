import play.Project._

name := """etikett"""

version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  cache,javaJdbc,javaEbean,
  "org.eclipse.rdf4j" % "rdf4j-runtime" % "2.0.1",
   "mysql" % "mysql-connector-java" % "5.1.18",
   "com.github.rjeschke" % "txtmark" % "0.13"
  )

playJavaSettings