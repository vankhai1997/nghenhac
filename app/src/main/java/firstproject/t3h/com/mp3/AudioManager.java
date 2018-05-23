package firstproject.t3h.com.mp3;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by LE VAN KHAI on 4/7/2018.
 */

public class AudioManager implements AlbumAdapter.IArtistAdapter{
    private Context context;
    private static final String TAG = AudioManager.class.getSimpleName();
    private List<AudioOffline> audioOfflines = new ArrayList<>();

    public AudioManager(Context context) {
        this.context = context;
    }



    public List<AudioOffline> getAudioOfflines() {
        return audioOfflines;
    }

    public void setAudioOfflines(List<AudioOffline> audioOfflines) {
        this.audioOfflines = audioOfflines;
    }

    public void getAllAudio() {
         Cursor c= context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, null);
        if (c == null) {
            return;
        }
        String[] cl = c.getColumnNames();
        for (int i = 0; i < cl.length; i++) {
            Log.d(TAG, "getAllAudio: " + cl.toString());

        }
        c.moveToFirst();
        int indexId = c.getColumnIndex("_id");
        int indexData = c.getColumnIndex("_data");
        int indexDisplayName = c.getColumnIndex("title");
        int indexMineType = c.getColumnIndex("mime_type");
        int indexDateAdd = c.getColumnIndex("date_added");
        int indexAlbumId = c.getColumnIndex("album_id");
        int indexDuration = c.getColumnIndex("duration");
        int indexArtist = c.getColumnIndex("artist");
        int iAlbum = c.getColumnIndex("album");


        while (!c.isAfterLast()) {
            long id = c.getLong(indexId);
            String data = c.getString(indexData);
            String displayName = c.getString(indexDisplayName);
            String mineType = c.getString(indexMineType);
            String artist = c.getString(indexArtist);
            long dateAdd = c.getLong(indexDateAdd); //giay
            long albumId = c.getLong(indexAlbumId);
            long duration = c.getLong(indexDuration);
            String album = c.getString(iAlbum);
            AudioOffline audioOffline = new AudioOffline();
            audioOffline.setId(id);
            audioOffline.setPath(data);
            audioOffline.setAlbum(album);
            audioOffline.setDisplayName(displayName);
            audioOffline.setMineType(mineType);
            audioOffline.setDateCreated(
                    new Date(dateAdd * 1000)
            );
            audioOffline.setAlbumId(albumId);
            audioOffline.setDuration(duration);
            audioOffline.setArtis(artist);
            audioOfflines.add(audioOffline);
            c.moveToNext();
        }
        c.close();

    }


    @Override
    public int getCountAlbum() {
        return 0;
    }

    @Override
    public AudioOffline getItemAlbum(int position) {
        return null;
    }

    @Override
    public List<AudioOffline> getAudio() {
        return audioOfflines;
    }

    @Override
    public void onClickItemAlbum(int position) {

    }
}
