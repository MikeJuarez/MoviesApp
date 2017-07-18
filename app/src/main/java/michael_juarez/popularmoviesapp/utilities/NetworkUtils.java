package michael_juarez.popularmoviesapp.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Michael Juarez on 7/10/2017.
 */

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String API_KEY = "e5435433c91bf76618018e62e4b8f5fc";
    private static final String POPULAR_MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/popular?api_key=";
    private static final String TOP_RATED_MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/top_rated?api_key=";
    private static final String TRAILER_AND_REVIEW_START_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String TRAILER_END_BASE_URL = "/videos?api_key=";
    private static final String REVIEW_END_BASE_URL = "/reviews?api_key=";



    private static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";
    private static final String YOUTUBE_APP_BASE_URL = "vnd.youtube://";

    private static final String POSTER_DEFAULT_SIZE = "w500";
    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";

    public static final String TYPE_POPULAR = "POPULAR";
    public static final String TYPE_TOP_RATED = "TOP RATED";

    public static URL getMoviesURL(String sortBy, String pageNumber) {
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

    public static String getTrailerURL(String id) {
        try {
            return getResponseFromHttpUrl(new URL(TRAILER_AND_REVIEW_START_BASE_URL + id + TRAILER_END_BASE_URL + API_KEY));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String getReviewURL(String id) {
        try {
            URL url = new URL(TRAILER_AND_REVIEW_START_BASE_URL + id + REVIEW_END_BASE_URL + API_KEY);
            return getResponseFromHttpUrl(new URL(TRAILER_AND_REVIEW_START_BASE_URL + id + REVIEW_END_BASE_URL + API_KEY));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String getYoutubeFormatURL(){
        return YOUTUBE_BASE_URL;
    }

    public static String getYoutubeAppFormatURL(){
        return YOUTUBE_APP_BASE_URL;
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
