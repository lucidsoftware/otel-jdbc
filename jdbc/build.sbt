libraryDependencies ++= Seq(
  "io.opentelemetry" % "opentelemetry-bom" %  "1.41.0",
  "io.opentelemetry" % "opentelemetry-api" %  "1.41.0",
  "io.opentelemetry.semconv" % "opentelemetry-semconv" %  "1.27.0-alpha",
  "p6spy" % "p6spy" % "3.9.1",
)

moduleName := s"otel-${name.value}"
publishTo := sonatypePublishToBundle.value
