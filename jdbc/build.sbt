libraryDependencies ++= Seq(
  "io.opentracing" % "opentracing-api" % "0.20.7",
  "io.opentracing.contrib" % "opentracing-globaltracer" % "0.1.0",
  "io.opentracing.contrib" % "opentracing-spanmanager" % "0.0.1",
  "p6spy" % "p6spy" % "3.0.0"
)

moduleName := s"opentracing-${name.value}"
