package hamzagupur.facilityentranceprofiling;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pass.Spass;
import com.samsung.android.sdk.pass.SpassFingerprint;
import com.samsung.android.sdk.pass.SpassInvalidStateException;

public class FingerPrintAuthenticaiton extends AppCompatActivity implements Handler.Callback {


    private Spass mSpass;
    private ImageView image;
    private ImageView image2;
    private ListView mListView;
    private List<String> mItemArray = new ArrayList<String>();
    private ArrayAdapter<String> mListAdapter;
    private ArrayList<Integer> designatedFingers = null;
    private ArrayList<Integer> temp = null;
    private ArrayList<Integer> designatedFingersDialog = null;


    private boolean needRetryIdentify = false;
    private boolean onReadyIdentify = false;
    private boolean onReadyEnroll = false;
    private boolean hasRegisteredFinger = false;
    private SpassFingerprint mSpassFingerprint;
    private Spass spass;
    private Context mContext;
    boolean isFeatureEnabled = false;
    public long startTime;
    public long endTime;


    private boolean isFeatureEnabled_fingerprint = false;
    private boolean isFeatureEnabled_index = false;
    private boolean isFeatureEnabled_uniqueId = false;
    private boolean isFeatureEnabled_custom = false;
    private boolean isFeatureEnabled_backupPw = false;

    private Handler mHandler;
    private static final int MSG_AUTH = 1000;
    private static final int MSG_AUTH_UI_WITH_PW = 1001;
    private static final int MSG_AUTH_UI_WITHOUT_PW = 1002;
    private static final int MSG_CANCEL = 1003;
    private static final int MSG_REGISTER = 1004;
    private static final int MSG_GET_NAME = 1005;
    private static final int MSG_GET_UNIQUEID = 1006;
    private static final int MSG_AUTH_INDEX = 1007;
    private static final int MSG_AUTH_UI_INDEX = 1008;
    private static final int MSG_AUTH_UI_CUSTOM_LOGO = 1009;

    private static final int MSG_AUTH_UI_CUSTOM_DISMISS = 1011;
    private static final int MSG_AUTH_UI_CUSTOM_BUTTON_STANDBY = 1012;


    private Button mButton_Register;
    private Button mButton_GetName;
    private Button mButton_GetUnique;
    public static boolean reg=false;
    private Button mButton_Auth_Index;
    private Button mButton_Auth_UI_Index;
    private Button mButton_Auth_UI_Custon_logo;
    public ValueAnimator animator;
    AnimationDrawable rocketAnimation;
    AnimationDrawable rocketAnimation2;

    private SparseArray<Button> mButtonList = null;

    private BroadcastReceiver mPassReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            final String action = intent.getAction();
            if (SpassFingerprint.ACTION_FINGERPRINT_RESET.equals(action)) {
                Toast.makeText(mContext, "all fingerprints are removed", Toast.LENGTH_SHORT).show();
            } else if (SpassFingerprint.ACTION_FINGERPRINT_REMOVED.equals(action)) {
                int fingerIndex = intent.getIntExtra("fingerIndex", 0);
                Toast.makeText(mContext, fingerIndex + " fingerprints is removed", Toast.LENGTH_SHORT).show();
            } else if (SpassFingerprint.ACTION_FINGERPRINT_ADDED.equals(action)) {
                int fingerIndex = intent.getIntExtra("fingerIndex", 0);
                Toast.makeText(mContext, fingerIndex + " fingerprints is added", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SpassFingerprint.ACTION_FINGERPRINT_RESET);
        filter.addAction(SpassFingerprint.ACTION_FINGERPRINT_REMOVED);
        filter.addAction(SpassFingerprint.ACTION_FINGERPRINT_ADDED);
        mContext.registerReceiver(mPassReceiver, filter);
    };

    private void unregisterBroadcastReceiver() {
        try {
            if (mContext != null) {
                mContext.unregisterReceiver(mPassReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetAll() {
        designatedFingers = null;
        needRetryIdentify = false;
        onReadyIdentify = false;
        onReadyEnroll = false;
        hasRegisteredFinger = false;
    }

    private SpassFingerprint.IdentifyListener mIdentifyListener = new SpassFingerprint.IdentifyListener() {
        @Override
        public void onFinished(int eventStatus) {
            log("identify finished : reason =" + getEventStatusName(eventStatus));
            int FingerprintIndex = 0;
            String FingerprintGuideText = null;
            try {
                FingerprintIndex = mSpassFingerprint.getIdentifiedFingerprintIndex();
            } catch (IllegalStateException ise) {
                log(ise.getMessage());
            }
            if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS) {

                endTime   = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                Log.d(this.getClass().getName(), "Fingerprint Authentication Process Time: "+totalTime);
                finish();
                Intent myIntent = new Intent(FingerPrintAuthenticaiton.this, Auth_OK.class);
                FingerPrintAuthenticaiton.this.startActivity(myIntent);

            } else if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS) {
                log("onFinished() : Password authentification Success");
            } else if (eventStatus == SpassFingerprint.STATUS_OPERATION_DENIED) {
                log("onFinished() : Authentification is blocked because of fingerprint service internally.");
            } else if (eventStatus == SpassFingerprint.STATUS_USER_CANCELLED) {
                log("onFinished() : User cancel this identify.");
            } else if (eventStatus == SpassFingerprint.STATUS_TIMEOUT_FAILED) {
                log("onFinished() : The time for identify is finished.");
            } else if (eventStatus == SpassFingerprint.STATUS_QUALITY_FAILED) {
                log("onFinished() : Authentification Fail for identify.");
                needRetryIdentify = true;
                FingerprintGuideText = mSpassFingerprint.getGuideForPoorQuality();
                Toast.makeText(mContext, FingerprintGuideText, Toast.LENGTH_SHORT).show();
            } else {
                log("onFinished() : Authentification Fail for identify");
                needRetryIdentify = true;
            }
            if (!needRetryIdentify) {
                resetIdentifyIndex();
            }
        }

        @Override
        public void onReady() {
            log("identify state is ready");
        }

        @Override
        public void onStarted() {
            log("User touched fingerprint sensor");
        }

        @Override
        public void onCompleted() {
            log("the identify is completed");
            onReadyIdentify = false;
            if (needRetryIdentify) {
                needRetryIdentify = false;
                mHandler.sendEmptyMessageDelayed(MSG_AUTH, 100);
            }
        }
    };

    private SpassFingerprint.IdentifyListener mIdentifyListenerDialog = new SpassFingerprint.IdentifyListener() {
        @Override
        public void onFinished(int eventStatus) {
            log("identify finished : reason =" + getEventStatusName(eventStatus));
            int FingerprintIndex = 0;
            boolean isFailedIdentify = false;
            onReadyIdentify = false;
            try {
                FingerprintIndex = mSpassFingerprint.getIdentifiedFingerprintIndex();
            } catch (IllegalStateException ise) {
                log(ise.getMessage());
            }
            if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS) {
                endTime   = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                Log.d(this.getClass().getName(), "Fingerprint Authentication Process Time: "+totalTime);
                finish();
                Intent myIntent = new Intent(FingerPrintAuthenticaiton.this, Auth_OK.class);
                FingerPrintAuthenticaiton.this.startActivity(myIntent);


            } else if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS) {
                log("onFinished() : Password authentification Success");
            } else if (eventStatus == SpassFingerprint.STATUS_USER_CANCELLED
                    || eventStatus == SpassFingerprint.STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE) {
                log("onFinished() : User cancel this identify.");
            } else if (eventStatus == SpassFingerprint.STATUS_TIMEOUT_FAILED) {
                log("onFinished() : The time for identify is finished.");
            } else if (!mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT_AVAILABLE_PASSWORD)) {
                if (eventStatus == SpassFingerprint.STATUS_BUTTON_PRESSED) {
                    log("onFinished() : User pressed the own button");
                    Toast.makeText(mContext, "Please connect own Backup Menu", Toast.LENGTH_SHORT).show();
                }
            } else {
                log("onFinished() : Authentification Fail for identify");
                isFailedIdentify = true;
            }
            if (!isFailedIdentify) {
                resetIdentifyIndexDialog();
            }
        }

        @Override
        public void onReady() {
            log("identify state is ready");
        }

        @Override
        public void onStarted() {
            log("User touched fingerprint sensor");
        }

        @Override
        public void onCompleted() {
            log("the identify is completed");
        }
    };
    private SpassFingerprint.RegisterListener mRegisterListener = new SpassFingerprint.RegisterListener() {
        @Override
        public void onFinished() {
            onReadyEnroll = false;
            log("RegisterListener.onFinished()");
        }
    };

    private static String getEventStatusName(int eventStatus) {
        switch (eventStatus) {
            case SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS:
                return "STATUS_AUTHENTIFICATION_SUCCESS";
            case SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS:
                return "STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS";
            case SpassFingerprint.STATUS_TIMEOUT_FAILED:
                return "STATUS_TIMEOUT";
            case SpassFingerprint.STATUS_SENSOR_FAILED:
                return "STATUS_SENSOR_ERROR";
            case SpassFingerprint.STATUS_USER_CANCELLED:
                return "STATUS_USER_CANCELLED";
            case SpassFingerprint.STATUS_QUALITY_FAILED:
                return "STATUS_QUALITY_FAILED";
            case SpassFingerprint.STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE:
                return "STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE";
            case SpassFingerprint.STATUS_BUTTON_PRESSED:
                return "STATUS_BUTTON_PRESSED";
            case SpassFingerprint.STATUS_OPERATION_DENIED:
                return "STATUS_OPERATION_DENIED";
            case SpassFingerprint.STATUS_AUTHENTIFICATION_FAILED:
            default:
                return "STATUS_AUTHENTIFICATION_FAILED";
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_AUTH:
                startIdentify();
                break;
            case MSG_AUTH_UI_WITH_PW:
                startIdentifyDialog(true);
                break;
            case MSG_AUTH_UI_WITHOUT_PW:
                startIdentifyDialog(false);
                break;
            case MSG_CANCEL:
                cancelIdentify();
                break;
            case MSG_REGISTER:
                registerFingerprint();
                break;
            case MSG_GET_NAME:
                getFingerprintName();
                break;
            case MSG_GET_UNIQUEID:
                getFingerprintUniqueID();
                break;
            case MSG_AUTH_INDEX:
                makeIdentifyIndex(1);
                startIdentify();
                break;
            case MSG_AUTH_UI_INDEX:
                startTime = System.currentTimeMillis();
                setDialogTitleAndLogo();
                int x = LoginActivity.UserID;
                makeIdentifyIndexDialog(1+x);
                //makeIdentifyIndexDialog(3);
                startIdentifyDialog(false);
                break;
            case MSG_AUTH_UI_CUSTOM_LOGO:
                setDialogTitleAndLogo();
                startIdentifyDialog(false);
                break;
            case MSG_AUTH_UI_CUSTOM_DISMISS:
                setDialogTitleAndDismiss();
                startIdentifyDialog(false);
                break;
            case MSG_AUTH_UI_CUSTOM_BUTTON_STANDBY:
                setDialogButtonAndStandbyText();
                startIdentifyDialog(false);
                break;
        }
        return true;
    }

    private void startIdentify() {
        if (onReadyIdentify == false) {
            try {
                onReadyIdentify = true;
                if (mSpassFingerprint != null) {
                    setIdentifyIndex();
                    mSpassFingerprint.startIdentify(mIdentifyListener);
                }
                if (designatedFingers != null) {
                    log("Please identify finger to verify you with " + designatedFingers.toString() + " finger");
                } else {
                    log("Please identify finger to verify you");
                }
            } catch (SpassInvalidStateException ise) {
                onReadyIdentify = false;
                resetIdentifyIndex();
                if (ise.getType() == SpassInvalidStateException.STATUS_OPERATION_DENIED) {
                    log("Exception: " + ise.getMessage());
                }
            } catch (IllegalStateException e) {
                onReadyIdentify = false;
                resetIdentifyIndex();
                log("Exception: " + e);
            }
        } else {
            log("The previous request is remained. Please finished or cancel first");
        }
    }

    private void startIdentifyDialog(boolean backup) {
        if (onReadyIdentify == false) {
            onReadyIdentify = true;
            try {
                if (mSpassFingerprint != null) {
                    setIdentifyIndexDialog();
                    mSpassFingerprint.startIdentifyWithDialog(FingerPrintAuthenticaiton.this, mIdentifyListenerDialog, backup);
                }
                if (designatedFingersDialog != null) {
                    log("Please identify finger to verify you with " + designatedFingersDialog.toString() + " finger");
                } else {
                    log("Please identify finger to verify you");
                }
            } catch (IllegalStateException e) {
                onReadyIdentify = false;
                resetIdentifyIndexDialog();
                log("Exception: " + e);
            }
        } else {
            log("The previous request is remained. Please finished or cancel first");
        }
    }

    private void cancelIdentify() {
        if (onReadyIdentify == true) {
            try {
                if (mSpassFingerprint != null) {
                    mSpassFingerprint.cancelIdentify();
                }
                log("cancelIdentify is called");
            } catch (IllegalStateException ise) {
                log(ise.getMessage());
            }
            onReadyIdentify = false;
            needRetryIdentify = false;
        } else {
            log("Please request Identify first");
        }
    }

    private void registerFingerprint() {
        if (onReadyIdentify == false) {
            if (onReadyEnroll == false) {
                onReadyEnroll = true;
                if (mSpassFingerprint != null) {
                    mSpassFingerprint.registerFinger(FingerPrintAuthenticaiton.this, mRegisterListener);
                }
                log("Jump to the Enroll screen");
            } else {
                log("Please wait and try to register again");
            }
        } else {
            log("Please cancel Identify first");
        }
    }

    private void getFingerprintName() {
        reg=false;
        SparseArray<String> mList = null;
        //log("=Fingerprint Name=");
        if (mSpassFingerprint != null) {
            mList = mSpassFingerprint.getRegisteredFingerprintName();
        }
        if (mList == null) {
            //log("Registered fingerprint is not existed.");
        } else {
            for (int i = 0; i < mList.size(); i++) {
                int index = mList.keyAt(i);
                String name = mList.get(index);
                if(index==(1+LoginActivity.UserID))
                {
                   reg=true;
                }
               // log("index " + index + ", Name is " + name);
            }
        }
    }

    private void getFingerprintUniqueID() {
        SparseArray<String> mList = null;
        try {
            log("=Fingerprint Unique ID=");
            if (mSpassFingerprint != null) {
                mList = mSpassFingerprint.getRegisteredFingerprintUniqueID();
            }
            if (mList == null) {
                log("Registered fingerprint is not existed.");
            } else {
                for (int i = 0; i < mList.size(); i++) {
                    int index = mList.keyAt(i);
                    String ID = mList.get(index);
                    log("index " + index + ", Unique ID is " + ID);
                }
            }
        } catch (IllegalStateException ise) {
            log(ise.getMessage());
        }
    }

    private void setIdentifyIndex() {
        if (isFeatureEnabled_index) {
            if (mSpassFingerprint != null && designatedFingers != null) {
                mSpassFingerprint.setIntendedFingerprintIndex(designatedFingers);
                mSpassFingerprint.setIntendedFingerprintIndex(temp);

            }
        }
    }

    private void makeIdentifyIndex(int i) {
        if (designatedFingers == null) {
            designatedFingers = new ArrayList<Integer>();
        }
        for(int j = 0; j< designatedFingers.size(); j++){
            if(i == designatedFingers.get(j)){
                return;
            }
        }
        designatedFingers.add(i);
    }

    private void resetIdentifyIndex() {
        designatedFingers = null;
    }
    private void setIdentifyIndexDialog() {
        if (isFeatureEnabled_index) {
            if (mSpassFingerprint != null && designatedFingersDialog != null) {
                mSpassFingerprint.setIntendedFingerprintIndex(designatedFingersDialog);
            }
        }
    }

    private void makeIdentifyIndexDialog(int i) {
        if (designatedFingersDialog == null) {
            designatedFingersDialog = new ArrayList<Integer>();
        }
        for(int j = 0; j< designatedFingersDialog.size(); j++){
            if(i == designatedFingersDialog.get(j)){
                return;
            }
        }
        designatedFingersDialog.add(i);
    }

    private void resetIdentifyIndexDialog() {
        designatedFingersDialog = null;
    }
    private void setDialogTitleAndLogo() {
        if (isFeatureEnabled_custom) {
            try {
                if (mSpassFingerprint != null) {
                    mSpassFingerprint.setDialogTitle("Facility Entrance Profiling", 0x000000);
                    mSpassFingerprint.setDialogIcon(String.valueOf(R.drawable.icon1));
                }
            } catch (IllegalStateException ise) {
                log(ise.getMessage());
            }
        }
    }



    private void setDialogTitleAndDismiss() {
        if (isFeatureEnabled_custom) {
            try {
                if (mSpassFingerprint != null) {
                    mSpassFingerprint.setDialogTitle("CFacility Entrance Profiling", 0x000000);
                    mSpassFingerprint.setCanceledOnTouchOutside(true);
                }
            } catch (IllegalStateException ise) {
                log(ise.getMessage());
            }
        }
    }

    private void setDialogButtonAndStandbyText() {
        if (!isFeatureEnabled_backupPw) {
            try {
                if (mSpassFingerprint != null) {
                    mSpassFingerprint.setDialogButton("OWN BUTTON");
                    mSpassFingerprint.changeStandbyString("Touch your fingerprint or press the button for launching own menu");
                }
            } catch (IllegalStateException ise) {
                log(ise.getMessage());
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(this.getClass().getName(), "back button pressed");
            cancelIdentify();
            finish();
            Intent myIntent = new Intent(FingerPrintAuthenticaiton.this, MainScreenActivity.class);
            FingerPrintAuthenticaiton.this.startActivity(myIntent);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.header_4);
        setContentView(R.layout.activity_finger_print_authenticaiton);
        ImageView rocketImage = (ImageView) findViewById(R.id.imageView5);
        ImageView rocketImage2 = (ImageView) findViewById(R.id.imageView6);
        rocketImage.setBackgroundResource(R.drawable.anm);
        rocketImage2.setBackgroundResource(R.drawable.anm2);
        rocketAnimation = (AnimationDrawable) rocketImage.getBackground();
        rocketAnimation2 = (AnimationDrawable) rocketImage2.getBackground();
        rocketAnimation.start();
        rocketAnimation2.start();


        mContext = this;
        mListAdapter = new ArrayAdapter<String>(this, R.layout.list, mItemArray);
        mListView = (ListView)findViewById(R.id.listView1);
        mHandler = new Handler(this);

        if (mListView != null) {
            mListView.setAdapter(mListAdapter);
        }
        mSpass = new Spass();

        try {
            mSpass.initialize(FingerPrintAuthenticaiton.this);
        } catch (SsdkUnsupportedException e) {
            log("Exception: " + e);
        } catch (UnsupportedOperationException e) {
            log("Fingerprint Service is not supported in the device");
        }
        isFeatureEnabled_fingerprint = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT);

        if (isFeatureEnabled_fingerprint) {
            mSpassFingerprint = new SpassFingerprint(FingerPrintAuthenticaiton.this);
            log("Fingerprint Service is supported in the device.");
            log("SDK version : " + mSpass.getVersionName());
        } else {
            logClear();
            log("Fingerprint Service is not supported in the device.");
            return;
        }

        isFeatureEnabled_index = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT_FINGER_INDEX);
        isFeatureEnabled_custom = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT_CUSTOMIZED_DIALOG);
        isFeatureEnabled_uniqueId = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT_UNIQUE_ID);
        //isFeatureEnabled_backupPw = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT_AVAILABLE_PASSWORD);

        registerBroadcastReceiver();
        setButton();

    }

    private Button.OnClickListener onButtonClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            logClear();

            switch (v.getId()) {


                case R.id.registerFinger:
                    mHandler.sendEmptyMessage(MSG_REGISTER);
                    image2.setVisibility(View.INVISIBLE);
                    break;

                case R.id.identifyDialogWithIndex:
                   image.setVisibility(View.INVISIBLE);
                    mHandler.sendEmptyMessage(MSG_AUTH_UI_INDEX);
                    break;


            }
        }
    };

    private void setButton() {

        mButton_Register = (Button)findViewById(R.id.registerFinger);

        image = (ImageView)findViewById(R.id.imageView5);
        image2 = (ImageView)findViewById(R.id.imageView6);
        mButton_Auth_UI_Index = (Button)findViewById(R.id.identifyDialogWithIndex);



        mButtonList = new SparseArray<Button>();

        mButtonList.put(R.id.registerFinger, mButton_Register);

        mButtonList.put(R.id.identifyDialogWithIndex, mButton_Auth_UI_Index);


    }

    private void setButtonEnable() {


        if (mSpassFingerprint == null || mButtonList == null) {
            return;
        }
        try {
            hasRegisteredFinger = mSpassFingerprint.hasRegisteredFinger();
        } catch (UnsupportedOperationException e) {
            log("Fingerprint Service is not supported in the device");
        }
        if (hasRegisteredFinger) {
            log("The registered Fingerprint is existed");
        } else {
            log("Please register finger first");
        }

        final int N = mButtonList.size();

        for (int i = 0; i < N; i++) {
            int id = mButtonList.keyAt(i);
            Button button = (Button)findViewById(id);
            if (button != null) {
                getFingerprintName();
                button.setOnClickListener(onButtonClick);
                button.setTextAppearance(mContext, R.style.AppTheme);
                if (!isFeatureEnabled_fingerprint) {
                    button.setEnabled(false);

                } else if (!hasRegisteredFinger && button != mButton_Register)
                {
                    button.setEnabled(false);
                    image.setVisibility(View.INVISIBLE);
                    image2.setVisibility(View.VISIBLE);



                }

                else if (reg==true && button == mButton_Register)
                //else if (temp.get(1+LoginActivity.UserID)!=null && button == mButton_Register)
                {
                    button.setEnabled(false);
                    button.setText("You have registered Fingerprint");
                    image.setVisibility(View.VISIBLE);
                    image2.setVisibility(View.INVISIBLE);

                }

                else if (reg==true && button == mButton_Auth_UI_Index)
                //else if (temp.get(1+LoginActivity.UserID)!=null && button == mButton_Register)
                {
                    button.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Please press START IDENTIFY button", Toast.LENGTH_LONG).show();
                    image.setVisibility(View.VISIBLE);
                    image2.setVisibility(View.INVISIBLE);


                }
                else{
                    if (button == mButton_Auth_UI_Index)
                    {
                        button.setEnabled(false);
                        image.setVisibility(View.INVISIBLE);
                    }

                }



            }
        }



    }

    @Override
    protected void onResume() {
        setButtonEnable();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcastReceiver();
        resetAll();
    }



    private void log(String text) {
        final String txt = text;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // mItemArray.add(0, txt);
                mListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void logClear() {
        if (mItemArray != null) {
            mItemArray.clear();
        }
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
        }
    }
}
