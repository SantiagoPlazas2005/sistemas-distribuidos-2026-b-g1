package com.synkro.workflow.observability;

import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** W3C trace context: one trace_id for the whole request, one span_id per hop. */
public record TraceContext(String traceId, String spanId) {

    private static final Pattern TRACEPARENT =
            Pattern.compile("^00-([0-9a-f]{32})-([0-9a-f]{16})-[0-9a-f]{2}$");
    private static final SecureRandom RANDOM = new SecureRandom();

    /** Returns null when the header is missing or malformed. */
    public static TraceContext parse(String header) {
        if (header == null) {
            return null;
        }
        Matcher m = TRACEPARENT.matcher(header.trim());
        if (!m.matches() || m.group(1).equals("0".repeat(32))) {
            return null;
        }
        return new TraceContext(m.group(1), m.group(2));
    }

    public static TraceContext create() {
        return new TraceContext(randomHex(16), randomHex(8));
    }

    /** Same trace_id, new span_id: this service's own span. */
    public TraceContext child() {
        return new TraceContext(traceId, randomHex(8));
    }

    public String toTraceparent() {
        return "00-" + traceId + "-" + spanId + "-01";
    }

    private static String randomHex(int bytes) {
        byte[] data = new byte[bytes];
        RANDOM.nextBytes(data);
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
