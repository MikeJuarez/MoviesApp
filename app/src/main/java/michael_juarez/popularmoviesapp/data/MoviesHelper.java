package michael_juarez.popularmoviesapp.data;

import android.content.Context;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import michael_juarez.popularmoviesapp.PopularMovies;
import michael_juarez.popularmoviesapp.utilities.NetworkUtils;
import michael_juarez.popularmoviesapp.utilities.OpenMoviesJsonUtils;

/**
 * Created by user on 7/11/2017.
 */

public class MoviesHelper {

    private static MoviesHelper sMoviesHelper;
    private List<Movie> mMovieList;


    public static MoviesHelper get(Context context) {
        if (sMoviesHelper == null)
            return new MoviesHelper(context);
        return sMoviesHelper;
    }

    private MoviesHelper(Context context) {
        mMovieList = new ArrayList<Movie>();
    }

    public List<Movie> getMovieList() {
        return mMovieList;
    }

    public int getPageNumberInt(String pageNumber) {
        return Integer.parseInt(pageNumber);
    }


    public int getMoviesListSize() {
        return mMovieList.size();
    }

    public void addMovie(Movie movie) {
        mMovieList.add(movie);
    }

    public void addMoreMovies(ArrayList<Movie> movieList) {
        for (Movie movie: movieList) {
            mMovieList.add(movie);
        }
    }

    public void clearList(){
        mMovieList.clear();
    }
}
