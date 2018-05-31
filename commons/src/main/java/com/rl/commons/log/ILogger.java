package com.rl.commons.log;

/**
 * @Title Logger
 * @Description Logger是一个日志的接口
 * @date 2013-1-16 14:20
 * @version V1.0
 */
public interface ILogger
{
	void v(String tag, String message);

	void d(String tag, String message);

	void i(String tag, String message);

	void w(String tag, String message);

	void e(String tag, String message);

	void open();

	void close();

	void println(int priority, String tag, String message);
}
