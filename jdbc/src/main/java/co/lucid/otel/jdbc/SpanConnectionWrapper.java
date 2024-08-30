package co.lucid.otel.jdbc;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.wrapper.ConnectionWrapper;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import java.sql.Connection;

public class SpanConnectionWrapper {

    public static SpanConnectionWrapper createDefault(JdbcPeer peer) {
        return new SpanConnectionWrapper(GlobalOpenTelemetry.getTracer("co.lucid.otel.jdbc"), peer);
    }

    private final JdbcEventListener eventListener;

    public SpanConnectionWrapper(Tracer tracer, JdbcPeer peer) {
      this.eventListener = new SpanEventListener(tracer, peer);
    }

    public Connection wrap(Connection delegate) {
        return ConnectionWrapper.wrap(
            delegate,
            eventListener,
            ConnectionInformation.fromTestConnection(delegate)
        );
    }
}
