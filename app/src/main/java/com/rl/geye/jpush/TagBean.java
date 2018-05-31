package com.rl.geye.jpush;

import com.rl.geye.constants.Constants;

import java.util.Set;

/**
 * Created by Nicky on 2017/8/10.
 */
public class TagBean {


    private int action;

    private Set<String> tags;


    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }


    public String getActionStr() {
        switch (action) {
            case Constants.OpAction.ACTION_ADD:
                return "add";
            case Constants.OpAction.ACTION_DELETE:
                return "delete";
            case Constants.OpAction.ACTION_SET:
                return "set";

        }
        return "unkonw operation";
    }


    @Override
    public String toString() {
        return "TagBean{" +
                "action=" + action +
                ", tags=" + tags +
                '}';
    }
}
