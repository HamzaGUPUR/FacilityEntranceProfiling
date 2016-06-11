package hamzagupur.facilityentranceprofiling;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;
import com.samsung.android.sdk.pass.Spass;

public class MainScreenActivity extends AppCompatActivity {

    TextView t;
    private Spass spass;
    private Context mContext;
    boolean isFeatureEnabled = false;
    public static String NameSurname="Mr. Hacker";
    private AlphaAnimation buttonClick = new AlphaAnimation(10F, 0.5F);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.header_1);
        mContext = this;
        spass = new Spass();
        try {
            spass.initialize(mContext);
        } catch (Exception e) {
            // TODO: handle exception
        }
        isFeatureEnabled = spass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT);
        setContentView(R.layout.activity_main);
        t=(TextView)findViewById(R.id.textView);
        t.setText("Welcome "+NameSurname);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(this.getClass().getName(), "back button pressed");
            finish();
            Intent myIntent = new Intent(MainScreenActivity.this, LoginActivity.class);
            MainScreenActivity.this.startActivity(myIntent);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onClickFaceRec(View v) {
        v.startAnimation(buttonClick);
        finish();
        Intent myIntent = new Intent(MainScreenActivity.this, FaceDetection.class);
        MainScreenActivity.this.startActivity(myIntent);
    }

    public void onClickMobileAut(View v) {
        v.startAnimation(buttonClick);
        finish();
        Intent myIntent = new Intent(MainScreenActivity.this, MobileAut.class);
        MainScreenActivity.this.startActivity(myIntent);
    }

    public void onClickFingerPrint(View v) {
        v.startAnimation(buttonClick);
        if(isFeatureEnabled)
        {
            finish();
            Intent myIntent = new Intent(MainScreenActivity.this, FingerPrintAuthenticaiton.class);
            MainScreenActivity.this.startActivity(myIntent);
            //Toast.makeText(getApplicationContext(), "Please press START IDENTIFY button", Toast.LENGTH_LONG).show();
        }

        else
            Toast.makeText(getApplicationContext(), "Sorry, Your Mobile Device do not have fingerprint scanner", Toast.LENGTH_LONG).show();


    }

    public void onClickSettings(View v) {
        v.startAnimation(buttonClick);
        finish();
        Intent myIntent = new Intent(MainScreenActivity.this, Settings.class);
        MainScreenActivity.this.startActivity(myIntent);
    }

}
