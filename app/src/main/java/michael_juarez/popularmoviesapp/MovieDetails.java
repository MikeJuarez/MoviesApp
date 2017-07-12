package michael_juarez.popularmoviesapp;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import michael_juarez.popularmoviesapp.data.Movie;
import michael_juarez.popularmoviesapp.data.MoviesHelper;
import michael_juarez.popularmoviesapp.utilities.NetworkUtils;

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



public class MovieDetails extends AppCompatActivity{
    private static final String EXTRA_TITLE = "com.michael_juarez.popularmoviesapp.title";
    private static final String EXTRA_POSTER_PATH = "com.michael_juarez.popularmoviesapp.posterPath";
    private static final String EXTRA_OVERVIEW = "com.michael_juarez.popularmoviesapp.overview";
    private static final String EXTRA_VOTE_AVERAGE = "com.michael_juarez.popularmoviesapp.voteAverage";
    private static final String EXTRA_RELEASE_DATE = "com.michael_juarez.popularmoviesapp.releaseDate";
    private static final String EXTRA_BACKDROP_PATH = "com.michael_juarez.popularmoviesapp.backdropPath";

    private MoviesHelper mMoviesHelper;
    private ArrayList<Movie> mMoviesList;

    private ImageView mBackDropPath;
    private ImageView mPosterImageView;
    private TextView mTitle;
    private TextView mOverview;
    private TextView mVoteAverage;
    private TextView mReleaseDate;

    public static Intent newIntent(Context packageContext, String title, String posterPath, String overview, String voteAverage, String releaseDate, String backdropPath) {
        Intent intent = new Intent(packageContext, MovieDetails.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_POSTER_PATH, posterPath);
        intent.putExtra(EXTRA_OVERVIEW, overview);
        intent.putExtra(EXTRA_VOTE_AVERAGE, voteAverage);
        intent.putExtra(EXTRA_RELEASE_DATE, releaseDate);
        intent.putExtra(EXTRA_BACKDROP_PATH, backdropPath);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mBackDropPath = (ImageView) findViewById(R.id.movie_details_backdrop);
        mPosterImageView = (ImageView) findViewById(R.id.movie_details_poster);
        mTitle = (TextView) findViewById(R.id.movie_details_title);
        mOverview = (TextView) findViewById(R.id.movie_details_overview);
        mVoteAverage = (TextView) findViewById(R.id.movie_details_vote_average);
        mReleaseDate = (TextView) findViewById(R.id.movie_details_release_date);

        String backDropPath = (String) getIntent().getSerializableExtra(EXTRA_BACKDROP_PATH);
        String backDropURL = NetworkUtils.getImageURL(backDropPath);
        String title = (String) getIntent().getSerializableExtra(EXTRA_TITLE);
        String posterPath = (String) getIntent().getSerializableExtra(EXTRA_POSTER_PATH);
        String posterURL = NetworkUtils.getImageURL(posterPath);
        String overview = (String) getIntent().getSerializableExtra(EXTRA_OVERVIEW);
        String voteAverage = (String) getIntent().getSerializableExtra(EXTRA_VOTE_AVERAGE);
        String releaseDate = (String) getIntent().getSerializableExtra(EXTRA_RELEASE_DATE);

        Picasso.with(this).load(backDropURL).into(mBackDropPath);
        mTitle.setText(title);
        Picasso.with(this).load(posterURL).into(mPosterImageView);
        mOverview.setText(overview);
        mVoteAverage.setText(voteAverage);
        mReleaseDate.setText(releaseDate);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.menu_movie_details, menu);
        /* Return true so that the menu is displayed in the Toolbar */

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.back);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
