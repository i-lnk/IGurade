package com.rl.geye.bean;


import com.rl.commons.BaseApp;
import com.rl.commons.interf.Chooseable;
import com.rl.geye.R;
import com.rl.p2plib.constants.P2PConstants;


/**
 * Created by Nicky on 2016/10/12.
 * 子设备类型(433)
 */
public class SubType implements Chooseable {

    private String name = "";
    private int type;

    public SubType() {
    }

    public SubType(int type) {
        this.type = type;
        switch (type) {
            case P2PConstants.SubDevType.REMOTE_CONTROL:
                name = BaseApp.context().getString(R.string.sub_type_1);
                break;
            case P2PConstants.SubDevType.ALARM:
                name = BaseApp.context().getString(R.string.sub_type_2);
                break;
            case P2PConstants.SubDevType.OTHER:
                name = BaseApp.context().getString(R.string.sub_type_3);
                break;
        }
    }


    public SubType(String name, int type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubType subType = (SubType) o;

        return type == subType.type;

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean hasIcon() {
        return true;
    }

    @Override
    public int getIconResid() {
        int resId = -1;
        switch (type) {
            case P2PConstants.SubDevType.REMOTE_CONTROL:
                resId = R.mipmap.ic_433_remote;
                break;
            case P2PConstants.SubDevType.ALARM:
                resId = R.mipmap.ic_433_alarm;
                break;
            case P2PConstants.SubDevType.OTHER:
                resId = R.mipmap.ic_433_other;
                break;
        }
        return resId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
