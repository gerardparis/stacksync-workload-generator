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
public class ClientDummyThreads extends Thread {

    protected int totalCommits;//commitsPerSecond, minutes;
    protected UUID[] usersId;
    protected UUID[] workspacesId;
    protected UUID[] devicesId;
    protected ISyncService syncService;
    protected final Logger logger;

    public ClientDummyThreads(int totalCommits, UUID[] users, UUID[] workspaces, UUID[] devices, ISyncService syncService, Logger logger) {
	this.totalCommits = totalCommits;
	this.usersId = users;
        this.workspacesId = workspaces;
        this.devicesId = devices;
	this.syncService = syncService;
	this.logger = logger;
    }

    @Override
    public void run() {
	Random ran = new Random(System.currentTimeMillis());

	for (int j = 0; j < totalCommits; j++) {
            int randomPos = ran.nextInt(usersId.length);
            
	    doCommit(usersId[randomPos], workspacesId[randomPos], devicesId[randomPos], ran, 1, 8);
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

    protected ItemMetadata createItemMetadata(Random ran, int min, int max,
	    UUID deviceId) {
	String[] mimes = {"pdf", "php", "java", "docx", "html", "png", "jpeg", "xml"};

	UUID id = null;
	Long version = 1L;

	UUID parentId = null;
	Long parentVersion = 0L;

	String status = "NEW";
	Date modifiedAt = new Date();
	Long checksum = (long) ran.nextInt(Integer.MAX_VALUE);
	List<String> chunks = new ArrayList<String>();
	Boolean isFolder = false;
	String filename = UUID.randomUUID().toString();
	String mimetype = mimes[ran.nextInt(mimes.length)];

	// Fill chunks
	int numChunks = ran.nextInt((max - min) + 1) + min;
	long size = numChunks * CHUNK_SIZE;
	for (int i = 0; i < numChunks; i++) {
	    String str = UUID.randomUUID().toString();
	    try {
		chunks.add(doHash(str));
	    } catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	    } catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
	    }
	}
        
        UUID randomId = UUID.randomUUID();

	ItemMetadata itemMetadata = new ItemMetadata(randomId, version, deviceId, parentId, parentVersion, status, modifiedAt, checksum, size, isFolder, filename, mimetype, chunks);
	itemMetadata.setChunks(chunks);
	//itemMetadata.setTempId((long) ran.nextLong());
	//itemMetadata.setId(UUID.randomUUID());

	return itemMetadata;
    }

    protected String doHash(String str) throws UnsupportedEncodingException,
	    NoSuchAlgorithmException {

	MessageDigest crypt = MessageDigest.getInstance("SHA-1");
	crypt.reset();
	crypt.update(str.getBytes("UTF-8"));

	return new BigInteger(1, crypt.digest()).toString(16);

    }
}
