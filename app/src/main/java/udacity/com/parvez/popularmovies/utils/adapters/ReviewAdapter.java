package udacity.com.parvez.popularmovies.utils.adapters;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import udacity.com.parvez.popularmovies.R;
import udacity.com.parvez.popularmovies.utils.MovieReview;

/**
 * Created by parvez on 26/2/16.
 */
public class ReviewAdapter extends ArrayAdapter<MovieReview> {
    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();

    public ReviewAdapter(Context context,  ArrayList<MovieReview> objects) {
        super(context,0,objects);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent){

        //get Movie object from the ArrayAdapter at the appropriate position
        MovieReview current = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate (R.layout.review_list_item,parent,false);
        }


        TextView review_author_textview = (TextView)convertView.findViewById(R.id.list_item_review_author_textbox);
        TextView review_content_textview = (TextView)convertView.findViewById(R.id.list_item_review_content_textbox);
        TextView review_url_textview = (TextView)convertView.findViewById(R.id.list_item_review_url_textbox);

        review_author_textview.setText(current.getReview_author());
        review_url_textview.setText(current.getReview_url());
        review_content_textview.setText(current.getContent());
        review_url_textview.setMovementMethod(LinkMovementMethod.getInstance());


        return convertView;
    }
}