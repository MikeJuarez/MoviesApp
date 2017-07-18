package michael_juarez.popularmoviesapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Network;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

import michael_juarez.popularmoviesapp.data.Movie;
import michael_juarez.popularmoviesapp.data.MoviesHelper;
import michael_juarez.popularmoviesapp.utilities.NetworkUtils;
import michael_juarez.popularmoviesapp.utilities.OpenMoviesJsonUtils;

/**
 *  Created by Michael Juarez on 7/10/2017.
 */

public class MoviesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>, MoviesAdapter.ScrollMaxListener, MoviesAdapter.ListItemClickListener{
    private final static String MOVIES_ACTIVITY_LOADER_SORTBY = "com.michael_juarez_popularmoviesapp.loader.sortby";
    private final static String MOVIES_ACTIVITY_LOADER_PAGE = "com.michael_juarez_popularmoviesapp.loader.page";
    private final static int MOVIES_ACTIVITY_LOADER = 46;

    private final String startingPageNumber = "1";

    private RecyclerView mRecyclerView;
    private MoviesAdapter mAdapter;
    private GridLayoutManager layoutManager;
    private ProgressBar mProgressBar;
    private TextView mTextViewErrorMessage;
    private MoviesHelper moviesHelper;
    private String filterType;

    private final static String KEY_TYPE = "com.popularmoviesapp.michael_juarez/key_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movies);
        initialization(savedInstanceState);
        loadMovieData(filterType, startingPageNumber);
    }

    //Set up references and RecyclerView
    private void initialization(Bundle savedInstanceState) {
        filterType = NetworkUtils.TYPE_POPULAR;
        moviesHelper = MoviesHelper.get(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mTextViewErrorMessage = (TextView) findViewById(R.id.error_message_display);

        int orientation = this.getResources().getConfiguration().orientation;
        layoutManager = null;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(this, 2);
        } else {
            layoutManager = new GridLayoutManager(this, 3);
        }

        mRecyclerView.setLayoutManager(layoutManager);
    }

    private void loadMovieData(String sortBy, String pageNumber) {
        showMovieDataView();

        Bundle loadMovieBundle = new Bundle();
        loadMovieBundle.putString(MOVIES_ACTIVITY_LOADER_SORTBY, sortBy);
        loadMovieBundle.putString(MOVIES_ACTIVITY_LOADER_PAGE, pageNumber);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<Movie>> movieLoader = loaderManager.getLoader(MOVIES_ACTIVITY_LOADER);

        if (movieLoader == null)
            loaderManager.initLoader(MOVIES_ACTIVITY_LOADER, loadMovieBundle, this);
        else
            loaderManager.restartLoader(MOVIES_ACTIVITY_LOADER, loadMovieBundle, this);
    }

    //Hide error message, display RecyclerView
    private void showMovieDataView() {
        mTextViewErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mTextViewErrorMessage.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<Movie>>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null)
                    return;
                mProgressBar.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public ArrayList<Movie> loadInBackground() {
                String filterType = args.getString(MOVIES_ACTIVITY_LOADER_SORTBY);
                String pageNumber = args.getString(MOVIES_ACTIVITY_LOADER_PAGE);

                if (filterType == null || filterType.isEmpty())
                    return null;

                ArrayList<Movie> movieList;
                URL moviesRequestURL = NetworkUtils.getMoviesURL(filterType, pageNumber);

                try {
                    String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(moviesRequestURL);
                    movieList = OpenMoviesJsonUtils
                            .getSimpleMovieStringsFromJson(jsonMoviesResponse);
                    return movieList;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }


    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movieList) {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (movieList != null) {
            showMovieDataView();

            if (mAdapter == null) {
                moviesHelper.addMoreMovies(movieList);
                mAdapter = new MoviesAdapter((ArrayList<Movie>)moviesHelper.getMovieList(), this, this);
                mRecyclerView.setAdapter(mAdapter);
            }
            else {
                moviesHelper.addMoreMovies(movieList);
                mAdapter.notifyDataSetChanged();
            }

        } else {showErrorMessage();};
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }


    @Override
    public void reloadList(String pageNumber) {
        loadMovieData(filterType, pageNumber);
    }

    @Override
    public void onListItemClick(int itemClicked) {
        Movie movie = moviesHelper.getMovieList().get(itemClicked);

        if (mAdapter == null) {
            return;
        }

        String movieBackDropPath = movie.getBackDrop_path();
        String moviePosterPath = movie.getPoster_path();
        String title = movie.getTitle();
        String overview = movie.getOverview();
        String voteAverage = movie.getVoteAverage();
        String releaseDate = movie.getRelease_date();
        String id = movie.getId();

        Intent intent = MovieDetails.newIntent(getBaseContext(), title, moviePosterPath, overview, voteAverage, releaseDate, movieBackDropPath, id);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_sortby_popular : {
                moviesHelper.clearList();
                mAdapter.notifyDataSetChanged();
                mAdapter = null;
                filterType = NetworkUtils.TYPE_POPULAR;
                loadMovieData(filterType, startingPageNumber);
                return true;
            }

            case R.id.menu_sortby_toprated : {
                moviesHelper.clearList();
                mAdapter.notifyDataSetChanged();
                mAdapter = null;
                filterType = NetworkUtils.TYPE_TOP_RATED;
                loadMovieData(filterType, startingPageNumber);
                return true;
            }

            case R.id.menu_sortby_favorites : {
                moviesHelper.clearList();
                mAdapter.notifyDataSetChanged();
                mAdapter = null;
                /*filterType = NetworkUtils.TYPE_TOP_RATED;
                loadMovieData(filterType, startingPageNumber)*/
                //getFavorites();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }


}