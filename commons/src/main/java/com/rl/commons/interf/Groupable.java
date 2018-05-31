package com.rl.commons.interf;

import java.util.List;

/**
 * 
 * 分组接口(用于2级列表)
 * @author NickyHuang
 *
 */
public interface Groupable<T> {

	/**
	 *
	 * @return the member list of this group
     */
	List<T> getMemberList();

	/**
	 * set the member list for this group
	 * @param memberList
     */
	void setMemberList(List<T> memberList);

	/**
	 * 添加成员
	 * @param m 成员信息
	 */
	void addMember(T m);

	/**
	 * 删除成员
	 * @param m 成员信息
	 */
	void removeMember(T m);

	/**
	 * 删除成员
	 * @param index 成员下标
	 */
	void removeMember(int index);

	/**
	 * Gets the number of children in this group.
	 */
	int getChildrenCount();


	/**
	 * Gets the data associated with the given child in this group.
	 *
	 * @param childPosition the position of the child with respect to other
	 *            children in the group
	 * @return the data of the child
	 */
	T getChild(int childPosition);

}
