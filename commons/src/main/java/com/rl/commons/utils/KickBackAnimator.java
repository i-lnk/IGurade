package com.rl.commons.utils;

import android.animation.TypeEvaluator;
import android.annotation.TargetApi;
import android.os.Build;

/**
 * 
 * @ClassName: KickBackAnimator 
 * @Description: 回退动画效果
 * @author NickyHuang
 * @date 2016-5-19 下午5:36:51 
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class KickBackAnimator implements TypeEvaluator<Float> {
	private final float s = 1.70158f;
	float mDuration = 0f;

	public void setDuration(float duration) {
		mDuration = duration;
	}

	public Float evaluate(float fraction, Float startValue, Float endValue) {
		float t = mDuration * fraction;
		float b = startValue.floatValue();
		float c = endValue.floatValue() - startValue.floatValue();
		float d = mDuration;
		float result = calculate(t, b, c, d);
		return result;
	}

	public Float calculate(float t, float b, float c, float d) {
		return c * ((t = t / d - 1) * t * ((s + 1) * t + s) + 1) + b;
	}
}
