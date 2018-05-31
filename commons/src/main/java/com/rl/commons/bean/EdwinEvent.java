package com.rl.commons.bean;


/**
 * 
 * @ClassName: EdwinEvent
 * @Description: App内传递事件
 * @author NickyHuang
 * @date 2016-3-10
 * @param <T>
 */
public class EdwinEvent<T> {

    public static final int UNKNOWN = -1;//未知类型

    /**
     * reserved data
     */
    private T data;
    
    /**
     * from where
     */
    private Class<?> fromCls;

    /**
     * this code distinguish between different events
     */
    private int eventCode = UNKNOWN;
    
    
    public EdwinEvent(int eventCode) {
        this(eventCode, null, null);
    }
    
    
    public EdwinEvent(int eventCode,Class<?> fromCls) {
        this(eventCode, null,fromCls);
    }

    public EdwinEvent(int eventCode,T data,Class<?> fromCls) {
        this.eventCode = eventCode;
        this.data = data;
        this.fromCls = fromCls;
    }

    /**
     * get event code
     */
    public int getEventCode() {
        return this.eventCode;
    }

    /**
     * get event reserved data
     */
    public T getData() {
        return this.data;
    }

    public void setFromCls(Class<?> fromCls) {
        this.fromCls = fromCls;
    }

    /**
     * get where event post 
     */
	public Class<?> getFromCls() {
		return fromCls;
	}


	@Override
	public String toString() {
		return "EdwinEvent [ fromCls=" + fromCls
				+ ", eventCode=" + eventCode + "]";
	}


}
