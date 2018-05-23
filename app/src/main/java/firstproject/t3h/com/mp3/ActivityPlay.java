package firstproject.t3h.com.mp3;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by LE VAN KHAI on 4/8/2018.
 */

public class ActivityPlay extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private final static int ID = 1;
    private ImageButton btnPlay, btnResumm, btnNext, btnPrevious;
    private TextView tvName, tvArtis, tvTimePlayed, tvDuration;
    private CircleImageView imgAvatar;
    private Animation ani;
    private SeekBar skSong;
    private SimpleDateFormat fm = new SimpleDateFormat("mm:ss");
    private int timePlayed;
    private Handler mHandler;
    private Thread thread;
    private boolean isPause;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isPause = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        inisView();
        timePlayed = skSong.getProgress();
        int po = ServiceMediaOffline.getPositionPlaying();
        setTitleSong(po);
        ani = AnimationUtils.loadAnimation(this, R.anim.animation);
        imgAvatar.startAnimation(ani);
        setDuration();
        inisSeekBar();

        inisThread();
        inisHander();
    }

    private void setDuration() {
        tvDuration.setText(fm.format(ServiceMediaOffline.audioManager.getAudioOfflines().get(ServiceMediaOffline.getPositionPlaying())
                .getDuration()));
    }

    private void setTitleSong(int position) {
        tvName.setText(ServiceMediaOffline.audioManager.getAudioOfflines().get(ServiceMediaOffline.getPositionPlaying()).getDisplayName());
        tvArtis.setText(ServiceMediaOffline.audioManager.getAudioOfflines().get(ServiceMediaOffline.getPositionPlaying()).getArtis());
    }

    private void autoNextSong() {
        if (tvTimePlayed.getText().toString().replace(":", "").equals(tvDuration.getText().
                toString().replace(":", ""))) {
            int position = ServiceMediaOffline.getPositionPlaying();
            if (position >= 0 && position < ServiceMediaOffline.audioManager.getAudioOfflines().size() - 1) {
                position++;
                ServiceMediaOffline.setPositionPlaying(position);
                try {
                    ServiceMediaOffline.manager.setDataSource(ServiceMediaOffline.audioManager.getAudioOfflines().
                            get(position).getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setProgessStart();
                setDuration();
                setTitleSong(position);
                skSong.setMax((int) ServiceMediaOffline.audioManager.getAudioOfflines().get(ServiceMediaOffline.getPositionPlaying()).getDuration());

                ServiceMediaOffline.manager.play();
            } else {
                btnPlay.setVisibility(View.VISIBLE);
                btnResumm.setVisibility(View.INVISIBLE);
                startAnimationPausing();
            }

        }
    }

    private void inisHander() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ID:
                        int time = msg.arg1 * 1000;
                        if (time <= skSong.getMax()) {
                            tvTimePlayed.setText(fm.format(time));
                            skSong.setProgress(msg.arg1 * 1000);
                        }

                        while (true) {
                            if (isPause == true) {
                                autoNextSong();
                                if (ServiceMediaOffline.isClickPause()==true){
                                    clickPause();
                                    ServiceMediaOffline.setClickPause(false);
                                }
                            }


                            break;
                        }
                }
            }
        };
    }

    private void inisThread() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    while (timePlayed * 1000 < skSong.getMax()) {
                        try {
                            Thread.sleep(1000);
                            if (isPause == true) {
                                timePlayed++;
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message mgs = new Message();
                        mgs.what = ID;
                        mgs.arg1 = timePlayed;
                        mHandler.sendMessage(mgs);
                    }
                }

            }
        });
        thread.start();
    }


    private void inisSeekBar() {
        skSong.setMax((int) ServiceMediaOffline.audioManager.getAudioOfflines().get(ServiceMediaOffline.getPositionPlaying())
                .getDuration());
        skSong.setOnSeekBarChangeListener(this);
    }

    private void inisView() {
        btnPlay = findViewById(R.id.btn_play);
        btnResumm = findViewById(R.id.btn_pause);
        btnNext = findViewById(R.id.btn_next);
        btnPrevious = findViewById(R.id.btn_previous);
        btnPlay.setOnClickListener(this);
        btnResumm.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        tvName = findViewById(R.id.tv_name);
        tvArtis = findViewById(R.id.tv_artis);
        imgAvatar = findViewById(R.id.profile_image);
        imgAvatar.setImageResource(R.drawable.ic_launcher_background);
        tvTimePlayed = findViewById(R.id.tv_time_played);
        tvDuration = findViewById(R.id.tv_duration);
        skSong = findViewById(R.id.sk_song);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pause:
               clickPause();
                break;
            case R.id.btn_play:
           clickPlay();
                break;
            case R.id.btn_next:
                clickNext();
                break;

            case R.id.btn_previous:
                clickPrevious();
                break;
        }
    }

    public void clickPlay() {
      int position = ServiceMediaOffline.getPositionPlaying();
        if (tvTimePlayed.getText().toString().replace(":", "").equals(tvDuration.getText().
                toString().replace(":", ""))) {

            if (position == ServiceMediaOffline.audioManager.getAudioOfflines().size() - 1) {
                setProgessStart();
                MainActivity.service.play(position);
            }

        }
        btnPlay.setVisibility(View.INVISIBLE);
        btnResumm.setVisibility(View.VISIBLE);
        startAnimationPlaying();
        ServiceMediaOffline.manager.play();
        MainActivity.service.inisImgResoursePlay();
        isPause = true;
    }

    private void clickNext() {
        MainActivity.service.clickNext();
       int  position = ServiceMediaOffline.getPositionPlaying();
        setProgessStart();
        setTitleSong(position);
        skSong.setMax((int) ServiceMediaOffline.audioManager.getAudioOfflines().
                get(ServiceMediaOffline.getPositionPlaying()).getDuration());
        setDuration();
    }

    private void clickPrevious() {
        MainActivity.service.clickPrevious();
       int  position = ServiceMediaOffline.getPositionPlaying();
        setProgessStart();
        setTitleSong(position);
        setDuration();
        skSong.setMax((int) ServiceMediaOffline.audioManager.getAudioOfflines().get(ServiceMediaOffline.getPositionPlaying()).getDuration());

    }

    public void clickPause() {
        MainActivity.service.clickPause();
        btnPlay.setVisibility(View.VISIBLE);
        btnResumm.setVisibility(View.INVISIBLE);
        startAnimationPausing();
        isPause = false;
    }

    private void setProgessStart() {
        skSong.setProgress(0);
        timePlayed = 0;
    }

    private void startAnimationPausing() {
        ani = AnimationUtils.loadAnimation(this, R.anim.animation_nomal);
        imgAvatar.startAnimation(ani);
    }

    private void startAnimationPlaying() {
        ani = AnimationUtils.loadAnimation(this, R.anim.animation);
        imgAvatar.startAnimation(ani);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        tvTimePlayed.setText(fm.format(skSong.getProgress()));

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        tvTimePlayed.setText(fm.format(skSong.getProgress()));
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        ServiceMediaOffline.manager.seek(skSong.getProgress());
        tvTimePlayed.setText(fm.format(skSong.getProgress()));
        timePlayed = skSong.getProgress() / 1000;
    }
}
