package com.rl.geye.image;


public class OfflineUtils {

    public static final int BUFFER_SIZE = 1024;

    //private static final String TAG = "OfflineUtils";
    public static String offlineFileName(String url) {
        int start = url.lastIndexOf("/");
        int end = url.lastIndexOf(".");
        return url.substring(start + 1, end);
    }

    public static String offlineName(String paramString) {
        return paramString.substring(1 + paramString.lastIndexOf('/'));
    }

    private static String offlineImageResourceName(String path) {
        return offlineName(path);
    }

//	public static File offlineResourceFile(String url) {
//		String fileName = offlineResourceName(url);
//		//FCLog.d("offline", "resourceName: " + fileName);
//		if (fileName != null) {
//			File file = OfflineLocalCache.getInstance().offlineResourceFile(
//					fileName);
//			//FCLog.d("offline", "fileName: " + file.getAbsolutePath());
//			if (file.exists()) {
//				return file;
//			}
//		}
//		return null;
//	}

    public static String offlineResourceName(String url) {
        String fileName = null;
        if (!UrlUtils.isAbsolutePath(url)) {
            return url;
        } else {
            fileName = offlineImageResourceName(url);
        }
        return fileName;
    }
}