package udacity.com.parvez.popularmovies.ui;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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
import udacity.com.parvez.popularmovies.utils.Movie;
import udacity.com.parvez.popularmovies.utils.MovieTrailer;
import udacity.com.parvez.popularmovies.utils.adapters.MoviePosterAdapter;
import udacity.com.parvez.popularmovies.utils.helper.HelperUtility;

public class PlaceholderFragment extends Fragment implements  LoaderManager.LoaderCallbacks<Cursor> {
    private MoviePosterAdapter posterAdapter;
    private Movie[] movies;
    private ArrayList<Movie> movie_list = new ArrayList<Movie>();
    private ArrayList<MovieTrailer> movieTrailerArrayList = new ArrayList<MovieTrailer>();
    private String sort_by_saved_state = null;
    private String sort_by_saved_instance_state = null;
    final String FAVORITE = "favorite";
    public static final int LOADER_ID_FAVORITE_MOVIES = 101;
    private SwipeRefreshLayout swipeRefreshLayout;


    public PlaceholderFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        // if saveinstancestate is not null and movies array object is null
        // load the saved instance in the movie object
        if(savedInstancesState != null && movies == null){
            movie_list = savedInstancesState.getParcelableArrayList("movie_details");
            sort_by_saved_instance_state = savedInstancesState.getString("sort_by");
            movies = new Movie[movie_list.size()];
            for (int i = 0 ; i < movie_list.size() ; i++){
                movies[i] = new Movie();
                movies[i] = movie_list.get(i);
            }
        }
        else {
            // do nothing;
        }
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.poster_fragment, menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (movies != null){
            movie_list.clear();
            for (int i = 0 ; i < movies.length ; i++){
                movie_list.add(movies[i]);
            }
        }
        // save bundle as parcelable array list to save instance state of movie array object;
        outState.putParcelableArrayList("movie_details", movie_list);
        // Saving sortbyorder as pref
        outState.putString("sort_by", sort_by_saved_state);
        super.onSaveInstanceState(outState);
    }





    @Override
    public void onStart() {
        super.onStart();
        // shared prefs so that while coming back from setting screen it load the data from
        // saved instances state if there is no change
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = prefs.getString(getString(R.string.pref_sort_by_key),
                getString(R.string.pref_default_sort_by));
        sort_by_saved_state = sort_by;
        updatePosterGrid(sort_by_saved_state);
    }

    public void updatePosterGrid(String sort_by_saved_state){
        if (movies == null || (sort_by_saved_state != null
                && sort_by_saved_instance_state != sort_by_saved_state)){

            FetchPostersTask fetchPostersTask = new FetchPostersTask();
            // get shared prefs

            if (sort_by_saved_state.equals(FAVORITE)) {
                getLoaderManager().restartLoader(LOADER_ID_FAVORITE_MOVIES, null,this);
            }
            else {
                fetchPostersTask.execute(sort_by_saved_state);
            }
        }
        // else movies object is not null ie its is restored by savedInstanceState load that;
        else{
            posterAdapter.clear();
            for(Movie movie : movies){
                posterAdapter.add(movie);
            }
        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), FavoriteMovieProvider.Movies.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        swipeRefreshLayout.setRefreshing(false);
        if(cursor.getCount()!=0) {
            movies = new Movie[cursor.getCount()];
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                movies[i] = new Movie();
                movies[i].setTitle(cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.TITLE)));
                movies[i].setVote_average(cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.VOTE_AVERAGE)));
                movies[i].setRelease_date(cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.RELEASE_DATE)));
                movies[i].setPoster_path(cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.POSTER_PATH)));
                movies[i].setPlot_synopsis(cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.PLOT_SYNOPSIS)));
                movies[i].setId(cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.MOVIE_ID)));
                movies[i].setBackdrop_path(cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.BACKDROP_IMAGE)));
                cursor.moveToNext();
            }
        }
        else
            posterAdapter.clear();
        cursor.close();

        if (movies != null) {
            posterAdapter.clear();
            for(Movie movie : movies){
                posterAdapter.add(movie);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        posterAdapter.clear();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        posterAdapter = new MoviePosterAdapter(getActivity(), new ArrayList<Movie>());

        // swipe to refresh grid view
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.main_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (HelperUtility.hasNetworkConnection(getActivity())) {
                    movies = null;
                    posterAdapter.clear();
                    updatePosterGrid(HelperUtility.getPreferredSorting(getActivity()));
                    //Log.v(LOG_TAG, "refreshed");
                } else {
                    Toast.makeText(getActivity(), "Network Not Available!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });


        // setting grid view adaptor

        GridView gridView = (GridView) rootView.findViewById(R.id.poster_grid);
        gridView.setAdapter(posterAdapter);

        // Grid view on item click listener launch detail activity screen
        // for the selected poster
        // send parcelable object through intent

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((Callback) getActivity())
                        .onItemSelected(movies[position]);
            }
        });

        return rootView;
    }


    public class FetchPostersTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchPostersTask.class.getSimpleName();


        @Override
        protected Movie[] doInBackground(String ... params) {
            if (params.length == 0)
                return null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String posterJsonStr = null;

            try {
                final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORTBY_PARAM = "sort_by";
                final String API_KEY = "api_key";

                // API URL BUILDER TO FETECH JSON DATA

                Uri buildUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendQueryParameter(SORTBY_PARAM, params[0])
                        .appendQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                        .build();


                URL url = new URL(buildUri.toString());
                Log.v("URL",url.toString());
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

                posterJsonStr = buffer.toString();


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
            }
            try {
                return getPosterDataFromJson(posterJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }



    // Extracting data from JSON STRING

        private Movie[] getPosterDataFromJson(String posterJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String RESULTS= "results";
            final String POSTER_PATH = "poster_path";
            final String OVERVIEW = "overview";
            final String ID = "id";
            final String TITLE = "title";
            final String VOTE_AVERAGE = "vote_average";
            final String RELEASE_DATE = "release_date";
            final String BACKDROP_PATH = "backdrop_path";


            JSONObject forecastJson = new JSONObject(posterJsonStr);
            JSONArray movie_list = forecastJson.getJSONArray(RESULTS);

            movies = new Movie[movie_list.length()];

            for(int i = 0; i < movie_list.length(); i++) {
                JSONObject movieObject = movie_list.getJSONObject(i);
                movies[i] = new Movie();
                movies[i].setId(movieObject.getString(ID));
                movies[i].setTitle(movieObject.getString(TITLE));
                movies[i].setPoster_path(movieObject.getString(POSTER_PATH));
                movies[i].setPlot_synopsis(movieObject.getString(OVERVIEW));
                movies[i].setVote_average(movieObject.getString(VOTE_AVERAGE));
                movies[i].setRelease_date(movieObject.getString(RELEASE_DATE));
                movies[i].setBackdrop_path(movieObject.getString(BACKDROP_PATH));
            }

            return movies;

        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            super.onPostExecute(movies);
            if (movies != null) {
                posterAdapter.clear();
                for(Movie movie : movies){
                    posterAdapter.add(movie);
                }
                swipeRefreshLayout.setRefreshing(false);

            }
        }
    }

    void onSortingChanged() {
        String sorting = HelperUtility.getPreferredSorting(getActivity());
        updatePosterGrid(sorting);
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         *
         * @param movie
         */
        void onItemSelected(Movie movie);
    }

}



