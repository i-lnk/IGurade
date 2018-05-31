package com.rl.geye.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.rl.geye.db.bean.DaoMaster;
import com.rl.geye.db.bean.DeviceBgDao;
import com.rl.geye.db.bean.EdwinDeviceDao;
import com.rl.geye.db.bean.EdwinRecordDao;
import com.rl.geye.db.bean.PhotoVideoDao;
import com.rl.geye.db.bean.PushSetDao;
import com.rl.geye.db.bean.SubDeviceDao;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Nicky on 2017/10/12.
 * 数据库升级
 */
public class MyOpenHelper extends DaoMaster.OpenHelper {

    public MyOpenHelper(Context context, String name) {
        super(context, name);
    }

    public MyOpenHelper(Context context, String name, CursorFactory factory) {
        super(context, name, factory);
    }

    /**
     * 数据库升级
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {

        /** 新增加的和修改的字段最好为String类型，避免字段不能为null(不能为INT)的情况发生 */
        //操作数据库的更新 有几个表升级都可以传入到下面
        if (oldVersion >= 2) {
            /** 2版本以上才有SubDevice表 */
            MigrationHelper.getInstance().migrate(db,
                    EdwinDeviceDao.class, EdwinRecordDao.class, PhotoVideoDao.class,
                    DeviceBgDao.class, PushSetDao.class, SubDeviceDao.class);
        } else {
            MigrationHelper.getInstance().migrate(db,
                    EdwinDeviceDao.class, EdwinRecordDao.class, PhotoVideoDao.class,
                    DeviceBgDao.class, PushSetDao.class);
        }


    }


}
