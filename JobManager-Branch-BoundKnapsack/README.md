# SP-Jobmanager
An advanced job scheduler for distributed stream processor to allocated partial Siddhi applications among
the workers to get the optimal throughput performance under the resource constraints we considered. The proposed scheduler
has been implemented using Knapsack algorithm with branch and bound method.

In our implementation, we define a knapsack capacity for each worker based on the average CPU usage value of partial
Siddhi applications and partial Siddhi applications are considered as the items to be scheduled. Each item has a weight and a profit value and we define them as CPU usage and entire throughput for the run of each item respectively. The algorithm returns a subset which gives the maximum throughput such that the sum of the CPU usage values of this subset is smaller than or equal to the
Knapsack capacity that we defined.

The algorithm consists of core parts for finding the subset out of n partial Siddhi applications with maximum throughput performance. After selecting the subset, the remaining partial Siddhi applications are scheduled in such a way to reduce the inter-node communication cost among the workers. The steps of this algorithm include the calculating the upper bound, calculating the profit value, finding all possible solutions and selecting the optimum solution.

### Build Command
```
mvn clean install -Dcheckstyle.skip=true -Dfindbugs.skip=true
```

### Prerequisites
- Java 8 or above
- [Apache Maven](https://maven.apache.org/download.cgi#) 3.x.x
- [Node.js](https://nodejs.org/en/) 8.x.x or above

### Run Job Manager
- Download and build the repo
   - https://github.com/wso2/carbon-analytics.git

- Download Job Manager and replace carbon-analytics/components/org.wso2.carbon.sp.jobmanager.core by it
   - https://github.com/miyurud/RaspaCN/tree/master/JobManager-Branch-BoundKnapsack

- Rename JobManager-Branch-BoundKnapsack as org.wso2.carbon.sp.jobmanager.core and build carbon-analytics/components org.wso2.carbon.sp.jobmanager.core

- Copy and paste carbon-analytics/components/org.wso2.carbon.sp.jobmanager.core/target/ jar file to sp/wso2/lib/plugins in manager

- Start SP Cluster
