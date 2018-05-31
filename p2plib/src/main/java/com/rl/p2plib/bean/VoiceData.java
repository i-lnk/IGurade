package com.rl.p2plib.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by PYH on 2017/11/24.
 */

public class VoiceData implements Parcelable,Cloneable{
    private int audioVolume=1;

    public VoiceData() {
    }

    protected VoiceData(Parcel in) {
        audioVolume = in.readInt();
    }

    public static final Creator<VoiceData> CREATOR = new Creator<VoiceData>() {
        @Override
        public VoiceData createFromParcel(Parcel in) {
            return new VoiceData(in);
        }

        @Override
        public VoiceData[] newArray(int size) {
            return new VoiceData[size];
        }
    };

    public int getAudioVolume() {
        return audioVolume;
    }

    public void setAudioVolume(int audioVolume) {
        this.audioVolume = audioVolume;
    }

    @Override
    public String toString() {
        return "VoiceData{" +
                "audioVolume=" + audioVolume +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(audioVolume);
    }

}
