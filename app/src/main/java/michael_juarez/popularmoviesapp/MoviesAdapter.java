package michael_juarez.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import michael_juarez.popularmoviesapp.data.Movie;
import michael_juarez.popularmoviesapp.data.MoviesHelper;
import michael_juarez.popularmoviesapp.utilities.NetworkUtils;

import static android.R.attr.start;

/**
 * Created by user on 7/10/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {
    final private ScrollMaxListener mScrollMaxListener;
    final private ListItemClickListener mListItemClickListener;

    private ArrayList<Movie> movieList;

    public  interface ScrollMaxListener {
        void reloadList(String pageNumber);
    }

    public interface ListItemClickListener {
        void onListItemClick(int itemClicked);
    }

    public MoviesAdapter(ArrayList<Movie> listMovies, ScrollMaxListener scrollMaxListener, ListItemClickListener listItemClickListener) {
        movieList = listMovies;
        mScrollMaxListener = scrollMaxListener;
        mListItemClickListener = listItemClickListener;
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_list_item, parent, false);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        String poster_path = movieList.get(position).getPoster_path();
        String poster_url = NetworkUtils.getImageURL(poster_path);
        Picasso.with(context).load(poster_url).into(holder.mPoster_path);

        if (position >= movieList.size() - 5) {
            int pageNumber = Integer.parseInt(movieList.get(position).getPageNumber());
            pageNumber++;
            mScrollMaxListener.reloadList(Integer.toString(pageNumber));
        }
    }

    @Override
    public int getItemCount() {
        if (null == movieList) {
            return 0;
        }
        else{
            return movieList.size();
        }
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mPoster_path;

        public MoviesAdapterViewHolder(View itemView) {
            super(itemView);
            mPoster_path = (ImageView) itemView.findViewById(R.id.poster_path);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mListItemClickListener.onListItemClick(clickedPosition);
        }
    }

}


