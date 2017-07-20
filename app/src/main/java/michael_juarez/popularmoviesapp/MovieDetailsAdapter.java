package michael_juarez.popularmoviesapp;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by user on 7/16/2017.
 */

public class MovieDetailsAdapter extends RecyclerView.Adapter<MovieDetailsAdapter.MovieDetailsAdapterViewHolder> {

    private ArrayList<String> trailerList;
    final private ListItemClickListener mListItemClickListener;
    private String backDropPath;

    public MovieDetailsAdapter(ArrayList<String> trailers, String backDropPoster, ListItemClickListener listItemClickListener) {
        trailerList = trailers;
        backDropPath = backDropPoster;
        mListItemClickListener = listItemClickListener;
    }

    public interface ListItemClickListener {
        void onListItemClick(int itemClicked, String youtubeLink);
    }

    @Override
    public MovieDetailsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_details_list_item, parent, false);
        return new MovieDetailsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieDetailsAdapterViewHolder holder, int position) {
        Resources resources = holder.itemView.getContext().getResources();
        holder.mTrailer.setText(resources.getString(R.string.play_trailer) + (position+1));
        Picasso.with(holder.itemView.getContext()).load(backDropPath).into(holder.mTrailerBG);
    }

    @Override
    public int getItemCount() {
        if (null == trailerList)
            return 0;

        return trailerList.size();
    }

    public class MovieDetailsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mTrailer;
        private final ImageView mTrailerBG;

        public MovieDetailsAdapterViewHolder(View itemView) {
            super(itemView);
            mTrailer = (TextView) itemView.findViewById(R.id.movie_details_list_item_tv);
            mTrailerBG = (ImageView) itemView.findViewById(R.id.movie_details_list_item_iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            String link = trailerList.get(clickedPosition);

            mListItemClickListener.onListItemClick(clickedPosition, link);
        }
    }

}

