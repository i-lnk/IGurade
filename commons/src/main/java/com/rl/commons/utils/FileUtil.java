package com.rl.commons.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.text.DecimalFormat;

public class FileUtil {
	
	private FileUtil(){}
	
	/**
	 * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 
	 * @param directory
	 */
	public static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File child : directory.listFiles()) {
				if (child.isDirectory()) {
					deleteFilesByDirectory(child);
				} 
				child.delete();
			}
		}
	}


	public static boolean isFileExist( String filePath ){
		if( StringUtils.isEmpty(filePath))
			return false;
		File file = new File( filePath );
		return file.exists();
	}


	/**
	 * 转换文件大小
	 */
	public static String FormetFileSize(long mbZise) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		String wrongSize = "0MB";
		if (mbZise == 0) {
			return wrongSize;
		}
		if (mbZise < 1024) {
			fileSizeString = df.format((double) mbZise) + " B";
		} else if (mbZise < (1024*1024) ) {
			fileSizeString = df.format((double) mbZise / 1024) + " KB";
		} else if (mbZise < (1024*1024*1024) ) {
			fileSizeString = df.format( (double) mbZise / (1024*1024) ) + " MB";
		}else{
			fileSizeString = df.format((double) mbZise / (1024*1024*1024) ) + " GB";
		}
		return fileSizeString;
	}
	public static Uri getImageContentUri(File imageFile, Context context) {
		String filePath = imageFile.getAbsolutePath();
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Images.Media._ID },
				MediaStore.Images.Media.DATA + "=? ",
				new String[] { filePath }, null);

		if (cursor != null && cursor.moveToFirst()) {
			int id = cursor.getInt(cursor
					.getColumnIndex(MediaStore.MediaColumns._ID));
			Uri baseUri = Uri.parse("content://media/external/images/media");
			return Uri.withAppendedPath(baseUri, "" + id);
		} else {
			if (imageFile.exists()) {
				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.DATA, filePath);
				return context.getContentResolver().insert(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			} else {
				return null;
			}
		}
	}
}
