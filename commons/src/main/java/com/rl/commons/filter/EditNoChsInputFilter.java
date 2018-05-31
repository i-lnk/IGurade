package com.rl.commons.filter;

import android.text.Spanned;
import android.view.Gravity;
import android.widget.Toast;
import com.rl.commons.BaseApp;
import com.rl.commons.R;

/**
 * Created by Nicky on 2016/11/18.
 * 编辑框 禁止中文输入过滤
 *
 */
public class EditNoChsInputFilter extends EditEmojiFilter {

    public EditNoChsInputFilter() {

    }

    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {

        if( "".equals(super.filter(source, start, end, dest, dstart, dend)) )
        {
            return "";
        }
        if( dest!=null && source!=null )
        {

            //计算新增字串长度(中文算2个长度)
            for ( int i=start;i<end;i++ ) {
                if( isChineseChar(source.charAt(i)) ){
                    BaseApp.showToast(R.string.tips_input_no_chinese, Toast.LENGTH_SHORT, 0, Gravity.CENTER);
                    return "";
                }
            }
        }
        return source;

    }




    /**
     *  判断字符是否为中文(算法1)
     */
    public static boolean isChinese(char a) {
        int v = (int)a;
        return (v >=19968 && v <= 171941);
    }

    /**
     *  判断字符是否为中文(算法2)
     */
    public static boolean isChineseChar(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

        if ( ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                ) {
            return true;
        }
        return false;
    }

}
