package michael_juarez.popularmoviesapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;

import michael_juarez.popularmoviesapp.data.FavoriteMoviesContract;
import michael_juarez.popularmoviesapp.data.Movie;
import michael_juarez.popularmoviesapp.data.MoviesHelper;
import michael_juarez.popularmoviesapp.utilities.NetworkUtils;
import michael_juarez.popularmoviesapp.utilities.OpenMoviesJsonUtils;

import static android.R.attr.data;

/**
 *  Created by Michael Juarez on 7/10/2017.
 */

public class MoviesActivity extends AppCompatActivity implements MoviesAdapter.ScrollMaxListener, MoviesAdapter.ListItemClickListener{
    //For returned intent.  Checks if movie was removed from favorites.
    public final int DROPPED_FAVORITE_MOVIE = 2000;
    private final String TAG = getClass().getName();

    private final static String POPULAR_MOVIES_ACTIVITY_LOADER_SORTBY = "com.michael_juarez_popularmoviesapp.loader.sortby_popular";
    private final static String POPULAR_MOVIES_ACTIVITY_LOADER_PAGE = "com.michael_juarez_popularmoviesapp.loader.page_popular";
    private final static String TOP_RATED_MOVIES_ACTIVITY_LOADER_SORTBY = "com.michael_juarez_popularmoviesapp.loader.sortby_top_rated";
    private final static String TOP_RATED_MOVIES_ACTIVITY_LOADER_PAGE = "com.michael_juarez_popularmoviesapp.loader.page_top_rated";
    private final static String FAVORITE_MOVIES_ACTIVITY_LOADER_SORTBY = "com.michael_juarez_popularmoviesapp.loader.sortby_favorite";
    private final static String FAVORITE_MOVIES_ACTIVITY_LOADER_PAGE = "com.michael_juarez_popularmoviesapp.loader.pagefavorite";
    private final String MOVIES_ACTIVITY_SAVED_INSTANCE_FILTER_TYPE = "filtertype";


    private final static int MOVIES_ACTIVITY_POPULAR = 1000;
    private final static int MOVIES_ACTIVITY_TOP_RATING = 10001;
    private final static int MOVIES_ACTIVITY_FAVORITES = 10002;

    private String initialFilterType = NetworkUtils.TYPE_POPULAR;
    private final String startingPageNumber = "1";

    private RecyclerView mRecyclerView;
    private MoviesAdapter mAdapter;
    private GridLayoutManager layoutManager;
    private ProgressBar mProgressBar;
    private TextView mTextViewErrorMessage;
    private MoviesHelper moviesHelper;
    private String filterType;

    private boolean onFavoriteList = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            initialFilterType = savedInstanceState.getString(MOVIES_ACTIVITY_SAVED_INSTANCE_FILTER_TYPE);

        setContentView(R.layout.activity_popular_movies);
        initialization();
        loadMovieData(initialFilterType, startingPageNumber);
    }

    //Set up references and RecyclerView
    private void initialization() {
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

        switch (sortBy) {
            case NetworkUtils.TYPE_POPULAR:
                Bundle popularMovieBundle = new Bundle();
                popularMovieBundle.putString(POPULAR_MOVIES_ACTIVITY_LOADER_SORTBY, sortBy);
                popularMovieBundle.putString(POPULAR_MOVIES_ACTIVITY_LOADER_PAGE, pageNumber);
                LoaderManager popularLoaderManager = getSupportLoaderManager();
                Loader<ArrayList<Movie>> popularMovieLoader = popularLoaderManager.getLoader(MOVIES_ACTIVITY_POPULAR);

                if (popularMovieLoader == null)
                    popularLoaderManager.initLoader(MOVIES_ACTIVITY_POPULAR, popularMovieBundle, loaderMovies);
                else
                    popularLoaderManager.restartLoader(MOVIES_ACTIVITY_POPULAR, popularMovieBundle, loaderMovies);
                break;
            case NetworkUtils.TYPE_TOP_RATED:
                Bundle topRatedBundle = new Bundle();
                topRatedBundle.putString(TOP_RATED_MOVIES_ACTIVITY_LOADER_SORTBY, sortBy);
                topRatedBundle.putString(TOP_RATED_MOVIES_ACTIVITY_LOADER_PAGE, pageNumber);
                LoaderManager topRatedLoaderManager = getSupportLoaderManager();
                Loader<ArrayList<Movie>> topRatedMovieLoader = topRatedLoaderManager.getLoader(MOVIES_ACTIVITY_TOP_RATING);

                if (topRatedMovieLoader == null)
                    topRatedLoaderManager.initLoader(MOVIES_ACTIVITY_TOP_RATING, topRatedBundle, loaderMovies);
                else
                    topRatedLoaderManager.restartLoader(MOVIES_ACTIVITY_TOP_RATING, topRatedBundle, loaderMovies);
                break;
            case NetworkUtils.TYPE_FAVORITE_MOVIES:
                Bundle favoriteMovieBundle = new Bundle();
                favoriteMovieBundle.putString(FAVORITE_MOVIES_ACTIVITY_LOADER_SORTBY, sortBy);
                favoriteMovieBundle.putString(FAVORITE_MOVIES_ACTIVITY_LOADER_PAGE, "favorite_movies");
                LoaderManager favoriteLoaderManager = getSupportLoaderManager();
                Loader<ArrayList<Movie>> favoriteMovieLoader = favoriteLoaderManager.getLoader(MOVIES_ACTIVITY_FAVORITES);

                if (favoriteMovieLoader == null)
                    favoriteLoaderManager.initLoader(MOVIES_ACTIVITY_FAVORITES, favoriteMovieBundle, loaderMovies);
                else
                    favoriteLoaderManager.restartLoader(MOVIES_ACTIVITY_FAVORITES, favoriteMovieBundle, loaderMovies);
                break;
        }
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
    private LoaderManager.LoaderCallbacks<ArrayList<Movie>> loaderMovies = new LoaderManager.LoaderCallbacks<ArrayList<Movie>>() {
        @Override
        public Loader<ArrayList<Movie>> onCreateLoader(final int id, final Bundle args) {
            return new AsyncTaskLoader<ArrayList<Movie>>(getBaseContext()) {
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
                    String pageNumber = "0";
                    switch (id) {
                        case MOVIES_ACTIVITY_POPULAR:
                            onFavoriteList = false;
                            filterType = args.getString(POPULAR_MOVIES_ACTIVITY_LOADER_SORTBY);
                            pageNumber = args.getString(POPULAR_MOVIES_ACTIVITY_LOADER_PAGE);
                            break;
                        case MOVIES_ACTIVITY_TOP_RATING:
                            onFavoriteList = false;
                            filterType = args.getString(TOP_RATED_MOVIES_ACTIVITY_LOADER_SORTBY);
                            pageNumber = args.getString(TOP_RATED_MOVIES_ACTIVITY_LOADER_PAGE);
                            break;
                        case MOVIES_ACTIVITY_FAVORITES:
                            onFavoriteList = true;
                            filterType = args.getString(FAVORITE_MOVIES_ACTIVITY_LOADER_SORTBY);
                            pageNumber = args.getString(FAVORITE_MOVIES_ACTIVITY_LOADER_PAGE);
                            break;
                    }

                    switch (filterType) {
                        case (NetworkUtils.TYPE_FAVORITE_MOVIES):
                            return (loadInBackgroundOffLineHelper());
                        case (NetworkUtils.TYPE_POPULAR):
                            return loadInBackgroundOnlineHelper(pageNumber);
                        case (NetworkUtils.TYPE_TOP_RATED):
                            return loadInBackgroundOnlineHelper(pageNumber);
                        default:
                            return null;
                    }
                }

                private ArrayList<Movie> loadInBackgroundOnlineHelper(String pageNumber) {
                    URL moviesRequestURL = NetworkUtils.getMoviesURL(filterType, pageNumber);
                    ArrayList<Movie> movieList;
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

                private ArrayList<Movie> loadInBackgroundOffLineHelper() {
                    try {
                        Cursor cursor = getContentResolver().query(FavoriteMoviesContract.FavoriteMovies.CONTENT_URI,
                                null,
                                null,
                                null,
                                null,
                                null);
                        if (cursor.moveToFirst()){
                            ArrayList<Movie> movieList = new ArrayList();

                            for (int i = cursor.getPosition(); i < cursor.getCount(); i++) {
                                cursor.moveToPosition(i);
                                String mId = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_ID));
                                String mPoster_path = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_POSTER_URL));
                                String mTitle = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_TITLE));
                                String mOverview = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_SYNOPSIS));
                                String mRelease_date = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_RELEASE_DATE));
                                String mBackDrop_path = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_BACKDROP_URL));
                                String mVoteAverage = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_USER_RATING));
                                String mPageNumber = "favorite_movies";
//
                                Movie movie = new Movie(mId, mPoster_path, mTitle, mOverview, mRelease_date, mPageNumber, mBackDrop_path, mVoteAverage);
                                movieList.add(movie);
                            }

                            return movieList;
                        }
                        else
                            return null;
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to asynchronously load data.");
                        e.printStackTrace();
                        return null;
                    }
                }
            };

        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
            movieLoaderHelper(data);
            getSupportLoaderManager().destroyLoader(loader.getId());
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

        }
    };
    private void movieLoaderHelper(ArrayList<Movie> data) {
        mProgressBar.setVisibility(View.INVISIBLE);

        if (data != null) {
            showMovieDataView();

            if (mAdapter == null) {
                moviesHelper.addMoreMovies(data);
                mAdapter = new MoviesAdapter((ArrayList<Movie>)moviesHelper.getMovieList(), this, this);
                mRecyclerView.setAdapter(mAdapter);
            }
            else {
                moviesHelper.addMoreMovies(data);
                mAdapter.notifyDataSetChanged();
            }

        } else {
            mAdapter = new MoviesAdapter(new ArrayList<Movie>(), this, this);
            showErrorMessage();};
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

        Intent intent = MovieDetails.newIntent(getBaseContext(), title, moviePosterPath, overview, voteAverage, releaseDate, movieBackDropPath, id, filterType);
        startActivityForResult(intent, DROPPED_FAVORITE_MOVIE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DROPPED_FAVORITE_MOVIE && resultCode == RESULT_OK) {
            if (data.getBooleanExtra("dropped", true)) {
                moviesHelper.clearList();
                mAdapter.notifyDataSetChanged();
                mAdapter = null;
                filterType = NetworkUtils.TYPE_FAVORITE_MOVIES;
                loadMovieData(filterType, "favorite_movies");
            }
        }
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
                filterType = NetworkUtils.TYPE_FAVORITE_MOVIES;
                loadMovieData(filterType, "favorite_movies");
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putString(MOVIES_ACTIVITY_SAVED_INSTANCE_FILTER_TYPE, filterType);
        // etc.
    }
}
