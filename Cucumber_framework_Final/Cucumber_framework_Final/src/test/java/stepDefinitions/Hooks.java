package stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;

/**
 * ADDED (Aug 2026 revision): Automatic per-scenario timing capture for the
 * experimental evaluation required by the paper (RQ1/RQ4). Every scenario run
 * appends one row to test-output/timing-log.csv with a timestamp, the scenario
 * name, pass/fail status, and duration in milliseconds.
 *
 * Run the suite N times (e.g. `for i in {1..30}; do mvn test; done` or use a
 * CI loop) to build up the repeated measurements needed for mean/SD/95% CI
 * reporting. Do NOT hand-edit or fabricate rows in the resulting CSV --
 * analyze_timings.py expects real observations only.
 */
public class Hooks {

    private long startNanos;
    private static final String CSV_PATH = "test-output/timing-log.csv";

    @Before
    public void beforeScenario(Scenario scenario) {
        startNanos = System.nanoTime();
    }

    @After
    public void afterScenario(Scenario scenario) {
        long endNanos = System.nanoTime();
        double durationMs = (endNanos - startNanos) / 1_000_000.0;
        String status = scenario.isFailed() ? "FAIL" : "PASS";
        writeRow(scenario.getName(), status, durationMs);
    }

    private synchronized void writeRow(String scenarioName, String status, double durationMs) {
        try {
            Files.createDirectories(Paths.get("test-output"));
            boolean isNew = !Files.exists(Paths.get(CSV_PATH));
            try (FileWriter fw = new FileWriter(CSV_PATH, true)) {
                if (isNew) {
                    fw.write("timestamp,scenario,status,duration_ms\n");
                }
                String safeName = scenarioName.replace(",", ";");
                fw.write(Instant.now().toString() + "," + safeName + "," + status + "," + durationMs + "\n");
            }
        } catch (IOException e) {
            System.err.println("[Hooks] Failed to write timing log: " + e.getMessage());
        }
    }
}
