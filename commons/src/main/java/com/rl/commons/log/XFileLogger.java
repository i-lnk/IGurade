package com.rl.commons.log;

import com.rl.commons.BaseApp;
import com.rl.commons.utils.AndroidVersionCheckUtils;
import com.rl.commons.utils.TAExternalOverFroyoUtils;
import com.rl.commons.utils.TAExternalUnderFroyoUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 
 * @ClassName: XFileLogger
 * @Description: 打印到sdcard上面的日志类
 * @author NickyHuang
 * @date 2015-11-19
 *
 */
public class XFileLogger implements ILogger
{

	public static final int VERBOSE = 2;

	public static final int DEBUG = 3;

	public static final int INFO = 4;

	public static final int WARN = 5;

	public static final int ERROR = 6;

	public static final int ASSERT = 7;
	private String mPath;
	private Writer mWriter;

	private static final SimpleDateFormat TIMESTAMP_FMT = new SimpleDateFormat(
			"[yyyy-MM-dd HH:mm:ss] ");
	private String basePath = "";
	private static String LOG_DIR = "Edwin_log";
	private static String BASE_FILENAME = "ta.log";
	private File logDir;

	public XFileLogger()
	{
	}

	public void open()
	{
		if (AndroidVersionCheckUtils.hasFroyo())
		{
			logDir = TAExternalOverFroyoUtils.getDiskCacheDir(BaseApp.context(), LOG_DIR);
		} else
		{
			logDir = TAExternalUnderFroyoUtils.getDiskCacheDir(BaseApp.context(), LOG_DIR);
		}
		if (!logDir.exists())
		{
			// do not allow media scan
			logDir.mkdirs();
			try
			{
				new File(logDir, ".nomedia").createNewFile();
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		basePath = logDir.getAbsolutePath() + "/" + BASE_FILENAME;
		try
		{
			File file = new File(basePath + "-" + getCurrentTimeString());
			mPath = file.getAbsolutePath();
			mWriter = new BufferedWriter(new FileWriter(mPath,true), 2048);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private String getCurrentTimeString()
	{
		Date now = new Date();
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
//				"yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return simpleDateFormat.format(now);
	}

	public String getPath()
	{
		return mPath;
	}

	@Override
	public void d(String tag, String message)
	{
		// TODO Auto-generated method stub
		println(DEBUG, tag, message);
	}

	@Override
	public void e(String tag, String message)
	{
		println(ERROR, tag, message);
	}

	@Override
	public void i(String tag, String message)
	{
		println(INFO, tag, message);
	}

	@Override
	public void v(String tag, String message)
	{
		println(VERBOSE, tag, message);
	}

	@Override
	public void w(String tag, String message)
	{
		println(WARN, tag, message);
	}

	@Override
	public void println(int priority, String tag, String message)
	{
		String printMessage = "";
		switch (priority)
		{
		case VERBOSE:
			printMessage = "[V]|"
					+ tag
					+ "|"
					+ BaseApp.context()
							.getPackageName() + "|" + message;
			break;
		case DEBUG:
			printMessage = "[D]|"
					+ tag
					+ "|"
					+ BaseApp.context()
							.getPackageName() + "|" + message;
			break;
		case INFO:
			printMessage = "[I]|"
					+ tag
					+ "|"
					+ BaseApp.context()
							.getPackageName() + "|" + message;
			break;
		case WARN:
			printMessage = "[W]|"
					+ tag
					+ "|"
					+ BaseApp.context()
							.getPackageName() + "|" + message;
			break;
		case ERROR:
			printMessage = "[E]|"
					+ tag
					+ "|"
					+ BaseApp.context()
							.getPackageName() + "|" + message;
			break;
		default:

			break;
		}
		println(printMessage);

	}

	public void println(String message)
	{
		try
		{
			mWriter.write(TIMESTAMP_FMT.format(new Date()));
			mWriter.write(message);
			mWriter.write('\n');
			mWriter.flush();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void close()
	{
		try
		{
			mWriter.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
