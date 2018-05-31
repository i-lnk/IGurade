package com.rl.geye.ui.aty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.rl.commons.utils.AppDevice;
import com.rl.commons.utils.ClickUtil;
import com.rl.commons.utils.FileUtil;
import com.rl.geye.R;
import com.rl.geye.base.BaseMyAty;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.EdwinDevice;
import com.rl.geye.image.ImageUtil;
import com.rl.geye.util.PhotoVideoUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;

/**
 * Created by Nicky on 2016/9/20.
 * 设备二维码
 */
public class DevQRCodeAty extends BaseMyAty {

    private static final int IMAGE_HALFWIDTH = 40;// 宽度值，影响中间图片大小
    private static final int REFRESH_QRCODE = 11;
    private static final int SHOW_QRCODE_BITMAP = 12;
    protected EdwinDevice mDevice;
    @BindView(R.id.img_qr_code)
    ImageView ivQrCode; //二维码
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ly_all)
    View viewAll;
    private Bitmap qrBitmap;
    private Thread refreshThread = null;//刷新线程
    private volatile boolean refreshRunFlag = true;
    private Handler mHandler;
    private Thread createBitmapThread = null;//构建二维码图片线程

    /**
     * 生成二维码Bitmap
     *
     * @param context 文本内容
     * @param logoBm  二维码中心的Logo图标（可以为null）
     * @return 合成后的bitmap
     */
    public static Bitmap createQRImage(Context context, String data, Bitmap logoBm) {
        try {

            if (data == null || "".equals(data)) {
                return null;
            }

            int widthPix = (int) (AppDevice.getScreenWidth() * 11 / 20);
            int heightPix = widthPix;

            // 配置参数
            Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            // 设置空白边距的宽度
            hints.put(EncodeHintType.MARGIN, 3); // default is 4

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

    @Override
    protected int getLayoutId() {
        return R.layout.aty_dev_qr_code;
    }

    @Override
    public View getVaryTargetView() {
        return viewAll;
    }

    @Override
    protected boolean initPrepareData() {

        if (fromIntent != null) {
            mDevice = fromIntent.getParcelableExtra(Constants.BundleKey.KEY_DEV_INFO);
        }
        return mDevice != null;
    }

    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
        tvTitle.setText(R.string.qr_title);
    }

    @Override
    protected void initViewsAndEvents() {
        showLoading();
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case REFRESH_QRCODE:
//                        if( qrBitmap!=null )
//                        {
//                            ivQrCode.setImageBitmap(null);
//                            qrBitmap.recycle();
//                        }
                        startCreateBitmap((String) (msg.obj));
//                        qrBitmap = createQRImage( DevQRCodeAty.this, (String) (msg.obj), null);
//                        ImageUtil.saveBitmapFile(qrBitmap, "drcode_id.jpg");
//                        ivQrCode.setImageBitmap(qrBitmap);
                        break;
                    case SHOW_QRCODE_BITMAP:
                        hideMsgView();
                        ivQrCode.setImageBitmap(qrBitmap);
                        break;
                }
            }
        };

        refreshQrcode();
        startRefreshTimer();
    }

    @Override
    protected void onClickView(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_share, menu);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View v = findViewById(R.id.item_share);
                if (v != null) {
                    v.setOnLongClickListener(mMenuItemLongClickListener);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.item_share:
                if (ClickUtil.isFastClick(getActivity(), toolbar))
                    return super.onOptionsItemSelected(item);
                File picDir = PhotoVideoUtil.getPhotoDir(false);
                if (!picDir.exists()) {
                    picDir.mkdirs();
                }
                File photo = new File(picDir, "drcode_id.jpg");
                shareImg(getString(R.string.qr_share_to), getString(R.string.qr_share), getString(R.string.qr_title),
                        FileUtil.getImageContentUri(photo, getApplicationContext()));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRefreshTimer();
        stopCreateBitmap();
        if (qrBitmap != null)
            qrBitmap.recycle();
    }

    @SuppressLint("DefaultLocale")
    private void refreshQrcode() {
        String data;
        char[] random = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B',
                'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
                'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        int[] count = new int[]{2, 3, 5, 7, 9};
        StringBuffer randomUser = new StringBuffer();
        StringBuffer randomPass = new StringBuffer();
        for (int i = 0; i < count[new Random().nextInt(count.length)]; i++) {
            randomUser.append(random[new Random().nextInt(random.length)]);
            randomPass.append(random[new Random().nextInt(random.length)]);
        }
        data = String.format("p2p://%1$s:%2$s:%3$d@%4$s.gk.com",
                randomUser.toString(),
                randomPass.toString(),
                mDevice.getType(),
                mDevice.getDevId());
        Message message = mHandler.obtainMessage();
        message.obj = data;
        message.what = REFRESH_QRCODE;
        mHandler.sendMessage(message);
    }

    /**
     * 停止创建bitmapm
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
                        qrBitmap = createQRImage(DevQRCodeAty.this, data, null);
                        ImageUtil.saveBitmapFile(qrBitmap, "drcode_id.jpg");
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
     * 停止发送更新局域网状态
     */
    private void stopRefreshTimer() {
        if (refreshThread == null) {
            return;
        }
        if (refreshThread.isAlive()) {
            try {
                refreshRunFlag = false;
                refreshThread.join(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        refreshThread = null;
    }

    /**
     * 2分钟刷新一次
     */
    private void startRefreshTimer() {
        if (refreshThread == null) {
            refreshRunFlag = true;
            refreshThread = new Thread(new Runnable() {

                @Override
                public void run() {

                    while (refreshRunFlag) {
                        try {
                            /** 多次sleep 以便快速中断   */
                            for (int i = 0; i < 2 * 60 * 10; i++) {
                                Thread.sleep(100);
                                if (!refreshRunFlag) {
                                    break;
                                }
                            }
                            if (!refreshRunFlag) {
                                break;
                            }
                            refreshQrcode();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            refreshThread.start();
        }
    }

    /**
     * 生成二维码
     *
     * @param string  二维码中包含的文本信息
     * @param mBitmap logo图片
     * @param format  编码格式
     * @return Bitmap 位图
     * @throws WriterException
     */
    public Bitmap createCode(String string, Bitmap mBitmap, BarcodeFormat format)
            throws WriterException {
        Matrix m = new Matrix();
        float sx = (float) 2 * IMAGE_HALFWIDTH / mBitmap.getWidth();
        float sy = (float) 2 * IMAGE_HALFWIDTH / mBitmap.getHeight();
        m.setScale(sx, sy);// 设置缩放信息
        // 将logo图片按martix设置的信息缩放
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
                mBitmap.getHeight(), m, false);
        MultiFormatWriter writer = new MultiFormatWriter();
        Hashtable<EncodeHintType, String> hst = new Hashtable<EncodeHintType, String>();
        hst.put(EncodeHintType.CHARACTER_SET, "UTF-8");// 设置字符编码

        int bitmapSize = (int) (AppDevice.getScreenWidth() * 3 / 4);
        BitMatrix matrix = writer.encode(string, format, bitmapSize,
                bitmapSize, hst);// 生成二维码矩阵信息
        int width = matrix.getWidth();// 矩阵高度
        int height = matrix.getHeight();// 矩阵宽度
        int halfW = width / 2;
        int halfH = height / 2;
        int[] pixels = new int[width * height];// 定义数组长度为矩阵高度*矩阵宽度，用于记录矩阵中像素信息
        for (int y = 0; y < height; y++) {// 从行开始迭代矩阵
            for (int x = 0; x < width; x++) {// 迭代列
                if (x > halfW - IMAGE_HALFWIDTH && x < halfW + IMAGE_HALFWIDTH
                        && y > halfH - IMAGE_HALFWIDTH
                        && y < halfH + IMAGE_HALFWIDTH) {// 该位置用于存放图片信息
                    // 记录图片每个像素信息
                    pixels[y * width + x] = mBitmap.getPixel(x - halfW
                            + IMAGE_HALFWIDTH, y - halfH + IMAGE_HALFWIDTH);
                } else {
                    if (matrix.get(x, y)) {// 如果有黑块点，记录信息
                        pixels[y * width + x] = 0xff000000;// 记录黑块信息
                    }
                }

            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 分享图片和文字内容
     *
     * @param dlgTitle 分享对话框标题
     * @param subject  主题
     * @param content  分享内容（文字）
     * @param uri      图片资源URI
     */
    private void shareImg(String dlgTitle, String subject, String content,
                          Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("image/jpg");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        if (subject != null && !"".equals(subject)) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        if (content != null && !"".equals(content)) {
            intent.putExtra(Intent.EXTRA_TEXT, content);
        }
        // 设置弹出框标题
        if (dlgTitle != null && !"".equals(dlgTitle)) { // 自定义标题
            startActivity(Intent.createChooser(intent, dlgTitle));
        } else {
            // 系统默认标题
            startActivity(intent);
        }
//        Intent intent = new Intent();
//        ComponentName comp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.tools.ShareToTimeLineUI");
//        intent.setComponent(comp);
//        intent.setAction(Intent.ACTION_SEND);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setType("image/jpg");
//        intent.putExtra(Intent.EXTRA_STREAM, uri);//uri为你要分享的图片的uri
//        startActivity(intent);
    }
}
