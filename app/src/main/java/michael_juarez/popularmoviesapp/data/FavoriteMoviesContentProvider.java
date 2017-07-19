package michael_juarez.popularmoviesapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.R.attr.id;
import static michael_juarez.popularmoviesapp.data.FavoriteMoviesContract.FavoriteMovies.TABLE_NAME;

/**
 * Created by user on 7/18/2017.
 */

public class FavoriteMoviesContentProvider extends ContentProvider{

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    public static final int FAVORITE_MOVIES_DIRECTORY = 100;
    public static final int FAVORITE_MOVIES_WITH_ID = 101;

    private FavoriteMoviesDbHelper mFavoriteMoviesDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFavoriteMoviesDbHelper = new FavoriteMoviesDbHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mFavoriteMoviesDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor cursor;
        switch (match) {
            case FAVORITE_MOVIES_DIRECTORY:
                cursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITE_MOVIES_WITH_ID:
                cursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mFavoriteMoviesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri;
        long id;
        switch(match) {
            case FAVORITE_MOVIES_DIRECTORY:
                id = db.insert(FavoriteMoviesContract.FavoriteMovies.TABLE_NAME, null, values);
                if (id > 0) {
                    //Success
                    returnUri = ContentUris.withAppendedId(FavoriteMoviesContract.FavoriteMovies.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mFavoriteMoviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int tasksDeleted;
        switch(match) {
            case FAVORITE_MOVIES_WITH_ID:
                String mId = uri.getPathSegments().get(1);
                tasksDeleted = db.delete(TABLE_NAME, FavoriteMoviesContract.FavoriteMovies.COLUMN_MOVIE_ID + "=?", new String[]{mId});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //Add matches with addURI
        //Directory
        uriMatcher.addURI(FavoriteMoviesContract.AUTHORITY, FavoriteMoviesContract.PATH_FAVORITE_MOVIES, FAVORITE_MOVIES_DIRECTORY);
        //Single item
        uriMatcher.addURI(FavoriteMoviesContract.AUTHORITY, FavoriteMoviesContract.PATH_FAVORITE_MOVIES + "/*", FAVORITE_MOVIES_WITH_ID);

        return uriMatcher;
    }
}
