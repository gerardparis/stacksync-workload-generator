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
    
    private String kindOfClient = "random";

    public WorkloadGenerator(Broker broker, int numUsers) throws SQLException, AlreadyBoundException, omq.exception.AlreadyBoundException, RemoteException{
        this(broker, numUsers, "random");
    }
    
    public WorkloadGenerator(Broker broker, int numUsers, String kindOfClient) throws SQLException,
            AlreadyBoundException, omq.exception.AlreadyBoundException, RemoteException {
        this.broker = broker;

        usersId = new UUID[numUsers];
        workspacesId = new UUID[numUsers];
        devicesId = new UUID[numUsers];
        
        this.kindOfClient= kindOfClient;
        
        if(kindOfClient.equals("random_separated_queues") || kindOfClient.equals("no_conflict_separated_queues")){
            syncService = broker.lookup(Config.getProperties().getProperty("omq.queueName")+0, ISyncService.class);
        } else {
            syncService = broker.lookup("ISyncService", ISyncService.class);   
        }
        
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

    public void startExperiment(int numThreads, int numUsers, int commitsPerThread) throws RemoteException {

        threads = new ClientDummyThreads[numThreads];

        int assignedUsers = 0;
        
        for (int i = 0; i < threads.length; i++) {
            
            //System.out.println(Config.getProperties().getProperty("omq.queueName")+Integer.toString(i));
            
            UUID[] threadUsersId;
            UUID[] threadWorkspaceId;
            UUID[] threadDevicesId;
            
            if(i+1==numThreads){
                threadUsersId = new UUID[numUsers-assignedUsers];
                threadWorkspaceId = new UUID[numUsers-assignedUsers];
                threadDevicesId = new UUID[numUsers-assignedUsers];
                
                System.arraycopy( usersId, assignedUsers, threadUsersId, 0, numUsers-assignedUsers);
                System.arraycopy( workspacesId, assignedUsers, threadWorkspaceId, 0, numUsers-assignedUsers);
                System.arraycopy( devicesId, assignedUsers, threadDevicesId, 0, numUsers-assignedUsers);
            } else {
                threadUsersId = new UUID[numUsers/numThreads];
                threadWorkspaceId = new UUID[numUsers/numThreads];
                threadDevicesId = new UUID[numUsers/numThreads];
                assignedUsers += numUsers/numThreads;
                
                System.arraycopy( usersId, assignedUsers, threadUsersId, 0, numUsers/numThreads);
                System.arraycopy( workspacesId, assignedUsers, threadWorkspaceId, 0, numUsers/numThreads);
                System.arraycopy( devicesId, assignedUsers, threadDevicesId, 0, numUsers/numThreads);
            }
            
            if(kindOfClient.equals("random")){
                threads[i] = new ClientDummyThreads(commitsPerThread, threadUsersId, threadWorkspaceId, threadDevicesId, broker.lookup("ISyncService", ISyncService.class), logger);
            } else if(kindOfClient.equals("no_conflict")){
                threads[i] = new ClientDummyThreadsNoConflict(commitsPerThread, threadUsersId, threadWorkspaceId, threadDevicesId, broker.lookup("ISyncService", ISyncService.class), logger);
            } else if(kindOfClient.equals("random_separated_queues")){
                threads[i] = new ClientDummyThreads(commitsPerThread, threadUsersId, threadWorkspaceId, threadDevicesId, broker.lookup(Config.getProperties().getProperty("omq.queueName")+Integer.toString(i), ISyncService.class), logger); 
            } else if(kindOfClient.equals("no_conflict_separated_queues")){
                threads[i] = new ClientDummyThreadsNoConflict(commitsPerThread, threadUsersId, threadWorkspaceId, threadDevicesId, broker.lookup(Config.getProperties().getProperty("omq.queueName")+Integer.toString(i), ISyncService.class), logger); 
            }
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
        /*if (args.length != 3) {
            System.err.println("Usage: numThreads numUsers totalCommits");//("Usage: commitsPerSecond numUsers minutes threads");
            System.exit(0);
        }*/

        //int commitsPerSecond = Integer.parseInt(args[0]);
        //int minutes = Integer.parseInt(args[2]);
        int numThreads = Integer.parseInt(args[0]);
        int numUsers = Integer.parseInt(args[1]);
        int totalCommits = Integer.parseInt(args[2]);
        
        Config.loadProperties("./config.properties");
        Broker broker = new Broker(Config.getProperties());
        
        if(args.length > 3){
            System.out.println("Type of client: " + args[3] );
            String kindOfClient = args[3];
            WorkloadGenerator client = new WorkloadGenerator(broker, numUsers, kindOfClient);
            client.startExperiment(numThreads, numUsers, totalCommits / numThreads);
        } else {
            WorkloadGenerator client = new WorkloadGenerator(broker, numUsers);
            client.startExperiment(numThreads, numUsers, totalCommits / numThreads);
        }



    }
}
