package com.rl.p2plib.bean;
/**
 * 
 * @ClassName: PicMode
 * @Description: 图像翻转模式
 * @author NickyHuang
 * @date 2016-6-30 下午4:34:05 
 *
 */
public class PicMode {
	
	private int channel = 0;
	private int flip = 0;
	private int mirrow = 0;
	
	public boolean isRotate(){
		return flip==1;
	}
	public void setRotate( boolean isRotate ){
		flip = (isRotate?1:0);
	}
	public void toggleRotate() {
		if(flip==0){
			flip = 1;
		}else{
			flip =0;
		}
	}
	public void toggleMirror() {
		if(mirrow==0){
			mirrow = 1;
		}else{
			mirrow =0;
		}
	}
	
	public int getChannel() {
		return channel;
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	public int getFlip() {
		return flip;
	}
	public void setFlip(int flip) {
		this.flip = flip;
	}
	public int getMirrow() {
		return mirrow;
	}
	public void setMirrow(int mirrow) {
		this.mirrow = mirrow;
	}
	
}
