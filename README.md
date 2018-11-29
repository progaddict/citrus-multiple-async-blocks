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
