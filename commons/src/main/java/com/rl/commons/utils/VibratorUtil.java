package com.rl.commons.utils;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

/**
 * 
 * @ClassName: VibratorUtil 
 * @Description: 振动工具类
 * @author NickyHuang
 * @date 2016-4-5 下午3:46:09 
 *
 */
public class VibratorUtil {
	
	private VibratorUtil(){}

	public static void Vibrate(final Activity activity, long milliseconds) {
		Vibrator vib = (Vibrator) activity
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}

	public static void Vibrate(final Activity activity, long[] pattern,
			boolean isRepeat) {
		Vibrator vib = (Vibrator) activity
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(pattern, isRepeat ? 1 : -1);
	}
}
