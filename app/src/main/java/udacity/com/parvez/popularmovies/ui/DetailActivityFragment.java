package udacity.com.parvez.popularmovies.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import udacity.com.parvez.popularmovies.BuildConfig;
import udacity.com.parvez.popularmovies.R;
import udacity.com.parvez.popularmovies.data.FavoriteMovieContract;
import udacity.com.parvez.popularmovies.data.FavoriteMovieProvider;
import udacity.com.parvez.popularmovies.utils.FavoriteMovieManager;
import udacity.com.parvez.popularmovies.utils.Movie;
import udacity.com.parvez.popularmovies.utils.MovieReview;
import udacity.com.parvez.popularmovies.utils.MovieTrailer;
import udacity.com.parvez.popularmovies.utils.adapters.ReviewAdapter;
import udacity.com.parvez.popularmovies.utils.adapters.TrailerAdapter;
import udacity.com.parvez.popularmovies.utils.helper.HelperUtility;

/**
 * Created by parvez on 21/2/16.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private Movie movie;
    private String movie_Id;

    //details of movies
    private  TextView title_textview,
            release_date_textview,
            vote_count_textview,
            synopsis_textview;

    private  ImageView poster_imageview,
            backdrop_imageview;

    //list view - for tailers and reviews
    private ListView trailer_listView,
            review_listView;

    // favorite Button
    private FloatingActionButton favorite_button ,
            unfavorite_button,
            share_button;


    // Array list for trailers and reviews
    ArrayList<MovieTrailer> trailers = new ArrayList<MovieTrailer>();

    ArrayList<MovieReview> reviews = new ArrayList<MovieReview>();

    private static final String LOG_TAG = Detail_activity.class.getSimpleName();
    private static final int CURSOR_LOADER_ID = 0;
    private  View rootView;

    private FavoriteMovieManager favoriteMovieManager;


    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        findviewsbyid();
        Cursor c = getActivity().getContentResolver().query(FavoriteMovieProvider.Movies.CONTENT_URI,
                null, null, null, null);

        if (savedInstanceState == null)
            getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        else if (savedInstanceState != null)
            LoadfromIntent(movie);

        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (movie != null)
        {
            String mSelectionClause = FavoriteMovieContract.MOVIE_ID + " = ?";
            String[] mSelectionArgs = {""};
            mSelectionArgs[0] = movie.getId().toString();

            return new CursorLoader(getActivity(), FavoriteMovieProvider.Movies.CONTENT_URI,
                    null,
                    mSelectionClause,
                    mSelectionArgs,
                    null);
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if  (cursor.getCount() == 1)
        {
            cursor.moveToFirst();
            Loadfromdatabase(cursor);

        }
        else if(movie != null){
            LoadfromIntent(movie);

        }
        cursor.close();

    }
    // offline in case of no internet
    public void Loadfromdatabase(Cursor cursor){

        movie.setId(cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.MOVIE_ID)));
        movie.setTitle(cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.TITLE)));
        movie.setRelease_date(cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.RELEASE_DATE)));
        movie.setBackdrop_path(cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.BACKDROP_IMAGE)));
        movie.setPlot_synopsis(cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.PLOT_SYNOPSIS)));
        movie.setPoster_path(cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.POSTER_PATH)));
        movie.setVote_average(cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.VOTE_AVERAGE)));

        title_textview.setText(movie.getTitle());
        release_date_textview.setText(movie.getRelease_date());
        vote_count_textview.setText(movie.getVote_average() + "/10");
        synopsis_textview.setText(movie.getPlot_synopsis());
        if(HelperUtility.hasNetworkConnection(getActivity())){
            Picasso.with(getActivity())
                    .load(movie.getPoster_path())
                    .into(poster_imageview);

            Picasso.with(getActivity())
                    .load(movie.getBackdrop_path())
                    .into(backdrop_imageview);
        }
        else{
            setplaceholderimages();

        }
        get_data(movie);
    }

    // from preferences and internet
    public void LoadfromIntent(Movie movie){
        title_textview.setText(movie.getTitle());
        release_date_textview.setText(movie.getRelease_date());
        vote_count_textview.setText(movie.getVote_average() + "/10");
        synopsis_textview.setText(movie.getPlot_synopsis());
        if(HelperUtility.hasNetworkConnection(getActivity())){
            Picasso.with(getActivity())
                    .load(movie.getPoster_path())
                    .into(poster_imageview);

            Picasso.with(getActivity())
                    .load(movie.getBackdrop_path())
                    .into(backdrop_imageview);
        }
        else{
            setplaceholderimages();

        }
        get_data(movie);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
             movie = (Movie)arguments.getParcelable("data");
        }
        if(arguments == null )
            rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        else
            rootView = inflater.inflate(R.layout.activity_detail, container, false);

        return rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save bundle as parcelable array list to save instance state of movie array object;
        outState.putParcelable("movie_details", movie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        // if saveinstancestate is not null and movies array object is null
        // load the saved instance in the movie object
        if(savedInstancesState != null && movie == null){
            ///edit
            movie = savedInstancesState.getParcelable("movie_details");
        }
        else {
            // do nothing;
        }
        setHasOptionsMenu(true);
    }



    public void setplaceholderimages(){
        poster_imageview.setImageResource(R.drawable.test);
        backdrop_imageview.setImageResource(R.drawable.backdroptest);
        Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
    }

    void get_data(final Movie movie){
        favoriteMovieManager = FavoriteMovieManager.create(getActivity());
        Boolean fav = favoriteMovieManager.isFavorite(movie);

        favorite_button = (FloatingActionButton) getActivity().findViewById(R.id.favorite_button);
        unfavorite_button = (FloatingActionButton) getActivity().findViewById(R.id.unfavorite_button);
        share_button = (FloatingActionButton) getActivity().findViewById(R.id.share_button);


        if (fav) {
            unfavorite_button.setVisibility(View.VISIBLE);
        } else {
            favorite_button.setVisibility(View.VISIBLE);
        }

        //favorite button on click listener
        favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteMovieManager.insertData(movie);
                favorite_button.setVisibility(View.GONE);
                unfavorite_button.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Added to Favourites", Toast.LENGTH_SHORT).show();

            }
        });

        //on click listener unfavorite button
        unfavorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteMovieManager.deleteFavourite(movie);
                favorite_button.setVisibility(View.VISIBLE);
                unfavorite_button.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Delete from favourites", Toast.LENGTH_SHORT).show();

            }
        });


        //share button intent
        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT,
                                movie.getTitle() + " Watch : " + trailers.get(0).getTrailer_url());
                    intent.setType("text/plain");
                    startActivity(intent);

                }
                catch (Exception e){
                    Log.e("Log",e.toString());
                    Toast.makeText(getActivity(), "Connection Failed", Toast.LENGTH_SHORT).show();

                }
            }
        });
        //fetching trailer and review data
        if (HelperUtility.hasNetworkConnection(getActivity())) {
               try
               {
                   FetchTrailerTask fetchTrailerTask = new FetchTrailerTask();
                   FetchReviewTask fetchReviewTask = new FetchReviewTask();
                   fetchTrailerTask.execute(movie.getId());
                   fetchReviewTask.execute(movie.getId());
               }
               catch (Exception e){
                   Log.d("Exception",e.toString());
               }
        }
    }

    void onSortingChanged() {
        String mI = movie_Id;
        if (null != mI) {
            getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
        }
    }


    public void findviewsbyid(){
        title_textview = (TextView) getActivity().findViewById(R.id.textview_title);
        release_date_textview = (TextView) getActivity().findViewById(R.id.textview_release_date);
        vote_count_textview = (TextView) getActivity().findViewById(R.id.textview_rating);
        synopsis_textview = (TextView) getActivity().findViewById(R.id.textview_plot_synopsis);
        poster_imageview = (ImageView) getActivity().findViewById(R.id.imageview_poster);
        backdrop_imageview = (ImageView) getActivity().findViewById(R.id.backdropImg);
        trailer_listView = (ListView) getActivity().findViewById(R.id.list_item_trailer);
        review_listView = (ListView) getActivity().findViewById(R.id.list_item_review);

    }



    public class FetchTrailerTask extends AsyncTask<String, Void, MovieTrailer[]>{

        private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

        @Override
        protected MovieTrailer[] doInBackground(String... params) {

            if (params.length == 0)
                return null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String TrailerJsonStr = null;

            try {
                final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String API_KEY = "api_key";

                // API URL BUILDER TO FETECH JSON DATA

                Uri buildUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendEncodedPath("videos")
                        .appendQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                Log.v(LOG_TAG,buildUri.toString());
                URL url = new URL(buildUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0)
                    return null;

                TrailerJsonStr = buffer.toString();


            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }

                Log.v(LOG_TAG, TrailerJsonStr);
                try {
                    return getTrailerDataFromJson(TrailerJsonStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        // Extracting data from JSON STRING

        private MovieTrailer[] getTrailerDataFromJson(String TrailerJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String ID = "id";
            final String RESULTS= "results";
            final String KEY = "key";
            final String NAME = "name";

            String movie_Id;

            JSONObject TrailerJson = new JSONObject(TrailerJsonStr);
            movie_Id = TrailerJson.getString(ID);
            JSONArray trailer_list = TrailerJson.getJSONArray(RESULTS);
            MovieTrailer[] movieTrailer = new MovieTrailer[trailer_list.length()];


            for(int i = 0; i < trailer_list.length(); i++) {
                JSONObject trailerObject = trailer_list.getJSONObject(i);
                movieTrailer[i] = new MovieTrailer();
                movieTrailer[i].setMovie_id(movie_Id);
                movieTrailer[i].setTrailer_id(trailerObject.getString(ID));
                movieTrailer[i].setTrailer_key(trailerObject.getString(KEY));
                movieTrailer[i].setTailer_name(trailerObject.getString(NAME));
                movieTrailer[i].setTailer_key(trailerObject.getString(KEY));
            }
            return movieTrailer;

        }


        @Override
        protected void onPostExecute(MovieTrailer[] movieTrailers) {
            super.onPostExecute(movieTrailers);
            if (movieTrailers != null) {
                for(MovieTrailer movieTrailer : movieTrailers){
                    trailers.add(movieTrailer);
                }
                //setting listview adapter for trailers
                final TrailerAdapter trailerAdapter = new TrailerAdapter (getActivity(),
                        trailers);
                trailer_listView.setAdapter(trailerAdapter);

                //setting height of trailer listview dynamically
               // ListUtils.setDynamicHeight(trailer_listView);

                //Intent on click youtube
                trailer_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailerAdapter.getItem(position).getTrailer_url())));
                    }
                });

            }
        }


    }




    public class FetchReviewTask extends AsyncTask<String, Void, MovieReview[]> {

        private final String LOG_TAG = FetchReviewTask.class.getSimpleName();

        @Override
        protected MovieReview[] doInBackground(String... params) {

            if (params.length == 0)
                return null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String ReviewJsonStr = null;

            try {
                final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String API_KEY = "api_key";

                // API URL BUILDER TO FETECH JSON DATA

                Uri buildUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendEncodedPath("reviews")
                        .appendQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                        .build();
                URL url = new URL(buildUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0)
                    return null;

                ReviewJsonStr = buffer.toString();


            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
                try {
                    return getreviewDataFromJson(ReviewJsonStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        // Extracting data from JSON STRING

        private MovieReview[] getreviewDataFromJson(String ReviewJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String ID = "id";
            final String RESULTS= "results";
            final String AUTHOR = "author";
            final String CONTENT = "content";
            final String URL = "url";
            String movie_Id;

            JSONObject ReviewJson = new JSONObject(ReviewJsonStr);
            movie_Id = ReviewJson.getString(ID);
            JSONArray review_list = ReviewJson.getJSONArray(RESULTS);
            MovieReview[] movieReviews = new MovieReview[review_list.length()];
            for(int i = 0; i < review_list.length(); i++) {
                JSONObject trailerObject = review_list.getJSONObject(i);
                movieReviews[i] = new MovieReview();
                movieReviews[i].setMovie_id(movie_Id);
                movieReviews[i].setReview_author(trailerObject.getString(AUTHOR).toString());
                movieReviews[i].setReview_url(trailerObject.getString(URL).toString());
                movieReviews[i].setContent(trailerObject.getString(CONTENT).toString());
            }

            return movieReviews;

        }

        @Override
        protected void onPostExecute(MovieReview[] movieReviews) {
            super.onPostExecute(movieReviews);
            if (movieReviews != null) {
                for(MovieReview movieReview : movieReviews){
                    reviews.add(movieReview);
                }
                //setting review listview apdapter and its data

                final ReviewAdapter reviewAdapter = new ReviewAdapter (getActivity(),
                        reviews);
                review_listView.setAdapter(reviewAdapter);
            }
        }
    }



}