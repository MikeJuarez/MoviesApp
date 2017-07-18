package michael_juarez.popularmoviesapp.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael Juarez on 7/10/2017.
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

    public void addMoreMovies(ArrayList<Movie> movieList) {
        for (Movie movie: movieList) {
            mMovieList.add(movie);
        }
    }

    public void clearList(){
        mMovieList.clear();
    }
}
