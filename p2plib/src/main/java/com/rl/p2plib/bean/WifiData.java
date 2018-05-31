package com.rl.p2plib.bean;
/**
 * 
 * @ClassName: WifiData 
 * @Description: Wifi信息
 * @author NickyHuang
 * @date 2016-6-30 下午4:34:05 
 *
 */
public class WifiData {
	
	private String ssid;
	private String password;
	private int encrypt;
	private int mode;
	private int signal;
	private int status;
	
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getEncrypt() {
		return encrypt;
	}
	public void setEncrypt(int encrypt) {
		this.encrypt = encrypt;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public int getSignal() {
		return signal;
	}
	public void setSignal(int signal) {
		this.signal = signal;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
