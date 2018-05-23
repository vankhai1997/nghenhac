package firstproject.t3h.com.mp3;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AudioAdapter.IAudioAdapter, AlbumAdapter.IArtistAdapter, ArtistAdapter.IGetArtistAdapter, View.OnClickListener {
    private RecyclerView rcAudio, rcArtist, rcAlbum;
    private AudioAdapter adapter;
    private AlbumAdapter albumAdapter;
    private ServiceConnection conn;
    public static ServiceMediaOffline service;
    private ImageButton btnNext, btnPrevious, btnPause, btnPlay;
    private TextView tvArtis, tvTitle;
    private View vSong;
    private View vAlbum;
    private View vArtis;
    private ArtistAdapter artistAdapter;
    FrameLayout frl;
    private boolean isCheckPlay;
    private Handler mHandler;
    private final static int ID = 1;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addImgTabhost();

        rcAudio = findViewById(R.id.rc_audio);
        rcArtist = findViewById(R.id.rc_audio_artist);
        rcAlbum = findViewById(R.id.rc_audio_album);
        inisView();
        inisRecycleView();
        inisTaphost();
        startService();
//        inisService();
        inisHander();
        inisThread();


    }

    private void inisView() {
        btnNext = findViewById(R.id.btn_next);
        btnPause = findViewById(R.id.btn_pause);
        btnPlay = findViewById(R.id.btn_play);
        btnPrevious = findViewById(R.id.btn_previous);
        tvArtis = findViewById(R.id.tv_artist);
        tvTitle = findViewById(R.id.tv_title);
        frl = findViewById(R.id.rlt);
        btnPrevious.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        frl.setOnClickListener(this);
    }

    public void startService() {
        Intent serviceIntent = new Intent(MainActivity.this, ServiceMediaOffline.class);
        serviceIntent.setAction(Constant.ACTION.STARTFOREGROUND_ACTION);
        startService(serviceIntent);
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                ServiceMediaOffline.MyBinder binder = (ServiceMediaOffline.MyBinder) iBinder;
                service = binder.getService();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        Intent intent = new Intent();
        intent.setClass(this, ServiceMediaOffline.class);
        bindService(intent, conn, BIND_AUTO_CREATE);

    }

    private void inisService() {
        Intent intentStartService = new Intent();
        intentStartService.setClass(this, ServiceMediaOffline.class);
        startService(intentStartService);

        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                ServiceMediaOffline.MyBinder binder = (ServiceMediaOffline.MyBinder) iBinder;
                service = binder.getService();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        Intent intent = new Intent();
        intent.setClass(this, ServiceMediaOffline.class);
        bindService(intent, conn, BIND_AUTO_CREATE);

    }

    private void inisRecycleView() {
        adapter = new AudioAdapter(this);
        rcAudio.setLayoutManager(new LinearLayoutManager(this));
        rcAudio.setAdapter(adapter);
        artistAdapter = new ArtistAdapter(this);
        rcArtist.setLayoutManager(new LinearLayoutManager(this));
        rcArtist.setAdapter(artistAdapter);
        albumAdapter = new AlbumAdapter(this);
        rcAlbum.setLayoutManager(new LinearLayoutManager(this));
        rcAlbum.setAdapter(albumAdapter);

    }

    private void inisTaphost() {
        TabHost mTaphost = findViewById(R.id.tabhost);
        mTaphost.setup();
        TabHost.TabSpec tapSong = mTaphost.newTabSpec("");
        tapSong.setIndicator(vSong);
        tapSong.setContent(R.id.tapcontent);
        mTaphost.addTab(tapSong);

        TabHost.TabSpec tapAlbum = mTaphost.newTabSpec("");
        tapAlbum.setIndicator(vAlbum);
        tapAlbum.setContent(R.id.tapcontent_album);
        mTaphost.addTab(tapAlbum);

        TabHost.TabSpec tapArtist = mTaphost.newTabSpec("");
        tapArtist.setIndicator(vArtis);
        tapArtist.setContent(R.id.tapcontent_artist);
        mTaphost.addTab(tapArtist);
    }

    private void addImgTabhost() {
        vSong = LayoutInflater.from(MainActivity.this).inflate(R.layout.image_song, null);
        vAlbum = LayoutInflater.from(MainActivity.this).inflate(R.layout.image_albumn, null);
        vArtis = LayoutInflater.from(MainActivity.this).inflate(R.layout.image_artist, null);
    }


    @Override
    public int getCount() {
        if (service == null) {
            return 0;
        }
        if (service.getAudioOfflines() == null) {
            return 0;
        }
        return service.getAudioOfflines().size();
    }

    @Override
    public AudioOffline getItem(int position) {
        return service.getAudioOfflines().get(position);
    }

    @Override
    public void onClickItem(int position) {
        service.play(position);
        Intent intent = new Intent(MainActivity.this, ActivityPlay.class);
        startActivity(intent);
        isCheckPlay = true;

        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                frl.setVisibility(View.VISIBLE);
            }
        }.start();
        inisText(position);
    }

    private void inisHander() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ID:
                        if (ServiceMediaOffline.isClickPause() == true) {
                            service.clickPause();
                            ServiceMediaOffline.setClickPause(false);
                            btnPlay.setVisibility(View.VISIBLE);
                            btnPause.setVisibility(View.INVISIBLE);
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
                    try {
                        Thread.sleep(1000);


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message mgs = new Message();
                    mgs.what = ID;
                    mHandler.sendMessage(mgs);

                }

            }
        });
        thread.start();
    }

    private void inisText(int position) {
        AudioOffline audioOffline = service.getAudioOfflines().get(position);
        tvTitle.setText(audioOffline.getDisplayName());
        tvArtis.setText(audioOffline.getArtis());
    }
//    @Override
//    public void onClickItem(int position) {

//
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        manager.release();
//    }

    @Override
    public int getCountAlbum() {
        if (service == null) {
            return 0;
        }
        if (service.getAudioOfflines() == null) {
            return 0;
        }
        return service.getAudioOfflines().size();
    }

    @Override
    public AudioOffline getItemAlbum(int position) {
        return service.getAudioOfflines().get(position);
    }

    @Override
    public List<AudioOffline> getAudio() {
        return null;
    }

    @Override
    public void onClickItemAlbum(int position) {

    }

    @Override
    public int getCountArtist() {
        if (service == null) {
            return 0;
        }
        if (service.getAudioOfflines() == null) {
            return 0;
        }
        return service.getAudioOfflines().size();
    }

    @Override
    public AudioOffline getItemArtist(int position) {
        return service.getAudioOfflines().get(position);
    }

    @Override
    public void onClickItemArtist(int position) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:

                service.clickNext();
                ServiceMediaOffline.manager.play();
                inisText(ServiceMediaOffline.getPositionPlaying());
                break;
            case R.id.btn_previous:

                service.clickPrevious();
                inisText(ServiceMediaOffline.getPositionPlaying());
                ServiceMediaOffline.manager.play();

                break;
            case R.id.btn_pause:
                service.clickPause();
                btnPlay.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.INVISIBLE);
                break;
            case R.id.btn_play:
                ServiceMediaOffline.manager.play();
                btnPlay.setVisibility(View.INVISIBLE);
                btnPause.setVisibility(View.VISIBLE);
                break;

            case R.id.rlt:
                Intent intent = new Intent(MainActivity.this, ActivityPlay.class);
                startActivity(intent);
                break;
            default:
                break;


        }
    }
}
