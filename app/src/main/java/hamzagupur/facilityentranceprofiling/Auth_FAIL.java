package hamzagupur.facilityentranceprofiling;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class Auth_FAIL extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.header_1);
        setContentView(R.layout.activity_auth_fail);
    }

    public void OK(View v) {
        finish();
        Intent myIntent = new Intent(Auth_FAIL.this, MainScreenActivity.class);
        Auth_FAIL.this.startActivity(myIntent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(this.getClass().getName(), "back button pressed");
            finish();
            Intent myIntent = new Intent(Auth_FAIL.this, MainScreenActivity.class);
            Auth_FAIL.this.startActivity(myIntent);
        }
        return super.onKeyDown(keyCode, event);
    }


}
