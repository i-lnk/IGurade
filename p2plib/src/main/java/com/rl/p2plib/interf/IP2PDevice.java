package com.rl.p2plib.interf;

/**
 * Created by Nicky on 2017/8/14.
 */

public interface IP2PDevice {

    String getDevId();
    String getName();
    int getType();
    int getStatus();
    String getUser();
    String getPwd();

}
