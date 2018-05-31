package com.rl.commons.compatibility;
/*
Compatibility.java
Copyright (C) 2012  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.preference.Preference;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

/**
 * @author Sylvain Berfini
 */
public class Compatibility {

	public static void overridePendingTransition(Activity activity, int idAnimIn, int idAnimOut) {
		if (Version.sdkAboveOrEqual(Version.API05_ECLAIR_20)) {
			ApiFivePlus.overridePendingTransition(activity, idAnimIn, idAnimOut);
		}
	}


	
	public static Notification createSimpleNotification(Context context, String title, String text, PendingIntent intent) {
		Notification notif = null;
		
		if (Version.sdkAboveOrEqual(Version.API21_LOLLIPOP_50)) {
			return ApiTwentyOnePlus.createSimpleNotification(context, title, text, intent);
		} else if (Version.sdkAboveOrEqual(Version.API16_JELLY_BEAN_41)) {
			notif = ApiSixteenPlus.createSimpleNotification(context, title, text, intent);
		} else if (Version.sdkAboveOrEqual(Version.API11_HONEYCOMB_30)) {
			notif = ApiElevenPlus.createSimpleNotification(context, title, text, intent);
		} else {
			notif = ApiFivePlus.createSimpleNotification(context, title, text, intent);
		}
		return notif;
	}
	

	//TODO
	public static Notification createInCallNotification(Context context, String title, String msg, int iconID, Bitmap contactIcon, String contactName, PendingIntent intent) {
		Notification notif = null;
		
		if (Version.sdkAboveOrEqual(Version.API21_LOLLIPOP_50)) {
			return ApiTwentyOnePlus.createInCallNotification(context, title, msg, iconID, contactIcon, contactName, intent);
		} else if (Version.sdkAboveOrEqual(Version.API16_JELLY_BEAN_41)) {
			notif = ApiSixteenPlus.createInCallNotification(context, title, msg, iconID, contactIcon, contactName, intent);
		} else if (Version.sdkAboveOrEqual(Version.API11_HONEYCOMB_30)) {
			notif = ApiElevenPlus.createInCallNotification(context, title, msg, iconID, contactIcon, contactName, intent);
		} else {
			notif = ApiFivePlus.createInCallNotification(context, title, msg, iconID, intent);
		}
		return notif;
	}

	public static Notification createNotification(Context context, String title, String message, int icon, int iconLevel, Bitmap largeIcon, PendingIntent intent, boolean isOngoingEvent,int priority) {
		if (Version.sdkAboveOrEqual(Version.API21_LOLLIPOP_50)) {
			return ApiTwentyOnePlus.createNotification(context, title, message, icon, iconLevel, largeIcon, intent, isOngoingEvent,priority);
		} else if (Version.sdkAboveOrEqual(Version.API16_JELLY_BEAN_41)) {
			return ApiSixteenPlus.createNotification(context, title, message, icon, iconLevel, largeIcon, intent, isOngoingEvent,priority);
		} else if (Version.sdkAboveOrEqual(Version.API11_HONEYCOMB_30)) {
			return ApiElevenPlus.createNotification(context, title, message, icon, iconLevel, largeIcon, intent, isOngoingEvent);
		} else {
			return ApiFivePlus.createNotification(context, title, message, icon, iconLevel, intent, isOngoingEvent);
		}
	}



	public static CompatibilityScaleGestureDetector getScaleGestureDetector(Context context, CompatibilityScaleGestureListener listener) {
		if (Version.sdkAboveOrEqual(Version.API08_FROYO_22)) {
			CompatibilityScaleGestureDetector csgd = new CompatibilityScaleGestureDetector(context);
			csgd.setOnScaleListener(listener);
			return csgd;
		}
		return null;
	}
	
	
	public static void setPreferenceChecked(Preference preference, boolean checked) {
		if (Version.sdkAboveOrEqual(Version.API14_ICE_CREAM_SANDWICH_40)) {
			ApiFourteenPlus.setPreferenceChecked(preference, checked);
		} else {
			ApiFivePlus.setPreferenceChecked(preference, checked);
		}
	}
	
	public static boolean isPreferenceChecked(Preference preference) {
		if (Version.sdkAboveOrEqual(Version.API14_ICE_CREAM_SANDWICH_40)) {
			return ApiFourteenPlus.isPreferenceChecked(preference);
		} else {
			return ApiFivePlus.isPreferenceChecked(preference);
		}
	}
	


	public static void copyTextToClipboard(Context context, String msg) {
		if(Version.sdkAboveOrEqual(Version.API11_HONEYCOMB_30)) {
			ApiElevenPlus.copyTextToClipboard(context, msg);
		} else {
		    ApiFivePlus.copyTextToClipboard(context, msg);
		}
	}
	


	public static void removeGlobalLayoutListener(ViewTreeObserver viewTreeObserver, OnGlobalLayoutListener keyboardListener) {
		if (Version.sdkAboveOrEqual(Version.API16_JELLY_BEAN_41)) {
			ApiSixteenPlus.removeGlobalLayoutListener(viewTreeObserver, keyboardListener);
		} else {
			ApiFivePlus.removeGlobalLayoutListener(viewTreeObserver, keyboardListener);
		}
	}
	
	public static void hideNavigationBar(Activity activity)
	{
		if (Version.sdkAboveOrEqual(Version.API14_ICE_CREAM_SANDWICH_40)) {
			ApiFourteenPlus.hideNavigationBar(activity);
		}
	}
	
	public static void showNavigationBar(Activity activity)
	{
		if (Version.sdkAboveOrEqual(Version.API14_ICE_CREAM_SANDWICH_40)) {
			ApiFourteenPlus.showNavigationBar(activity);
		}
	}
	
	public static void setAudioManagerInCallMode(AudioManager manager) {
		if (Version.sdkAboveOrEqual(Version.API11_HONEYCOMB_30)) {
			ApiElevenPlus.setAudioManagerInCallMode(manager);
		} else {
			ApiFivePlus.setAudioManagerInCallMode(manager);
		}
	}
	
	public static String getAudioManagerEventForBluetoothConnectionStateChangedEvent() {
		if (Version.sdkAboveOrEqual(Version.API14_ICE_CREAM_SANDWICH_40)) {
			return ApiFourteenPlus.getAudioManagerEventForBluetoothConnectionStateChangedEvent();
		} else {
			return ApiEightPlus.getAudioManagerEventForBluetoothConnectionStateChangedEvent();
		}
	}
}
