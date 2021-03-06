package udacity.com.parvez.popularmovies.utils;

import android.os.Parcel;
import android.os.Parcelable;


public class Movie implements Parcelable {
    private String id;
    private String title;
    private String poster_path;
    private String release_date;
    private String vote_average;
    private String plot_synopsis;
    private String backdrop_path;

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        String BACKDROP_BASE_URL = "http://image.tmdb.org/t/p/w500/";
        String BACKDROP_PATH_URL = backdrop_path;
        String BACKDROP_IMAGE_URL = BACKDROP_BASE_URL + BACKDROP_PATH_URL;
        this.backdrop_path = BACKDROP_IMAGE_URL;
    }

    public Movie() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        String BASE_IMG_URL ="http://image.tmdb.org/t/p/w185/";
        String MOVIE_POSTER_URL = poster_path;
        String POSTER_PATH_URL = BASE_IMG_URL + MOVIE_POSTER_URL;
        this.poster_path = POSTER_PATH_URL;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getPlot_synopsis() {
        return plot_synopsis;
    }

    public void setPlot_synopsis(String plot_synopsis) {
        this.plot_synopsis = plot_synopsis;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(poster_path);
        dest.writeString(vote_average);
        dest.writeString(plot_synopsis);
        dest.writeString(release_date);
        dest.writeString(backdrop_path);
    }


    // Creator
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in){
            return new Movie(in);
            }

        public Movie[] newArray(int size) {
            return new Movie[size];
            }
        };

        // "De-parcel object
        public Movie(Parcel in) {
            id = in.readString();
            title = in.readString();
            poster_path = in.readString();
            vote_average = in.readString();
            plot_synopsis = in.readString();
            release_date = in.readString();
            backdrop_path = in.readString();
        }

}
