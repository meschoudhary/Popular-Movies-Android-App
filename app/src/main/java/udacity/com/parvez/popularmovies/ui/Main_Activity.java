package udacity.com.parvez.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import udacity.com.parvez.popularmovies.R;
import udacity.com.parvez.popularmovies.utils.Movie;
import udacity.com.parvez.popularmovies.utils.helper.HelperUtility;


public class Main_Activity extends AppCompatActivity implements PlaceholderFragment.Callback{
    private Boolean exit = false;
    private Boolean mTwoPane;
    private String mSorting;

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSorting = HelperUtility.getPreferredSorting(this);
        if (findViewById(R.id.detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, new DetailActivityFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }
        else {
            mTwoPane = false;
        }


        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    // double back press exit
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        String sortOrder = HelperUtility.getPreferredSorting(this);
        // update the sorting Order in our second pane using the fragment manager
        if (sortOrder != null && !sortOrder.equals(mSorting)) {
            PlaceholderFragment ff = (PlaceholderFragment) getSupportFragmentManager().findFragmentById(R.id.placeholder_fragment);
            if (null != ff) {
                ff.onSortingChanged();
            }
            DetailActivityFragment df = (DetailActivityFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (null != df) {
                df.onSortingChanged();
            }
            mSorting = sortOrder;
        }
    }

    @Override
    public void onItemSelected(Movie movie) {
        if (mTwoPane) {

            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable("data",movie);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent sendIntent = new Intent(getApplicationContext(), Detail_activity.class);
            sendIntent.putExtra("data", movie);
            startActivity(sendIntent);
        }
    }

}


