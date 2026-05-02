package org.freeplane.features.map;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * FreeplaneStateLogger.java
 * Logs every FSM state transition to freeplane_events.log
 * Called at each state change in MapController / TextController
 */
public class FreeplaneStateLogger {

    private static final String LOG_FILE =
        System.getProperty("user.home") + "/freeplane_events.log";

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Writes a state transition event to the log file.
     * Format: TIMESTAMP | USER | STATE | EVENT
     */
    public static void logEvent(String user, String state, String event) {
        String timestamp = LocalDateTime.now().format(FMT);
        String line = String.format("%s | %-12s | %-14s | %s",
                timestamp, user, state, event);
        try (PrintWriter pw = new PrintWriter(
                new FileWriter(LOG_FILE, true))) {
            pw.println(line);
        } catch (Exception e) {
            System.err.println("[Logger] Error: " + e.getMessage());
        }
    }
}
