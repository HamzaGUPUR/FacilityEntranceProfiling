package hamzagupur.facilityentranceprofiling;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FaceDetection extends AppCompatActivity {

    public ValueAnimator animator;
    AnimationDrawable rocketAnimation;
    private int count = 0;
    private Boolean Controller = false;
    private Boolean Controller2 = false;
    private String Cont ="FaceRec";
    private FaceRec task = null;
    private FaceRec task2 = null;
    private FaceRec task3 = null;
    private TakeFaceRecResult task4 = null;
    int photo;
    public static int result=0;
    public List<String> list = new ArrayList<String>();
    public static Camera mCamera;
    private CameraPreview mCameraPreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.header_3);
        setContentView(R.layout.activity_face_detection);
        ImageView rocketImage = (ImageView) findViewById(R.id.imageView2);
        rocketImage.setBackgroundResource(R.drawable.animation);
        rocketAnimation = (AnimationDrawable) rocketImage.getBackground();
        rocketAnimation.start();
        list.clear();
        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);

        final Button captureButton = (Button) findViewById(R.id.button);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureButton.setEnabled(false);
                new CountDownTimer(9000,500){

                    @Override
                    public void onFinish() {
                        Toast.makeText(getBaseContext(), "Pictures are taken", Toast.LENGTH_LONG).show();
                        //mCamera.startPreview();
                    }

                    @Override
                    public void onTick(long millisUntilFinished) {
                        //mCamera.startPreview();
                        Controller2=true;
                        Log.d(this.getClass().getName(), "SHOT");
                        mCamera.takePicture(null, null, jpegCallback);
                    }

                }.start();
           }
        });


        animator = new ValueAnimator();
        animator.setObjectValues(10, count);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                TextView sentScore = (TextView) findViewById(R.id.textView6);
                sentScore.setText(String.valueOf(animation.getAnimatedValue()));
                if (String.valueOf(animation.getAnimatedValue())=="0") {
                    if (Controller == false && Controller2 == true)
                    {
                    if(list.size()==1)
                    {
                        task = new FaceRec(LoginActivity.Vemail, LoginActivity.Vpass, 0);
                        task.execute((Void) null);
                        try {
                            task.get(9000, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                    }
                        else if(list.size()==2) {
                        task = new FaceRec(LoginActivity.Vemail, LoginActivity.Vpass, 0);
                        task.execute((Void) null);
                        try {
                            task.get(9000, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }

                        task2 = new FaceRec(LoginActivity.Vemail, LoginActivity.Vpass, 1);
                        task2.execute((Void) null);
                        try {
                            task2.get(9000, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(list.size()>=3) {
                        task = new FaceRec(LoginActivity.Vemail, LoginActivity.Vpass, 0);
                        task.execute((Void) null);
                        try {
                            task.get(9000, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }

                        task2 = new FaceRec(LoginActivity.Vemail, LoginActivity.Vpass, 1);
                        task2.execute((Void) null);
                        try {
                            task2.get(9000, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }

                        task3 = new FaceRec(LoginActivity.Vemail, LoginActivity.Vpass, 2);
                        task3.execute((Void) null);
                        try {
                            task3.get(9000, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                    }

                        File sdCard = Environment.getExternalStorageDirectory();
                        File dir = new File(sdCard.getAbsolutePath() + "/camtest");
                        if (dir.isDirectory())
                        {
                            String[] children = dir.list();
                            for (int i = 0; i < children.length; i++)
                            {
                                new File(dir, children[i]).delete();
                            }
                        }

                    Log.d(this.getClass().getName(), "START");
                    Controller=true;
                    }

                    if(list.size()==0)
                    {
                        finish();
                        Intent myIntent = new Intent(FaceDetection.this, Auth_FAIL.class);
                        FaceDetection.this.startActivity(myIntent);
                    }
                    else {

                        task4 = new TakeFaceRecResult();
                        task4.execute();
                        try {
                            task4.get(9000, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }





                    }



                }
            }
        });
        animator.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue - (endValue + startValue) * fraction);
            }
        });
        animator.setDuration(20000);
        animator.start();



    }

    /*public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            rocketAnimation.start();
            return true;
        }
        return super.onTouchEvent(event);
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(this.getClass().getName(), "back button pressed");
            animator.cancel();
            animator.end();
            finish();
            Intent myIntent = new Intent(FaceDetection.this, MainScreenActivity.class);
            FaceDetection.this.startActivity(myIntent);
        }
        return super.onKeyDown(keyCode, event);
    }

    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            //
            //
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);   // If you want front camera use this one
            //camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);   // If you want back camera use this one
            //
            //
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Camera is not reachable for now", Toast.LENGTH_LONG).show();
        }
        return camera;
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            //			 Log.d(TAG, "onShutter'd");
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            //			 Log.d(TAG, "onPictureTaken - raw");
        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            new SaveImageTask().execute(data);
            resetCam();
            //Log.d(TAG, "onPictureTaken - jpeg");
        }
    };

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/camtest");
                dir.mkdirs();

                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                list.add(fileName);
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();


                refreshGallery(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }

    }

    private void resetCam() {
        mCamera.startPreview();
        //preview.setCamera(mCamera);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class FaceRec extends AsyncTask<Void, Void, Boolean> {

        String S_username;
        String S_password;
        int S_photo;
        String Status= "";
        String response = "";

        FaceRec(String user, String pass, int photocount){
            S_username=user;
            S_password=pass;
            S_photo=photocount;
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
                outputToClient.writeInt(list.size());
                Status=inputFromClient.readUTF();
                String temp=inputFromClient.readUTF();

                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/camtest");




                    File file = new File(sdCard.getAbsolutePath() + "/camtest/" + list.get(S_photo));

                    InputStream in = new FileInputStream(file);
                    outputToClient = new DataOutputStream(socket.getOutputStream());
                    byte[] b1 = new byte[16*1024];
                    int count=0;
                    while ((count = in.read(b1)) > 0) {
                        outputToClient.write(b1, 0, count);
                    }
                    in.close();
                    outputToClient.flush();
                    socket.close();




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


    }

    public class TakeFaceRecResult extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Void... arg0) {


            DataInputStream fromServer2 = null;
            DataOutputStream toServer2 = null;
            Socket socket2 = null;
            try {
                socket2 = new Socket("192.168.137.1", 8000);
                fromServer2 = new DataInputStream(socket2.getInputStream());
                toServer2 = new DataOutputStream(socket2.getOutputStream());
                toServer2.writeUTF("FaceRecToMobile"); // sending client's asnwer to server
                toServer2.writeUTF("FaceRecToMobile"); // sending client's asnwer to server



                result = fromServer2.readInt();

                if(result==1) {
                    finish();
                    Intent myIntent = new Intent(FaceDetection.this, Auth_OK.class);
                    FaceDetection.this.startActivity(myIntent);

                }
                else if(result==0) {
                    finish();
                    Intent myIntent = new Intent(FaceDetection.this, Auth_FAIL.class);
                    FaceDetection.this.startActivity(myIntent);

                }

                socket2.close();
                toServer2.close();
                fromServer2.close();


            } catch (IOException e) {


                finish();
                Intent myIntent = new Intent(FaceDetection.this, Auth_FAIL.class);
                FaceDetection.this.startActivity(myIntent);
                //e.printStackTrace();
               }




                return true;
            }

        @Override
        protected void onPostExecute(final Boolean success) {



            if (success) {

                task4.cancel(true);
            }
            else
            {

            }
        }


        }




}
