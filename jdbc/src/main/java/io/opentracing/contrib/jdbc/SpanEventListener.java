package io.opentracing.contrib.jdbc;

import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.event.JdbcEventListener;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.contrib.spanmanager.SpanManager;
import io.opentracing.tag.Tags;
import java.sql.Driver;
import java.sql.SQLException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class SpanEventListener extends JdbcEventListener {

    private final SpanManager spanManager;
    private final Tracer tracer;
    private final JdbcPeer peer;

    public SpanEventListener(Tracer tracer, SpanManager spanManager, JdbcPeer peer) {
        this.spanManager = spanManager;
        this.tracer = tracer;
        this.peer = peer;
    }

    public void onAfterExecute(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
        onAfterExecute("execute", statementInformation, timeElapsedNanos, e);
    }

    public void onAfterExecute(PreparedStatementInformation statementInformation, long timeElaspedNanos, SQLException e) {
        onAfterExecute("execute", statementInformation, timeElaspedNanos, e);
    }

    public void onAfterExecuteUpdate(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
        onAfterExecute("update", statementInformation, timeElapsedNanos, e);
    }

    public void onAfterExecuteUpdate(PreparedStatementInformation statementInformation, long timeElaspedNanos, SQLException e) {
        onAfterExecute("update", statementInformation, timeElaspedNanos, e);
    }

    public void onAfterExecuteQuery(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
        onAfterExecute("query", statementInformation, timeElapsedNanos, e);
    }

    public void onAfterExecuteQuery(PreparedStatementInformation statementInformation, long timeElaspedNanos, SQLException e) {
        onAfterExecute("query", statementInformation, timeElaspedNanos, e);
    }

    private void onAfterExecute(String type, StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
        String sql = statementInformation.getSql();
        Span old = spanManager.currentSpan();
        Instant now = Instant.now();
        Instant start = now.minusNanos(timeElapsedNanos);
        Span span = tracer.buildSpan("SQL " + type + sql.split("\\s", 2)[0])
            .asChildOf(old)
            .withStartTimestamp(TimeUnit.SECONDS.toMicros(start.getEpochSecond()) + TimeUnit.NANOSECONDS.toMicros(start.getNano()))
            .start();
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_CLIENT);
        if (peer.name != null) {
            Tags.PEER_SERVICE.set(span, peer.name);
        }
        if (peer.ipv4 != null) {
            Tags.PEER_HOST_IPV4.set(span, peer.ipv4);
        }
        if (peer.ipv6 != null) {
            Tags.PEER_HOST_IPV6.set(span, peer.ipv6);
        }
        if (peer.port != null) {
            Tags.PEER_PORT.set(span, peer.port);
        }
        Driver driver = statementInformation.getConnectionInformation().getDriver();
        if (driver != null) {
            span.setTag("jdbc.driver", driver.getClass().getName());
        }
        span.setTag("jdbc.query", sql);
        if (e != null) {
            Tags.ERROR.set(span, true);
        }
        span.finish(TimeUnit.SECONDS.toMicros(now.getEpochSecond()) + TimeUnit.NANOSECONDS.toMicros(now.getNano()));
    }

}
