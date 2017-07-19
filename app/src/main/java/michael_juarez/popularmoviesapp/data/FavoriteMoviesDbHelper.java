package michael_juarez.popularmoviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 7/18/2017.
 */

public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favoritemovies.db";
    private static final int DATABASE_VERSION = 2;

    public FavoriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITE_MOVIES_TABLE = "CREATE TABLE " +
                FavoriteMoviesContract.FavoriteMovies.TABLE_NAME + " (" +
                FavoriteMoviesContract.FavoriteMovies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_ID + " TEXT, " +
                FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                /*FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_POSTER_URL + "BLOB NOT NULL, " +
                FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_BACKDROP_URL + "BLOB NOT NULL, " +*/
                FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_POSTER_URL + " TEXT NOT NULL, " +
                FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_BACKDROP_URL + " TEXT NOT NULL, " +
                FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL, " +
                FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_USER_RATING + " TEXT NOT NULL " + "); ";
        db.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMoviesContract.FavoriteMovies.TABLE_NAME);
        onCreate(db);
    }

}
