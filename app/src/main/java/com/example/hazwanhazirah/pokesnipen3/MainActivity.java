package com.example.hazwanhazirah.pokesnipen3;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.*;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Messenger;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    public Location location = null;
    public Activity activity;
    public String provider = LocationManager.GPS_PROVIDER;
    public double lat, lon;
    public File file;
    public String filename = "DatabaseSnipe";
    public String[] GetDataFromFile;
    public Boolean FileExist=false;
    public static int OVERLAY_PERMISSION_REQ_CODE=1234;
    public TextView UpdateIntervalTV;
    public TextView DelayMockValTV;
        //Read text from file


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        //create file
        file = new File(this.getFilesDir().getPath(), filename);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isAccessGranted()) {
            Toast.makeText(getApplicationContext(), "Please allow PokeSnipe@N3 with usage access!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }


        if (Build.VERSION.SDK_INT > 22) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            }


        }

        FileExist =new File(this.getFilesDir().getPath() + "/" + filename).isFile();

        if(FileExist == true) {
            ReadDataFromFile();
            updateLastSaveLocationTextField();
        }
     //  if(GetDataFromFile[0] != null) {
        // Toast.makeText(getApplicationContext(), GetDataFromFile[0], Toast.LENGTH_SHORT).show();
      //  }

        //Define input value from user
        UpdateIntervalTV =(TextView) findViewById(R.id.UpdateInterval);
        DelayMockValTV = (TextView) findViewById(R.id.delayMockVal);

        UpdateIntervalTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           showInputDialogUpdateInterval(UpdateIntervalTV,"Update Interval value");
            ;}
        });

        DelayMockValTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialogUpdateInterval(DelayMockValTV,"Delay mock value");
                ;}
        });


        Button b2b = (Button) findViewById(R.id.button2);

        b2b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String GetSnipeCoordinate;

                if(FileExist == true){

                    GetSnipeCoordinate = GetDataFromFile[0];

                } else{
                    EditText SnipeCoordinate = (EditText)findViewById(R.id.editText2);

                    GetSnipeCoordinate = SnipeCoordinate.getText().toString();
                }

                Boolean CoordValid=checkCoordinateInput(GetSnipeCoordinate);

                if(CoordValid == true) {

                }else {
                    Toast.makeText(getApplicationContext(), "Latitude or Longitude provided is not valid!", Toast.LENGTH_SHORT).show();
                    return;
                }


                String[] CoordinateForSnipe = GetSnipeCoordinate.split(",");

                String[] PassingValBundle={"a","b","c","d"};

                PassingValBundle[0]=CoordinateForSnipe[0];
                PassingValBundle[1]=CoordinateForSnipe[1];
                PassingValBundle[2]=UpdateIntervalTV.getText().toString();
                PassingValBundle[3]=DelayMockValTV.getText().toString();

               // Toast.makeText(getApplicationContext(),PassingValBundle[0], Toast.LENGTH_SHORT).show();

                Bundle b = new Bundle();

                   b.putStringArray("PassVal",PassingValBundle);

//                Toast.makeText(getApplicationContext(), "Build is " + Build.VERSION.SDK_INT, Toast.LENGTH_SHORT).show();


                    Intent FloatWinIntent =new Intent(MainActivity.this,FloatingWindow.class);

                    FloatWinIntent.putExtras(b);

                    startService(FloatWinIntent);
//
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);

                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.nianticlabs.pokemongo");
                if (launchIntent != null) {

                    startActivity(launchIntent);//null pointer check in case package name was not found
                }



            }
        });

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location loc = lm.getLastKnownLocation("passive");
            if (loc != null) {
                location = loc;

            }

        } catch (Exception e) {
            Toast toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }

        //update Home Location at Apps created
        //launchLocationListener("passive");

        //create button to start floating window


     }

    @Override
    protected void onResume() {
        super.onResume();

        FileExist =new File(this.getFilesDir().getPath() + "/" + filename).isFile();

        if(FileExist == true) {
            ReadDataFromFile();
            updateLastSaveLocationTextField();
        }
    }

    protected void showInputDialogUpdateInterval(final TextView tv,final String dialogMenuText){

        //get prompt from xml
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptView=li.inflate(R.layout.inputdialog,null);
        AlertDialog.Builder alb =new AlertDialog.Builder(MainActivity.this);
        alb.setView(promptView);

        final EditText InputfromUser = (EditText)promptView.findViewById(R.id.editText3);
        final TextView DialogMenu = (TextView)promptView.findViewById(R.id.textView14);

        DialogMenu.setText(dialogMenuText);

        boolean clickedOk = false;

        //setup a dialog
        alb.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    tv.setText(InputfromUser.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // create an alert dialog
        AlertDialog alert = alb.create();
        alert.show();

    }

    private void launchLocationListener(String providerName){

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener listener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                EditText HomeCoordText =(EditText)findViewById(R.id.editText2);

                //   String HomeEditCoordtext = Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());

                HomeCoordText.setText(String.format("%.8f",location.getLatitude()) + "," + String.format("%.8f",location.getLongitude()));

                //lm.removeTestProvider(provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

        };

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

        lm.requestLocationUpdates(providerName, 60000, 0, listener);

        }   catch (Exception e) {
            Toast toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }

    }


    public void setLatLongVal(double la,double lo){

        this.lat =la;
        this.lon =lo;

    }

    public double getLat() {
        return lat;
    }

    public double getLon(){
        return lon;
    }



    protected void onDestroy(){
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        lm.removeTestProvider(provider);
        super.onDestroy();
    }


    public void ReadDataFromFile(){

        StringBuilder text = new StringBuilder();

        GetDataFromFile = new String[]{"dummy"};

        BufferedReader br = null;
        try{

            br = new BufferedReader(new FileReader(file));
            String line;
            int i=0;
            while ((line = br.readLine()) != null) {
                text.append(line);
                GetDataFromFile[i] = String.valueOf(text);
               // Log.i("Test", "text : " + text + " : end");
                text.append('\n');
                i = i + 1;
            }
        }

        catch(IOException e)
        {
            e.printStackTrace();
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkCoordinateInput(String inputCoord){

        boolean checkPass=true;

        if (inputCoord==null) {
            checkPass = false;
            return checkPass;
        } else {

            if (inputCoord.indexOf(",") == -1){

            checkPass = false;
            return checkPass;

            } else {

            String [] LatLontoCheck =inputCoord.split(",");

            if (LatLontoCheck[0].indexOf(".") == -1 || LatLontoCheck[1].indexOf(".") == -1){
                checkPass = false;
                return checkPass;

            }

            }

        }
        return checkPass;
    }


    public boolean checkPermissionIsGranted(String permission){
        int res = getApplicationContext().checkCallingPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    public void updateLastSaveLocationTextField(){

        EditText SnipeCoordinate = (EditText)findViewById(R.id.editText2);

        String CoordLastLocation = GetDataFromFile[0];

        String[] CoordLastLocationLatLon =CoordLastLocation.split(",");

        SnipeCoordinate.setText(String.format("%.12s",CoordLastLocationLatLon[0]) + "," + String.format("%.12s",CoordLastLocationLatLon[1]));
    }

    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}




