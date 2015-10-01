/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stacksync.syncservice.dummy.client;

import com.stacksync.commons.omq.ISyncService;
import com.stacksync.syncservice.dummy.infinispan.Notifier;
import com.stacksync.syncservice.dummy.infinispan.Start;
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
import omq.server.RemoteObject;
import org.apache.log4j.Logger;

/**
 *
 * @author Laura Martínez Sanahuja <lauramartinezsanahuja@gmail.com>
 */
public class ClientDummy extends RemoteObject implements Start {

    protected final Logger logger = Logger.getLogger(ClientDummy.class.getName());

    protected static final int CHUNK_SIZE = 512 * 1024;

    private Broker broker;
    private ISyncService[] shardProxies;
    private WorkspaceImpl[] workspaces;

    private Notifier notifier;
    private ClientDummyThreads[] threads;

    public ClientDummy(Broker broker, int numUsers) throws SQLException, NoStorageManagerAvailable,
	    AlreadyBoundException, omq.exception.AlreadyBoundException {
	this.broker = broker;

	try {
	    this.notifier = broker.lookup(Notifier.BINDING_NAME, Notifier.class);
	} catch (RemoteException ex) {
	    java.util.logging.Logger.getLogger(ClientDummy.class.getName()).log(Level.SEVERE, null, ex);
	}

	shardProxies = new ISyncService[numUsers];
	workspaces = new WorkspaceImpl[numUsers];

	for (int i = 0; i < numUsers; i++) {
	    try {
		Properties env = broker.getEnvironment();
		String syncServerExchange = env.getProperty(ParameterQueue.RPC_EXCHANGE, "rpc_global_exchange");

		UUID clientId = UUID.randomUUID();
		shardProxies[i] = broker.lookup(ISyncService.class.getSimpleName(), ISyncService.class);
		shardProxies[i].createUser(clientId);

		env.setProperty(ParameterQueue.RPC_EXCHANGE, "rpc_return_exchange");

		/*workspaces[i] = new WorkspaceImpl();
		broker.bind(clientId.toString(), workspaces[i]);

		env.setProperty(ParameterQueue.RPC_EXCHANGE, syncServerExchange);*/
	    } catch (RemoteException ex) {
		logger.error(ex);
	    }
	}
    }

    @Override
    public void startWarmUp(int numThreads, int numUsers, int commitsPerSecond, int minutes) {

	threads = new ClientDummyThreads[numThreads];

	System.out.println("Waiting 20 seconds to stabilize the system.");
	try {
	    Thread.sleep(20000);
	} catch (InterruptedException ex) {
	    java.util.logging.Logger.getLogger(ClientDummy.class.getName()).log(Level.SEVERE, null, ex);
	}
	System.out.println("Go.");

	for (int i = 0; i < threads.length; i++) {
	    //threads[i] = new ClientDummyThreads(commitsPerSecond, minutes, shardProxies, logger);
	}

	notifier.endWarmUp();
    }

    @Override
    public void startExperiment() {

	for (ClientDummyThreads thread : threads) {
	    thread.start();
	}

	for (ClientDummyThreads t : threads) {
	    try {
		t.join();
	    } catch (InterruptedException ex) {
		java.util.logging.Logger.getLogger(ClientDummy.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}

	notifier.endExperiment();

	BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
	System.out.println("Waiting the queue to finish. Press any key to stop waiting.");
	try {
	    read.readLine();
	} catch (IOException ex) {
	    java.util.logging.Logger.getLogger(ClientDummy.class.getName()).log(Level.SEVERE, null, ex);
	}

	try {
	    broker.stopBroker();
	} catch (Exception ex) {
	    java.util.logging.Logger.getLogger(ClientDummy.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public static void main(String[] args) throws Exception {
	if (args.length != 1) {
	    System.err.println("Usage: " +/*commitsPerSecond */ "numUsers"/* minutes threads*/);
	    System.exit(0);
	}

	//int commitsPerSecond = Integer.parseInt(args[0]);
	int numUsers = Integer.parseInt(args[0]);
	//int minutes = Integer.parseInt(args[2]);
	//int numThreads = Integer.parseInt(args[3]);
	Config.loadProperties("config.properties");
	Broker broker = new Broker(Config.getProperties());

	ClientDummy client = new ClientDummy(broker, numUsers);
	broker.bind(ClientDummy.BINDING_NAME, client);
    }

}
