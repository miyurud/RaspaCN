# SP-Jobmanager
An advanced job scheduler for distributed stream processor to allocated partial Siddhi applications among
the workers to get the optimal throughput performance under the resource constraints we considered. The proposed scheduler
has been implemented using Knapsack algorithm with branch and bound based heuristic method.

In our implementation, we define a knapsack capacity for each worker based on the average CPU usage value of partial
Siddhi applications and partial Siddhi applications are considered as the items to be scheduled. Each item has a weight and a profit value and we define them as CPU usage and entire throughput for the run of each item respectively. The algorithm returns a subset which gives the maximum throughput such that the sum of the CPU usage values of this subset is smaller than or equal to the
Knapsack capacity that we defined.

The algorithm consists of following core parts for finding the subset out of n partial Siddhi applications with maximum through- put performance. After selecting the subset, the remaining partial Siddhi applications are scheduled in such a way to reduce the inter-node communication cost among the workers. The steps of this algorithm include the calculating the upper bound, calculating the profit value, finding all possible solutions and selecting the optimum solution.
