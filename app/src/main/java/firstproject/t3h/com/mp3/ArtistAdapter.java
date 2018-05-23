package firstproject.t3h.com.mp3;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHodler> {
    IGetArtistAdapter iar;

    public ArtistAdapter(IGetArtistAdapter iar) {
        this.iar = iar;
    }

    @Override
    public ArtistAdapter.ArtistViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new  ArtistViewHodler(inflater.inflate(R.layout.item_artist,parent,false));
    }

    @Override
    public void onBindViewHolder(ArtistAdapter.ArtistViewHodler holder, int position) {
        AudioOffline offline = iar.getItemArtist(position);
        holder.tvArtist.setText(offline.getArtis());
//        holder.ImgProfile.setImageResource(R.);

    }

    @Override
    public int getItemCount() {
        return iar.getCountArtist();
    }

    static final class ArtistViewHodler extends RecyclerView.ViewHolder {
        private ImageView ImgProfile;
        private TextView tvArtist;

        public ArtistViewHodler(View itemView) {
            super(itemView);
            ImgProfile= itemView.findViewById(R.id.img_profile);
            tvArtist= itemView.findViewById(R.id.tv_artis);
        }
    }
    public interface IGetArtistAdapter{
        int getCountArtist();
        AudioOffline getItemArtist(int position);
        void onClickItemArtist(int position);}
}
