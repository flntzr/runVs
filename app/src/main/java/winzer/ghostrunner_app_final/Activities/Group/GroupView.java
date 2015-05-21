package winzer.ghostrunner_app_final.Activities.Group;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import winzer.ghostrunner_app_final.Activities.Run.Run;
import winzer.ghostrunner_app_final.R;

public class GroupView extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_view, menu);
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

    public void goToMakeUserAdmin(View view) {
        Intent intent = new Intent(this, MakeUserAdmin.class);
        startActivity(intent);
    }

    public void goToKickUser(View view) {
        Intent intent = new Intent(this, KickUser.class);
        startActivity(intent);
    }

    public void goToRenameGroup(View view) {
        Intent intent = new Intent(this, RenameGroup.class);
        startActivity(intent);
    }

    public void goToInviteUser(View view) {
        Intent intent = new Intent(this, InviteUser.class);
        startActivity(intent);
    }

    public void goToGroupStatistics(View view) {
        Intent intent = new Intent(this, GroupStatistics.class);
        startActivity(intent);
    }

    public void goToRun(View view) {
        Intent intent = new Intent(this, Run.class);
        startActivity(intent);
    }
}
