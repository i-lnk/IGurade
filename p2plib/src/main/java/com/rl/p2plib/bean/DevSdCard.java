package com.rl.p2plib.bean;


import com.rl.p2plib.constants.P2PConstants;

/**
 * 
 * @ClassName: DevSdCard 
 * @Description: SD卡状态
 * @author NickyHuang
 * @date 2016-7-27 下午2:53:28 
 *
 */
public class DevSdCard {
	
	private int status = 0;
	
	private int size = 0 ;
	private int free = 0;
	private int progress = 0;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getFree() {
		return free;
	}
	public void setFree(int free) {
		this.free = free;
	}
	
	public int getUsed(){
		return size>free?(size-free):0;
	}
	
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	
	/** SD卡是否存在 */
	public boolean isSdCardExist(){
		return status!= P2PConstants.SDStatus.SD_STATUS_NOTINIT;
	}
	
	/** 设备是否支持SD卡读写指令 */
	public boolean isValidDev(){
		return (status== P2PConstants.SDStatus.SD_STATUS_NOTINIT
				||status== P2PConstants.SDStatus.SD_STATUS_OK
				||status== P2PConstants.SDStatus.SD_STATUS_NOTFORMAT
				||status== P2PConstants.SDStatus.SD_STATUS_FORMAT_OK
				||status== P2PConstants.SDStatus.SD_STATUS_READONLY
				||status== P2PConstants.SDStatus.SD_STATUS_FORMATING);
	}
	
	public boolean isFormatOk(){
		return status== P2PConstants.SDStatus.SD_STATUS_OK || status== P2PConstants.SDStatus.SD_STATUS_FORMAT_OK;
	}
	
}
