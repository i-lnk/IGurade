package com.rl.geye.bean;


import com.rl.commons.interf.Chooseable;

/**
 * 可选项
 *
 * @author NickyHuang
 */
public class ChooseItem implements Chooseable {

    private boolean hasIcon;
    private String name;
    private int iconId = -1;


    public ChooseItem(String name, boolean hasIcon, int iconId) {
        super();
        this.hasIcon = hasIcon;
        this.name = name;
        this.iconId = iconId;
    }


    public ChooseItem() {
        super();
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChooseItem other = (ChooseItem) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    public void setHasIcon(boolean hasIcon) {
        this.hasIcon = hasIcon;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    @Override
    public boolean hasIcon() {
        return hasIcon;
    }

    @Override
    public int getIconResid() {
        return iconId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
