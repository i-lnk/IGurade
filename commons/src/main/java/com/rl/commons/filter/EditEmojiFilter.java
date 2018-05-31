package com.rl.commons.filter;

import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.widget.Toast;
import com.rl.commons.BaseApp;
import com.rl.commons.R;
import java.util.regex.Pattern;

/**
 * 
 * @ClassName: EditEmojiFilter
 * @Description: 编辑框过滤表情
 * @author NickyHuang
 * @date 2015-11-2
 *
 */
public class EditEmojiFilter implements InputFilter{

	private final String reg ="^([a-z]|[A-Z]|[0-9]|[\u2E80-\u9FFF]){3,}|@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?|[wap.]{4}|[www.]{4}|[blog.]{5}|[bbs.]{4}|[.com]{4}|[.cn]{3}|[.net]{4}|[.org]{4}|[http://]{7}|[ftp://]{6}$";  

	@SuppressWarnings("unused")
	private Pattern pattern = Pattern.compile(reg);
	
	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
//		if(end - start >= 3){//表情符号的字符长度最小为3
//            //正则匹配是否是表情符号
//            Matcher matcher = pattern.matcher(source.toString());
//            if(!matcher.matches()){
//            	AppContext.showToast("不支持表情输入", Toast.LENGTH_SHORT, 0,Gravity.CENTER);
//                return "";
//             }
//        }
        if( EmojiFilter.containsEmoji(source.toString()) )
        {
			//TODO
        	BaseApp.showToast(R.string.tips_input_no_emoji2, Toast.LENGTH_SHORT, 0, Gravity.CENTER);
            return "";
        }
		return source;
	}
}


