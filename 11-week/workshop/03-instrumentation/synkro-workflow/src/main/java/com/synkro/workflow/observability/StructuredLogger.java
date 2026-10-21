package com.synkro.workflow.observability;

import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/** Writes one JSON line per event, following 02-log-contract.md. */
public class StructuredLogger {

    private static final DateTimeFormatter TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);

    private final String service;
    private final String version;
    private final String environment;
    private final PrintStream out;

    public StructuredLogger(String service, String version, String environment, PrintStream out) {
        this.service = service;
        this.version = version;
        this.environment = environment;
        this.out = out;
    }

    public void info(String event, String message, long durationMs, Map<String, Object> domain) {
        log("INFO", event, message, durationMs, domain);
    }

    public void warn(String event, String message, long durationMs, Map<String, Object> domain) {
        log("WARN", event, message, durationMs, domain);
    }

    public void error(String event, String message, long durationMs, Map<String, Object> domain) {
        log("ERROR", event, message, durationMs, domain);
    }

    private void log(String level, String event, String message, long durationMs,
                     Map<String, Object> domain) {
        RequestContext ctx = RequestContext.get();
        Map<String, Object> line = new LinkedHashMap<>();
        line.put("timestamp", TIMESTAMP.format(Instant.now()));
        line.put("level", level);
        line.put("service", service);
        line.put("version", version);
        line.put("environment", environment);
        line.put("trace_id", ctx == null ? "none" : ctx.trace().traceId());
        line.put("span_id", ctx == null ? "none" : ctx.trace().spanId());
        line.put("correlation_id", ctx == null ? "none" : ctx.correlationId());
        line.put("event", event);
        line.put("message", message);
        line.put("duration_ms", durationMs);
        line.putAll(domain);
        out.println(toJson(line));
    }

    private static String toJson(Map<String, Object> fields) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> field : fields.entrySet()) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            sb.append('"').append(field.getKey()).append("\":");
            Object value = field.getValue();
            if (value instanceof Number) {
                sb.append(value);
            } else {
                sb.append('"').append(escape(String.valueOf(value))).append('"');
            }
        }
        return sb.append('}').toString();
    }

    private static String escape(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}
