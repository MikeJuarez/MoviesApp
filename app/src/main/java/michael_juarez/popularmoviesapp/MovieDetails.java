package michael_juarez.popularmoviesapp;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;

import michael_juarez.popularmoviesapp.data.FavoriteMoviesContract;
import michael_juarez.popularmoviesapp.data.Review;
import michael_juarez.popularmoviesapp.utilities.NetworkUtils;
import michael_juarez.popularmoviesapp.utilities.OpenMoviesJsonUtils;

import static android.graphics.BitmapFactory.decodeByteArray;
import static michael_juarez.popularmoviesapp.R.string.back;

/**
 * Created by Michael Juarez on 7/10/2017.
 *
 * Allow the user to tap on a movie poster and transition to a details screen with additional information such as:
    - original title
    - movie poster image thumbnail
    - A plot synopsis (called overview in the api)
    - user rating (called vote_average in the api)
    - release date
 */



public class MovieDetails extends AppCompatActivity implements MovieDetailsAdapter.ListItemClickListener {
    private static final String TAG = MovieDetails.class.getName();
    private static final int LOADER_MANAGER_TRAILERS = 64;
    private static final int LOADER_MANAGER_REVIEWS = 65;
    private static final int LOADER_FAVORITE_CHECK = 66;
    private static final int LOADER_IMAGE_DB = 67;
    private static final int LOADER_INSERT_MOVIES = 68;

    private static final String EXTRA_TITLE = "com.michael_juarez.popularmoviesapp.title";
    private static final String EXTRA_POSTER_PATH = "com.michael_juarez.popularmoviesapp.posterPath";
    private static final String EXTRA_OVERVIEW = "com.michael_juarez.popularmoviesapp.overview";
    private static final String EXTRA_VOTE_AVERAGE = "com.michael_juarez.popularmoviesapp.voteAverage";
    private static final String EXTRA_RELEASE_DATE = "com.michael_juarez.popularmoviesapp.releaseDate";
    private static final String EXTRA_BACKDROP_PATH = "com.michael_juarez.popularmoviesapp.backdropPath";
    private static final String EXTRA_ID = "com.michael_juarez.popularmoviesapp.id";
    private static final String EXTRA_FILTER_TYPE = "com.michael_juarez.popularmoviesapp.filterType";



    private static final String EXTRA_TRAILERS_BOOLEAN = "com.michael_juarez.popularmoviesapp.trailers";
    private static final String EXTRA_REVIEWS_BOOLEAN =  "com.michael_juarez.popularmoviesapp.reviews";

    private SQLiteDatabase mDb;

    private ImageView mBackDropImageView;
    private ImageView mPosterImageView;
    private TextView mTitle;
    private TextView mOverview;
    private TextView mVoteAverage;
    private TextView mReleaseDate;
    private TextView mReviews;
    private RecyclerView mRecyclerView;
    private String mId;
    private LinearLayoutManager mLayoutManager;
    private MovieDetailsAdapter mAdapter;

    private String backDropURL;
    private String backDropPath;
    private String title;
    private String posterPath;
    private String posterURL;
    private String overview;
    private String voteAverage;
    private String releaseDate;
    private String filterType;

    private boolean returnIntentBoolean;

    //This Intent is properly formatted for use by other classes to use this class.
    public static Intent newIntent(Context packageContext, String title, String posterPath, String overview, String voteAverage, String releaseDate, String backdropPath, String id, String filterType) {
        Intent intent = new Intent(packageContext, MovieDetails.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_POSTER_PATH, posterPath);
        intent.putExtra(EXTRA_OVERVIEW, overview);
        intent.putExtra(EXTRA_VOTE_AVERAGE, voteAverage);
        intent.putExtra(EXTRA_RELEASE_DATE, releaseDate);
        intent.putExtra(EXTRA_BACKDROP_PATH, backdropPath);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_FILTER_TYPE, filterType);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        //Find and assign Views and Widgets to member variables
        mBackDropImageView = (ImageView) findViewById(R.id.movie_details_backdrop);
        mPosterImageView = (ImageView) findViewById(R.id.movie_details_poster);
        mTitle = (TextView) findViewById(R.id.movie_details_title);
        mOverview = (TextView) findViewById(R.id.movie_details_overview);
        mVoteAverage = (TextView) findViewById(R.id.movie_details_vote_average);
        mReleaseDate = (TextView) findViewById(R.id.movie_details_release_date);
        mRecyclerView = (RecyclerView) findViewById(R.id.movie_details_trailers_rv);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mReviews = (TextView) findViewById(R.id.movie_details_reviews_tv);

        //Assign intent values from intent into proper member variables
        mId = (String) getIntent().getSerializableExtra(EXTRA_ID);
        backDropPath = (String) getIntent().getSerializableExtra(EXTRA_BACKDROP_PATH);
        backDropURL = NetworkUtils.getImageURL(backDropPath);
        title = (String) getIntent().getSerializableExtra(EXTRA_TITLE);
        posterPath = (String) getIntent().getSerializableExtra(EXTRA_POSTER_PATH);
        posterURL = NetworkUtils.getImageURL(posterPath);
        overview = (String) getIntent().getSerializableExtra(EXTRA_OVERVIEW);
        voteAverage = (String) getIntent().getSerializableExtra(EXTRA_VOTE_AVERAGE);
        releaseDate = (String) getIntent().getSerializableExtra(EXTRA_RELEASE_DATE);
        filterType = getIntent().getStringExtra(EXTRA_FILTER_TYPE);

        //If this is not a favorite, then fill ImageView with Picasso
        //Else use images from DB
        if (!filterType.equals(NetworkUtils.TYPE_FAVORITE_MOVIES)) {
            Picasso.with(this).load(backDropURL).into(mBackDropImageView);
            Picasso.with(this).load(posterURL).into(mPosterImageView);
        }
        else {
            setupImageLoadFromDb();
        }

        //Load widgets and views with variables from intent
        mTitle.setText(title);

        mOverview.setText(overview);
        mVoteAverage.setText(voteAverage);
        mReleaseDate.setText(releaseDate);

        //LayoutManager assignment for the recyclerView
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Query imdb's api by initializing the trailers and reviews
        setupTrailers();
        setupReviews();
    }

    private void setupTrailers() {
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_TRAILERS_BOOLEAN, true);
        args.putBoolean(EXTRA_REVIEWS_BOOLEAN, false);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList> movieLoader = loaderManager.getLoader(LOADER_MANAGER_TRAILERS);

        if (movieLoader == null)
            loaderManager.initLoader(LOADER_MANAGER_TRAILERS,args,trailersAndReviewsLoader);
        else
            loaderManager.restartLoader(LOADER_MANAGER_TRAILERS,args,trailersAndReviewsLoader);
    }

    private void setupReviews() {
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_REVIEWS_BOOLEAN, true);
        args.putBoolean(EXTRA_TRAILERS_BOOLEAN, false);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList> movieLoader = loaderManager.getLoader(LOADER_MANAGER_REVIEWS);

        if (movieLoader == null)
            loaderManager.initLoader(LOADER_MANAGER_REVIEWS,args,trailersAndReviewsLoader);
        else
            loaderManager.restartLoader(LOADER_MANAGER_REVIEWS,args,trailersAndReviewsLoader);
    }

    //This class is only called when the Filter Type is equal to Favorite Movies
    private void setupImageLoadFromDb() {
        Bundle args = new Bundle();
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList> imageDBLader = loaderManager.getLoader(LOADER_IMAGE_DB);

        if (imageDBLader == null)
            loaderManager.initLoader(LOADER_IMAGE_DB,args,loaderImagesFromDb);
        else
            loaderManager.restartLoader(LOADER_IMAGE_DB,args,loaderImagesFromDb);
    }

    /*
      This background thread is used to load the images from the Database
      This Loader is only called when the Filter Type is equal to Favorite Movies
    */
    private LoaderManager.LoaderCallbacks<ArrayList<byte[]>> loaderImagesFromDb = new LoaderManager.LoaderCallbacks<ArrayList<byte[]>>() {

        @Override
        public Loader<ArrayList<byte[]>> onCreateLoader(int id, final Bundle args)  {
            return new AsyncTaskLoader<ArrayList<byte[]>>(getBaseContext()) {

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    forceLoad();
                }

                @Override
                public ArrayList<byte[]> loadInBackground() {
                    try {
                        Cursor cursor = getContentResolver().query(FavoriteMoviesContract.FavoriteMovies.CONTENT_URI,
                                null,
                                FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_ID + "=?",
                                new String[]{mId},
                                null,
                                null);
                        if (cursor.moveToFirst()){
                            byte[] posterByte = cursor.getBlob(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_POSTER_BLOB));
                            byte[] backDropByte = cursor.getBlob(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_BACKDROP_BLOB));
                            cursor.close();
                            ArrayList<byte[]> returnList = new ArrayList<byte[]>();
                            returnList.add(posterByte);
                            returnList.add(backDropByte);
                            return returnList;
                        }
                        else {
                            cursor.close();
                            return null;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to asynchronously load data.");
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<byte[]>> loader, ArrayList<byte[]> data) {
            byte[] posterByte = data.get(0);
            byte[] backDropByte = data.get(1);

            mPosterImageView.setImageBitmap(BitmapFactory.decodeByteArray(posterByte, 0, posterByte.length));
            mBackDropImageView.setImageBitmap(BitmapFactory.decodeByteArray(backDropByte, 0, backDropByte.length));
            getSupportLoaderManager().destroyLoader(loader.getId());
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<byte[]>> loader) {

        }
    };

    //This background thread can handle retrieving Movie Trailers and Reviews
    private LoaderManager.LoaderCallbacks<ArrayList> trailersAndReviewsLoader = new LoaderManager.LoaderCallbacks<ArrayList>() {

        @Override
        public Loader<ArrayList> onCreateLoader(int id, final Bundle args)  {
            return new AsyncTaskLoader<ArrayList>(getBaseContext()) {

                Boolean trailers;
                Boolean reviews;

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if (mId == null || mId.isEmpty())
                        return;
                    trailers = args.getBoolean(EXTRA_TRAILERS_BOOLEAN);
                    reviews = args.getBoolean(EXTRA_REVIEWS_BOOLEAN);
                    forceLoad();
                }

                @Override
                public ArrayList loadInBackground() {
                    ArrayList listToReturn;

                    if (trailers) {
                        String jsonTrailerResponse = (NetworkUtils.getTrailerURL(mId));
                        try {
                            listToReturn = OpenMoviesJsonUtils.getSimpleTrailerStringsFromJson(jsonTrailerResponse);
                            return listToReturn;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return null;
                        }
                    } else if (reviews) {
                        String spanAuthor = "<b>Author: </b>";
                        String spanComment = "<b>Comment: </b>";
                        String jsonReviewResponse = (NetworkUtils.getReviewURL(mId));
                        try {
                            ArrayList<Review> reviewList = OpenMoviesJsonUtils.getSimpleReviewsStringsFromJson(jsonReviewResponse);
                            listToReturn = new ArrayList();

                            String reviewListString = new String("");
                            for (int i = 0; i < reviewList.size(); i++) {
                                reviewListString = reviewListString + spanAuthor + reviewList.get(i).getAuthor() + "<br>"
                                        + spanComment + "<br>" + "\"" + "<i>" + reviewList.get(i).getContent() + "</i>" + "\"" + "<br><br><hr>";
                            }
                            listToReturn.add(reviewListString);
                            return listToReturn;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ArrayList> loader, ArrayList data) {
                trailerloaderHelper(data, loader.getId());
            getSupportLoaderManager().destroyLoader(loader.getId());
        }

        @Override
        public void onLoaderReset(Loader<ArrayList> loader) {

        }
    };

    private void trailerloaderHelper(ArrayList data, int id) {
        if (data == null)
            return;

        if (id == LOADER_MANAGER_TRAILERS) {
            mAdapter = new MovieDetailsAdapter(data, backDropURL, this);
            mRecyclerView.setAdapter(mAdapter);
            return;
        }
        else if (id == LOADER_MANAGER_REVIEWS){
            mReviews.setText(Html.fromHtml((String)data.get(0)));
        }
    }

    @Override
    public void onListItemClick(int itemClicked, String youtubeLink) {
        try {
            Uri trailerUri = Uri.parse(NetworkUtils.getYoutubeAppFormatURL() + youtubeLink);
            Intent trailerIntent = new Intent(Intent.ACTION_VIEW, trailerUri);
            trailerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(trailerIntent);
        } catch (ActivityNotFoundException e){
            // youtube is not installed.Will be opened in other available apps
            Uri trailerUri = Uri.parse(NetworkUtils.getYoutubeFormatURL() + youtubeLink);
            Intent i = new Intent(Intent.ACTION_VIEW, trailerUri);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.menu_movie_details, menu);
        /* Return true so that the menu is displayed in the Toolbar */

        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(back);
        }

        //Check if movie is saved already
        //If saved, then change favorite icon to yellow
        LoaderManager.LoaderCallbacks<Boolean> queryLoader = new LoaderManager.LoaderCallbacks<Boolean>() {

            @Override
            public Loader<Boolean> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<Boolean>(getBaseContext()){

                    @Override
                    protected void onStartLoading() {
                        forceLoad();
                    }

                    @Override
                    public Boolean loadInBackground() {
                        try {
                            String mSelection = FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_ID + "=?";
                            String[] mSelectionArgs = new String[]{mId};
                            Cursor cursor = getContentResolver().query(FavoriteMoviesContract.FavoriteMovies.CONTENT_URI,
                                    //new String[] {FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_ID},
                                    //FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_ID + "=" + mId,
                                    null,
                                    mSelection,
                                    mSelectionArgs,
                                    null,
                                    null);

                            if (cursor.moveToFirst())
                                return true;
                            else
                                return false;
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to asynchronously load data.");
                            e.printStackTrace();
                            return false;
                        }
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
                if (data){
                    MenuItem item = menu.getItem(0).setIcon(R.drawable.ic_star_checked);
                    item.setChecked(true);
                }
                getSupportLoaderManager().destroyLoader(loader.getId());
            }

            @Override
            public void onLoaderReset(Loader<Boolean> loader) {

            }
        };

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Boolean> favoriteLoader = loaderManager.getLoader(LOADER_FAVORITE_CHECK);

        if (favoriteLoader == null)
            loaderManager.initLoader(LOADER_FAVORITE_CHECK,null,queryLoader);
        else
            loaderManager.restartLoader(LOADER_FAVORITE_CHECK,null,queryLoader);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_favorite : {
                if (!item.isChecked()){
                    item.setChecked(true);
                    item.setIcon(R.drawable.ic_star_checked);
                    insertFavoriteMovie();
                } else {
                    item.setChecked(false);
                    item.setIcon(R.drawable.ic_star);
                    removeFavoriteMovie();
                }
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void insertFavoriteMovie() {

        LoaderManager.LoaderCallbacks<ContentValues> insertFavoriteLoader = new LoaderManager.LoaderCallbacks<ContentValues>() {

            @Override
            public Loader<ContentValues> onCreateLoader(int id, final Bundle args)  {
                return new AsyncTaskLoader<ContentValues>(getBaseContext()) {

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        forceLoad();
                    }

                    @Override
                    public ContentValues loadInBackground() {
                        ContentValues cv = new ContentValues();
                        cv.put(FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_ID, mId);
                        cv.put(FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_TITLE, title);
                        cv.put(FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
                        cv.put(FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_SYNOPSIS, overview);
                        cv.put(FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_USER_RATING, voteAverage);
                        cv.put(FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_POSTER_URL, posterPath);
                        cv.put(FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_BACKDROP_URL, backDropPath);

                        byte[] posterPathByte = getBitmapAsByteArray(getBitmapFromUrl(posterURL));
                        byte[] backDropURLByte = getBitmapAsByteArray(getBitmapFromUrl(backDropURL));
                        cv.put(FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_POSTER_BLOB, posterPathByte);
                        cv.put(FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_BACKDROP_BLOB, backDropURLByte);

                        return cv;
                    }

                    private Bitmap getBitmapFromUrl(String src) {
                        try {
                            URL url = new URL(src);
                            /*HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setDoInput(true);
                            connection.connect();
                            InputStream input = connection.getInputStream();*/
                            Bitmap myBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            return myBitmap;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                    private byte[] getBitmapAsByteArray(Bitmap bitmap) {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        return outputStream.toByteArray();
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<ContentValues> loader, ContentValues cv) {
                Uri uri = getContentResolver().insert(FavoriteMoviesContract.FavoriteMovies.CONTENT_URI,cv);
                if (uri != null)
                    Toast.makeText(getBaseContext(), "" + title + " " + getResources().getString(R.string.added_sucessfully), Toast.LENGTH_LONG).show();
                getSupportLoaderManager().destroyLoader(loader.getId());
            }

            @Override
            public void onLoaderReset(Loader<ContentValues> loader) {

            }
        };
        Bundle args = new Bundle();
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList> imageDBLader = loaderManager.getLoader(LOADER_INSERT_MOVIES);

        if (imageDBLader == null)
            loaderManager.initLoader(LOADER_INSERT_MOVIES,args,insertFavoriteLoader);
        else
            loaderManager.restartLoader(LOADER_INSERT_MOVIES,args,insertFavoriteLoader);

    }

    private void removeFavoriteMovie() {
        Uri uri = FavoriteMoviesContract.FavoriteMovies.CONTENT_URI;
        uri = uri.buildUpon().appendPath(mId).build();
        int deleteMovie = getContentResolver().delete(uri,FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_ID,null);
        if (deleteMovie != 0) {
            Toast.makeText(this, "" + title + " " + getResources().getString(R.string.removed_successfully), Toast.LENGTH_LONG).show();
            returnIntentBoolean = true;
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("dropped", returnIntentBoolean);
        setResult(RESULT_OK, returnIntent);
        finish();
        super.onBackPressed();
    }
}
