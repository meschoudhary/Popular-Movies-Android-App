package udacity.com.parvez.popularmovies.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import udacity.com.parvez.popularmovies.R;
import udacity.com.parvez.popularmovies.utils.MovieTrailer;
import udacity.com.parvez.popularmovies.utils.helper.RoundedTransformation;

/**
 * Created by parvez on 21/2/16.
 */
public class TrailerAdapter extends ArrayAdapter<MovieTrailer> {
    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    public TrailerAdapter(Context context,  ArrayList<MovieTrailer> objects) {
        super(context,0,objects);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent){
        //get Movie object from the ArrayAdapter at the appropriate position
        MovieTrailer current = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate (R.layout.trailer_list_item,parent,false);
        }
        TextView trailerName = (TextView)convertView.findViewById(R.id.list_item_trailer_textview);
        ImageView youtubethumbnail = (ImageView) convertView.findViewById(R.id.youtube_thumbnail);
        final String BASE_URL = "http://img.youtube.com/vi/";
        final String url = BASE_URL + getItem(position).getTailer_key() + "/0.jpg";

        Picasso
                .with(getContext())
                .load(url)
                .transform(new RoundedTransformation(10, 5))
                .fit()
                .centerCrop()
                .into(youtubethumbnail);
        trailerName.setText(current.getTailer_name());

        return convertView;
    }
}