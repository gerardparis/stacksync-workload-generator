/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stacksync.syncservice.dummy.client;

import com.stacksync.syncservice.dummy.infinispan.Start;
import omq.server.RemoteObject;

/**
 *
 * @author Laura Martínez Sanahuja <lauramartinezsanahuja@gmail.com>
 */
public class StartImpl extends RemoteObject implements Start {

    private int numNodes;
    private boolean warmUpRunning, experimentRunning;

    public StartImpl(int numNodes) {
        this.numNodes = numNodes;
        this.warmUpRunning = false;
    }

    @Override
    public void startWarmUp(int numThreads, int numUsers, int commitsPerSecond, int minutes) {
        this.warmUpRunning = true;

    }

    @Override
    public void startExperiment() {
        this.experimentRunning = true;
    }

    public boolean isWarmUpRunning() {
        return this.warmUpRunning;
    }

    public boolean isExperimentRunning() {
        return this.experimentRunning;
    }
}
