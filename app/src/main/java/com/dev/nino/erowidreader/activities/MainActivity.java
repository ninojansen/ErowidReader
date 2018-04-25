package com.dev.nino.erowidreader.activities;



import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import com.dev.nino.erowidreader.R;
import com.dev.nino.erowidreader.fragments.SubstanceListFragment;
import com.dev.nino.erowidreader.scrapers.SubstanceListScraper;
import org.jetbrains.annotations.NotNull;

//TODO redo cache for substance list !!!!!!!!!
//TODO add more search options for subtances i.e views/amount of reports etc.. Required databese?

//TODO http://www.vogella.com/tutorials/AndroidApplicationOptimization/article.html
//TODO Add mark as read option/functionaility
//TODO add views to report tiles
//TODO Add info button to substances
//TODO add ability to remove substances?

//TODO stay where you left off/ give option
//TODO refactor code with this in mind: https://android.jlelse.eu/android-development-the-solid-principles-3b5779b105d2
//TODO https://codelabs.developers.google.com/codelabs/material-design-style/index.html?index=..%2F..%2Findex#8
//TODO https://blog.mindorks.com/android-code-style-and-guidelines-d5f80453d5c7
//TODO https://developer.android.com/training/system-ui/immersive.html

/**
 * Main activity that starts the app with nothing in the frame and a navbar + default_toolbar
 */
public class MainActivity extends AppCompatActivity {

    private ActionBarDrawerToggle mDrawerToggle;
    private static final String TAG_SUBSTANCE_LIST_FRAGMENT = "SUBSTANCE_LIST_FRAGMENT";
    /**
     * Sets up the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureToolbar();
        configureNavigationDrawer();
        /* For testing reader:
        Intent reader = new Intent(this, ReaderActivity.class);
        reader.putExtra("url", "https://erowid.org/experiences/exp.php?ID=70283");
        this.startActivity(reader);
*/
    }

    /**
     * Setup the default_toolbar
     */
    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Erowid Reader");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /**
     * Setup the navigation drawer
     */
    private void configureNavigationDrawer() {
        final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.navigation);

        //Setup the navigation bar toggle in the default_toolbar


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, (Toolbar) findViewById(R.id.main_toolbar), R.string.app_name, R.string.app_name);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        //Setup listening to the items

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NotNull MenuItem menuItem) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                int itemId = menuItem.getItemId();

                if (itemId == R.id.experiences) {
                    // Start a new fragment and commit it
                    SubstanceListFragment f = (SubstanceListFragment) fm.findFragmentByTag(TAG_SUBSTANCE_LIST_FRAGMENT);
                    // Fragment doesn't exist yet
                    if (f == null) {
                        f = new SubstanceListFragment();
                    }
                    transaction.replace(R.id.frame, f);
                    transaction.addToBackStack(TAG_SUBSTANCE_LIST_FRAGMENT);
                    transaction.commit();
                }
                // Close the drawers once done
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    /**
     * Create options menu for default_toolbar
     * @param menu menu to inflate
     * @return return super
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_toolbar_buttons, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * When the user presses back go back a fragment otherwise regular behavior
     */
    @Override
    public void onBackPressed()
    {
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }
        else {
            getFragmentManager().popBackStack();
        }
    }

    /**
     * Does action based on selected toolbar item
     * @param item chosen menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }
}
