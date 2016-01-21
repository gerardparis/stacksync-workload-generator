/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stacksync.syncservice.dummy.client;

import com.stacksync.commons.models.User;
import com.stacksync.commons.omq.ISyncService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AlreadyBoundException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import omq.common.broker.Broker;
import omq.common.util.ParameterQueue;
import omq.exception.RemoteException;
import org.apache.log4j.Logger;

/**
 *
 * @author Laura Martínez Sanahuja <lauramartinezsanahuja@gmail.com>
 */
public class WorkloadGenerator {

    protected final Logger logger = Logger.getLogger(WorkloadGenerator.class.getName());
    protected static final int CHUNK_SIZE = 512 * 1024;
    private ISyncService syncService;
    private UUID[] usersId;
    private UUID[] workspacesId;
    private UUID[] devicesId;
    private Broker broker;
    private ClientDummyThreads[] threads;

    public WorkloadGenerator(Broker broker, int numUsers) throws SQLException,
            AlreadyBoundException, omq.exception.AlreadyBoundException, RemoteException {
        this.broker = broker;

        usersId = new UUID[numUsers];
        workspacesId = new UUID[numUsers];
        devicesId = new UUID[numUsers];
        
        syncService = broker.lookup(ISyncService.class.getSimpleName(), ISyncService.class);
        for (int i = 0; i < numUsers; i++) {
            Properties env = broker.getEnvironment();
            String syncServerExchange = env.getProperty(ParameterQueue.RPC_EXCHANGE, "rpc_global_exchange");

            UUID[] client = syncService.createRandomUser();
            usersId[i] = client[0];
            workspacesId[i] = client[1];
            devicesId[i] =  client[2];
            
            env.setProperty(ParameterQueue.RPC_EXCHANGE, "rpc_return_exchange");

            /*workspaces[i] = new WorkspaceImpl();
             broker.bind(clientId.toString(), workspaces[i]);*/
            env.setProperty(ParameterQueue.RPC_EXCHANGE, syncServerExchange);
        }
        this.pressAnyKeyToContinue();
    }

    public void startExperiment(int numThreads, int numUsers, int commitsPerThread) {

        threads = new ClientDummyThreads[numThreads];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new ClientDummyThreads(commitsPerThread, usersId, workspacesId, devicesId, syncService, logger);
            threads[i].start();
        }

        for (ClientDummyThreads t : threads) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(WorkloadGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Waiting the queue to finish. Press any key to stop waiting.");
        try {
            read.readLine();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(WorkloadGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            broker.stopBroker();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(WorkloadGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void pressAnyKeyToContinue() {
        System.out.println("Press any key to continue...");
        try {
            System.in.read();
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Usage: numThreads numUsers totalCommits");//("Usage: commitsPerSecond numUsers minutes threads");
            System.exit(0);
        }

        //int commitsPerSecond = Integer.parseInt(args[0]);
        //int minutes = Integer.parseInt(args[2]);
        int numThreads = Integer.parseInt(args[0]);
        int numUsers = Integer.parseInt(args[1]);
        int totalCommits = Integer.parseInt(args[2]);
        Config.loadProperties("../config.properties");
        Broker broker = new Broker(Config.getProperties());

        WorkloadGenerator client = new WorkloadGenerator(broker, numUsers);
        client.startExperiment(numThreads, numUsers, totalCommits / numThreads);
    }
}
