package me.ragan262.quester.utils;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import me.ragan262.quester.Quester;

public final class Ql {
	
	private static Logger debugLogger = Logger.getGlobal();
	private static Logger logger = Logger.getGlobal();
	
	private Ql() {
		throw new IllegalAccessError();
	}
	
	private static class QuesterDebugLogger extends Logger {
		
		private static final String prefix = "[QuesterDEBUG] ";
		
		QuesterDebugLogger(final Logger serverLogger) {
			super("QuesterDEBUG", null);
			setParent(serverLogger);
		}
		
		@Override
		public void log(final LogRecord record) {
			record.setMessage(prefix + record.getMessage());
			super.log(record);
		}
	}
	
	public static void init(final Quester quester) {
		if(quester != null) {
			try {
				debugLogger = new QuesterDebugLogger(quester.getServer().getLogger());
			}
			catch (final IllegalArgumentException e) {
				quester.getLogger().warning("Failed to initialize debug logger.");
				debugLogger = quester.getLogger();
			}
			logger = quester.getLogger();
		}
		else {
			throw new IllegalArgumentException("Quester cannot be null.");
		}
	}
	
	public static void info(final String msg) {
		logger.log(Level.INFO, msg);
	}
	
	public static void info(final String msg, final Throwable throwable) {
		logger.log(Level.INFO, msg, throwable);
	}
	
	public static void warning(final String msg) {
		logger.log(Level.WARNING, msg);
	}
	
	public static void warning(final String msg, final Throwable throwable) {
		logger.log(Level.WARNING, msg, throwable);
	}
	
	public static void severe(final String msg) {
		logger.log(Level.SEVERE, msg);
	}
	
	public static void severe(final String msg, final Throwable throwable) {
		logger.log(Level.SEVERE, msg, throwable);
	}
	
	public static void verbose(final String msg) {
		logger.log(Level.INFO, msg);
	}
	
	public static void verbose(final String msg, final Throwable throwable) {
		logger.log(Level.SEVERE, msg, throwable);
	}
	
	public static void debug(final String msg) {
		debugLogger.log(Level.INFO, msg);
	}
	
	public static void debug(final String msg, final Throwable throwable) {
		debugLogger.log(Level.SEVERE, msg, throwable);
	}
}
