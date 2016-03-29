/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stacksync.syncservice.dummy.client;

import com.stacksync.commons.models.ItemMetadata;
import com.stacksync.commons.omq.ISyncService;
import com.stacksync.commons.requests.CommitRequest;
import static com.stacksync.syncservice.dummy.client.WorkloadGenerator.CHUNK_SIZE;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.apache.log4j.Logger;

/**
 *
 * @author Laura Martínez Sanahuja <lauramartinezsanahuja@gmail.com>
 */
public class ClientDummyThreadsNoConflict extends ClientDummyThreads {

    public ClientDummyThreadsNoConflict(int totalCommits, UUID[] users, UUID[] workspaces, UUID[] devices, ISyncService syncService, Logger logger) {
	super(totalCommits, users, workspaces, devices, syncService, logger);
    }

    @Override
    public void run() {
	Random ran = new Random(System.currentTimeMillis());

	for (int j = 0; j < totalCommits; j++) {
	    doCommit(usersId[j%usersId.length], workspacesId[j%usersId.length], devicesId[j%usersId.length], ran, 1, 8);
	}

    }

    public void doCommit(UUID userId, UUID workspaceId, UUID deviceId, Random ran, int min, int max) {
	
	// Create a ItemMetadata List
	List<ItemMetadata> items = new ArrayList<ItemMetadata>();
	items.add(createItemMetadata(ran, min, max, userId));

	// Create a CommitRequest
	CommitRequest commitRequest = new CommitRequest(userId, workspaceId, deviceId, items);

	logger.info("RequestID=" + commitRequest.getRequestId());
	syncService.commit(commitRequest);

    }
}
