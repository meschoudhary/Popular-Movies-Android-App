package udacity.com.parvez.popularmovies.utils.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import udacity.com.parvez.popularmovies.R;
import udacity.com.parvez.popularmovies.data.FavoriteMovieContract;
import udacity.com.parvez.popularmovies.data.FavoriteMovieProvider;
import udacity.com.parvez.popularmovies.utils.Movie;
import udacity.com.parvez.popularmovies.utils.helper.HelperUtility;
import udacity.com.parvez.popularmovies.utils.helper.RoundedTransformation;

/**
 * Created by parvez on 2/2/16.
 */
public class MoviePosterAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = MoviePosterAdapter.class.getSimpleName();
    private  ImageView iconView,fav_icon;
    private TextView title_textview, releasedate_textview, vote_textview;

    public MoviePosterAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = getContext();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.poster_item, parent, false);
        }

        iconView = (ImageView) convertView.findViewById(R.id.poster_image);
        fav_icon = (ImageView) convertView.findViewById(R.id.fav_icon);
        TextView title_textview = (TextView) convertView.findViewById(R.id.title_card_textview);
        TextView releasedate_textview = (TextView) convertView.findViewById(R.id.release_date_card_textview);
        TextView vote_textview = (TextView) convertView.findViewById(R.id.vote_card_textview);

        String date = getItem(position).getRelease_date();
        int pos = date.indexOf('-');
        releasedate_textview.setText(date.substring(0, pos >= 0 ? pos : 0));
        title_textview.setText(getItem(position).getTitle());
        vote_textview.setText(getItem(position).getVote_average() + "/10");

        // checking favorite form through content resolver

        String mSelectionClause = FavoriteMovieContract.MOVIE_ID + " = ?";
        // Initializes an array to contain selection arguments
        String[] mSelectionArgs = {""};
        mSelectionArgs[0] = getItem(position).getId().toString();

       Cursor cursor = context.getContentResolver().query(
                FavoriteMovieProvider.Movies.CONTENT_URI, null, mSelectionClause, mSelectionArgs, null);
        int fav = cursor.getCount();
        cursor.close();

        if (fav == 1) {
            fav_icon.setImageResource(R.drawable.ic_star_bookmark);
        } else {
            fav_icon.setImageResource(R.drawable.ic_star_border_bookmark);
        }


        // picasso
        if(HelperUtility.hasNetworkConnection(getContext())){
            Picasso.with(context)
                    .load(getItem(position).getPoster_path())
                    .fit()
                    .transform(new RoundedTransformation(5, 5))
                    .centerCrop()
                    .into(iconView);
        }
        else {
            iconView.setImageResource(R.drawable.test);
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        return convertView;

    }

}

