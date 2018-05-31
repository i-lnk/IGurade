package com.rl.commons.filter;

import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.widget.Toast;
import com.rl.commons.BaseApp;
import com.rl.commons.R;


/**
 * 
 * @ClassName: EditLenInputFilter
 * @Description: 编辑框 长度过滤
 * @author NickyHuang
 * @date 2015-11-11
 *
 */
public class EditLenInputFilter implements InputFilter {

		public EditLenInputFilter(int max) {
			mMax = max;
		}

		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			
			int keep = mMax - (dest.length() - (dend - dstart));

			if (keep <= 0) {
				BaseApp.showToast( R.string.tips_input_max_length2,mMax,Toast.LENGTH_SHORT, 0, Gravity.CENTER);
				return "";
			} else if (keep >= end - start) {
				return null; // keep original
			} else {
				keep += start;
				if (Character.isHighSurrogate(source.charAt(keep - 1))) {
					--keep;
					if (keep == start) {
						return "";
					}
				}
				return source.subSequence(start, keep);
			}
		}

		private int mMax;

	}