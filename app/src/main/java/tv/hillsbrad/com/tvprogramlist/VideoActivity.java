package tv.hillsbrad.com.tvprogramlist;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;

import tv.hillsbrad.com.Utils;

/**
 * Created by alex on 8/30/16.
 */
public class VideoActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Button mBtn_play, mBtn_pause, mBtn_stop;
    private SeekBar mSeekBar;
    private VideoView mVideoView;
    private boolean mIsPlaying;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mBtn_play = (Button) findViewById(R.id.btn_play);
        mBtn_play.setOnClickListener(this);
        mBtn_pause = (Button) findViewById(R.id.btn_pause);
        mBtn_pause.setOnClickListener(this);
        mBtn_stop = (Button) findViewById(R.id.btn_stop);
        mBtn_stop.setOnClickListener(this);

        mSeekBar = (SeekBar) findViewById(R.id.video_seekBar);
        mVideoView = (VideoView) findViewById(R.id.video_view);

        mSeekBar.setOnSeekBarChangeListener(this);


        Uri uri=Uri.parse("rtsp://r5---sn-oguesnzz.googlevideo.com/Cj0LENy73wIaNAltcAyvi-QbchMYDSANFC1DYsVXMOCoAUIASARgvPucksLSy9NVigELQ1Y0WDJ1M3BOdmcM/31423B0903F595416838DBBD3F6487320B9483CE.94A19D800E300DFF47D9CBE1F3EAD9209DC51336/yt6/1/video.3gp");
        mVideoView.setVideoURI(uri);
        MediaController mediaController = new MediaController(VideoActivity.this);
        mediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mediaController);
        mVideoView.requestFocus();
        mVideoView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVideoView.isPlaying()) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                playVideo();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void playVideo() {
        Log.d(Utils.TAG, "playVideo...");
        // http://www.androidbegin.com/tutorial/AndroidCommercial.3gp
        // rtsp://v2.cache2.c.youtube.com/CjgLENy73wIaLwm3JbT_%ED%AF%80%ED%B0%819HqWohMYESARFEIJbXYtZ29vZ2xlSARSB3Jlc3VsdHNg_vSmsbeSyd5JDA==/0/0/0/video.3gp
        Uri uri=Uri.parse("http://www.androidbegin.com/tutorial/AndroidCommercial.3gp");
        mVideoView.setVideoURI(uri);
        mVideoView.setMediaController(new MediaController(VideoActivity.this));
        mVideoView.requestFocus();
        mVideoView.start();


    }
}
