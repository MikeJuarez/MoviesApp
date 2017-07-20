package michael_juarez.popularmoviesapp.data;

/**
 * Created by user on 7/17/2017.
 * This class creates and stores Review Objects
 * Reviews contain two variables, an author and their comment/review *
 */

public class Review {

    String mAuthor;
    String mContent;

    public Review(String author, String content) {
        mAuthor = author;
        mContent = content;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }
}

