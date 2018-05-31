package com.rl.geye;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.keeplibrary.Keep;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.rl.commons.BaseApp;
import com.rl.commons.bean.EdwinEvent;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.DaoMaster;
import com.rl.geye.db.bean.DaoSession;
import com.rl.geye.db.bean.EdwinDevice;
import com.rl.geye.jpush.JpushUtil;
import com.rl.p2plib.utils.IdUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.database.Database;

import java.util.List;
import java.util.logging.Level;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Nicky on 2017/3/2.
 */

public class MyApp extends BaseApp {
    private final static String TAG = "MyApp";
    private final static String DB_NAME = "rl-geye-encrypted.db";
    private final static String DB_PWD = "rl-geye-pwd";
    public static boolean IS_NEW_VERSION = true;
    public static String url;
    //    private static final Object handleCall = new Object();
    private static MyApp instance;
    private static DaoSession daoSession;

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static MyApp getInstance() {
        return instance;
    }

    /**
     * 设备
     */
    private static synchronized EdwinDevice getDevice(String did) {
        List<EdwinDevice> list = getDaoSession().getEdwinDeviceDao().loadAll();
        if (list != null && !list.isEmpty()) {
            for (EdwinDevice device : list) {
                String strDid = device.getDevId();
                if (IdUtil.isSameId(did, strDid)) {
                    return device;
                }
            }
        }
        return null;
    }


    @Override
    protected void onCreateProcess() {
        instance = this;
        initLog();
        initDb();
        initJPush();
        initOKGo();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        MyException.start(this);
        // BridgeService.init(NotificationClickReceiver.class, null);

        // Keep initialization
        Keep.initialization(this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        EventBus.getDefault().unregister(this);

    }

    @Override
    protected void onGotoBackground() {
        postEdwinEvent(Constants.EdwinEventType.EVENT_GOTO_BACKGROUND);
    }

    @Override
    protected void onGotoForeground() {
        postEdwinEvent(Constants.EdwinEventType.EVENT_GOTO_FOREGROUND);
    }

    /**
     * greenDao 初始化
     */
    private void initDb() {
        if (daoSession == null) {
//            SQLiteDatabase.loadLibs(this);
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DB_NAME);
//            /** 数据库升级 */
//            MyOpenHelper helper = new MyOpenHelper(this, DB_NAME);
            Database db = helper.getEncryptedWritableDb(DB_PWD);// helper.getWritableDb();
            daoSession = new DaoMaster(db).newSession();
        }
    }


    /**
     * JPUSH 初始化
     */
    private void initJPush() {
        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);// 初始化 JPush
        JPushInterface.initCrashHandler(this);
        JpushUtil.setDefaultNotification(this, R.mipmap.ic_logo_a);
        JPushInterface.resumePush(this);
    }


    /**
     * LOG 初始化
     */
    private void initLog() {
        if (BuildConfig.DEBUG) {
            Logger.clearLogAdapters();
            FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                    .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                    .methodCount(0)
//                    .methodCount(2)         // (Optional) How many method line to show. Default 2
//                    .methodOffset(5)        // (Optional) Skips some method invokes in stack trace. Default 5
//                .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
//              .tag(tag)   // (Optional) Custom tag for each log. Default PRETTY_LOGGER
                    .build();

            Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        } else {

            Logger.clearLogAdapters();
            Logger.addLogAdapter(new DiskLogAdapter() {
                @Override
                public boolean isLoggable(int priority, String tag) {
                    return priority >= Logger.INFO;
                }
            });
        }

//        Logger.clearLogAdapters();
//        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
//                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
//                .methodCount(0)
////                    .methodCount(2)         // (Optional) How many method line to show. Default 2
////                    .methodOffset(5)        // (Optional) Skips some method invokes in stack trace. Default 5
////                .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
////              .tag(tag)   // (Optional) Custom tag for each log. Default PRETTY_LOGGER
//                .build();
//
//        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
//
//        Logger.addLogAdapter(new DiskLogAdapter() {
//            @Override
//            public boolean isLoggable(int priority, String tag) {
//                return priority >= Logger.INFO ;
//            }
//        });

    }

    /**
     * OKGo网络请求框架 初始化
     */
    private void initOKGo() {
        //---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
//        HttpHeaders headers = new HttpHeaders();
//        headers.put("Content-Type", "application/json;charset=utf-8");
//        headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文，不允许有特殊字符
//        headers.put("commonHeaderKey2", "commonHeaderValue2");
//        HttpParams params = new HttpParams();
//        params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
//        params.put("commonParamsKey2", "这里支持中文参数");
        //-----------------------------------------------------------------------------------//

        //必须调用初始化
        OkGo.init(this);

        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        //好处是全局参数统一,特定请求可以特别定制参数
        try {
            //以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
            OkGo.getInstance()

                    // 打开该调试开关,打印级别INFO,并不是异常,是为了显眼,不需要就不要加入该行
                    // 最后的true表示是否打印okgo的内部异常，一般打开方便调试错误
                    .debug("OkGo", Level.INFO, false)

                    //如果使用默认的 60秒,以下三行也不需要传
                    .setConnectTimeout(OkGo.DEFAULT_MILLISECONDS)  //全局的连接超时时间
                    .setReadTimeOut(OkGo.DEFAULT_MILLISECONDS)     //全局的读取超时时间
                    .setWriteTimeOut(OkGo.DEFAULT_MILLISECONDS)    //全局的写入超时时间

                    //可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体其他模式看 github 介绍 https://github.com/jeasonlzy/
                    .setCacheMode(CacheMode.NO_CACHE)

                    //可以全局统一设置缓存时间,默认永不过期,具体使用方法看 github 介绍
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)

                    //可以全局统一设置超时重连次数,默认为三次,那么最差的情况会请求4次(一次原始请求,三次重连请求),不需要可以设置为0
                    .setRetryCount(3)

                    //如果不想让框架管理cookie（或者叫session的保持）,以下不需要
//                .setCookieStore(new MemoryCookieStore())            //cookie使用内存缓存（app退出后，cookie消失）
                    .setCookieStore(new PersistentCookieStore())        //cookie持久化存储，如果cookie不过期，则一直有效

                    //可以设置https的证书,以下几种方案根据需要自己设置
                    .setCertificates()                                  //方法一：信任所有证书,不安全有风险
//                    .setCertificates(new SafeTrustManager())            //方法二：自定义信任规则，校验服务端证书
//                    .setCertificates(getAssets().open("srca.cer"))      //方法三：使用预埋证书，校验服务端证书（自签名证书）
//                    //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
//                    .setCertificates(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"))//

            //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
//                    .setHostnameVerifier(new SafeHostnameVerifier())

            //可以添加全局拦截器，不需要就不要加入，错误写法直接导致任何回调不执行
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        return chain.proceed(chain.request());
//                    }
//                })

//                    //这两行同上，不需要就不要加入
//                    .addCommonHeaders(headers)  //设置全局公共头
//                    .addCommonParams(params);   //设置全局公共参数
            ;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /****************************************事件传递******************************************/

    protected void postEdwinEvent(EdwinEvent<?> event) {
        if (null != event) {
            EventBus.getDefault().post(event);
        }
    }

    protected void postEdwinEvent(int eventCode) {
        postEdwinEvent(new EdwinEvent<>(eventCode, MyApp.class));
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessage(EdwinEvent event) {
        if (event == null)
            return;
    }


}
