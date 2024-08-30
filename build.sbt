lazy val jdbc = project

inScope(Global)(Seq(
  autoScalaLibrary := false,
  credentials += Credentials(
    "Sonatype Nexus Repository Manager",
    "oss.sonatype.org",
    sys.env.getOrElse("SONATYPE_USERNAME", ""),
    sys.env.getOrElse("SONATYPE_PASSWORD", ""
  )),
  crossPaths := false,
  developers ++= List(
    Developer("lucidsoftware", "Lucid Software, Inc.", "support@lucid.co", url("https://lucid.co")),
    Developer("tmccombs", "Thayne McCombs", "thayne@lucid.co", url("https://github.com/tmccombs")),
  ),
  homepage := Some(url("https://git.lucidchart.com/lucidsoftware/otel-jdbc")),
  licenses += "Apache 2.0 License" -> url("https://www.apache.org/licenses/LICENSE-2.0"),
  organization := "com.lucidchart",
  organizationHomepage := Some(url("http://lucid.co/")),
  organizationName := "Lucid Software, Inc.",
  scmInfo := Some(ScmInfo(
    url("https://github.com/lucidsoftware/otel-jdbc"),
    "scm:git:git@github.com:lucidsoftware/otel-jdbc.git"
  )),
  startYear := Some(2024),
  version := sys.props.getOrElse("build.version", "0-SNAPSHOT")
))

publish / skip := true
