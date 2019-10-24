package org.wso2.carbon.sp.jobmanager.core.allocation;

import java.util.LinkedList;

/**
 * A class that represents a Branch & Bound Node.
 */
public class BranchAndBoundNode implements Comparable<BranchAndBoundNode> {
    private double upperBound;
    private double profitValue;
    private LinkedList<PartialSiddhiApp> partialSiddhiAppsOfNode;

    /**
     * Constructor that creates a new branch & bound node with a upperBound, profitValue and the set of partial siddhi apps.
     *
     * @param upperBound
     * @param profitValue
     * @param partialSiddhiAppsOfNode
     */
    public BranchAndBoundNode(double upperBound, double profitValue, LinkedList<PartialSiddhiApp> partialSiddhiAppsOfNode) {
        this.upperBound = upperBound;
        this.profitValue = profitValue;
        this.partialSiddhiAppsOfNode = partialSiddhiAppsOfNode;
    }

    @Override
    public int compareTo(BranchAndBoundNode branchAndBoundNode) {
        return 0;
    }

    /**
     * Method that returns the upper bound of the node
     *
     * @return
     */
    public double getUpperBound() {
        return upperBound;
    }

    /**
     * Sets the upper bound to the branch and bound node
     *
     * @param upperBound
     */
    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
    }

    /**
     * Method that returns the profit value of the branch and bound node
     *
     * @return
     */
    public double getProfitValue() {
        return profitValue;
    }

    /**
     * Sets the profit value to the branch and bound node
     *
     * @param profitValue
     */
    public void setProfitValue(double profitValue) {
        this.profitValue = profitValue;
    }

    /**
     * Method that returns the set of partial siddhi apps of the branch and bound node
     *
     * @return
     */
    public LinkedList<PartialSiddhiApp> getPartialSiddhiAppsOfNode() {
        return partialSiddhiAppsOfNode;
    }

    /**
     * Method that sets the branch and bound node's partial siddhi apps to the node
     *
     * @param partialSiddhiAppsOfNode
     */
    public void setPartialSiddhiAppsOfNode(LinkedList<PartialSiddhiApp> partialSiddhiAppsOfNode) {
        this.partialSiddhiAppsOfNode = partialSiddhiAppsOfNode;
    }
}
