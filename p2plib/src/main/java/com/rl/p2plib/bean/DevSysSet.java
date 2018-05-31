package com.rl.p2plib.bean;


import com.rl.commons.BaseApp;
import com.rl.p2plib.R;
import com.rl.p2plib.constants.P2PConstants;

/**
 * 
 * @ClassName: DevSysSet
 * @Description: 设备语音
 * @author NickyHuang
 * @date 2016-6-30 下午4:34:05 
 *
 */
public class DevSysSet  implements Cloneable{
	
	private int language = 0;

	private int enableAutomicUpdate = 0; //自动升级
	private int enablePreviewUnlock = 0; //监视开锁
	private int enableRingingButton = 0; //撞击门铃

	private String datetime = "";//设备时间

	@Override
	public Object clone(){
		DevSysSet obj = null;
		try {
			obj = (DevSysSet) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DevSysSet devSysSet = (DevSysSet) o;

		if (language != devSysSet.language) return false;
		if (enableAutomicUpdate != devSysSet.enableAutomicUpdate) return false;
		if (enablePreviewUnlock != devSysSet.enablePreviewUnlock) return false;
		if (enableRingingButton != devSysSet.enableRingingButton) return false;
		return datetime != null ? datetime.equals(devSysSet.datetime) : devSysSet.datetime == null;

	}

//	@Override
//	public int hashCode() {
//		int result = language;
//		result = 31 * result + enableAutomicUpdate;
//		result = 31 * result + enablePreviewUnlock;
//		result = 31 * result + enableRingingButton;
//		result = 31 * result + (datetime != null ? datetime.hashCode() : 0);
//		return result;
//	}

	public DevSysSet(){
	}
	public DevSysSet(int language){
		this.language = language;
	}


	public int getEnableAutomicUpdate() {
		return enableAutomicUpdate;
	}

	public void setEnableAutomicUpdate(int enableAutomicUpdate) {
		this.enableAutomicUpdate = enableAutomicUpdate;
	}

	public int getEnablePreviewUnlock() {
		return enablePreviewUnlock;
	}

	public void setEnablePreviewUnlock(int enablePreviewUnlock) {
		this.enablePreviewUnlock = enablePreviewUnlock;
	}

	public int getEnableRingingButton() {
		return enableRingingButton;
	}

	public void setEnableRingingButton(int enableRingingButton) {
		this.enableRingingButton = enableRingingButton;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public int getLanguage() {
		return language;
	}

	public void setLanguage(int language) {
		this.language = language;
	}


	public String getLanguageStr(){

		return BaseApp.context().getString( language== P2PConstants.LanguageType.TYPE_CN?
						R.string.language_ch:R.string.language_en   );

	}

}
