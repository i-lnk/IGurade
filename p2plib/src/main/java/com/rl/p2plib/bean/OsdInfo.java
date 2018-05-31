package com.rl.p2plib.bean;
/**
 * 
 * @ClassName: OsdInfo 
 * @Description: OSD信息
 * @author NickyHuang
 * @date 2016-6-30 下午4:34:05 
 *
 */
public class OsdInfo {
	
	private int channel = 0 ;
	private String channel_name_text = "";
	
	public int getChannel() {
		return channel;
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	
	public String getChannel_name_text() {
		return channel_name_text;
	}
	public void setChannel_name_text(String channel_name_text) {
		this.channel_name_text = channel_name_text;
	}

	
	
}
