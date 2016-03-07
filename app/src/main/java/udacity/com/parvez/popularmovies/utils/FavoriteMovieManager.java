package udacity.com.parvez.popularmovies.utils;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;

import udacity.com.parvez.popularmovies.data.FavoriteMovieContract;
import udacity.com.parvez.popularmovies.data.FavoriteMovieDatabase;
import udacity.com.parvez.popularmovies.data.FavoriteMovieProvider;

/**
 * Created by parvez on 5/3/16.
 */
public class FavoriteMovieManager {

    private static final SQLiteQueryBuilder sQueryBuilder;
    private static FavoriteMovieManager sMoviesManager;

    static {
        sQueryBuilder = new SQLiteQueryBuilder();
        sQueryBuilder.setTables(FavoriteMovieDatabase.MOVIES);
    }

    private final ContentResolver mContentResolver;


    private FavoriteMovieManager(Context context) {
        mContentResolver = context.getContentResolver();
    }

    public static FavoriteMovieManager create(Context context) {
        if (sMoviesManager == null) {
            sMoviesManager = new FavoriteMovieManager(context);
        }
        return sMoviesManager;
    }


    public void insertData(Movie movie) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                FavoriteMovieProvider.Movies.CONTENT_URI);
        builder.withValue(FavoriteMovieContract.MOVIE_ID, movie.getId());
        builder.withValue(FavoriteMovieContract.PLOT_SYNOPSIS, movie.getPlot_synopsis());
        builder.withValue(FavoriteMovieContract.POSTER_PATH, movie.getPoster_path());
        builder.withValue(FavoriteMovieContract.RELEASE_DATE, movie.getRelease_date());
        builder.withValue(FavoriteMovieContract.TITLE, movie.getTitle());
        builder.withValue(FavoriteMovieContract.VOTE_AVERAGE, movie.getVote_average());
        builder.withValue(FavoriteMovieContract.POSTER_RESOURCE, movie.getPoster_path());
        builder.withValue(FavoriteMovieContract.BACKDROP_IMAGE, movie.getBackdrop_path());
        batchOperations.add(builder.build());
        try {
            mContentResolver.applyBatch(FavoriteMovieProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e("LOG_TAG", "Error applying batch insert", e);

        }
    }


    public void deleteFavourite(Movie movie) {
        mContentResolver.delete(FavoriteMovieProvider.Movies.CONTENT_URI
                , FavoriteMovieContract.MOVIE_ID + " = " + movie.getId(), null);

    }

    public boolean isFavorite(Movie movie) {
        // Defines a string to contain the selection clause
        String mSelectionClause = FavoriteMovieContract.MOVIE_ID + " = ?";
        // Initializes an array to contain selection arguments
        String[] mSelectionArgs = {""};
        Log.d("is fav",movie.getTitle());
        mSelectionArgs[0] = movie.getId().toString();

        Cursor cursor = mContentResolver.query(
                FavoriteMovieProvider.Movies.CONTENT_URI, null, mSelectionClause, mSelectionArgs, null);

        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }


    public Cursor getfavoriteMovies(){
        Cursor cursor = mContentResolver.query(
                FavoriteMovieProvider.Movies.CONTENT_URI, null, null, null, null);
        return cursor;

    }


}


