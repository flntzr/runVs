package winzer.ghostrunner_app_final.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import winzer.ghostrunner_app_final.Activities.Drawer.FragmentNavigationDrawer;
import winzer.ghostrunner_app_final.Activities.Drawer.TestDrawerEntry;
import winzer.ghostrunner_app_final.Activities.Group.Groups;
import winzer.ghostrunner_app_final.Activities.Options.Options;
import winzer.ghostrunner_app_final.Activities.Run.ChooseGroupDistance;
import winzer.ghostrunner_app_final.R;
import winzer.ghostrunner_app_final.Services.RestClient;


public class MainMenu extends ActionBarActivity {

    private FragmentNavigationDrawer dlDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        RestClient.authenticateToken(this);

        // Set a Toolbar to replace the ActionBar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        dlDrawer = (FragmentNavigationDrawer) findViewById(R.id.drawer_layout);
        // Setup drawer view
        dlDrawer.setupDrawerConfiguration((ListView) findViewById(R.id.lvDrawer), toolbar, R.layout.drawer_nav_item, R.id.flContent);
        // Add nav items
        dlDrawer.addNavItem("First", "First Fragment", TestDrawerEntry.class);
        // Select default
        if (savedInstanceState == null) {
            dlDrawer.selectDrawerItem(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goToGroups(View view) {
        Intent intent = new Intent(this, Groups.class);
        startActivity(intent);
    }

    public void goToChooseGroupDistance(View view) {
        Intent intent = new Intent(this, ChooseGroupDistance.class);
        startActivity(intent);
    }

    public void goToStatistics(View view) {
        Intent intent = new Intent(this, Statistics.class);
        startActivity(intent);
    }

    public void goToOptions(View view) {
        Intent intent = new Intent(this, Options.class);
        startActivity(intent);
    }

    public void goToAbout(View view) {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }
}
