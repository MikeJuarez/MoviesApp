package michael_juarez.popularmoviesapp.utilities;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.sql.Array;
import java.util.ArrayList;

import michael_juarez.popularmoviesapp.data.Movie;
import michael_juarez.popularmoviesapp.data.MoviesHelper;

import static android.R.attr.name;
import static android.os.Build.VERSION_CODES.M;

/**
 * Created by user on 7/10/2017.
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
            Movie movie = new Movie(id, poster_path, title, overview, release_date, Integer.toString(pageNumber), backdrop_path,vote_average);
            movieList.add(movie);
        }

        return movieList;
    }

}
