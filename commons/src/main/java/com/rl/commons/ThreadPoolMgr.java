package com.rl.commons;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * @ClassName: ThreadPoolMgr 
 * @Description: 线程池管理
 * @author NickyHuang
 * @date 2016-7-13 下午5:06:57 
 *
 */
public class ThreadPoolMgr {
	
	private static ExecutorService SINGLE_TASK_EXECUTOR; //单个任务线程池 
    private static ExecutorService LIMITED_TASK_EXECUTOR;  //限制线程个数的线程池
    private static ExecutorService LIMITED_TASK_EXECUTOR2;  //限制线程个数的线程池2
    private static ExecutorService FULL_TASK_EXECUTOR;  //无限制线程池
    

    private ThreadPoolMgr(){
    }
    

    public static ExecutorService getFullThreadPool(){
    	if( FULL_TASK_EXECUTOR==null ){
    		FULL_TASK_EXECUTOR = Executors.newCachedThreadPool();
    	}
    	return FULL_TASK_EXECUTOR;
    }
    
    public static ExecutorService getCustomThreadPool(){
    	if( LIMITED_TASK_EXECUTOR==null ){
    		LIMITED_TASK_EXECUTOR = Executors.newFixedThreadPool(10);
    	}
    	return LIMITED_TASK_EXECUTOR;
    }

	public static ExecutorService getCustomThreadPool2(){
		if( LIMITED_TASK_EXECUTOR2==null ){
			LIMITED_TASK_EXECUTOR2 = Executors.newFixedThreadPool(20);
		}
		return LIMITED_TASK_EXECUTOR2;
	}

	
    public static ExecutorService getSingleThreadPool(){
    	if( SINGLE_TASK_EXECUTOR==null ){
    		SINGLE_TASK_EXECUTOR = Executors.newSingleThreadExecutor();
    	}
    	return SINGLE_TASK_EXECUTOR;
    }

}
