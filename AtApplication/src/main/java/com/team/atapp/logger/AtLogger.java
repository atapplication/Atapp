package com.team.atapp.logger;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.team.atapp.utils.StackTraceUtil;

/**
 * 
 * @author Shankara
 *
 */
public class AtLogger {

	private final Logger logger;
	
	public static AtLogger getLogger(Class<?> cls) {
		return getLogger (cls.getName());
	}
	
	public static AtLogger getLogger(String pkgName) {
		return new AtLogger(Logger.getLogger(pkgName));
	}
	
	private AtLogger(Logger logger) {
		this.logger = logger;
	}
	
	public boolean isDebug() {
		return logger.isEnabledFor(Level.DEBUG);
	}
	
	public void debug(Object... objs) {
		if (isDebug()) {
			StringBuilder buf = new StringBuilder();
			buf.append(lineNumber ());
			for (Object obj : objs) {
				buf.append(obj);
			}
			logger.debug(buf.toString());
		}
	}

	public boolean isInfo() {
		return logger.isEnabledFor(Level.INFO);
	}
	
	public void info(Object... objs) {
		if (isInfo()) {
			StringBuilder buf = new StringBuilder();
			buf.append(lineNumber ());
			for (Object obj : objs) {
				if(null != obj){
					buf.append(obj);
				}else{
					buf.append("null");
				}
			}
			logger.info(buf.toString());
		}
	}
	
	public boolean isWarn() {
		return logger.isEnabledFor(Level.WARN);
	}
	
	public void warn(Object... objs) {
		if (isWarn()) {
			StringBuilder buf = new StringBuilder();
			buf.append(lineNumber ());
			for (Object obj : objs) {
				buf.append(obj);
			}
			logger.warn(buf.toString());
		}
	}
	
	public void error(Object... objs) {
		StringBuilder buf = new StringBuilder();
		buf.append(lineNumber ());
		for (Object obj : objs) {
			buf.append(obj);
		}
		logger.error(buf.toString());
	}
	
	/*
	 * Get the Actual Line Number
	 */
	private String lineNumber() {
		StackTraceElement threadTrackArray[] = Thread.currentThread().getStackTrace();
		if (threadTrackArray.length > 3) {
			return ":" + Integer.toString(threadTrackArray[3].getLineNumber()) + "-";
		}
		return "";
	}
	
	public static void consoleLoggerInit(Level logLevel) {
		Logger root = Logger.getRootLogger();
		if (root != null) {
			root.removeAllAppenders();
			root.addAppender(new ConsoleAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p %c %m%n")));
		}
	}
	
	public void errorException(Throwable t, Object... objs) {
		StringBuilder buf = new StringBuilder();
		buf.append(lineNumber ());
		for (Object obj : objs) {
			buf.append(obj);
		}
		buf.append("\n");
		buf.append(StackTraceUtil.constructStackTrace(t));
		logger.error(buf.toString());
	}
}