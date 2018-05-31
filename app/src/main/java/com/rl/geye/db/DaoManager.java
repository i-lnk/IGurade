package com.rl.geye.db;


import com.rl.geye.MyApp;
import com.rl.geye.db.bean.DaoSession;

/**
 * Created by Nicky on 2017/9/1.
 */

public class DaoManager {

    private static DaoManager instance;

    private DaoSession daoSession;

    /* 私有构造方法，防止被实例化 */
    private DaoManager() {
        daoSession = MyApp.getDaoSession();
    }

    /* 1:懒汉式，静态工程方法，创建实例 */
    public static DaoManager getInstance() {
        if (instance == null) {
            instance = new DaoManager();
        }
        return instance;
    }


}
