package udacity.com.parvez.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
import java.util.Arrays;

public class PlaceholderFragment extends Fragment {
    private MoviePosterAdapter posterAdapter;
    private Movie[] movies;
    private ArrayList<Movie> movie_list = new ArrayList<Movie>();
    private String sort_by_saved_state = null;
    private String sort_by_saved_instance_state = null;


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
//                Log.v("LOG_TAG","onSaveInstanceState" + " movies != null and movie_list.add and size = " + movie_list.size());
                movie_list.add(movies[i]);
            }
        }
        // save bundle as parcelable array list to save instance state of movie array object;
        outState.putParcelableArrayList("movie_details", movie_list);
        // Saving sortbyorder as pref
        outState.putString("sort_by", sort_by_saved_state);
        super.onSaveInstanceState(outState);
    }


    public void updatePosterGrid(String sort_by_saved_state){
        // I think i may have overcomplicated this. suggest some better way if possible to do this
        // Run fetch poster task only if the movies array object is null ie only once since
        // activity is launched
        // If sort_by_saved_state is not null and and the saved instance of sort by order is not equal
        // to the sort by saved state then fetch the data from api

        if (movies == null || (sort_by_saved_state != null
                && sort_by_saved_instance_state != sort_by_saved_state)){

            FetchPostersTask fetchPostersTask = new FetchPostersTask();
            // get shared prefs
            fetchPostersTask.execute(sort_by_saved_state);
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        posterAdapter = new MoviePosterAdapter(getActivity(), new ArrayList<Movie>());

        // setting grid view adaptor

        GridView gridView = (GridView) rootView.findViewById(R.id.poster_grid);
        gridView.setAdapter(posterAdapter);

        // Grid view on item click listener launch detail activity screen
        // for the selected poster
        // send parcelable object through intent

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent sendIntent = new Intent(getActivity(), Detail_activity.class);
                // Parcelable object
                sendIntent.putExtra("data", movies[position]);
                startActivity(sendIntent);
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
            String api_key = "#API_KEY";

            try {
                final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORTBY_PARAM = "sort_by";
                final String API_KEY = "api_key";

                // API URL BUILDER TO FETECH JSON DATA

                Uri buildUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendQueryParameter(SORTBY_PARAM, params[0])
                        .appendQueryParameter(API_KEY, api_key)
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
            }
        }
    }

}



