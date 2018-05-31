package com.rl.p2plib.bean;

/**
 * Created by Nicky on 2017/4/15.
 * 某月的录像数据
 */

public class MonthRecord {

    private int year;
    private int month;
    private long map;

    private boolean[] datas = null ;



    public MonthRecord() {

    }




    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public long getMap() {
        return map;
    }

    public void setMap(long map) {
        this.map = map;
    }


    public boolean[] getDatas(){
        if( datas==null ){
            datas = new boolean[32];
            for (int i=0;i<32;i++){
                datas[i] =  (byte) ( map>>i & 0xFF & 0x01)>0;
//                XLog.i("---->" + i + " ,datas[i] "+datas[i]);
            }
        }
        return datas;
    }



}
