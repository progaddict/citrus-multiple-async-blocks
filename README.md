# This super-duper-small project reproduces the Citrus bug with 2 async blocks

### The bug:
https://github.com/citrusframework/citrus/issues/550

### The bug extermination plan:
1. `git clone https://github.com/progaddict/citrus-multiple-async-blocks.git`
1. `cd citrus-multiple-async-blocks`
1. make sure ports `8080` (SUT) and `8085` (mock server) are available
1. `mvn clean install payara-micro:start`
1. wait for the SUT to start
1. go to your favorite IDE (e.g. Intellij IDEA)
1. run the tests in `org.fixiewixie.FixieWixie` and watch 2 of them fail
1. the fun part: fix the failing tests :smiley:
1. release a new version of Citrus with the fix
1. ???
1. PROFIT!!!

### WTF is SUT?
The SUT (System Under Test) is an example toy app which keeps two sets of strings in memory:
* one for submitted jobs (further referred as `pendingJobs`)
* one for completed jobs (further referred as `completedJobs`)

The app has the `org.fixiewixie.JobProcessor` background job (Java EE timer job) which gets triggered every `5` seconds. One invocation of the job does the following:
1. Take all currently submitted jobs from the `pendingJobs` (a set of strings)
1. Sort them
1. Take the first one
1. And "process" it:
    1. Make an HTTP POST request (synchronously) to an external system (`http://localhost:8085/`). This simulates the situation when SUT needs to ask an external system for some additional data.
    1. Mark the job as completed:
        1. Delete it from the `pendingJobs`
        1. Add it to the `completedJobs`
1. Simulate (relatively) long running computations via `Thread.sleep`

The SUT has HTTP endpoints for submitting a job, cleaning all jobs and getting a list of pending and completed jobs which are used in Citrus tests.
