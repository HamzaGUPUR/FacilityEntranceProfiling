package hamzagupur.facilityentranceprofiling;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MobileAut extends AppCompatActivity {



    private LoginActivity.UserLoginTask mAuthTask = null;
    private EditText takeit;
    private TextView QuestionText;
    private EditText takeit2;
    private TextView QuestionText2;
    private boolean check = false;
    // UI references.
    private String SecretQuestion;
    private String SecretAnswer="Not Provided";
    private String SecretAnswer2="Not Provided";
    private MobileAuthentication task = null;
    private String Cont="MobileAut";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.header_2);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_aut);
        takeit = (EditText)findViewById(R.id.editText);
        takeit2 = (EditText)findViewById(R.id.editText2);
        QuestionText = (TextView) findViewById(R.id.textView8);
        QuestionText2 = (TextView) findViewById(R.id.textView9);
        QuestionText.setText(LoginActivity.secret);

        if(LoginActivity.secret2.equals("Not Provided3"))
        {
            Toast.makeText(getApplicationContext(), "Server is unreachable", Toast.LENGTH_LONG).show();
            finish();
            Intent myIntent = new Intent(MobileAut.this, Auth_FAIL.class);
            MobileAut.this.startActivity(myIntent);
        }

        if(LoginActivity.secret2.equals("Not Provided"))
        {
            QuestionText2.setVisibility(View.GONE);
            takeit2.setVisibility(View.GONE);
        }
        else
        {
            QuestionText2.setText(LoginActivity.secret2);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(this.getClass().getName(), "back button pressed");
            finish();
            Intent myIntent = new Intent(MobileAut.this, MainScreenActivity.class);
            MobileAut.this.startActivity(myIntent);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void StartAuth(View v) {
        attemptLogin();
    }


    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        check=false;
        // Reset errors.
        takeit2.setError(null);
        takeit.setError(null);


        // Store values at the time of the login attempt.
        SecretAnswer = takeit.getText().toString();
        if(!LoginActivity.secret2.equals("Not Provided"))
        {
            SecretAnswer2 = takeit2.getText().toString();
        }



        boolean cancel = false;
        View focusView = null;


        // Check for a valid email address.
        if (TextUtils.isEmpty(SecretAnswer)) {
            takeit.setError(getString(R.string.error_field_required));
            focusView = takeit;
            check =true;
            cancel = true;
        }

        if(!LoginActivity.secret2.equals("Not Provided"))
        {
            if (TextUtils.isEmpty(SecretAnswer2)) {
                takeit2.setError(getString(R.string.error_field_required));
                focusView = takeit2;
                if (check==true)
                {
                    focusView = takeit;
                }
                cancel = true;
            }
        }



        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            //finish();

            task = new MobileAuthentication(LoginActivity.Vemail, LoginActivity.Vpass, SecretAnswer, SecretAnswer2);
            task.execute((Void) null);


        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class MobileAuthentication extends AsyncTask<Void, Void, Boolean> {

        String S_username;
        String S_password;
        String S_Answer;
        String S_Answer2;
        String Status= "";
        String Status2= "";
        String Status3= "";
        String response = "";

        MobileAuthentication(String user, String pass, String answer, String answer2){
            S_username=user;
            S_password=pass;
            S_Answer=answer;
            S_Answer2=answer2;
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {

            Socket socket = null;
            try {
                socket = new Socket("192.168.137.1", 8000);


                DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
                outputToClient.writeUTF(S_username);
                outputToClient.writeUTF(S_password);
                String secret=inputFromClient.readUTF();
                String secret2=inputFromClient.readUTF();
                int ıd = inputFromClient.readInt();
                outputToClient.writeUTF(Cont);
                outputToClient.writeInt(0);
                Status=inputFromClient.readUTF();
                String temp=inputFromClient.readUTF();

                //SecretQuestion=inputFromClient.readUTF();
                outputToClient.writeUTF(S_Answer);
                outputToClient.writeUTF(S_Answer2);
                Status2=inputFromClient.readUTF();
                if(!LoginActivity.secret2.equals("Not Provided"))
                {
                    Status3=inputFromClient.readUTF();
                }






            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }finally{
                if(socket != null){

                    Log.d(this.getClass().getName(), "OUT");
                }

            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;


            if (success) {
                if(Status2.equals("1OK")) {
                    if(!LoginActivity.secret2.equals("Not Provided")) {
                        if (Status3.equals("2OK")) {
                            //showProgress(false);
                            finish();
                            Intent myIntent = new Intent(MobileAut.this, Auth_OK.class);
                            MobileAut.this.startActivity(myIntent);
                        } else {
                            takeit2.setError(getString(R.string.wrong_secret_answer));
                            takeit2.requestFocus();
                        }
                    }
                    else
                    {
                        finish();
                        Intent myIntent = new Intent(MobileAut.this, Auth_OK.class);
                        MobileAut.this.startActivity(myIntent);
                    }
                }
                else  if(Status2.equals("1FAIL")) {
                    //showProgress(false);
                    takeit.setError(getString(R.string.wrong_secret_answer));
                    takeit.requestFocus();
                }

            }
        }



    }


}
