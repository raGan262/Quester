package com.gmail.molnardad.quester.utils;

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.Quester;

public final class Ql {
	
	private static Logger fileLogger = Logger.getGlobal();
	private static Logger logger = Logger.getGlobal();
	
	private Ql() {
		throw new IllegalAccessError();
	}
	
	private static class QuesterDebugLogger extends Logger {
		
		private static final String prefix = "[QuesterDebug] ";
		
		QuesterDebugLogger(final Logger serverLogger) {
			super("QuesterFileLogger", null);
			if(serverLogger == null) {
				throw new IllegalArgumentException("serverLogger cannot be null.");
			}
			setUseParentHandlers(false);
			final Handler[] handlers = serverLogger.getHandlers();
			boolean foundHandler = false;
			for(final Handler h : handlers) {
				logger.info(h.getClass().getSimpleName());
				if(h instanceof FileHandler) {
					addHandler(h);
					foundHandler = true;
				}
			}
			if(!foundHandler) {
				throw new IllegalArgumentException("Could not find any file handlers.");
			}
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
				fileLogger = new QuesterDebugLogger(quester.getServer().getLogger());
			}
			catch (final IllegalArgumentException e) {
				quester.getLogger().warning(
						"Failed to initialize file logger, will log only into console.");
				fileLogger = quester.getLogger();
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
		if(QConfiguration.verbose) {
			logger.log(Level.INFO, msg);
		}
		else {
			fileLogger.log(Level.INFO, msg);
		}
	}
	
	public static void verbose(final String msg, final Throwable throwable) {
		if(QConfiguration.verbose) {
			logger.log(Level.SEVERE, msg, throwable);
		}
		else {
			fileLogger.log(Level.SEVERE, msg, throwable);
		}
	}
	
	public static void debug(final String msg) {
		if(QConfiguration.debug) {
			logger.log(Level.INFO, msg);
		}
		else {
			fileLogger.log(Level.INFO, msg);
		}
	}
	
	public static void debug(final String msg, final Throwable throwable) {
		if(QConfiguration.debug) {
			logger.log(Level.SEVERE, msg, throwable);
		}
		else {
			fileLogger.log(Level.SEVERE, msg, throwable);
		}
	}
}
