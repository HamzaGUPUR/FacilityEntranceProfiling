package hamzagupur.facilityentranceprofiling;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.header_1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        TextView myAwesomeTextView1 = (TextView)findViewById(R.id.textView10);
        TextView myAwesomeTextView2 = (TextView)findViewById(R.id.textView11);
        TextView myAwesomeTextView3 = (TextView)findViewById(R.id.textView12);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String versionName= pInfo.versionName;
        String v=pInfo.packageName;
        myAwesomeTextView1.setText("Facility Entrance Profiling");
        myAwesomeTextView2.setText(v);
        myAwesomeTextView3.setText("Version: "+versionName);
        //Toast.makeText(getApplicationContext(), "Further settings can be added here but it is empty for now. Please press back to return.", Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(this.getClass().getName(), "back button pressed");
            finish();
            Intent myIntent = new Intent(Settings.this, MainScreenActivity.class);
            Settings.this.startActivity(myIntent);
        }
        return super.onKeyDown(keyCode, event);
    }
}
