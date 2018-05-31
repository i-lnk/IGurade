package com.rl.commons.compatibility;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;

import com.rl.commons.R;
import com.rl.commons.log.XLog;

/*
ApiElevenPlus.java
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
@TargetApi(11)
public class ApiElevenPlus {


	@SuppressWarnings("deprecation")
	public static Notification createInCallNotification(Context context,
			String title, String msg, int iconID, Bitmap contactIcon,
			String contactName, PendingIntent intent) {

		Notification notif = new Notification.Builder(context).setContentTitle(contactName)
						.setContentText(msg).setSmallIcon(iconID)
						.setAutoCancel(false)
						.setContentIntent(intent)
						.setWhen(System.currentTimeMillis())
						.setLargeIcon(contactIcon).getNotification();
		notif.flags |= Notification.FLAG_ONGOING_EVENT;

		return notif;
	}
	
	@SuppressWarnings("deprecation")
	public static Notification createNotification(Context context, String title, String message, int icon, int level, Bitmap largeIcon, PendingIntent intent, boolean isOngoingEvent) {
		Notification notif;
		
		if (largeIcon != null) {
			notif = new Notification.Builder(context)
	        .setContentTitle(title)
	        .setContentText(message)
	        .setSmallIcon(icon, level)
	        .setLargeIcon(largeIcon)
	        .setContentIntent(intent)
	        .setWhen(System.currentTimeMillis())
	        .getNotification();
		} else {
			notif = new Notification.Builder(context)
	        .setContentTitle(title)
	        .setContentText(message)
	        .setSmallIcon(icon, level)
	        .setContentIntent(intent)
	        .setWhen(System.currentTimeMillis())
	        .getNotification();
		}
		if (isOngoingEvent) {
			notif.flags |= Notification.FLAG_ONGOING_EVENT;
		}
		
		return notif;
	}

	public static void copyTextToClipboard(Context context, String msg) {
		ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE); 
	    ClipData clip = ClipData.newPlainText("Message", msg);
	    clipboard.setPrimaryClip(clip);
	}

	public static void setAudioManagerInCallMode(AudioManager manager) {
		if (manager.getMode() == AudioManager.MODE_IN_COMMUNICATION) {
			XLog.w("---AudioManager: already in MODE_IN_COMMUNICATION, skipping...");
			return;
		}
		XLog.d("---AudioManager: set mode to MODE_IN_COMMUNICATION");
		manager.setMode(AudioManager.MODE_IN_COMMUNICATION);
	}
	


	@SuppressWarnings("deprecation")
	public static Notification createSimpleNotification(Context context, String title, String text, PendingIntent intent) {
		Notification notif = new Notification.Builder(context)
		.setContentTitle(title)
		.setContentText(text)
		.setContentIntent(intent)
		.setSmallIcon(R.mipmap.ic_exception)//TODO
		.setAutoCancel(true)
		.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
		.setWhen(System.currentTimeMillis()).getNotification();
		return notif;
	}
}
