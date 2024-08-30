# OpenTelemetry for JDBC

![Maven Version](https://img.shields.io/maven-central/v/com.lucidchart/otel-jdbc.svg)

## Install

Published as the `com.lucidchart:otel-jdbc` artifact.

## Example

```java
import com.lucidchart.otel.jdbc.*;
import java.net.InetAddress;
import java.nio.ByteBuffer;

// optionally include peer info
JdbcPeer peer = new JdbcPeer();
peer.name = "my_db";
peer.ipv4 = ByteBuffer.wrap(InetAddress.getByName("127.0.0.1").getAddress()).getInt();
peer.port = 3306;

// create a SpanConnectionWrapper
SpanConnectionWrapper wrapper = SpanConnectionWrapper.createDefault(peer);

// wrap Connections
Connection rawConnection = ...
Connection tracedConnection = wrapper.wrap(rawConnection)
```
