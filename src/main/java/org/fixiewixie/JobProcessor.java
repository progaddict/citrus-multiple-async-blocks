package org.fixiewixie;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Singleton
public class JobProcessor {

    private final Random random = new Random();

    @Inject
    private JobStore jobStore;

    @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
    public void atSchedule() {
        try {
            processJobs();
        } catch (Throwable error) {
            error.printStackTrace();
        }
    }

    /**
     * 1. sort jobs
     * 2. take the 1st one
     * 3. process it:
     *      3.1 request some data via POST from an external system
     *      3.2 mark as completed
     * 4. simulate long computation via {@link Thread#sleep(long)}
     */
    private void processJobs() throws InterruptedException, IOException {
        List<String> jobs = new ArrayList<>(jobStore.getPendingJobs());
        if (jobs.isEmpty()) {
            return;
        }
        Collections.sort(jobs);
        processJob(jobs.get(0));
        Thread.sleep(2500 + random.nextInt(1500));
    }

    private void processJob(String jobName) throws IOException {
        requestSomeData(jobName);
        jobStore.markJobAsCompleted(jobName);
    }

    public static int requestSomeData(String jobName) throws IOException {
        final String body = String.format("{ \"job\": \"%s\" }", jobName);
        URL url = new URL("http://localhost:8085/");
        HttpURLConnection postConnection = (HttpURLConnection) url.openConnection();
        postConnection.setRequestMethod("POST");
        postConnection.setRequestProperty("Content-Type", "application/json");
        postConnection.setDoOutput(true);
        try (OutputStream os = postConnection.getOutputStream()) {
            os.write(body.getBytes());
            os.flush();
        }
        return postConnection.getResponseCode();
    }
}
