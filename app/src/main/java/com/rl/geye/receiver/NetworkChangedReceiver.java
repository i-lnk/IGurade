package com.rl.geye.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

public class NetworkChangedReceiver extends BroadcastReceiver {
    private static final Object handleStatusCallBack = new Object();
    private static NetworkInfo lastNetwork;
    private static boolean isFirstChanged = true;
    private static ArrayList<INetChangeListener> netChangeListeners = new ArrayList<>();
    private String TAG = "NetworkChangedReceiver";

    /**
     * 注册网络连接观察者
     */
    public static void registerObserver(INetChangeListener observer) {
        if (netChangeListeners == null) {
            netChangeListeners = new ArrayList<>();
        }
        netChangeListeners.add(observer);
    }

    /**
     * 注销网络连接观察者
     */
    public static void removeRegisterObserver(INetChangeListener observer) {
        if (netChangeListeners != null) {
            netChangeListeners.remove(observer);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//		XLog.e(TAG, " -------------> intent "+intent.getAction());
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {

//			NetworkInfo netInfo = (NetworkInfo) b.get(ConnectivityManager.EXTRA_NETWORK_TYPE);
//			NetworkInfo.State state = netInfo.getState();
//			ConnectivityManager connectivityManager = (ConnectivityManager) context
//					.getSystemService(Context.CONNECTIVITY_SERVICE);
//			NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
//
//			if ((state == NetworkInfo.State.CONNECTED) && (activeNetInfo != null) && (activeNetInfo.getType() != netInfo.getType())) {
//				return;
//			}

            synchronized (handleStatusCallBack) {
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                Logger.t(TAG).i("CONNECTIVITY_ACTION");

                NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

                if (isFirstChanged || !isSameNetwork(lastNetwork, activeNetwork)) {
                    isFirstChanged = false;
                    Logger.t(TAG).i("CONNECTIVITY CHANGED.............");
//                    if (activeNetwork != null) {
//                        XLog.e(TAG, "info.getTypeName()" + activeNetwork.getTypeName());
//                        XLog.e(TAG, "getSubtypeName()" + activeNetwork.getSubtypeName());
//                        XLog.e(TAG, "getState()" + activeNetwork.getState());
//                        XLog.e(TAG, "getDetailedState()" + activeNetwork.getDetailedState().name());
//                        XLog.e(TAG, "getExtraInfo()" + activeNetwork.getExtraInfo());
//                        XLog.e(TAG, "getType()" + activeNetwork.getType());
//                    }
                    notifyObserver(activeNetwork != null && activeNetwork.isConnected());
                }
                lastNetwork = activeNetwork;
            }

//			if (activeNetwork != null) {
//				// connected to the internet
//				if (activeNetwork.isConnected()) {
//					if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
//						// connected to wifi
////						APP.getInstance().setWifi(true);
//						XLog.e(TAG, "当前WiFi连接可用 ");
//					} else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
//						// connected to the mobile provider's data plan
////						APP.getInstance().setMobile(true);
//						XLog.e(TAG, "当前移动网络连接可用 ");
//					}
//				} else {
//					XLog.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");
//				}
//
//				XLog.e(TAG, "info.getTypeName()" + activeNetwork.getTypeName());
//				XLog.e(TAG, "getSubtypeName()" + activeNetwork.getSubtypeName());
//				XLog.e(TAG, "getState()" + activeNetwork.getState());
//				XLog.e(TAG, "getDetailedState()" + activeNetwork.getDetailedState().name());
//				XLog.e(TAG, "getDetailedState()" + activeNetwork.getExtraInfo());
//				XLog.e(TAG, "getType()" + activeNetwork.getType());
//			} else {
//				// not connected to the internet
//				XLog.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");
////				APP.getInstance().setWifi(false);
////				APP.getInstance().setMobile(false);
////				APP.getInstance().setConnected(false);
//
//			}


        }


    }

    private boolean isSameNetwork(NetworkInfo net1, NetworkInfo net2) {
        if (net1 == null && net2 == null) {
            return true;
        }
        if (net1 != null && net2 != null) {
            return (net1.getState() == net2.getState()
                    && net1.getDetailedState() == net2.getDetailedState()
                    && net1.getExtraInfo().equals(net2.getExtraInfo())
                    && net1.getType() == net2.getType());
        }
        return false;
    }

    private void notifyObserver(boolean isConnecter) {
        for (INetChangeListener observer : netChangeListeners) {
            Logger.t(TAG).i(".............isConnecter: " + isConnecter);
            observer.onNetChanged(isConnecter);
        }
    }

}
