package michael_juarez.popularmoviesapp.data;

import android.content.pm.PackageInfo;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by user on 7/18/2017.
 */

public class FavoriteMoviesContract {

    public static final String AUTHORITY = "michael_juarez.popularmoviesapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITE_MOVIES = "favorite_movies";

    public static final class FavoriteMovies implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE_MOVIES).build();

        public static final String TABLE_NAME = "favorite_movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_POSTER_URL = "poster_url";
        public static final String COLUMN_MOVIE_BACKDROP_URL = "backdrop_url";
        public static final String COLUMN_MOVIE_SYNOPSIS = "synopsis";
        public static final String COLUMN_MOVIE_USER_RATING = "user_rating";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_BACKDROP_BLOB = "backdrop_blob";
        public static final String COLUMN_MOVIE_POSTER_BLOB = "poster_blob";
    }
}
