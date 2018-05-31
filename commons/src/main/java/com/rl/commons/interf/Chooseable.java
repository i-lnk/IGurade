package com.rl.commons.interf;

/**
 * 
 * 可选接口(用于选择列表、选择对话框， 一半为文字  或 图标+文字)
 * @author NickyHuang
 *
 */
public interface Chooseable {
	
	/**
	 * 
	 * @return 是否包含图标
	 */
	public boolean hasIcon();
	
	/**
	 * 
	 * @return 图标id
	 */
	public int getIconResid();
	
	/**
	 * @return 名称
	 */
	public String getName();
		

}
