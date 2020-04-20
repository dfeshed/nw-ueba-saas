package fortscale.utils.logging;

import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class Logger implements org.slf4j.Logger {	
	private org.slf4j.Logger thisLogger;
	private String loggerName;

	public static final Logger getLogger(Class<?> loggedClass){
		return new Logger(loggedClass);
	}
	public static final Logger getLogger(String loggerName){
		return new Logger(loggerName);
	}
	
	private Logger(Class<?> loggedClass){
		this(loggedClass.getName());
	}	
	private Logger(String loggerName){
		this.loggerName = loggerName;
		this.thisLogger = LoggerFactory.getLogger(loggerName);
	}
	
	
	public String getName() {
		return loggerName;
	}
	
	public boolean isTraceEnabled() {
		return thisLogger.isTraceEnabled();
	}
	
	public void trace(String msg) {
		thisLogger.trace(msg);
	}
	
	public void trace(String format, Object arg) {
		thisLogger.trace(format, arg);
	}
	
	public void trace(String format, Object arg1, Object arg2) {
		thisLogger.trace(format, arg1, arg2);
	}
	
	public void trace(String format, Object... argArray) {
		thisLogger.trace(format, argArray);
	}
	
	public void trace(String msg, Throwable t) {
		thisLogger.trace(msg, t);
	}
	
	public boolean isTraceEnabled(Marker marker) {
		return thisLogger.isTraceEnabled(marker);
	}
	
	public void trace(Marker marker, String msg) {
		thisLogger.trace(marker, msg);
	}
	
	public void trace(Marker marker, String format, Object arg) {
		thisLogger.trace(marker, format, arg);
	}
	
	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		thisLogger.trace(marker, format, arg1, arg2);
	}
	
	public void trace(Marker marker, String format, Object... argArray) {
		thisLogger.trace(marker, format, argArray);
	}
	
	public void trace(Marker marker, String msg, Throwable t) {
		thisLogger.trace(marker, msg, t);
	}
	
	public boolean isDebugEnabled() {
		return thisLogger.isDebugEnabled();
	}
	
	public void debug(String msg) {
		thisLogger.debug(msg);
	}
	
	public void debug(String format, Object arg) {
		thisLogger.debug(format, arg);
	}
	
	public void debug(String format, Object arg1, Object arg2) {
		thisLogger.debug(format, arg1, arg2);
	}
	
	public void debug(String format, Object... argArray) {
		thisLogger.debug(format, argArray);
	}
	
	public void debug(String msg, Throwable t) {
		thisLogger.debug(msg, t);
	}
	
	public boolean isDebugEnabled(Marker marker) {
		return thisLogger.isDebugEnabled(marker);
	}
	
	public void debug(Marker marker, String msg) {
		thisLogger.debug(marker, msg);
	}
	
	public void debug(Marker marker, String format, Object arg) {
		thisLogger.debug(marker, format, arg);
	}
	
	public void debug(Marker marker, String format, Object arg1, Object arg2) {
		thisLogger.debug(marker, format, arg1, arg2);
	}
	
	public void debug(Marker marker, String format, Object... argArray) {
		thisLogger.debug(marker, format, argArray);
	}
	
	public void debug(Marker marker, String msg, Throwable t) {
		thisLogger.debug(marker, msg, t);
	}
	
	public boolean isInfoEnabled() {
		return thisLogger.isInfoEnabled();
	}
	
	public void info(String msg) {
		thisLogger.info(msg);
	}
	
	public void info(String format, Object arg) {
		thisLogger.info(format, arg);
	}
	
	public void info(String format, Object arg1, Object arg2) {
		thisLogger.info(format, arg1, arg2);
	}
	
	public void info(String format, Object... argArray) {
		thisLogger.info(format, argArray);
	}
	
	public void info(String msg, Throwable t) {
		thisLogger.info(msg, t);
	}
	
	public boolean isInfoEnabled(Marker marker) {
		return thisLogger.isInfoEnabled(marker);
	}
	
	public void info(Marker marker, String msg) {
		thisLogger.info(marker, msg);
	}
	
	public void info(Marker marker, String format, Object arg) {
		thisLogger.info(marker, format, arg);
	}
	
	public void info(Marker marker, String format, Object arg1, Object arg2) {
		thisLogger.info(marker, format, arg1, arg2);
	}
	
	public void info(Marker marker, String format, Object... argArray) {
		thisLogger.info(marker, format, argArray);
	}
	
	public void info(Marker marker, String msg, Throwable t) {
		thisLogger.info(marker, msg, t);
	}
	
	public boolean isWarnEnabled() {
		return thisLogger.isWarnEnabled();
	}
	
	public void warn(String msg) {
		thisLogger.warn(msg);
	}
	
	public void warn(String format, Object arg) {
		thisLogger.warn(format, arg);
	}
	
	public void warn(String format, Object... argArray) {
		thisLogger.warn(format, argArray);
	}
	
	public void warn(String format, Object arg1, Object arg2) {
		thisLogger.warn(format, arg1, arg2);
	}
	
	public void warn(String msg, Throwable t) {
		thisLogger.warn(msg, t);
	}
	
	public boolean isWarnEnabled(Marker marker) {
		return thisLogger.isWarnEnabled(marker);
	}
	
	public void warn(Marker marker, String msg) {
		thisLogger.warn(marker, msg);
	}
	
	public void warn(Marker marker, String format, Object arg) {
		thisLogger.warn(marker, format, arg);
	}
	
	public void warn(Marker marker, String format, Object arg1, Object arg2) {
		thisLogger.warn(marker, format, arg1, arg2);
	}
	
	public void warn(Marker marker, String format, Object... argArray) {
		thisLogger.warn(marker, format, argArray);
	}
	
	public void warn(Marker marker, String msg, Throwable t) {
		thisLogger.warn(marker, msg, t);
	}
	
	public boolean isErrorEnabled() {
		return thisLogger.isErrorEnabled();
	}
	
	public void error(String msg) {
		thisLogger.error(msg);
	}
	
	public void error(String format, Object arg) {
		thisLogger.error(format, arg);
	}
	
	public void error(String format, Object arg1, Object arg2) {
		thisLogger.error(format, arg1, arg2);
	}
	
	public void error(String format, Object... argArray) {
		thisLogger.error(format, argArray);
	}
	
	public void error(String msg, Throwable t) {
		thisLogger.error(msg, t);
	}
	
	public boolean isErrorEnabled(Marker marker) {
		return thisLogger.isErrorEnabled(marker);
	}
	
	public void error(Marker marker, String msg) {
		thisLogger.error(marker, msg);
	}
	
	public void error(Marker marker, String format, Object arg) {
		thisLogger.error(marker, format, arg);
	}
	
	public void error(Marker marker, String format, Object arg1, Object arg2) {
		thisLogger.error(marker, format, arg1, arg2);
	}
	
	public void error(Marker marker, String format, Object... argArray) {
		thisLogger.error(marker, format, argArray);
	}
	
	public void error(Marker marker, String msg, Throwable t) {
		thisLogger.error(marker, msg, t);
	}
}
