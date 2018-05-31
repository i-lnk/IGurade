package com.rl.geye.ui.aty;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.rl.commons.ThreadPoolMgr;
import com.rl.geye.R;
import com.rl.geye.adapter.ChooseAdapter;
import com.rl.geye.base.BaseMyAty;
import com.rl.geye.bean.RingBean;
import com.rl.geye.constants.Constants;
import com.rl.geye.logic.DataLogic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * 设置铃声
 *
 * @author NickyHuang
 */
@SuppressLint("NewApi")
public class RingSetAty extends BaseMyAty implements AdapterView.OnItemClickListener {

    private static final int MAX_LOCAL_RINGS = 500;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    //	@BindView(R.id.rv_ring)
//    RecyclerView rvRing;
    @BindView(R.id.ly_all)
    View lyAll;
    @BindView(R.id.lv_ring)
    ListView mListView;
    @BindView(R.id.btn_ok)
    Button btnOk;
    private List<RingBean> mDataList = new ArrayList<>();
    private List<RingBean> mLocalRings = new ArrayList<>();
    private ChooseAdapter<RingBean> mAdapter;
    private int curType = -1;
    private RingBean choosedRing;
    private MediaPlayer mPlayer;

    @Override
    protected int getLayoutId() {
        return R.layout.aty_ring_list;
    }

    @Override
    protected boolean initPrepareData() {
        if (fromIntent != null) {
            curType = fromIntent.getIntExtra(Constants.BundleKey.KEY_RING_TYPE, -1);
        }
        return curType == RingBean.RingType.CALL || curType == RingBean.RingType.ALARM;
    }

    @Override
    public View getVaryTargetView() {
        return lyAll;
    }

    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
    }

    @Override
    protected void initViewsAndEvents() {

//		btnOk.getBackground().setLevel(2);
        mAdapter = new ChooseAdapter<>(this, mDataList);
        btnOk.setOnClickListener(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        if (curType == RingBean.RingType.CALL) {
            choosedRing = DataLogic.getRingBell();
            tvTitle.setText(R.string.ring_call);
        } else {
            choosedRing = DataLogic.getRingAlarm();
            tvTitle.setText(R.string.ring_alarm);
        }

        getLocalRings();
    }

    @Override
    protected void onClickView(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                DataLogic.saveRing(choosedRing);
                setResult(RESULT_OK, getIntent());
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlayer();
    }

    private void stopPlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        RingBean item = mDataList.get(position);
        if (item != null) {
            stopPlayer();
            choosedRing = item;
            mAdapter.chooseItem(choosedRing);

            if (item.getRemindType() == RingBean.RingRemindType.MUTE) {
                //DO nothing
            } else if (item.getRemindType() == RingBean.RingRemindType.DEFAULT) {
                //
                mPlayer = MediaPlayer.create(this, item.getRingDefaultResId());
                mPlayer.start();
                mPlayer.setLooping(true);
            } else {
                File file = new File(item.getRingUrl());
                if (file.exists()) {
                    try {
                        Uri uri = Uri.parse(item.getRingUrl());
                        mPlayer = new MediaPlayer();
                        mPlayer.setDataSource(this, uri);
                        final AudioManager audioManager = (AudioManager) this
                                .getSystemService(Context.AUDIO_SERVICE);
                        if (audioManager
                                .getStreamVolume(AudioManager.STREAM_RING) != 0) {
                            mPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                        }
                        mPlayer.setLooping(true);
//						mPlayer.setOnPreparedListener(new OnPreparedListener() {
//
//							@Override
//							public void onPrepared(MediaPlayer mp) {
//								mPlayer.start();
//							}
//						});
//						mPlayer.prepareAsync();
                        mPlayer.prepare();
                        mPlayer.start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }


    public void refreshList() {
        mDataList.clear();
        RingBean defaultRing = new RingBean();
        defaultRing.setRingType(curType);
        defaultRing.setRemindType(RingBean.RingRemindType.DEFAULT);
//		defaultRing.setName(getString(R.string.default_ring , defaultRing.getRingDefaultName()));
        defaultRing.setName(getString(R.string.default_ring_2));
        mDataList.add(defaultRing);

        RingBean silentRing = new RingBean();
        silentRing.setName(getString(R.string.silent_ring));
        silentRing.setRingType(curType);
        silentRing.setRemindType(RingBean.RingRemindType.MUTE);
        mDataList.add(silentRing);
        for (RingBean localRing : mLocalRings) {
            localRing.setRingType(curType);
            mDataList.add(localRing);
        }
        mAdapter.chooseItem(choosedRing);
        mAdapter.notifyDataSetChanged();

    }


    private void getLocalRings() {

        new AsyncTask<Void, Void, Void>() {

            protected void onPreExecute() {
//				showLoadDialog(null,null);
                mListView.setEnabled(false);
                btnOk.setEnabled(false);
            }

            @Override
            protected Void doInBackground(Void... params) {
                mLocalRings.clear();
                getLocalRings(true);
//                getLocalRings(false);
                return null;
            }

            protected void onPostExecute(Void result) {
                mListView.setEnabled(true);
                btnOk.setEnabled(true);
                refreshList();
            }

        }.executeOnExecutor(ThreadPoolMgr.getCustomThreadPool());

    }

    /**
     * 获取本机音乐
     *
     * @param isSys 是否为系统自带音乐
     */
    private void getLocalRings(boolean isSys) {

        // query external audio
        final String track_id = MediaStore.Audio.Media._ID;
        final String track_title = MediaStore.Audio.Media.TITLE;
        final String track_path = MediaStore.Audio.Media.DATA;
        Uri ringUri = null;
        if (!isSys)
            ringUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        else
            ringUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;


        String[] columns = {track_id, track_title, track_path};
        ContentResolver musicResolver = getContentResolver();
        Cursor musicCursor = musicResolver.query(ringUri, columns, null, null,
                null);
        // iterate over results if valid
        if (musicCursor != null && musicCursor.moveToFirst()) {
            // get columns
            int titleColumn = musicCursor.getColumnIndex(track_title);
            int idColumn = musicCursor.getColumnIndex(track_id);
            int dataColumn = musicCursor.getColumnIndex(track_path);
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String data = musicCursor.getString(dataColumn);
                if (!data.equals("")) {
                    RingBean ring = new RingBean();
                    ring.setId(thisId);
                    ring.setName(thisTitle);
                    ring.setRingUrl(data);
                    ring.setRemindType(RingBean.RingRemindType.CUSTOM);

//					XLog.i(TAG," ring : "+ ring);

                    if (!mLocalRings.contains(ring)) {
                        mLocalRings.add(ring);
                        if (mLocalRings.size() >= MAX_LOCAL_RINGS)
                            break;
                    }
                }
            } while (musicCursor.moveToNext());
        }
        if (musicCursor != null)
            musicCursor.close();
    }


}
