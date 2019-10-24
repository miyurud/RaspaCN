package org.wso2.carbon.sp.jobmanager.core.allocation;

import org.apache.log4j.Logger;
import org.wso2.carbon.sp.jobmanager.core.model.ResourceNode;

import java.util.*;

/**
 * Multiple Knapsack Allocation algorithm implementation
 */
public class MultipleKnapsack {

    private static final Logger log = Logger.getLogger(MetricsBasedAllocationAlgorithm.class);
    private LinkedList<Knapsack> knapsacks;
    private LinkedList<PartialSiddhiApp> partialSiddhiApps;
    private double latency;

    double upperBound;
    double throughputValue;
    double initialUpperBound = Double.MAX_VALUE;
    int noOfMaxLevels;
    int level = 0;

    List<BranchAndBoundNode> branchAndBoundTreeNodes = Collections.synchronizedList(new ArrayList<>());
    List<BranchAndBoundNode> tempTreeNodes = Collections.synchronizedList(new ArrayList<>());
    List<BranchAndBoundNode> feasibleTreeNodes = Collections.synchronizedList(new ArrayList<>());
    List<BranchAndBoundNode> toRemove = new ArrayList();
    List<BranchAndBoundNode> toRemove1 = new ArrayList();
    public Map<ResourceNode, List<PartialSiddhiApp>> output_map = new HashMap<>();

    /**
     * Method that gets all neighbors a current solution to the Multiple Knapsack Problem has.
     *
     * @param knapsacks
     * @param partialSiddhiApps
     * @return
     */
    public LinkedList<MultipleKnapsack> getNeighbors(LinkedList<Knapsack> knapsacks, LinkedList<PartialSiddhiApp> partialSiddhiApps) {

        LinkedList<MultipleKnapsack> knapsackNeighbors = new LinkedList<>();

        for (int gKnapsack = 0; gKnapsack < knapsacks.size(); gKnapsack++) {
            for (int gItem = 0; gItem < knapsacks.get(gKnapsack).getpartialSiddhiApps().size(); gItem++) {
                for (int lKnapsack = 0; lKnapsack < knapsacks.size(); lKnapsack++) {
                    for (int lItem = 0; lItem < knapsacks.get(lKnapsack).getpartialSiddhiApps().size(); lItem++) {

                        Knapsack globalKnapsack = knapsacks.get(gKnapsack);
                        Knapsack localKnapsack = knapsacks.get(lKnapsack);

                        if (!globalKnapsack.equals(localKnapsack)) {
                            LinkedList<PartialSiddhiApp> globalpartialSiddhiApps = globalKnapsack.getpartialSiddhiApps();
                            LinkedList<PartialSiddhiApp> localpartialSiddhiApps = localKnapsack.getpartialSiddhiApps();
                            if (globalpartialSiddhiApps.get(gItem).getcpuUsage() <= localpartialSiddhiApps.get(lItem).getcpuUsage() + localKnapsack.getcapacity()) {

                                MultipleKnapsack neighbor = new MultipleKnapsack();
                                LinkedList<Knapsack> currentKnapsack = new LinkedList<>();
                                LinkedList<PartialSiddhiApp> currentpartialSiddhiApps = new LinkedList<>(partialSiddhiApps);
                                for (Knapsack knapsack : knapsacks) {
                                    if (knapsack.equals(localKnapsack)) {
                                        Knapsack local = new Knapsack(knapsack);
                                        local.setcapacity(localKnapsack.getcapacity() + localpartialSiddhiApps.get(lItem).getcpuUsage() - globalpartialSiddhiApps.get(gItem).getcpuUsage());
                                        local.getpartialSiddhiApps().set(lItem, globalpartialSiddhiApps.get(gItem));
                                        currentKnapsack.add(local);
                                    } else if (knapsack.equals(globalKnapsack)) {
                                        Knapsack global = new Knapsack(knapsack);
                                        global.setcapacity(global.getcapacity() + global.getpartialSiddhiApps().get(gItem).getcpuUsage());
                                        global.getpartialSiddhiApps().remove(gItem);
                                        currentKnapsack.add(global);
                                    } else {
                                        currentKnapsack.add(new Knapsack(knapsack));
                                    }
                                }

                                neighbor.setKnapsacks(currentKnapsack);
                                neighbor.setpartialSiddhiApps(currentpartialSiddhiApps);
                                neighbor.shufflepartialSiddhiAppsInKnapsacks();
                                neighbor.greedyMultipleKnapsack(currentpartialSiddhiApps);
                                neighbor.calculatelatency();
                                knapsackNeighbors.add(neighbor);
                            }
                        }
                    }
                }
            }
        }

        return knapsackNeighbors;
    }

    /**
     * Method that tries to find neighbors for a solution that have a better outcome than the solution itself.
     *
     * @param knapsacks
     * @return
     */
    public MultipleKnapsack neighborSearch(MultipleKnapsack knapsacks) {
        LinkedList<MultipleKnapsack> neighbors = getNeighbors(knapsacks.getKnapsacks(), knapsacks.getpartialSiddhiApps());
        for (MultipleKnapsack neighbor : neighbors) {
            if (neighbor.getlatency() > knapsacks.getlatency()) {
                knapsacks = neighborSearch(neighbor);
            }
        }
        return knapsacks;
    }

    /**
     * Method that shuffles or packs the partialSiddhiApps so that there's space for other partialSiddhiApps to be added.
     */
    public void shufflepartialSiddhiAppsInKnapsacks() {
        LinkedList<PartialSiddhiApp> partialSiddhiAppsInKnapsacks = new LinkedList<>();
        for (Knapsack knapsack : knapsacks) {
            partialSiddhiAppsInKnapsacks.addAll(knapsack.getpartialSiddhiApps());
        }
        Collections.sort(partialSiddhiAppsInKnapsacks, new Comparator<PartialSiddhiApp>() {
            @Override
            public int compare(PartialSiddhiApp i1, PartialSiddhiApp i2) {
                if (i1.getcpuUsage() > i2.getcpuUsage()) {
                    return -1;
                } else if (i2.getcpuUsage() > i1.getcpuUsage()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        for (Knapsack knapsack : knapsacks) {
            knapsack.getpartialSiddhiApps().clear();
            knapsack.resetcapacity();
            for (Iterator<PartialSiddhiApp> it = partialSiddhiAppsInKnapsacks.iterator(); it.hasNext(); ) {
                PartialSiddhiApp item = it.next();
                if (item.getcpuUsage() <= knapsack.getcapacity()) {
                    knapsack.addPartialSiddhiApps(item);
                    it.remove();
                }
            }
        }
    }

    /**
     * Method that solves the Multiple Knapsack Problem by a greedy approach.
     *
     * @param partialSiddhiApps
     */
    public void greedyMultipleKnapsack(LinkedList<PartialSiddhiApp> partialSiddhiApps) {

        Collections.sort(partialSiddhiApps, new Comparator<PartialSiddhiApp>() {
            @Override
            public int compare(PartialSiddhiApp i1, PartialSiddhiApp i2) {
                if (i1.getlatencyBycpuUsage() > i2.getlatencyBycpuUsage()) {
                    return -1;
                } else if (i2.getlatencyBycpuUsage() > i2.getlatencyBycpuUsage()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        Knapsack bestKnapsack;
        double bestcpuUsageDifference;
        double currentcpuUsageDifference;

        for (int i = 0; i < partialSiddhiApps.size(); i++) {
            if (!this.partialSiddhiApps.contains(partialSiddhiApps.get(i))) {
                this.partialSiddhiApps.add(partialSiddhiApps.get(i));
            }
            bestcpuUsageDifference = Integer.MAX_VALUE;
            bestKnapsack = null;
            for (int j = 0; j < knapsacks.size(); j++) {
                if (knapsacks.get(j).getcapacity() >= partialSiddhiApps.get(i).getcpuUsage()) {
                    currentcpuUsageDifference = knapsacks.get(j).getcapacity() - partialSiddhiApps.get(i).getcpuUsage();
                    if (currentcpuUsageDifference < bestcpuUsageDifference && currentcpuUsageDifference > 0) {
                        bestcpuUsageDifference = currentcpuUsageDifference;
                        bestKnapsack = knapsacks.get(j);
                    }
                }
            }
            if (bestKnapsack != null) {
                bestKnapsack.addPartialSiddhiApps(partialSiddhiApps.get(i));
                this.partialSiddhiApps.remove(partialSiddhiApps.get(i));
            }
        }
    }

    /**
     * Method that solves the Multiple Knapsack Problem by a branch and bound approach.
     *
     * @param partialSiddhiApps
     */
    public LinkedList<PartialSiddhiApp> executeBranchAndBoundKnapsack(LinkedList<PartialSiddhiApp> partialSiddhiApps) {

        for (int i = 0; i < partialSiddhiApps.size(); i++) {
            if (!this.partialSiddhiApps.contains(partialSiddhiApps.get(i))) {
                this.partialSiddhiApps.add(partialSiddhiApps.get(i));
            }
        }

        Collections.sort(this.partialSiddhiApps, new Comparator<PartialSiddhiApp>() {
            @Override
            public int compare(PartialSiddhiApp i1, PartialSiddhiApp i2) {
                if (i1.getcpuUsage() > i2.getcpuUsage()) {
                    return -1;
                } else if (i2.getcpuUsage() > i1.getcpuUsage()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        for (Knapsack knapsack : knapsacks) {
            noOfMaxLevels = this.partialSiddhiApps.size();
            if (this.partialSiddhiApps.size() == 0) {
                break;
            }
            log.info("Executing graph traversal with " + this.partialSiddhiApps.size() + " partial siddhi apps..........");
            initialUpperBound = Double.MAX_VALUE;
            level = 0;
            LinkedList<PartialSiddhiApp> tempPartialSiddhiApps = new LinkedList<>();
            tempPartialSiddhiApps.addAll(this.partialSiddhiApps);

            feasibleTreeNodes = graphTraversal(this.partialSiddhiApps, knapsack);

            this.partialSiddhiApps.clear();
            this.partialSiddhiApps.addAll(tempPartialSiddhiApps);
            ArrayList<Double> throughputValues = new ArrayList<>();
            BranchAndBoundNode finalTreeNode = null;

            for (BranchAndBoundNode feasibleNode : feasibleTreeNodes) {
                double weight = 0.0;
                double currentThroughput = 0.0;
                if (feasibleNode.getPartialSiddhiAppsOfNode().size() == 0) {
                    toRemove.add(feasibleNode);
                    continue;
                }

                for (PartialSiddhiApp app : feasibleNode.getPartialSiddhiAppsOfNode()) {
                    weight += app.getcpuUsage();
                    currentThroughput += app.getThroughput();
                }
                if (weight > knapsack.getcapacity()) {
                    toRemove.add(feasibleNode);
                } else {
                    throughputValues.add(currentThroughput);
                }
            }
            feasibleTreeNodes.removeAll(toRemove);
            if (toRemove.size() != 0) {
                toRemove.clear();
            }
            double finalThroughputValue = 0.0;
            if (throughputValues.size() != 0) {
                finalThroughputValue = Collections.max(throughputValues);
            } else {
                branchAndBoundTreeNodes.clear();
                feasibleTreeNodes.clear();
                continue;
            }
            for (BranchAndBoundNode fNode : feasibleTreeNodes) {
                double finalThroughput = 0.0;
                for (PartialSiddhiApp app : fNode.getPartialSiddhiAppsOfNode()) {
                    finalThroughput += app.getThroughput();
                }
                if (finalThroughputValue == finalThroughput) {
                    finalTreeNode = fNode;
                    break;
                }
            }
            double totalCPU = 0.0;
            for (PartialSiddhiApp app : finalTreeNode.getPartialSiddhiAppsOfNode()) {
                knapsack.addPartialSiddhiApps(app);
                this.partialSiddhiApps.remove(app);
                totalCPU += app.getcpuUsage();
            }
            branchAndBoundTreeNodes.clear();
            feasibleTreeNodes.clear();
        }
        return this.partialSiddhiApps;
    }

    /**
     * Method that traverses branch and bound decision tree to find all possible solutions
     *
     * @param partialSiddhiApps
     * @param knapsack
     * @return
     */
    public List<BranchAndBoundNode> graphTraversal(LinkedList<PartialSiddhiApp> partialSiddhiApps, Knapsack knapsack) {
        log.info("Starting graph traversal.....");
        if (branchAndBoundTreeNodes.size() != 0) {
            for (BranchAndBoundNode branchAndBoundNode : branchAndBoundTreeNodes) {
                if (branchAndBoundNode.getProfitValue() > initialUpperBound) {
                    toRemove1.add(branchAndBoundNode);
                }
            }
            branchAndBoundTreeNodes.removeAll(toRemove1);
            if (toRemove1.size() != 0) {
                toRemove1.clear();
            }
        }

        if (level < noOfMaxLevels) {
            upperBound = calculateUpperBound(partialSiddhiApps, knapsack);
            if (upperBound != 0.0) {
                upperBound = -1 * upperBound;
            }
            throughputValue = calculateThroughputValue(partialSiddhiApps, knapsack);

            if (throughputValue != 0.0) {
                throughputValue = -1 * throughputValue;
            }

            if (initialUpperBound > upperBound) {
                initialUpperBound = upperBound;
            }
            if (throughputValue <= initialUpperBound) {
                int index = branchAndBoundTreeNodes.size();

                LinkedList<PartialSiddhiApp> newPartialSiddhiApps = new LinkedList<>();
                newPartialSiddhiApps.addAll(partialSiddhiApps);

                branchAndBoundTreeNodes.add(index, new BranchAndBoundNode(upperBound, throughputValue, newPartialSiddhiApps));
                tempTreeNodes.add(index, new BranchAndBoundNode(upperBound, throughputValue, newPartialSiddhiApps));
            }
            if (level < getpartialSiddhiApps().size()) {
                partialSiddhiApps.remove(getpartialSiddhiApps().get(level));
            }

            level++;
            upperBound = calculateUpperBound(partialSiddhiApps, knapsack);
            if (upperBound != 0.0) {
                upperBound = -1 * upperBound;
            }
            throughputValue = calculateThroughputValue(partialSiddhiApps, knapsack);

            if (throughputValue != 0.0) {
                throughputValue = -1 * throughputValue;
            }

            if (initialUpperBound > upperBound) {
                initialUpperBound = upperBound;
            }

            if ((throughputValue) <= initialUpperBound) {
                LinkedList<PartialSiddhiApp> newPartialSiddhiApps1 = new LinkedList<>();
                newPartialSiddhiApps1.addAll(partialSiddhiApps);
                int index1 = branchAndBoundTreeNodes.size();
                branchAndBoundTreeNodes.add(index1, new BranchAndBoundNode(upperBound, throughputValue, newPartialSiddhiApps1));
                tempTreeNodes.add(index1, new BranchAndBoundNode(upperBound, throughputValue, newPartialSiddhiApps1));
            }

            while (tempTreeNodes.size() != 0) {
                log.info("tempTreeNodes is not empty...................................");

                if (tempTreeNodes.get(0).getPartialSiddhiAppsOfNode().size() != 0) {
                    graphTraversal(tempTreeNodes.get(0).getPartialSiddhiAppsOfNode(), knapsack);
                }
                if (tempTreeNodes.size() != 0) {
                    tempTreeNodes.remove(tempTreeNodes.get(0));
                } else {
                    break;
                }
            }
            log.info("tempTreeNodes is empty....................");
        }
        return branchAndBoundTreeNodes;

    }

    /**
     * Method that calculates a MultipleKnapsack's total latency.
     */
    public void calculatelatency() {
        double latency = 0;
        for (Knapsack knapsack : knapsacks) {
            for (PartialSiddhiApp item : knapsack.getpartialSiddhiApps()) {
                latency += item.getlatency();
            }
        }

        this.latency = latency;
    }

    /**
     * Method that prints out the result of a MultipleKnapsack.
     */
    public LinkedList<PartialSiddhiApp> printResult(boolean flag) {
        for (Knapsack knapsack : knapsacks) {
            ;
            log.info("\nResourceNode\n" + "resourceNode: " + knapsack.getresourceNode()
                    + "\nTotal Usable CPU : " + knapsack.getStartcpuUsage() +
                    "\nUsed CPU in this iteration: " + (knapsack.getStartcpuUsage() - knapsack.getcapacity()) +
                    "\nRemaining CPU : " + knapsack.getcapacity() + "\n");

            knapsack.setStartcpuUsage(knapsack.getcapacity());

            log.info("Initial CPU Usage of " + knapsack.getresourceNode() + " is set to " + knapsack.getStartcpuUsage() + "\n");
            try {
                for (PartialSiddhiApp item : knapsack.getpartialSiddhiApps()) {
                    log.info("\n\nPartial siddhi app\n" + "Name: " + item.getName()
                            + "\nLatency : " + item.getlatency() + "\nCPU Usage : " + item.getcpuUsage());
                    partialSiddhiApps.remove(item);
                    log.info("removing " + item.getName() + " from partialsiddhiapps list");
                    log.info("\n");
                }

            } catch (ConcurrentModificationException e) {
                e.printStackTrace();
            }
            log.info("---------------------------\n");
        }

        log.info("Total latency: " + latency);
        return partialSiddhiApps;
    }


    public Map<ResourceNode, List<PartialSiddhiApp>> updatemap(Map<ResourceNode, List<PartialSiddhiApp>> map) {
        for (Knapsack knapsack : knapsacks) {
            List<PartialSiddhiApp> temp = new LinkedList<>();
            for (PartialSiddhiApp item : knapsack.getpartialSiddhiApps()) {
                temp.add(item);
            }
            if (map.containsKey(knapsack.getresourceNode())) {
                for (PartialSiddhiApp partialSiddhiApps : temp) {
                    map.get(knapsack.getresourceNode()).add(partialSiddhiApps);
                    log.info("Updating " + knapsack.getresourceNode().getId() + " with " +
                            partialSiddhiApps.getName());
                }
            } else {
                map.put(knapsack.getresourceNode(), temp);
                log.info("Adding to" + knapsack.getresourceNode().getId());
            }
        }
        return map;
    }

    /**
     * Method that sets the partialSiddhiApps that are not in a knapsack already.
     *
     * @param partialSiddhiApps
     */
    public void setpartialSiddhiApps(LinkedList<PartialSiddhiApp> partialSiddhiApps) {
        this.partialSiddhiApps = partialSiddhiApps;
    }

    /**
     * Method that sets all of the knapsacks.
     *
     * @param knapsacks
     */
    public void setKnapsacks(LinkedList<Knapsack> knapsacks) {
        this.knapsacks = knapsacks;
    }

    /**
     * Method that gets the total latency of a MultipleKnapsack.
     *
     * @return
     */
    public double getlatency() {
        return latency;
    }

    /**
     * Constructor that instantiates necessary objects.
     */
    public MultipleKnapsack() {
        knapsacks = new LinkedList<>();
        partialSiddhiApps = new LinkedList<>();
    }

    /**
     * Method that gets all of the knapsacks in the MultipleKnapsack.
     *
     * @return
     */
    public LinkedList<Knapsack> getKnapsacks() {
        return knapsacks;
    }

    /**
     * Method that gets all of the partialSiddhiApps that are not in a knapsack already.
     *
     * @return
     */
    public LinkedList<PartialSiddhiApp> getpartialSiddhiApps() {
        return partialSiddhiApps;
    }

    /**
     * Method that adds a knapsack into the MultipleKnapsack.
     *
     * @param knapsack
     */
    public void addKnapsack(Knapsack knapsack) {
        knapsacks.add(knapsack);
    }

    public Map<ResourceNode, List<PartialSiddhiApp>> getMap() {
        return output_map;
    }

    /**
     * Method that calculates the throughput value for a set of partial siddhi apps related with a knapsack(K)
     *
     * @param partialSiddhiApps
     * @param knapsack
     * @return
     */
    public double calculateThroughputValue(LinkedList<PartialSiddhiApp> partialSiddhiApps, Knapsack knapsack) {
        double maxCPUUsage = 0.0;
        double upperBound = 0.0;
        double lastThroughputValue = 0.0;
        double lastCPUUsage = 0.0;
        double remainingThroughput = 0.0;
        double remainingCPUUsage = 0.0;
        double throughputValue = 0.0;
        Collections.sort(partialSiddhiApps, new Comparator<PartialSiddhiApp>() {
            @Override
            public int compare(PartialSiddhiApp i1, PartialSiddhiApp i2) {
                if (i1.getcpuUsage() > i2.getcpuUsage()) {
                    return -1;
                } else if (i2.getcpuUsage() > i1.getcpuUsage()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        for (PartialSiddhiApp app : partialSiddhiApps) {
            if (knapsack.getcapacity() > maxCPUUsage && knapsack.getcapacity() > app.getcpuUsage()) {

                maxCPUUsage += app.getcpuUsage();
                upperBound += app.getThroughput();
                lastThroughputValue = app.getThroughput();
                lastCPUUsage = app.getcpuUsage();
            } else {
                if (knapsack.getcapacity() < maxCPUUsage) {
                    upperBound -= lastThroughputValue;
                    maxCPUUsage -= lastCPUUsage;
                    lastThroughputValue = 0.0;
                    lastCPUUsage = 0.0;
                }
                remainingThroughput += app.getThroughput();
                remainingCPUUsage += app.getcpuUsage();
            }
            if (remainingCPUUsage != 0) {
                throughputValue = upperBound + (remainingThroughput / remainingCPUUsage) * (knapsack.getcapacity() - maxCPUUsage);
            } else {
                throughputValue = upperBound;
            }
        }
        return throughputValue;
    }

    /**
     * Method that calculates the upper bound for a set of partial siddhi apps related with a knapsack(K)
     *
     * @param updatedPartialSiddhiApps
     * @param knapsack
     * @return
     */
    public double calculateUpperBound(LinkedList<PartialSiddhiApp> updatedPartialSiddhiApps, Knapsack knapsack) {
        double maxCPUUsage = 0.0;
        double upperBound = 0.0;
        double lastThroughputValue = 0.0;
        Collections.sort(updatedPartialSiddhiApps, new Comparator<PartialSiddhiApp>() {
            @Override
            public int compare(PartialSiddhiApp i1, PartialSiddhiApp i2) {
                if (i1.getcpuUsage() > i2.getcpuUsage()) {
                    return -1;
                } else if (i2.getcpuUsage() > i1.getcpuUsage()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        for (PartialSiddhiApp app : updatedPartialSiddhiApps) {

            if (knapsack.getcapacity() > maxCPUUsage && knapsack.getcapacity() > app.getcpuUsage()) {

                maxCPUUsage += app.getcpuUsage();
                upperBound += app.getThroughput();
                lastThroughputValue = app.getThroughput();
            } else {
                if (knapsack.getcapacity() < maxCPUUsage) {
                    upperBound -= lastThroughputValue;
                }
                break;
            }
        }
        return upperBound;
    }


}
