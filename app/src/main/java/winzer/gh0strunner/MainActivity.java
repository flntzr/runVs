package winzer.gh0strunner;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import winzer.gh0strunner.group.FollowExtInviteFragment;
import winzer.gh0strunner.group.GroupsFragment;
import winzer.gh0strunner.run.StartRunFragment;
import winzer.gh0strunner.services.AuthenticateTokenCallback;
import winzer.gh0strunner.services.RestClient;
import winzer.gh0strunner.settings.SettingsFragment;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RestClient.authenticateToken(this, new AuthenticateTokenCallback() {
            @Override
            public void tokenAuthenticated() {
            }
        });
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction tx = fragmentManager.beginTransaction();
        switch (position) {
            case 0:
                tx.replace(R.id.container, GroupsFragment.newInstance(position + 1)).commit();
                break;
            case 1:
                tx.replace(R.id.container, StartRunFragment.newInstance(position + 1)).commit();
                break;
            case 2:
                tx.replace(R.id.container, StatisticsFragment.newInstance(position + 1)).commit();
                break;
            case 3:
                tx.replace(R.id.container, FollowExtInviteFragment.newInstance(position + 1)).commit();
                break;
            case 4:
                tx.replace(R.id.container, SettingsFragment.newInstance(position + 1)).commit();
                break;
            case 5:
                tx.replace(R.id.container, AboutFragment.newInstance(position + 1)).commit();
                break;
            case 6:
                tx.replace(R.id.container, EulaFragment.newInstance(position + 1)).commit();
                break;
            case 7:
                RestClient.logout(this);
                break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section_follow_invite);
                break;
            case 5:
                mTitle = getString(R.string.title_section4);
                break;
            case 6:
                mTitle = getString(R.string.title_section5);
                break;
            case 7:
                mTitle = getString(R.string.title_section6);
                break;
            case 8:
                mTitle = getString(R.string.title_section7);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            restoreActionBar();
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}
