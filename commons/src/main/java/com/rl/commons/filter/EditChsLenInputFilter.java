package com.rl.commons.filter;

import android.text.Spanned;

/**
 * Created by Nicky on 2016/11/18.
 * 编辑框 长度过滤( 中文按2个字符长度计算 )
 *
 */
public class EditChsLenInputFilter extends EditEmojiFilter {

    public EditChsLenInputFilter(int max) {
        mMax = max;
    }

    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {

        if( "".equals(super.filter(source, start, end, dest, dstart, dend)) )
        {
            return "";
        }
        if( dest!=null && source!=null )
        {
            int len = 0;
            //计算原有字串保留长度(中文算2个长度)
            for ( int i=0;i<dstart;i++ ) {
                if( isChineseChar(dest.charAt(i)) ){
                    len += 2;
                }else{
                    len++;
                }

                //此处应该走不到----NickyHuang
                if( len>=mMax ){
//            			AppContext.showToast(getString(R.string.tips_input_max_length,title,maxEditSize), Toast.LENGTH_SHORT, 0,Gravity.CENTER);
                    return "";
                }
            }
            //计算新增字串长度(中文算2个长度)
            for ( int i=start;i<end;i++ ) {
                if( isChineseChar(source.charAt(i)) ){
                    len += 2;
                }else{
                    len++;
                }
                if(  len>=mMax ){
//        				AppContext.showToast(getString(R.string.tips_input_max_length,title,maxEditSize), Toast.LENGTH_SHORT, 0,Gravity.CENTER);
                    if( len==mMax ) //达到最大长度---截取字串
                    {
                        return source.subSequence(start, i+1);
                    }else{
                        //超出最大长度---截取字串
                        if( i>start) //回退一位
                        {
                            return source.subSequence(start, i);
                        }
                        return "";
                    }
                }
            }
        }


        int keep = mMax - (dest.length() - (dend - dstart));
        if (keep <= 0) {
//            	AppContext.showToast(getString(R.string.tips_input_max_length,title,maxEditSize), Toast.LENGTH_SHORT, 0,Gravity.CENTER);
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
