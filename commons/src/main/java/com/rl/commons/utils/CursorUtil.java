package com.rl.commons.utils;

import android.database.Cursor;

public class CursorUtil {
	private CursorUtil(){
	}
	
	public static String getString(Cursor c, String columnName) {
		return c.getString(c.getColumnIndex(columnName));
	}
	
	public static int getInt(Cursor c, String columnName) {
		return c.getInt(c.getColumnIndex(columnName));
	}
	
	public static long getLong(Cursor c, String columnName) {
		return c.getLong(c.getColumnIndex(columnName));
	}
	
	public static float getFloat(Cursor c, String columnName) {
		return c.getFloat(c.getColumnIndex(columnName));
	}
	
	public static boolean getBoolean(Cursor c, String columnName) {
		return c.getInt(c.getColumnIndex(columnName)) == 1 ? true : false;
	}

}
