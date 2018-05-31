package com.rl.geye.bean;

/**
 * Created by Nicky on 2017/8/28.
 */

public class PushData {


    private PushDev dev;


    public PushDev getDev() {
        return dev;
    }

    public void setDev(PushDev dev) {
        this.dev = dev;
    }

    @Override
    public String toString() {
        return "PushData{" +
                "dev=" + dev +
                '}';
    }

    public static class PushDev {

        private String uuid;
        private int type;
        private long time;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        @Override
        public String toString() {
            return "PushDev{" +
                    "uuid='" + uuid + '\'' +
                    ", type=" + type +
                    ", time=" + time +
                    '}';
        }
    }
}
