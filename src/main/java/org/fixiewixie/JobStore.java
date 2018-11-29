package org.fixiewixie;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@ApplicationScoped
public class JobStore {

    private final Set<String> pendingJobs = new ConcurrentSkipListSet<>();
    private final Set<String> completedJobs = new ConcurrentSkipListSet<>();

    public Set<String> getPendingJobs() {
        return Collections.unmodifiableSet(pendingJobs);
    }

    public Set<String> getCompletedJobs() {
        return Collections.unmodifiableSet(completedJobs);
    }

    public boolean submitJob(String jobName) {
        completedJobs.remove(jobName);
        return pendingJobs.add(jobName);
    }

    public void markJobAsCompleted(String jobName) {
        pendingJobs.remove(jobName);
        completedJobs.add(jobName);
    }

    public void clear() {
        pendingJobs.clear();
        completedJobs.clear();
    }
}
