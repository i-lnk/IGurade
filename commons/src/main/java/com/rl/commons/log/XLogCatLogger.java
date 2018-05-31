package com.rl.commons.log;

import android.util.Log;

/**
 * 
 * @ClassName: XLogCatLogger
 * @Description: 打印到LogCat上面的日志类(系统默认)
 * @author NickyHuang
 * @date 2015-11-19
 *
 */
public class XLogCatLogger implements ILogger
{
	@Override
	public void d(String tag, String message)
	{
		Log.d(tag, message);
	}

	@Override
	public void e(String tag, String message)
	{
		Log.e(tag, message);
	}

	@Override
	public void i(String tag, String message)
	{
		Log.i(tag, message);
	}

	@Override
	public void v(String tag, String message)
	{
		Log.v(tag, message);
	}

	@Override
	public void w(String tag, String message)
	{
		Log.w(tag, message);
	}

	@Override
	public void println(int priority, String tag, String message)
	{
		Log.println(priority, tag, message);
	}

	@Override
	public void open()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
		
	}
}
