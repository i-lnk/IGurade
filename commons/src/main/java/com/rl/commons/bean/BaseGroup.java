package com.rl.commons.bean;

import com.rl.commons.interf.Groupable;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Nicky on 2016/9/12.
 * 群组基类(一般用于二级列表)
 */
public abstract  class BaseGroup<T> implements Groupable<T> {

    protected List<T> memberList = new ArrayList<>();

    public BaseGroup(){
        memberList = new ArrayList<>();
    }

    public BaseGroup( List<T> datas ){
        if (datas == null)
            datas = new ArrayList<>();
        memberList = datas;
    }


    @Override
    public List<T> getMemberList() {
        return memberList;
    }

    @Override
    public void setMemberList(List<T> memberList) {
        this.memberList = memberList;
    }

    @Override
    public void addMember(T m) {
        memberList.add(m);
    }

    @Override
    public void removeMember(T m) {
        memberList.remove(m);
    }

    @Override
    public void removeMember(int index) {
        memberList.remove(index);
    }

    @Override
    public int getChildrenCount() {
        return memberList.size();
    }

    @Override
    public T getChild(int childPosition) {
        return memberList.get(childPosition);
    }


}
