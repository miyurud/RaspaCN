/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.sp.jobmanager.core.allocation;

import org.wso2.carbon.sp.jobmanager.core.model.ResourceNode;

import java.util.LinkedList;

/**
 * A class that represents a Knapsack.
 */
public class Knapsack {

    private double capacity;
    private double startCPUUsage;
    private ResourceNode resourceNode;
    private LinkedList<PartialSiddhiApp> partialSiddhiApps;

    /**
     * Constructor that creates a new knapsack with a capacity, resourceNode and a startcpuUsage latency.
     *
     * @param capacity
     * @param resourceNode
     */
    public Knapsack(double capacity, ResourceNode resourceNode) {
        this.capacity = capacity;
        this.resourceNode = resourceNode;
        this.startCPUUsage = capacity;
        partialSiddhiApps = new LinkedList<>();
    }

    /**
     * Copy constructor which copies a knapsack object and creates a new identical one.
     *
     * @param knapsack
     */
    public Knapsack(Knapsack knapsack) {
        this.capacity = knapsack.getCapacity();
        this.startCPUUsage = knapsack.getStartcpuUsage();
        this.resourceNode = knapsack.getresourceNode();
        this.partialSiddhiApps = new LinkedList<>(knapsack.getPartialSiddhiApps());
    }

    /**
     * Adds an item to the item-list and updates the capacity so it's up to date.
     *
     * @param item
     */
    public void addPartialSiddhiApps(PartialSiddhiApp item) {
        if (item != null) {
            partialSiddhiApps.add(item);
            capacity = capacity - item.getcpuUsage();
            System.out.println();
        }
    }

    /**
     * Sets the capacity to the initial latency of the knapsack.
     */
    public void resetCapacity() {
        capacity = startCPUUsage;
    }

    /**
     * Sets the capacity to the latency provided to the method.
     *
     * @param capacity
     */
    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    /**
     * Method that returns the knapsack's startcpuUsage
     *
     * @return
     */
    public double getStartcpuUsage() {
        return startCPUUsage;
    }

    public void setStartcpuUsage(double startCPUUsage) {
        this.startCPUUsage = startCPUUsage;
    }

    /**
     * Method that returns the knapsack's capacity.
     *
     * @return
     */
    public double getCapacity() {
        return capacity;
    }

    /**
     * Method that returns the knapsack's resourceNode.
     *
     * @return
     */
    public ResourceNode getresourceNode() {
        return resourceNode;
    }

    /**
     * Method that returns the partialSiddhiApps the knapsack is currently holding.
     *
     * @return
     */
    public LinkedList<PartialSiddhiApp> getPartialSiddhiApps() {
        return partialSiddhiApps;
    }
}
