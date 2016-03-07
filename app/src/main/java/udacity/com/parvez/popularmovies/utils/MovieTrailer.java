package udacity.com.parvez.popularmovies.utils;

/**
 * Created by parvez on 19/2/16.
 */
public class MovieTrailer {
    private String movie_id;
    private String trailer_id;
    private String trailer_url;
    private String tailer_name;
    private String tailer_key;
    private final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";

    @Override
    public String toString() {
        return "MovieTrailer{" +
                "movie_id='" + movie_id + '\'' +
                ", trailer_id='" + trailer_id + '\'' +
                ", trailer_key='" + trailer_url + '\'' +
                ", tailer_name='" + tailer_name + '\'' +
                '}';
    }

    public MovieTrailer() {

    }

    public String getTailer_key() {
        return tailer_key;
    }

    public void setTailer_key(String tailer_key) {
        this.tailer_key = tailer_key;
    }

    public String getTrailer_id() {
        return trailer_id;
    }

    public void setTrailer_id(String trailer_id) {
        this.trailer_id = trailer_id;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public String getTrailer_url() {
        return trailer_url;
    }

    public void setTrailer_key(String trailer_key) {
        this.trailer_url = YOUTUBE_BASE_URL + trailer_key;
    }

    public String getTailer_name() {
        return tailer_name;
    }

    public void setTailer_name(String tailer_name) {
        this.tailer_name = tailer_name;
    }
}
