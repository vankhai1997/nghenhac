package firstproject.t3h.com.mp3;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 * Created by LE VAN KHAI on 4/7/2018.
 */

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHodler> {
    private IAudioAdapter inter;
    private SimpleDateFormat formatDuration = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");

    public AudioAdapter(IAudioAdapter inter) {
        this.inter = inter;
    }

    @Override
    public AudioAdapter.AudioViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new AudioViewHodler(inflater.inflate(R.layout.item_audio,parent,false));
    }

    @Override
    public void onBindViewHolder(AudioAdapter.AudioViewHodler holder, final int position) {
        AudioOffline offline = inter.getItem(position);
        holder.tvName.setText(offline.getDisplayName());
        holder.tvArtirs.setText(offline.getArtis());
        holder.tvDuration.setText(formatDuration.format(offline.getDuration()));
        holder.tvDate.setText(formatDate.format(offline.getDateCreated()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inter.onClickItem(position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return inter.getCount();
    }
    static final class AudioViewHodler extends RecyclerView.ViewHolder{
        private TextView tvName;
        private TextView tvArtirs;
        private TextView tvDuration;
        private TextView tvDate;

        public AudioViewHodler(View itemView) {
            super(itemView);
            tvName =itemView.findViewById(R.id.tv_name);
            tvArtirs =itemView.findViewById(R.id.tv_artis);
            tvDuration =itemView.findViewById(R.id.tv_duration);
            tvDate =itemView.findViewById(R.id.tv_date);
        }
    }
    public interface IAudioAdapter{
        int getCount();
        AudioOffline getItem(int position);
        void onClickItem(int position);
    }
}
