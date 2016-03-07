package udacity.com.parvez.popularmovies.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by parvez on 26/2/16.
 */

@Database(
        version = FavoriteMovieDatabase.VERSION,
        packageName = "udacity.com.parvez.popularmovies")


public final class FavoriteMovieDatabase {
    private FavoriteMovieDatabase(){}

    public static final int VERSION = 7;

    @Table(FavoriteMovieContract.class) public static final String MOVIES = "favmoviedb";

}
