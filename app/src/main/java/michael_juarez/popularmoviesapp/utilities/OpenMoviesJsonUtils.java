package michael_juarez.popularmoviesapp.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import michael_juarez.popularmoviesapp.data.Movie;
import michael_juarez.popularmoviesapp.data.Review;

/**
 * Created by Michael Juarez on 7/10/2017.
 */

public final class OpenMoviesJsonUtils {

    public static ArrayList<Movie> getSimpleMovieStringsFromJson(String moviesJsonStr)
            throws JSONException {

        JSONObject moviesJson = new JSONObject(moviesJsonStr);

        int pageNumber = moviesJson.getInt("page");

        JSONArray moviesArray = moviesJson.getJSONArray("results");

        ArrayList<Movie> movieList = new ArrayList<>();
        for (int i = 0; i < moviesArray.length(); i++) {

            JSONObject jsonMovie = moviesArray.getJSONObject(i);

            String id = jsonMovie.getString("id");
            String poster_path = jsonMovie.getString("poster_path");
            String title = jsonMovie.getString("title");
            String overview = jsonMovie.getString("overview");;
            String release_date = jsonMovie.getString("release_date");
            String backdrop_path = jsonMovie.getString("backdrop_path");
            String vote_average = jsonMovie.getString("vote_average");

            Movie movie = new Movie(id, poster_path, title, overview, release_date, Integer.toString(pageNumber), backdrop_path, vote_average);
            movieList.add(movie);
        }

        return movieList;
    }

    public static ArrayList<String> getSimpleTrailerStringsFromJson(String trailerJsonString) throws JSONException{

        //Get a JSONObject by passing in a JSON formatted String passed in
        JSONObject movieTrailersJsonURL = new JSONObject(trailerJsonString);

        //Turn movieTrailersJsonURL(above) into a JSON Array
        JSONArray movieTrailersJsonArray = movieTrailersJsonURL.getJSONArray("results");

        //This will hold the trailers in a ArrayList<String>
        ArrayList<String> movieTrailersArray = new ArrayList();

        //Fill movieTrailersArray
        for (int mt = 0; mt < movieTrailersJsonArray.length(); mt++) {
            JSONObject jsonTrailerObject = movieTrailersJsonArray.getJSONObject(mt);

            //movieTrailersArray.add(NetworkUtils.getYoutubeFormatURL(jsonTrailerObject.getString("key")).toString());
            movieTrailersArray.add(jsonTrailerObject.getString("key"));
        }

        return movieTrailersArray;
    }

    public static ArrayList<Review> getSimpleReviewsStringsFromJson(String reviewJsonString) throws JSONException {

        JSONObject reviewsJSON = new JSONObject(reviewJsonString);
        JSONArray reviewsJSONArray = reviewsJSON.getJSONArray("results");

        ArrayList<Review> reviewList = new ArrayList();

        for (int i=0; i < reviewsJSONArray.length();i++) {
            JSONObject jsonReviewObject = reviewsJSONArray.getJSONObject(i);
            String author = jsonReviewObject.getString("author");
            String content = jsonReviewObject.getString("content");

            Review review = new Review(author, content);
            reviewList.add(review);
        }
        return reviewList;
    }



}
