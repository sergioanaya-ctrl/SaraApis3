package org.sara.api.Util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ApiExecutionReportUtil {

    private static final Path LOG_DIR = Paths.get("src", "test", "resources", "logs");
    private static final Path SUCCESS_LOG = LOG_DIR.resolve("success.log");
    private static final Path ERROR_LOG = LOG_DIR.resolve("errors.log");
    private static final Path SUMMARY_REPORT = LOG_DIR.resolve("summary-report.txt");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final List<String> successEntries = Collections.synchronizedList(new ArrayList<>());
    private static final List<String> errorEntries = Collections.synchronizedList(new ArrayList<>());
    private static volatile boolean shutdownHookRegistered = false;

    private ApiExecutionReportUtil() {
    }

    public static void record(String expediente, String actionId, int statusCode, String responseBody) {
        ensureShutdownHook();

        String safeBody = responseBody == null || responseBody.trim().isEmpty()
                ? "<sin body>"
                : responseBody.replaceAll("\\s+", " ").trim();

        String line = String.format(
                "%s | expediente=%s | actionId=%s | status=%d | response=%s",
                LocalDateTime.now().format(FORMATTER),
                expediente,
                actionId,
                statusCode,
                safeBody
        );

        if (statusCode == 200 || statusCode == 201) {
            successEntries.add(line);
        } else {
            errorEntries.add(line);
        }
    }

    public static synchronized void writeFinalReport() {
        try {
            Files.createDirectories(LOG_DIR);
            writeLines(SUCCESS_LOG, successEntries);
            writeLines(ERROR_LOG, errorEntries);
            writeSummary();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo generar el reporte final de action-jobs", e);
        }
    }

    private static void writeLines(Path path, List<String> lines) throws IOException {
        StringBuilder content = new StringBuilder();
        for (String line : lines) {
            content.append(line).append(System.lineSeparator());
        }
        Files.write(
                path,
                content.toString().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }

    private static void writeSummary() throws IOException {
        StringBuilder summary = new StringBuilder();
        summary.append("Reporte final action-jobs").append(System.lineSeparator());
        summary.append("Generado: ").append(LocalDateTime.now().format(FORMATTER)).append(System.lineSeparator());
        summary.append("Total exitosos: ").append(successEntries.size()).append(System.lineSeparator());
        summary.append("Total fallidos: ").append(errorEntries.size()).append(System.lineSeparator());
        summary.append(System.lineSeparator());
        summary.append("Fallidos").append(System.lineSeparator());
        if (errorEntries.isEmpty()) {
            summary.append("Sin expedientes fallidos").append(System.lineSeparator());
        } else {
            for (String entry : errorEntries) {
                summary.append(entry).append(System.lineSeparator());
            }
        }

        Files.write(
                SUMMARY_REPORT,
                summary.toString().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }

    private static void ensureShutdownHook() {
        if (!shutdownHookRegistered) {
            synchronized (ApiExecutionReportUtil.class) {
                if (!shutdownHookRegistered) {
                    Runtime.getRuntime().addShutdownHook(new Thread(ApiExecutionReportUtil::safeWriteFinalReport));
                    shutdownHookRegistered = true;
                }
            }
        }
    }

    private static void safeWriteFinalReport() {
        try {
            writeFinalReport();
        } catch (Exception ignored) {
            // Evitamos romper el cierre de la JVM si el reporte no puede escribirse.
        }
    }
}
