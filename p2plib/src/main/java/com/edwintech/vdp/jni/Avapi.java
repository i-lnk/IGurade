package com.edwintech.vdp.jni;

import android.content.Context;



@SuppressWarnings("JniMissingFunction")
public class Avapi {

	private static final String LOG_TAG = "Avapi";

	/** Called when the activity is first created. */
	static {
		try {
			System.loadLibrary("IOTCAPIs");
			System.loadLibrary("AVAPIs");
			System.loadLibrary("vdp");
		} catch (UnsatisfiedLinkError ule) {
			System.err.println("WARNING: Could not load vdp library!"+ule);
		}
	}

	public native static void PPPPInitialize(String svr);

	public native static void PPPPManagementInit();

	public native static void PPPPManagementFree();

	/**  设置设备端与手机通信Context: (BridgeService)  */
	public native static int PPPPSetCallbackContext(Context object);

	/**  开始搜索设备  */
	public native static void StartSearch( String ssid, String pwd );

	/**  停止搜索设备  */
	public native static void CloseSearch();

	/**  发送操作指令(设置、获取等)  */
	public native static int SendCtrlCommand( String did, int msgType, String msg, int msgLens );

//	/**  发送操作指令(16进制--暂时不需要)  */
//	public native static int SendHexsCommand( String did, int msgType, byte[] msg, int msgLens );

	/**  发送操作指令(16进制--暂时不需要)  */
	public native static int SendBytes( String did, int cmdType, byte[] data, int dataLen );


	/**  YUV420 转 RGB565  */
	public native static int YUV4202RGB565(byte[] yuv, byte[] rgb, int width, int height);

	/**
	 * 开启P2P连接
	 * @param did 设备id
	 * @param user 用户名
	 * @param pwd 密码D
	 * @param server
	 * @param type 连接类型
	 * @return
	 */
	public native static int StartPPPP(String did, String user, String pwd, String server,String type);

	/**   停止P2P连接   */
	public native static int ClosePPPP(String did);

	/**
	 * 开启视频连接
	 * @param did 				设备ID
	 * @param url				远程回放设备录像的路径(实时连接时传空即可)
	 * @param audio_rate		采样率 8000 或 16000
	 * @param audio_channel		传1即可
	 * @param audioRecvCodec	手机端接收的音频类型@see com.edwintech.vdp.constants.Constants.AudioCodec
	 * @param audioSendCodec    手机送往设备端的音频类型
	 * @param videoRecvCodec    手机端接收的视频类型(暂时不需要,传0即可)
	 * @return
	 */
	public native static int StartPPPPLivestream( String did, String url,
												  int audio_rate,
												  int audio_channel,
												  int audioRecvCodec,int audioSendCodec ,int videoRecvCodec);

	/**   停止视频连接   */
	public native static int ClosePPPPLivestream( String did );


	/**   开始录制视频   */
	public native static int StartRecorder(String did, String filepath);

	/**   停止录制视频   */
	public native static int CloseRecorder(String did);


	/**
	 *
	 * 	设置麦克风 和 扩音器
	 * @param did 设备ID
	 * @param AudioStatus 	bit:0 audio(speaker) enable
	 *                      bit:1 speak(micro) enable
	 *
	 */
	public native static int SetAudioStatus(String did,int AudioStatus);

	/**
	 *
	 * @param did
	 * @param videoStatus 0:close 1:open
	 * @return 0:OK
	 */
	public native static int SetVideoStatus(String did,int videoStatus);

	public native static int GetAudioStatus(String did);

	public native static int Wake(String did);


//	public native static int SetPush(String did, int setType,
//									 String deviceToken, int evironment, int pushTYpe, int validity,
//									 String appkey, String masterkey, String alias, int type,
//									 String apikey, String ysecretkey, String channelid, int accessid,
//									 String xsecretkey);
//
//	public native static int DelPush(String did, int setType,
//									 String deviceToken, int evironment, int pushTYpe, int validity,
//									 String appkey, String masterkey, String alias, int type,
//									 String apikey, String ysecretkey, String channelid, int accessid,
//									 String xsecretkey);




}