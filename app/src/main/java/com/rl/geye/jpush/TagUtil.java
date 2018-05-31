package com.rl.geye.jpush;


import android.content.Context;
import android.util.SparseArray;

import com.orhanobut.logger.Logger;
import com.rl.geye.MyApp;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.EdwinDevice;
import com.rl.geye.logic.DataLogic;
import com.rl.geye.service.TaskWorkServer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;


/**
 * Created by Nicky on 2017/8/10.
 */
public class TagUtil {

    public static final String EMPTY_JPUSH_TAG = "empty_jpush_tag"; //极光TAG不允许为空
    private static final String TAG = "TagUtil";
    private static int retryTime = 0;
    /* 持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载 */
    private static TagUtil instance = null;
    SparseArray<TagBean> tagMaps = new SparseArray<>();


    /* 私有构造方法，防止被实例化 */
    private TagUtil() {
    }

    /* 1:懒汉式，静态工程方法，创建实例 */
    public static TagUtil getInstance() {
        if (instance == null) {
            instance = new TagUtil();
        }
        return instance;
    }

    public TagBean get(int sequence) {
        return tagMaps.get(sequence);
    }

    public void put(int sequence, TagBean tagAliasBean) {
        tagMaps.put(sequence, tagAliasBean);
    }

    public void remove(int sequence) {
        tagMaps.remove(sequence);
    }

    /**
     * 设置推送Tag
     */
    public void setTagsInNewThread(Context context, int sequence, TagBean tagBean) {
        put(sequence, tagBean);
        new Thread(new SetJPushTagThread(context, sequence, tagBean)).start();
    }

    /**
     * 设置推送Tag
     */
    public void setTags(Context context, int sequence, TagBean tagBean) {
        if (tagBean == null)
            return;
        put(sequence, tagBean);
        switch (tagBean.getAction()) {
            case Constants.OpAction.ACTION_ADD:
                JPushInterface.addTags(context, sequence, tagBean.getTags());
                break;
            case Constants.OpAction.ACTION_DELETE:
                JPushInterface.deleteTags(context, sequence, tagBean.getTags());
                break;
            case Constants.OpAction.ACTION_SET:
                JPushInterface.setTags(context, sequence, tagBean.getTags());
                break;
        }
    }

    private boolean isSameTags(Set<String> tags1, Set<String> tags2) {
        if (tags1 == null || tags1.isEmpty())
            return tags2 == null || tags2.isEmpty();
        if (tags2 == null || tags1.size() != tags2.size())
            return false;
        return tags1.containsAll(tags2);

    }

    /**
     * TAG 设置结果
     */
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        Logger.t(TAG).i("当前极光TAGS: " + jPushMessage.getTags());
        int sequence = jPushMessage.getSequence();
        //根据sequence从之前操作缓存中获取缓存记录
        TagBean tagBean = tagMaps.get(sequence);
        if (tagBean == null) {
            Logger.t(TAG).e("获取缓存记录失败,或者暂无标签设置");
//            JpushUtil.showToast("获取缓存记录失败", context);
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            retryTime = 0;
            tagMaps.remove(sequence);
            String logs = tagBean.getActionStr() + " tags [ " + tagBean.getTags() + "  ]success";
            Logger.t(TAG).i(logs);

            List<EdwinDevice> devs = MyApp.getDaoSession().getEdwinDeviceDao().loadAll();
            final Set<String> tags = new HashSet<>();
            for (EdwinDevice dev : devs) {
                tags.add(dev.getDevId().replace("-", ""));
            }
            if (tags.isEmpty()) {
                tags.add(EMPTY_JPUSH_TAG);
            }

            if (isSameTags(tags, tagBean.getTags())) {
                DataLogic.saveJpushTagOk(true);
                Logger.t(TAG).i("------- SAVE TAGS OK ----------");
            }
            TaskWorkServer.onTagSetFinished(jPushMessage.getErrorCode());
//            JpushUtil.showToast(logs, context);
        } else {
            retryTime++;
            String logs = "Failed to " + tagBean.getActionStr() + " tags";
            if (jPushMessage.getErrorCode() == 6018) {
                //tag数量超过限制,需要先清除一部分再add
                logs += ", tags is exceed limit need to clean";
            }
            logs += ", errorCode:" + jPushMessage.getErrorCode();
            Logger.t(TAG).e(logs);

            TaskWorkServer.onTagSetFinished(jPushMessage.getErrorCode());


//            if (!RetryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasBean)) {
//                JpushUtil.showToast(logs, context);
//            }
        }
    }

    /**
     * 设置推送Tag 线程
     */
    public static class SetJPushTagThread implements Runnable {

        private TagBean tagBean;
        private int sequence;
        private Context mContext;

        public SetJPushTagThread(Context context, int sequence, TagBean tagBean) {
            this.sequence = sequence;
            this.tagBean = tagBean;
            this.mContext = context;
        }

        @Override
        public void run() {
            if (tagBean == null)
                return;
            switch (tagBean.getAction()) {
                case Constants.OpAction.ACTION_ADD:
                    JPushInterface.addTags(mContext, sequence, tagBean.getTags());
                    break;
                case Constants.OpAction.ACTION_DELETE:
                    JPushInterface.deleteTags(mContext, sequence, tagBean.getTags());
                    break;
                case Constants.OpAction.ACTION_SET:
                    JPushInterface.setTags(mContext, sequence, tagBean.getTags());
                    break;
            }

        }
    }


}
