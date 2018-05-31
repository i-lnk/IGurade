package com.rl.commons.log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


/**
 * 
 * @ClassName: XLogger
 * @Description: XLogger是一个日志打印类
 * @author NickyHuang
 * @date 2015-11-19
 *
 */
public class XLog
{
	/**
	 * Priority constant for the println method; use XLog.v.
	 */
	public static final int VERBOSE = 2;

	/**
	 * Priority constant for the println method; use XLog.d.
	 */
	public static final int DEBUG = 3;

	/**
	 * Priority constant for the println method; use XLog.i.
	 */
	public static final int INFO = 4;

	/**
	 * Priority constant for the println method; use XLog.w.
	 */
	public static final int WARN = 5;

	/**
	 * Priority constant for the println method; use XLog.e.
	 */
	public static final int ERROR = 6;
	/**
	 * Priority constant for the println method.
	 */
	public static final int ASSERT = 7;
	
	private static HashMap<String, ILogger> loggerHashMap = new HashMap<String, ILogger>();
	private static final ILogger defaultLogger = new XLogCatLogger();

	public static void addLogger(ILogger logger)
	{
		String loggerName = logger.getClass().getName();
		String defaultLoggerName = defaultLogger.getClass().getName();
		if (!loggerHashMap.containsKey(loggerName)
				&& !defaultLoggerName.equalsIgnoreCase(loggerName))
		{
			logger.open();
			loggerHashMap.put(loggerName, logger);
		}
	}

	public static void removeLogger(ILogger logger)
	{
		String loggerName = logger.getClass().getName();
		if (loggerHashMap.containsKey(loggerName))
		{
			logger.close();
			loggerHashMap.remove(loggerName);
		}
	}

	public static void d(String message)
	{
		d(null, message);
	}
	public static void d(Object object, String message)
	{
		printLoger(DEBUG, getTagName(object), message);
	}

	public static void e(String message)
	{
		e(null, message);
	}
	public static void e(Object object, String message)
	{
		printLoger(ERROR, getTagName(object), message);
	}

	public static void i(String message)
	{
		i(null, message);
	}
	public static void i(Object object, String message)
	{
		printLoger(INFO, getTagName(object), message);
	}

	public static void v(String message)
	{
		v(null, message);
	}
	public static void v(Object object, String message)
	{
		printLoger(VERBOSE, getTagName(object), message);
	}

	public static void w(String message)
	{
		w(null, message);
	}
	public static void w(Object object, String message)
	{
		printLoger(WARN, getTagName(object), message);
	}


	private static String getTagName(Object object)
	{
		String tag = LoggerConfig.DEFAULT_TAG;
		if( object!=null )
		{
			if ((object instanceof String)) {
				tag = (String) object;
			} else {
//				String arrays[] = object.getClass().getName().split("\\.");
//				tag = arrays[arrays.length - 1];
				tag = object.getClass().getSimpleName();
			}
		}
		return tag;
	}

	private static void printLoger(int priority, String tag, String message)
	{
		if (LoggerConfig.LOG_DEBUG)
		{
			printLoger(defaultLogger, priority, tag, message);
			Iterator<Entry<String, ILogger>> iter = loggerHashMap.entrySet()
					.iterator();
			while (iter.hasNext())
			{
				Entry<String, ILogger> entry = iter.next();
				ILogger logger = entry.getValue();
				if (logger != null)
				{
					printLoger(logger, priority, tag, message);
				}
			}
		}
	}

	private static void printLoger(ILogger logger, int priority, String tag,
			String message)
	{
		switch (priority)
		{
		case VERBOSE:
			logger.v(tag, message);
			break;
		case DEBUG:
			logger.d(tag, message);
			break;
		case INFO:
			logger.i(tag, message);
			break;
		case WARN:
			logger.w(tag, message);
			break;
		case ERROR:
			logger.e(tag, message);
			break;
		default:
			break;
		}
	}
}
