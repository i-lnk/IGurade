package com.rl.commons.interf;

/**
 * 
 * @ClassName: EdwinTimeoutCallback
 * @Description: 超时接口 基础实现类
 * @author NickyHuang
 * @date 2016-3-10
 */
public abstract class EdwinTimeoutCallback implements TimeoutCallback{
	public long timeout;
	
	public EdwinTimeoutCallback(long timeout){
		this.timeout = timeout;
	}
}