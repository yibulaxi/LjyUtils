package com.ljy.ljyutils.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ljy.ljyutils.R;
import com.ljy.util.LjyToastUtil;
import com.ljy.view.LjyGifImageView;

public class GifActivity extends AppCompatActivity {
    LjyGifImageView gifImageView;
    Button pauseBtn;
    boolean hasPaused = true;
    TextView percentTv;
    SeekBar mSeekBar;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);
        pauseBtn = findViewById(R.id.pause);
        pauseBtn.setVisibility(View.GONE);
        mSeekBar = findViewById(R.id.seek);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float per = progress;
                gifImageView.setPercent(per / 100f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        percentTv = findViewById(R.id.percent);
        gifImageView = findViewById(R.id.gif);
        gifImageView.setGifResource(R.drawable.dog, new LjyGifImageView.OnPlayListener() {
            @Override
            public void onPlayStart() {
                LjyToastUtil.toast(mContext, "开始");
            }

            @Override
            public void onPlaying(@FloatRange(from = 0f, to = 1.0f) float percent) {
                int per = Math.round(percent * 100);
                mSeekBar.setProgress(per);
                percentTv.setText("播放进度: " + per + "%");
            }

            @Override
            public void onPlayPause(boolean pauseSuccess) {
                if (pauseSuccess)
                    LjyToastUtil.toast(mContext, "暂停成功");
                else {
                    LjyToastUtil.toast(mContext, "暂停失败");
                }
            }

            @Override
            public void onPlayRestart() {
                LjyToastUtil.toast(mContext, "继续");
            }

            @Override
            public void onPlayEnd() {
                LjyToastUtil.toast(mContext, "结束");
                pauseBtn.setVisibility(View.GONE);
            }
        });

    }

    public void onGifClick(View view) {
        switch (view.getId()) {
            case R.id.playCycle://循环播放
                playCycle();
                break;
            case R.id.playOne://单次播放
                playOne();
                break;
            case R.id.pause://暂停
                pause();
                break;
            case R.id.flashback://倒叙播放
                flashback();
                break;
            case R.id.loadNew://加载新的gif
                gifImageView.setGifResource(R.drawable.aaa);
                playCycle();
                break;

        }
    }

    private void playOne() {
        hasPaused = false;
        pauseBtn.setText("暂停");
        gifImageView.play(1);
        pauseBtn.setVisibility(View.VISIBLE);
    }

    private void playCycle() {
        hasPaused = false;
        pauseBtn.setText("暂停");
        gifImageView.play(-1);
        pauseBtn.setVisibility(View.VISIBLE);
    }

    private void pause() {
        if (hasPaused) {
            pauseBtn.setText("继续");
            gifImageView.play();
        } else {
            pauseBtn.setText("暂停");
            gifImageView.pause();
        }
        hasPaused = !hasPaused;
    }

    private void flashback() {
        hasPaused = false;
        pauseBtn.setText("暂停");
        gifImageView.playReverse();
        pauseBtn.setVisibility(View.VISIBLE);
    }


}