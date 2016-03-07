package udacity.com.parvez.popularmovies.data;


import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

/**
 * Created by parvez on 26/2/16.
 */
public class FavoriteMovieContract {

    @DataType(DataType.Type.INTEGER) @AutoIncrement @PrimaryKey
        public static final String _ID = "_id";
    @DataType(DataType.Type.INTEGER) @NotNull @Unique
        public static final String MOVIE_ID= "movie_id";
    @DataType(DataType.Type.TEXT) @NotNull
        public static final String TITLE= "title";
    @DataType(DataType.Type.TEXT) @NotNull
        public static final String POSTER_PATH= "poster_path";
    @DataType(DataType.Type.TEXT) @NotNull
        public static final String RELEASE_DATE= "release_date";
    @DataType(DataType.Type.TEXT) @NotNull
        public static final String VOTE_AVERAGE= "vote_average";
    @DataType(DataType.Type.TEXT) @NotNull
        public static final String PLOT_SYNOPSIS= "plot_synopsis";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String BACKDROP_IMAGE= "backdrop_image_path";
    @DataType(DataType.Type.BLOB) @NotNull
        public static final String POSTER_RESOURCE = "poster_resource";

}
