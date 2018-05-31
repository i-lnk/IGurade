package com.rl.geye.receiver;

/**
 * Created by Nicky on 2017/5/5.
 */

public interface INetChangeListener {

    /**
     * 网络连接连接时调用
     */
    void onNetChanged(boolean isConnected);

}
