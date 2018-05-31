package com.rl.commons.compatibility;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.media.AudioManager;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.v4.app.NotificationCompat;
import android.text.ClipboardManager;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.rl.commons.R;


/*
ApiFivePlus.java
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

/**
 * @author Sylvain Berfini
 */
@SuppressWarnings("deprecation")
@TargetApi(5)
public class ApiFivePlus {

	public static void overridePendingTransition(Activity activity, int idAnimIn, int idAnimOut) {
		activity.overridePendingTransition(idAnimIn, idAnimOut);
	}
	

	
	public static Notification createInCallNotification(Context context, String title, String msg, int iconID, PendingIntent intent) {
		NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
		.setSmallIcon(iconID)
		.setContentTitle(title)
		.setContentText(msg)
		.setContentIntent(intent);
	
	return notifBuilder.build();
	}

	public static void setPreferenceChecked(Preference preference, boolean checked) {
		((CheckBoxPreference) preference).setChecked(checked);
	}
	
	public static boolean isPreferenceChecked(Preference preference) {
		return ((CheckBoxPreference) preference).isChecked();
	}

	public static void copyTextToClipboard(Context context, String msg) {
	    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
	    clipboard.setText(msg);
	}
	


	public static void removeGlobalLayoutListener(ViewTreeObserver viewTreeObserver, OnGlobalLayoutListener keyboardListener) {
		viewTreeObserver.removeGlobalOnLayoutListener(keyboardListener);
	}

	public static void setAudioManagerInCallMode(AudioManager manager) {
		/* Do not use MODE_IN_CALL, because it is reserved to GSM. This is causing conflicts on audio system resulting in silenced audio.*/
		//manager.setMode(AudioManager.MODE_IN_CALL);
	}

	public static Notification createNotification(Context context, String title, String message, int icon, int level, PendingIntent intent, boolean isOngoingEvent) {
		NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
			.setSmallIcon(icon, level)
			.setContentTitle(title)
			.setContentText(message)
			.setContentIntent(intent);
		
		return notifBuilder.build();
	}

	public static Notification createSimpleNotification(Context context, String title, String text, PendingIntent intent) {
		NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.mipmap.ic_exception)//TODO
				.setContentTitle(title)
				.setContentText(text)
				.setContentIntent(intent);

		Notification notif = notifBuilder.build();
		notif.defaults |= Notification.DEFAULT_VIBRATE;
		notif.defaults |= Notification.DEFAULT_SOUND;
		notif.defaults |= Notification.DEFAULT_LIGHTS;
		
		return notif;
	}
}
