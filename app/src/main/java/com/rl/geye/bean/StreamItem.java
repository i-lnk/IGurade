package com.rl.geye.bean;

/**
 * Created by Nicky on 2016/8/24.
 */
public class StreamItem extends ChooseItem {

    private String simpleName;
    private int val;


    public StreamItem() {
    }

    public StreamItem(String name, boolean hasIcon, int iconId, String simpleName, int val) {
        super(name, hasIcon, iconId);
        this.simpleName = simpleName;
        this.val = val;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
//        if (!super.equals(o)) return false;

        StreamItem that = (StreamItem) o;

        return val == that.val;

    }

}
