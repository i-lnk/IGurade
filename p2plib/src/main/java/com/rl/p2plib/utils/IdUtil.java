package com.rl.p2plib.utils;


import com.rl.commons.utils.StringUtils;

/**
 * Created by Nicky on 2017/3/27.
 */

public class IdUtil {

    private IdUtil(){}


    public static boolean isSameId( String id1,String id2 ){

        if( StringUtils.isEmpty(id1) || StringUtils.isEmpty(id1) )
            return false;
        String idStr1 = id1.replace("-","").replace("_","");
        String idStr2 = id2.replace("-","").replace("_","");
        if( StringUtils.isEmpty(idStr1.trim()) || StringUtils.isEmpty(idStr2.trim()) )
            return false;
        return idStr1.equalsIgnoreCase(idStr2);
    }


}
