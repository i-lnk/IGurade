package com.rl.geye.image;

import android.content.Context;

import java.util.HashMap;


/**
 * 图片工具类
 */
@Deprecated
public class ImageFetcher {
    private static ImageFetcher instance;
    /**
     * 是否已加载过了相册集合
     */
    boolean hasBuildImagesBucketList = false;
    private Context mContext;
    //	private List<ImageBucket> mBucketList = new ArrayList<>();
    private HashMap<String, String> mThumbnailList = new HashMap<>();

    private ImageFetcher() {
    }

    private ImageFetcher(Context context) {
        this.mContext = context;
    }

    public static ImageFetcher getInstance(Context context) {
        // if(context==null)
        // context = MyApplication.getMyApplicationContext();
        if (instance == null) {
            synchronized (ImageFetcher.class) {
                instance = new ImageFetcher(context);
            }
        }
        return instance;
    }

//
//	/**
//	 * 获取指定目录下第一张图片
//	 */
//	public ImageItem getFirstImage(String dir) {
//		ImageItem imageItem = null;
//		Cursor cur = null;
//		try
//		{
////			// 构造缩略图索引
////			getThumbnail();
//
//			// 构造相册索引
//			String columns[] = new String[] { Media._ID, Media.DATA};
//
//			//设定查询目录
//			String selection = MediaStore.Images.Media.DATA + " like ?";
//
//			//定义selectionArgs：
//			String[] selectionArgs = {  "%" +dir+"%" };
//
//			// 得到一个游标
//			cur = mContext.getContentResolver().query(
//					Media.EXTERNAL_CONTENT_URI, columns, selection, selectionArgs, Media._ID+" DESC");
//			if (cur.moveToFirst())
//			{
//				// 获取指定列的索引
//				int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
//				int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
//
//				String _id = cur.getString(photoIDIndex);
//				String path = cur.getString(photoPathIndex);
//
//				imageItem = new ImageItem();
//				imageItem.setId(_id );
////					imageItem.setThumbnailPath( mThumbnailList.get(_id) );
//				imageItem.setAbsolutePath( path );
//			}
//		}
//		finally
//		{
//			cur.close();
//		}
//		return imageItem;
//	}
//
//
//
//
//
//	/**
//	 * 获取指定目录下图片集
//	 */
//	public List<ImageItem> getImageList(String dir) {
//		List<ImageItem> tmpList = new ArrayList<>();
//		Cursor cur = null;
//		try
//		{
////			// 构造缩略图索引
////			getThumbnail();
//
//			// 构造相册索引
//			String columns[] = new String[] { Media._ID, Media.DATA };
//
//			//设定查询目录
//			String selection = Media.DATA + " like ? AND ("+MIME_TYPE + "=? or " + MIME_TYPE + "=? or "+ MIME_TYPE + "=? )";
//
//			//定义selectionArgs：
//			String[] selectionArgs = {  "%" +dir+"%" ,"image/jpeg", "image/png", "image/jpg"};
//
//
//			// 得到一个游标
//			cur = mContext.getContentResolver().query(
//					Media.EXTERNAL_CONTENT_URI, columns, selection, selectionArgs, Media._ID+" DESC");
//			if (cur.moveToFirst())
//			{
//				// 获取指定列的索引
//				int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
//				int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
//
//				do
//				{
//					String _id = cur.getString(photoIDIndex);
//					String path = cur.getString(photoPathIndex);
//
////					bucket.count++;
//					ImageItem imageItem = new ImageItem();
//					imageItem.setId(_id );
////					imageItem.setThumbnailPath( mThumbnailList.get(_id) );
//					imageItem.setAbsolutePath( path );
//					tmpList.add( imageItem );
//
//				}
//				while (cur.moveToNext());
//			}
//		}
//		finally
//		{
//			cur.close();
//		}
//		return tmpList;
//
//	}
//
//
//	/**
//	 * 获取相册内第一张图片
//	 */
//	public ImageItem getFirstImage() {
//		ImageItem imageItem = null;
//		Cursor cur = null;
//		try
//		{
////			// 构造缩略图索引
////			getThumbnail();
//
//			// 构造相册索引
//			String columns[] = new String[] { Media._ID, Media.DATA };
//
//			// 得到一个游标
//			cur = mContext.getContentResolver().query(
//					Media.EXTERNAL_CONTENT_URI, columns, null, null, Media.DATE_ADDED  + " DESC");
//			if (cur.moveToFirst())
//			{
//				// 获取指定列的索引
//				int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
//				int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
//				String _id = cur.getString(photoIDIndex);
//				String path = cur.getString(photoPathIndex);
//				imageItem = new ImageItem();
//				imageItem.setId(_id );
////					imageItem.setThumbnailPath( mThumbnailList.get(_id) );
//				imageItem.setAbsolutePath( path );
//			}
//		}
//		finally
//		{
//			cur.close();
//		}
//		return imageItem;
//	}
//
//
//	/**
//	 * 获取制定相册下的图片
//	 * @param bucket
//	 * @return
//     */
//	public List<ImageItem> getImageList( ImageBucket bucket ){
//		List<ImageItem> tmpList = new ArrayList<>();
//		Cursor cur = null;
//		try
//		{
////			// 构造缩略图索引
////			getThumbnail();
//
//			// 构造相册索引
//			String columns[] = new String[] { Media._ID, Media.DATA };
//
//			//设定查询目录
//			String selection = Media.BUCKET_ID + " = ? AND ("+MIME_TYPE + "=? or " + MIME_TYPE + "=? or "+ MIME_TYPE + "=? )";
//
//			//定义selectionArgs：
//			String[] selectionArgs = {  bucket.getBucketId(),"image/jpeg", "image/png", "image/jpg" };
//
//
//			// 得到一个游标
//			cur = mContext.getContentResolver().query(
//					Media.EXTERNAL_CONTENT_URI, columns, selection, selectionArgs, Media._ID+" DESC");
//			if (cur.moveToFirst())
//			{
//				// 获取指定列的索引
//				int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
//				int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
//
//				do
//				{
//					String _id = cur.getString(photoIDIndex);
//					String path = cur.getString(photoPathIndex);
//
////					bucket.count++;
//					ImageItem imageItem = new ImageItem();
//					imageItem.setId(_id );
////					imageItem.setThumbnailPath( mThumbnailList.get(_id) );
//					imageItem.setAbsolutePath( path );
//					tmpList.add( imageItem );
//
//				}
//				while (cur.moveToNext());
//			}
//		}
//		finally
//		{
//			cur.close();
//		}
//		return tmpList;
//	}
//
//

//	/**
//	 * 得到图片集
//	 */
//	public List<ImageBucket> getImagesBucketList()
//	{
//		buildImagesBucketList();
//		return mBucketList;
//	}
//
//
//	/**
//	 * 得到图片集
//	 */
//	private void buildImagesBucketList()
//	{
//		Cursor cur = null;
//		try
//		{
//			// 构造相册索引
//			String columns[] = new String[] { Media._ID, Media.BUCKET_ID,
//					Media.DATA, Media.BUCKET_DISPLAY_NAME , "count(*) AS " + "img_count"};
//
//			String selection = MIME_TYPE + "=? or " + MIME_TYPE + "=? or "+ MIME_TYPE + "=? "
//								+")  GROUP BY (" + Media.BUCKET_ID;
//
//			String[] selectionArgs = new String[] { "image/jpeg", "image/png", "image/jpg" };
//
//
//			// 得到一个游标
//			cur = mContext.getContentResolver().query(
//					Media.EXTERNAL_CONTENT_URI, columns, selection, selectionArgs, Media.DATE_ADDED  + " DESC");
//			//Media.BUCKET_DISPLAY_NAME+" ASC ,"+
//			if(mBucketList!=null)
//				mBucketList.clear();
//			if (cur!=null && cur.moveToFirst())
//			{
//				// 获取指定列的索引
//				int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
//				int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
//				int bucketDisplayNameIndex = cur.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
//				int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
////				int bucketTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE);
//				int countIndex = cur.getColumnIndexOrThrow("img_count");
//
//				do
//				{
//					String _id = cur.getString(photoIDIndex);
//					String bucketId = cur.getString(bucketIdIndex);
//					String path = cur.getString(photoPathIndex);
//					String bucketName = cur.getString(bucketDisplayNameIndex);
//					String count = cur.getString(countIndex);
//
//					ImageBucket bucket = new ImageBucket();
//					bucket.setBucketId( bucketId );
//					bucket.setCount(StringUtils.toInt(count) );
//					bucket.setBucketName( bucketName );
//					bucket.setFirstImg( path );
//					mBucketList.add( bucket);
//
////					String bucketTitle = cur.getString(bucketTitleIndex);
////					+ " ----- bucketTitle: "+bucketTitle
//					XLog.e("---------- path: "+path + " ----- bucketName: "+bucketName + " ----- count: "+count );
//
//				}
//				while (cur.moveToNext());
//			}
//
//			hasBuildImagesBucketList = true;
////			long endTime = System.currentTimeMillis();
//		}
//		finally
//		{
//			if( cur!=null )
//				cur.close();
//		}
//	}


}
