package org.fixiewixie;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Set;

@RequestScoped
@Path("job")
public class JobRest {

    @Inject
    private JobStore jobStore;

    @GET
    @Path("pending")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<String> getPending() {
        return jobStore.getPendingJobs();
    }

    @GET
    @Path("completed")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<String> getCompleted() {
        return jobStore.getCompletedJobs();
    }

    @POST
    public boolean submit(@QueryParam("name") String jobName) {
        if (jobName == null) {
            return Boolean.FALSE;
        }
        return jobStore.submitJob(jobName);
    }

    @DELETE
    public void clear() {
        jobStore.clear();
    }
}
