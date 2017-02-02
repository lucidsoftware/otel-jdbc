package io.opentracing.contrib.jdbc;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.wrapper.ConnectionWrapper;
import io.opentracing.Tracer;
import io.opentracing.contrib.global.GlobalTracer;
import io.opentracing.contrib.spanmanager.DefaultSpanManager;
import io.opentracing.contrib.spanmanager.SpanManager;
import java.sql.Connection;

public class SpanConnectionWrapper {

    public static SpanConnectionWrapper createDefault(JdbcPeer peer) {
        return new SpanConnectionWrapper(GlobalTracer.get(), DefaultSpanManager.getInstance(), peer);
    }

    private final JdbcEventListener eventListener;

    public SpanConnectionWrapper(Tracer tracer, SpanManager spanManager, JdbcPeer peer) {
        this.eventListener = new SpanEventListener(tracer, spanManager, peer);
    }

    public Connection wrap(Connection delegate) {
        return ConnectionWrapper.wrap(
            delegate,
            eventListener,
            ConnectionInformation.fromTestConnection(delegate)
        );
    }

}
