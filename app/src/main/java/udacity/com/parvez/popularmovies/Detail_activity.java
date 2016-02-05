package udacity.com.parvez.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Detail_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, new PlaceholderFragment())
                    .commit();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,Setting_activity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static class PlaceholderFragment extends Fragment {
        private Movie movie;
        private TextView title_textview,release_date_textview,vote_count_textview,
                synopsis_textview;
        private ImageView poster_imageview;

        public PlaceholderFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            // GET PARCELABLE DATA through INTENT From Main FRAGMENT ACTIVITY
            Bundle data = getActivity().getIntent().getExtras();
            movie = (Movie) data.getParcelable("data");


            title_textview = (TextView)getActivity().findViewById(R.id.textview_title);
            release_date_textview = (TextView)getActivity().findViewById(R.id.textview_release_date);
            vote_count_textview = (TextView)getActivity().findViewById(R.id.textview_rating);
            synopsis_textview = (TextView)getActivity().findViewById(R.id.textview_plot_synopsis);
            poster_imageview = (ImageView)getActivity().findViewById(R.id.imageview_poster);

            title_textview.setText(movie.getTitle());
            release_date_textview.setText(movie.getRelease_date());
            vote_count_textview.setText(movie.getVote_average() + "/10");
            synopsis_textview.setText(movie.getPlot_synopsis());

            // Setting poster url for picasso
            String BASE_IMG_URL ="http://image.tmdb.org/t/p/w185/";
            String MOVIE_POSTER_URL = movie.getPoster_path();
            String IMAGE_URL;

            // IMAGE URL GENERATION
            IMAGE_URL = BASE_IMG_URL + MOVIE_POSTER_URL;

            Picasso.with(getActivity()).load(IMAGE_URL).into(poster_imageview);


            return rootView;

        }




    }


}
