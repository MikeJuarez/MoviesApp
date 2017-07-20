package michael_juarez.popularmoviesapp.data;

import java.util.ArrayList;

import static android.R.attr.id;
import static michael_juarez.popularmoviesapp.R.id.poster_path;

/**
 * Created by Michael Juarez on 7/10/2017.
 */

public class Movie {

    String mId;
    String mPoster_path;
    String mTitle;
    String mOverview;
    String mRelease_date;
    String mPageNumber;
    String mVoteAverage;
    String mBackDrop_path;

    public String getBackDrop_path() {
        return mBackDrop_path;
    }

    public void setBackDrop_path(String backDrop_path) {
        mBackDrop_path = backDrop_path;
    }

    public Movie(String id, String poster_path, String title, String overview, String release_date, String pageNumber, String backDrop_path, String voteAverage) {
        mId = id;
        mPoster_path = poster_path;
        mTitle = title;
        mOverview = overview;
        mRelease_date = release_date;
        mPageNumber = pageNumber;
        mBackDrop_path = backDrop_path;
        mVoteAverage = voteAverage;
    }
    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getPoster_path() {
        return mPoster_path;
    }

    public void setPoster_path(String poster_path) {
        mPoster_path = poster_path;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public String getRelease_date() {
        return mRelease_date;
    }

    public void setRelease_date(String release_date) {
        mRelease_date = release_date;
    }

    public String getPageNumber() { return mPageNumber;
    }
    public void setPageNumber(String pageNumber) { mPageNumber = pageNumber;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        mVoteAverage = voteAverage;
    }


}
