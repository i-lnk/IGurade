package com.rl.geye.bean;

import android.widget.Checkable;

/**
 * Created by Nicky on 2016/10/10.
 * 图片项
 */
public class ImageItem implements Checkable {

    private String path = ""; // 图片路径
    private String name = ""; // 图片名称
    private String id = ""; // 图片id
    private String thumbnailPath; //缩略图

    private boolean mChecked; // 是否选中

    public ImageItem() {
    }

    public ImageItem(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getAbsolutePath(){
//        String absolutePath = path;
//        if( path!=null && !path.endsWith("/") ){
//            absolutePath += "/";
//        }
//        absolutePath += name;
//        return absolutePath;
//    }
//
//    public void setAbsolutePath( String absolutePath ){
////        XLog.e("setAbsolutePath----------absolutePath: "+absolutePath);
//        int start=absolutePath.lastIndexOf("/");
////        int end=absolutePath.lastIndexOf(".jpg");
//        if(start!=-1){
//            name =  absolutePath.substring(start+1);
//            path = absolutePath.substring(0,start+1);
//        }
////        XLog.e("setAbsolutePath----------name: "+name);
////        XLog.e("setAbsolutePath----------path: "+path);
//    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        this.mChecked = checked;
    }

    @Override
    public void toggle() {
        mChecked = !mChecked;
    }
}
