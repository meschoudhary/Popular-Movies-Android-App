package udacity.com.parvez.popularmovies.utils;

/**
 * Created by parvez on 19/2/16.
 */
public class MovieReview {
    private String movie_id;
    private String review_author;
    private String review_url;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MovieReview() {}

    public String getMovie_id() {
        return movie_id;
    }

    @Override
    public String toString() {
        return "MovieReview{" +
                "movie_id='" + movie_id + '\'' +
                ", review_author='" + review_author + '\'' +
                ", review_url='" + review_url + '\'' +
                '}';
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public String getReview_author() {
        return review_author;
    }

    public void setReview_author(String review_author) {
        this.review_author = review_author;
    }

    public String getReview_url() {
        return review_url;
    }

    public void setReview_url(String review_url) {
        this.review_url = review_url;
    }
}
