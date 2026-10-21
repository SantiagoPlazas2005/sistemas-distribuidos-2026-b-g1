package com.synkro.workflow.observability;

/** Trace context and correlation id of the request running on the current thread. */
public record RequestContext(TraceContext trace, String correlationId) {

    private static final ThreadLocal<RequestContext> CURRENT = new ThreadLocal<>();

    public static void set(RequestContext context) {
        CURRENT.set(context);
    }

    public static RequestContext get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
