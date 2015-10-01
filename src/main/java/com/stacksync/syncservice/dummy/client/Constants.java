package com.stacksync.syncservice.dummy.client;

import java.util.UUID;

import omq.common.util.ParameterQueue;

public class Constants {

	/* PROPERTIES */
	public static final String PROP_DATASOURCE = "datasource";
	public static final String DEFAULT_CONFIG_FILE = "config.properties";
	public static final UUID API_DEVICE_ID = new UUID(1, 1);
	public static final String CONSISTENT_EXCHANGE = "x-consistent-hash";

	// QUEUE
	public static final String PROP_OMQ_HOST = ParameterQueue.RABBIT_HOST;
	public static final String PROP_OMQ_PORT = ParameterQueue.RABBIT_PORT;
	public static final String PROP_OMQ_EXCHANGE = ParameterQueue.RPC_EXCHANGE;
	public static final String PROP_OMQ_THREADS = ParameterQueue.NUM_THREADS;
	public static final String PROP_OMQ_USER = ParameterQueue.USER_NAME;
	public static final String PROP_OMQ_PASSWORD = ParameterQueue.USER_PASS;
	public static final String PROP_OMQ_CONSITENT_HASHING = "omq.use_consistent_hashing";
	public static final String PROP_OMQ_QUEUE_NAME = "omq.queue_name";
	public static final String PROP_OMQ_NUM_POINTS = "omq.num_points";
	public static final String PROP_OMQ_DURABLE_QUEUE = "omq.durable_queue";
	public static final String PROP_OMQ_EXCLUSIVE_QUEUE = "omq.exclusive_queue";
	public static final String PROP_OMQ_AUTODELETE_QUEUE = "omq.exclusive_queue";

	// PostgreSQL
	public static final String PROP_POSTGRESQL_HOST = "postgresql.host";
	public static final String PROP_POSTGRESQL_PORT = "postgresql.port";
	public static final String PROP_POSTGRESQL_DATABASE = "postgresql.database";
	public static final String PROP_POSTGRESQL_USERNAME = "postgresql.user";
	public static final String PROP_POSTGRESQL_PASSWORD = "postgresql.password";
	public static final String PROP_POSTGRESQL_INITIAL_CONNS = "postgresql.initial_cons";
	public static final String PROP_POSTGRESQL_MAX_CONNS = "postgresql.max_cons";

	/* SWIFT */
	public static final String PROP_SWIFT_HOST = "swift.host";
	public static final String PROP_SWIFT_PORT = "swift.port";
	public static final String PROP_SWIFT_PROTOCOL = "swift.protocol";
	public static final String PROP_SWIFT_KEYSTONE_HOST = "swift.keystone_host";
	public static final String PROP_SWIFT_KEYSTONE_PORT = "swift.keystone_port";
	public static final String PROP_SWIFT_KEYSTONE_ADMIN_PORT = "swift.keystone_admin_port";
	public static final String PROP_SWIFT_KEYSTONE_PROTOCOL = "swift.keystone_protocol";
	public static final String PROP_SWIFT_TENANT = "swift.tenant";
	public static final String PROP_SWIFT_USER = "swift.user";
	public static final String PROP_SWIFT_PASSWORD = "swift.password";

	/* GENERAL */
	public static final String DEFAULT_DATASOURCE = "postgresql";

	/* QUEUE */
	public static final String DEFAULT_OMQ_HOST = "localhost";
	public static final Integer DEFAULT_OMQ_PORT = 5672;
	public static final String DEFAULT_OMQ_EXCHANGE = "rpc_global_exchange";
	public static final String DEFAULT_OMQ_USER = "guest";
	public static final String DEFAULT_OMQ_PASSWORD = "guest";
	public static final String DEFAULT_OMQ_THREADS = "4";
	public static final String DEFAULT_OMQ_NUM_POINTS = "100";

	/* POSTGRESQL */
	public static final String DEFAULT_POSTGRESQL_HOST = "localhost";
	public static final String DEFAULT_POSTGRESQL_PORT = "5432";
	public static final String DEFAULT_POSTGRESQL_DATABASE = "stacksync";
	public static final String DEFAULT_POSTGRESQL_DRIVER = "org.postgresql.Driver";
	public static final String DEFAULT_POSTGRESQL_USERNAME = "stacksync";
	public static final String DEFAULT_POSTGRESQL_PASSWORD = "stacksync";
	public static final String DEFAULT_POSTGRESQL_INITIAL_CONNS = "1";
	public static final String DEFAULT_POSTGRESQL_MAX_CONNS = "100";

	/* SWIFT */
	public static final String DEFAULT_SWIFT_HOST = "localhost";
	public static final String DEFAULT_SWIFT_PORT = "8080";
	public static final String DEFAULT_SWIFT_PROTOCOL = "http";
	public static final String DEFAULT_SWIFT_KEYSTONE_HOST = "localhost";
	public static final String DEFAULT_SWIFT_KEYSTONE_PORT = "5000";
	public static final String DEFAULT_SWIFT_KEYSTONE_ADMIN_PORT = "35357";
	public static final String DEFAULT_SWIFT_KEYSTONE_PROTOCOL = "http";
	public static final String DEFAULT_SWIFT_TENANT = "stacksync";
	public static final String DEFAULT_SWIFT_USER = "stacksync_admin";
	public static final String DEFAULT_SWIFT_PASSWORD = "secrete";

	/* XMLRPC */
	public static final Integer XMLRPC_PORT = 61234;
}
