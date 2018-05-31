package com.rl.geye.ui.frag;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.rl.commons.utils.AppDevice;
import com.rl.commons.utils.ClickUtil;
import com.rl.geye.R;
import com.rl.geye.base.BaseDevAddFrag;
import com.rl.geye.bean.EdwinWifiInfo;
import com.rl.geye.constants.Constants;
import com.rl.geye.image.ImageUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;


/**
 * Created by Nicky on 2016/9/18.
 * 添加设备步骤 二维码
 */
public class AddStepQRFrag extends BaseDevAddFrag {


    //    private static final int REFRESH_QRCODE = 11;
    private static final int SHOW_QRCODE_BITMAP = 12;
    @BindView(R.id.img_qr_code)
    ImageView ivQrCode;
    @BindView(R.id.btn_next)
    Button btnNext;
    private EdwinWifiInfo mWifiInfo;
    private Bitmap qrBitmap;
    private Thread createBitmapThread = null;//构建二维码图片线程
    private Handler mHandler;
    private OnEvents mListener;

    /**
     * 生成二维码Bitmap
     *
     * @param context 文本内容
     * @param logoBm  二维码中心的Logo图标（可以为null）
     * @return 合成后的bitmap
     */
    public static Bitmap createQRImage(Context context, String data,
                                       Bitmap logoBm) {

        try {

            if (data == null || "".equals(data)) {
                return null;
            }

            int widthPix = (int) (AppDevice.getScreenWidth() * 0.5);
            int heightPix = widthPix;

            // 配置参数
            Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            // 设置空白边距的宽度
            hints.put(EncodeHintType.MARGIN, 1); // default is 4

            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(data,
                    BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = 0xff000000;
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;
                    }
                }
            }

            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);

            if (logoBm != null) {
                bitmap = addLogo(bitmap, logoBm);
            }

            return bitmap;
            // 必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
            // return bitmap != null &&
            // bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new
            // FileOutputStream(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 在二维码中间添加Logo图案
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }

        if (logo == null) {
            return src;
        }

        // 获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        // logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight,
                Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);

            canvas.drawBitmap(getRoundBitmap(logo, 10),
                    (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2,
                    null);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();

        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

    /**
     * 获取圆角矩形图片方法
     *
     * @param bitmap
     * @param roundPx ,一般设置成14
     * @return Bitmap
     * @author caizhiming
     */
    private static Bitmap getRoundBitmap(Bitmap bitmap, int roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        @SuppressWarnings("deprecation")
        Drawable imageDrawable = new BitmapDrawable(bitmap);
        Canvas canvas = new Canvas(output);

        // 新建一个矩形
        RectF outerRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());

        // 产生一个红色的圆角矩形
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        canvas.drawRoundRect(outerRect, 10, 10, paint);
        // 将源图片绘制到这个圆角矩形上
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        imageDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG);
        imageDrawable.draw(canvas);
        canvas.restore();

        return output;
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }

    /*
     * Deprecated on API 23
     * Use onAttachToContext instead
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    /*
     * Called when the fragment attaches to the context
     */
    protected void onAttachToContext(Context context) {
        //do something
        if (context instanceof OnEvents) {
            mListener = (OnEvents) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnStepQREvents");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_add_step_qr;
    }

    @Override
    public View getVaryTargetView() {
        return ivQrCode;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initViewsAndEvents() {
        showLoading();
        mWifiInfo = getArguments().getParcelable(AddStepSearchFrag.ARG_WIFI_INFO);
//        String qrData =  String.format( "p2p://%1$s:%2$s@%3$s.gk.com" ,mWifiInfo.getWifiName(),mWifiInfo.getWifiPwd(),"0123456789");
        String qrData = String.format("%1$s:%2$s:6:", mWifiInfo.getWifiName(), mWifiInfo.getWifiPwd());

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {

                    case SHOW_QRCODE_BITMAP:
                        hideMsgView();
                        if (ivQrCode != null)
                            ivQrCode.setImageBitmap(qrBitmap);
                        break;
                }
            }
        };

        startCreateBitmap(qrData);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastClick(getActivity(), view))
                    return;
                mListener.gotoNextForQR();
            }
        });
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onDestroy() {
        postEdwinEvent(Constants.EdwinEventType.EVENT_DEV_ADD_COMPLETE);
        if (qrBitmap != null)
            qrBitmap.recycle();
        super.onDestroy();
    }

    /**
     * 创建bitmap
     */
    private void startCreateBitmap(final String data) {
        stopCreateBitmap();
        if (createBitmapThread == null) {
            createBitmapThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        qrBitmap = createQRImage(getActivity(), data, null);
                        ImageUtil.saveBitmapFile(qrBitmap, "qrcode_wifi.jpg");
                        mHandler.sendEmptyMessage(SHOW_QRCODE_BITMAP);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            createBitmapThread.start();
        }
    }

    /**
     * 停止创建bitmap
     */
    private void stopCreateBitmap() {
        if (createBitmapThread == null) {
            return;
        }
        if (createBitmapThread.isAlive()) {
            try {
                createBitmapThread.interrupt();
//                createBitmapThread.join(150);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        createBitmapThread = null;
    }

    public interface OnEvents {
        void gotoNextForQR();
    }

}
