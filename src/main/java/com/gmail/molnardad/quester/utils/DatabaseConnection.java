package com.gmail.molnardad.quester.utils;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.Executor;

import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.Quester;

/*
 * This class was heavily inspired by LogBlock's connection pool class and various other mysql
 * libraries/classes on bukkit forums.
 */
public class DatabaseConnection {
	
	private static Vector<DatabaseConnection.MyConnection> connections = new Vector<DatabaseConnection.MyConnection>();
	private static String url, user, pass;
	private static volatile boolean isValid = false;
	
	private DatabaseConnection() {
		throw new IllegalAccessError();
	}
	
	public static boolean isValid() {
		return isValid;
	}
	
	public static synchronized Connection getConnection() throws SQLException {
		if(!isValid) {
			throw new SQLException("Cannot fetch connection from invalid pool.");
		}
		final Enumeration<MyConnection> conns = connections.elements();
		while(conns.hasMoreElements()) {
			final MyConnection conn = conns.nextElement();
			if(conn.use()) {
				if(conn.isValid()) {
					if(QConfiguration.debug) {
						Quester.log.info("Found good connection, fetching that.");
					}
					return conn;
				}
				connections.remove(conn);
				conn.closeQuietly();
			}
		}
		if(QConfiguration.debug) {
			Quester.log.info("Creating new connection to fetch.");
		}
		final MyConnection conn = new MyConnection(DriverManager.getConnection(url, user, pass));
		conn.use();
		if(!conn.isValid()) {
			conn.closeQuietly();
			throw new SQLException("Failed to establish a new valid connection.");
		}
		connections.add(conn);
		return conn;
	}
	
	public static synchronized void close() {
		final Enumeration<MyConnection> conns = connections.elements();
		while(conns.hasMoreElements()) {
			final MyConnection conn = conns.nextElement();
			connections.remove(conn);
			conn.closeQuietly();
		}
		if(QConfiguration.debug) {
			Quester.log.info("Connections closed.");
		}
	}
	
	public static synchronized void initialize(final String url, final String username, final String password) throws ClassNotFoundException, SQLException {
		if(isValid) {
			close();
			isValid = false;
		}
		
		DatabaseConnection.url = url;
		DatabaseConnection.user = username;
		DatabaseConnection.pass = password;
		
		Class.forName("com.mysql.jdbc.Driver");    // touch the MySQL driver
		
		// Test connection
		@SuppressWarnings("resource")
		// it is closed, really....
		final MyConnection c = new MyConnection(
				DriverManager.getConnection(url, username, password));
		if(!c.isValid()) {
			c.closeQuietly();
			throw new SQLException("Failed to establish a valid connection.");
		}
		else {
			c.closeQuietly();
			isValid = true;
		}
	}
	
	private static class MyConnection implements Connection {
		
		private final Connection conn;
		private boolean inUse;
		private int networkTimeout;
		private String schema;
		
		public MyConnection(final Connection conn) {
			this.conn = conn;
			inUse = false;
			networkTimeout = 30;
			schema = "default";
		}
		
		synchronized boolean use() {
			if(inUse) {
				return false;
			}
			inUse = true;
			return true;
		}
		
		boolean isValid() {
			try {
				return conn.isValid(1);
			}
			catch (final SQLException ex) {
				return false;
			}
		}
		
		@Override
		public void close() {
			inUse = false;
			try {
				if(!conn.getAutoCommit()) {
					conn.setAutoCommit(true);
				}
			}
			catch (final SQLException ex) {
				connections.remove(this);
				closeQuietly();
			}
		}
		
		void closeQuietly() {
			try {
				conn.close();
			}
			catch (final SQLException ignore) {}
		}
		
		// CUSTOM IMPLEMENTATION
		
		@Override
		public void setSchema(final String schema) throws SQLException {
			this.schema = schema;
		}
		
		@Override
		public String getSchema() throws SQLException {
			return schema;
		}
		
		@Override
		public void abort(final Executor executor) throws SQLException {
			// Allegedly not really implemented
		}
		
		@Override
		public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
			networkTimeout = milliseconds;
		}
		
		@Override
		public int getNetworkTimeout() throws SQLException {
			return networkTimeout;
		}
		
		// ORIGINAL IMPLEMENTATION
		
		@Override
		public <T> T unwrap(final Class<T> iface) throws SQLException {
			return conn.unwrap(iface);
		}
		
		@Override
		public boolean isWrapperFor(final Class<?> iface) throws SQLException {
			return conn.isWrapperFor(iface);
		}
		
		@Override
		public Statement createStatement() throws SQLException {
			return conn.createStatement();
		}
		
		@Override
		public PreparedStatement prepareStatement(final String sql) throws SQLException {
			return conn.prepareStatement(sql);
		}
		
		@Override
		public CallableStatement prepareCall(final String sql) throws SQLException {
			return conn.prepareCall(sql);
		}
		
		@Override
		public String nativeSQL(final String sql) throws SQLException {
			return conn.nativeSQL(sql);
		}
		
		@Override
		public void setAutoCommit(final boolean autoCommit) throws SQLException {
			conn.setAutoCommit(autoCommit);
		}
		
		@Override
		public boolean getAutoCommit() throws SQLException {
			return conn.getAutoCommit();
		}
		
		@Override
		public void commit() throws SQLException {
			conn.commit();
		}
		
		@Override
		public void rollback() throws SQLException {
			conn.rollback();
		}
		
		@Override
		public boolean isClosed() throws SQLException {
			return conn.isClosed();
		}
		
		@Override
		public DatabaseMetaData getMetaData() throws SQLException {
			return conn.getMetaData();
		}
		
		@Override
		public void setReadOnly(final boolean readOnly) throws SQLException {
			conn.setReadOnly(readOnly);
		}
		
		@Override
		public boolean isReadOnly() throws SQLException {
			return conn.isReadOnly();
		}
		
		@Override
		public void setCatalog(final String catalog) throws SQLException {
			conn.setCatalog(catalog);
		}
		
		@Override
		public String getCatalog() throws SQLException {
			return conn.getCatalog();
		}
		
		@Override
		public void setTransactionIsolation(final int level) throws SQLException {
			conn.setTransactionIsolation(level);
		}
		
		@Override
		public int getTransactionIsolation() throws SQLException {
			return conn.getTransactionIsolation();
		}
		
		@Override
		public SQLWarning getWarnings() throws SQLException {
			return conn.getWarnings();
		}
		
		@Override
		public void clearWarnings() throws SQLException {
			conn.clearWarnings();
		}
		
		@Override
		public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
			return conn.createStatement(resultSetType, resultSetConcurrency);
		}
		
		@Override
		public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
			return conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
		}
		
		@Override
		public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
			return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
		}
		
		@Override
		public Map<String, Class<?>> getTypeMap() throws SQLException {
			return conn.getTypeMap();
		}
		
		@Override
		public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
			conn.setTypeMap(map);
		}
		
		@Override
		public void setHoldability(final int holdability) throws SQLException {
			conn.setHoldability(holdability);
		}
		
		@Override
		public int getHoldability() throws SQLException {
			return conn.getHoldability();
		}
		
		@Override
		public Savepoint setSavepoint() throws SQLException {
			return conn.setSavepoint();
		}
		
		@Override
		public Savepoint setSavepoint(final String name) throws SQLException {
			return conn.setSavepoint(name);
		}
		
		@Override
		public void rollback(final Savepoint savepoint) throws SQLException {
			conn.rollback(savepoint);
		}
		
		@Override
		public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
			conn.releaseSavepoint(savepoint);
		}
		
		@Override
		public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
			return conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
		}
		
		@Override
		public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
			return conn.prepareStatement(sql, resultSetType, resultSetConcurrency,
					resultSetHoldability);
		}
		
		@Override
		public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
			return conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		}
		
		@Override
		public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
			return conn.prepareStatement(sql, autoGeneratedKeys);
		}
		
		@Override
		public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
			return conn.prepareStatement(sql, columnIndexes);
		}
		
		@Override
		public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
			return conn.prepareStatement(sql, columnNames);
		}
		
		@Override
		public Clob createClob() throws SQLException {
			return conn.createClob();
		}
		
		@Override
		public Blob createBlob() throws SQLException {
			return conn.createBlob();
		}
		
		@Override
		public NClob createNClob() throws SQLException {
			return conn.createNClob();
		}
		
		@Override
		public SQLXML createSQLXML() throws SQLException {
			return conn.createSQLXML();
		}
		
		@Override
		public boolean isValid(final int timeout) throws SQLException {
			return conn.isValid(timeout);
		}
		
		@Override
		public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
			conn.setClientInfo(name, value);
		}
		
		@Override
		public void setClientInfo(final Properties properties) throws SQLClientInfoException {
			conn.setClientInfo(properties);
		}
		
		@Override
		public String getClientInfo(final String name) throws SQLException {
			return conn.getClientInfo(name);
		}
		
		@Override
		public Properties getClientInfo() throws SQLException {
			return conn.getClientInfo();
		}
		
		@Override
		public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
			return conn.createArrayOf(typeName, elements);
		}
		
		@Override
		public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
			return conn.createStruct(typeName, attributes);
		}
	}
}
