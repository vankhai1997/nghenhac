package firstproject.t3h.com.mp3;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ArtistViewHodler> {
    private IArtistAdapter ialb;
    private AudioOffline offline;

    public AlbumAdapter(IArtistAdapter ialb) {
        this.ialb = ialb;
    }

    @Override
    public AlbumAdapter.ArtistViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ArtistViewHodler(inflater.inflate(R.layout.item_album, parent, false));
    }

    @Override
    public void onBindViewHolder(AlbumAdapter.ArtistViewHodler holder, final int position) {
        offline = ialb.getItemAlbum(position);
        String al = offline.getAlbum();
//        Log.d("a", "onBindViewHolder: "+ ialb.getItemAlbum(position).getAlbum());
//        holder.tvNumberSong.setText(offline.getAlbum());
        holder.tv_album.setText(offline.getAlbum());
        holder.tvNumberSong.setText(getNumberSongs(al));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ialb.onClickItemAlbum(position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return ialb.getCountAlbum();
    }

    static final class ArtistViewHodler extends RecyclerView.ViewHolder {
        private TextView tv_album;
        private TextView tvNumberSong;

        public ArtistViewHodler(View itemView) {
            super(itemView);
            tv_album = itemView.findViewById(R.id.tv_album);
            tvNumberSong = itemView.findViewById(R.id.tv_number_song);
        }
    }

    public int getNumberSongs(String album) {
        int dem = 1;
        for (int i = 0; i < ialb.getAudio().size(); i++) {
            String idAl = ialb.getAudio().get(i).getAlbum();
//            Log.d(TAG, "getNumberSongs: "+idAl);
//            Log.d(TAG, "------------");
//            for (int k = ialb.getAudio().size() - 1; k > i; k--) {
////                Log.d(TAG, "------------");
//                String id = ialb.getAudio().get(k).getAlbum();
//                Log.d(TAG, "getNumberSongs: "+id);
                if (album.equals(idAl)) {
                    dem++;
//                    Log.d(TAG, "------------");
                }
            }
//        }
//        Log.d(TAG, "getNumberSongs: " + dem);
        return dem;
    }

    public interface IArtistAdapter {
        int getCountAlbum();

        AudioOffline getItemAlbum(int position);

        List<AudioOffline> getAudio();

        void onClickItemAlbum(int position);
    }
}
