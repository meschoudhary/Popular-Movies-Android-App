package udacity.com.parvez.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by parvez on 2/2/16.
 */
public class MoviePosterAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = MoviePosterAdapter.class.getSimpleName();

    public MoviePosterAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String BASE_IMG_URL ="http://image.tmdb.org/t/p/w185/";
        String MOVIE_POSTER_URL = getItem(position).getPoster_path();
        String IMAGE_URL;

        Context context = getContext();

        // IMAGE URL GENERATION
        IMAGE_URL = BASE_IMG_URL + MOVIE_POSTER_URL;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.poster_item, parent, false);
        }
        ImageView iconView = (ImageView) convertView.findViewById(R.id.poster_image);
        Picasso.with(context).load(IMAGE_URL).into(iconView);
        return convertView;
    }

}