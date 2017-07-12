package michael_juarez.popularmoviesapp.utilities;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import michael_juarez.popularmoviesapp.data.MoviesHelper;

/**
 * Created by user on 7/10/2017.
 */

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String API_KEY = "e5435433c91bf76618018e62e4b8f5fc";
    private static final String POPULAR_MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/popular?api_key=";
    private static final String TOP_RATED_MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/top_rated?api_key=";
    private static final String POSTER_DEFAULT_SIZE = "w500";
    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";

    public static final String TYPE_POPULAR = "POPULAR";
    public static final String TYPE_TOP_RATED = "TOP RATED";

    public static URL getPopularMoviesURL(String sortBy, String pageNumber) {
        String pre_url;

        switch (sortBy) {
            case TYPE_POPULAR: {
                pre_url = POPULAR_MOVIES_BASE_URL + API_KEY;
                break;
            }
            case TYPE_TOP_RATED : {
                pre_url = TOP_RATED_MOVIES_BASE_URL + API_KEY;
                break;
            }
            default: {
                pre_url = POPULAR_MOVIES_BASE_URL + API_KEY;
                break;
            }
        }

        Uri builtUri = Uri.parse(pre_url).buildUpon()
                .appendQueryParameter("page", pageNumber)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String getImageURL(String image_path) {
        return POSTER_BASE_URL + POSTER_DEFAULT_SIZE + image_path;
    }
}
