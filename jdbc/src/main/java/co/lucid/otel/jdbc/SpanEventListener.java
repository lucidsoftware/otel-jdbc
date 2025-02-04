package co.lucid.otel.jdbc;

import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.event.JdbcEventListener;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.semconv.SemanticAttributes;
import io.opentelemetry.semconv.NetworkAttributes;
import java.sql.Driver;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class SpanEventListener extends JdbcEventListener {

    private final Tracer tracer;
    private final JdbcPeer peer;

    public SpanEventListener(Tracer tracer, JdbcPeer peer) {
        this.tracer = tracer;
        this.peer = peer;
    }


    @Override
    public void onAfterExecute(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
        onAfterExecute("execute", statementInformation, timeElapsedNanos, e);
    }

    @Override
    public void onAfterExecute(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
        onAfterExecute("execute", statementInformation, timeElapsedNanos, e);
    }

    @Override
    public void onAfterExecuteUpdate(StatementInformation statementInformation, long timeElapsedNanos, String sql, int rowCount, SQLException e) {
        onAfterExecute("update", statementInformation, timeElapsedNanos, e);
    }

    @Override
    public void onAfterExecuteUpdate(PreparedStatementInformation statementInformation, long timeElapsedNanos, int rowCount, SQLException e) {
        onAfterExecute("update", statementInformation, timeElapsedNanos, e);
    }

    @Override
    public void onAfterExecuteQuery(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
        onAfterExecute("query", statementInformation, timeElapsedNanos, e);
    }

    @Override
    public void onAfterExecuteQuery(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
        onAfterExecute("query", statementInformation, timeElapsedNanos, e);
    }

    private void onAfterExecute(String type, StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
        Instant end = Instant.now();
        Instant start = end.minusNanos(timeElapsedNanos);
        String sql = statementInformation.getSql();
        var span = tracer.spanBuilder(String.format("SQL %s %s", type, new Scanner(sql).next()))
            .setStartTimestamp(start)
            .setAttribute(SemanticAttributes.DB_STATEMENT, sql)
            .setSpanKind(SpanKind.CLIENT)
            .startSpan();
        if (peer.name != null) {
            span.setAttribute(SemanticAttributes.PEER_SERVICE, peer.name);
        }
        if (peer.ip != null) {
            span.setAttribute(NetworkAttributes.NETWORK_PEER_ADDRESS, peer.ip);
        }
        if (peer.port != null) {
            span.setAttribute(NetworkAttributes.NETWORK_PEER_PORT, peer.port);
        }
        Driver driver = statementInformation.getConnectionInformation().getDriver();
        if (driver != null) {
            span.setAttribute(SemanticAttributes.DB_JDBC_DRIVER_CLASSNAME, driver.getClass().getName());
        }

        if (e != null) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage() + ". SqlState: " + e.getSQLState() + ". ErrorCode: " + e.getErrorCode());
        }
        span.end(end);
    }

}
