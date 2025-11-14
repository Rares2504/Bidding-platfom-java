package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class AuditService {
    private static final String AUDIT_CSV_PATH = "C:\\Users\\rares\\IdeaProjects\\BiddingPlatform\\audit-log.csv";
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static synchronized void logAction(String action) {
        String timestamp = LocalDateTime.now().format(fmt);
        String line = action + "," + timestamp + "\n";

        try (FileWriter fw = new FileWriter(AUDIT_CSV_PATH, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(line);
        } catch (IOException e) {
            System.err.println("Failed to write to audit-log.csv: " + e.getMessage());
        }
    }
}
