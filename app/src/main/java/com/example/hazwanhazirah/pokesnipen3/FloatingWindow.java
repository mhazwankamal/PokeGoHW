package com.example.hazwanhazirah.pokesnipen3;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.icu.text.MessagePattern;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.RunnableFuture;

/**
 * Created by HazwanHazirah on 9/10/2016.
 */
public class FloatingWindow extends IntentService  {

    Location mLastLocation;
    LocationManager lm;
    String lat,lon;
    LocationListener listener;
    long timeMockGPS;
    private WindowManager wmDpadGPS,wmMainIconSnipe,wmSnipeMenu,wmGetMenuPanel,wmStatusBar,wmListPokestop;
    private LinearLayout llMainIconSnipe,llPokeStopList;
    private RelativeLayout rlDpadGPS,rlSnipeMenu;
    private Button stop;
    private Button start;
    private Button bhome;
    private Button cancelSnipeWin;
    private Button SnipeCoord;
    private ImageView NaviSpeed;
    private ImageView pokestopB;
    private EditText CoordPokemonToSnipe;
    private ImageView moveButton;
    private ImageView dpad;
    private ImageView Snipe;
    private ImageView HomeButton;
    private ImageView moveJoynMenuP;
    private ImageView CloseServiceButton;
    private ImageView openMoreMenu;
    private JoystickView MyJoyStick;
    private RelativeLayout dpadfl;
    private RelativeLayout mMenuPanel;
    private RelativeLayout mStaturBar;
    private TextView angelVal;
    private TextView LatLonVal;
    private ListView ListPokeStop;
    private Button dismisPwindow;
    private Button ClosePokeList;
    private boolean TimerUpdateLocRun=false;
    private double walkingSpeed;
    public String provider = LocationManager.GPS_PROVIDER;
    public String[] CoordinateSnipe;
    public Bundle b;
    public String[] CoordinateHome;
    private PopupWindow popupWindow;
    private LayoutInflater layoutinflater;
    public String[] CurrentLocation={"a","b"};
    public String [] PreviousLocation;
    public String UpdateIntervalVal;
    public String DelayMockVal;
    private boolean mainMenuShow=false;
    private boolean mainSnipeMenuShow=false;
    private Timer UpdateLocTimer;
    private int i = 0;
    public File file;
    public String filename="DatabaseSnipe";
    public Handler handler;
    public Runnable r=null;
    public Runnable checkPoGo=null;
    public boolean CheckPogoRunning=false;
    public boolean PokeSnipeONE;
    private int NaviMode = 0;
    private int NaviModeInSwitch = 0;
    public String[] PokeStopName = {
            "Silver Knight Armour",
            "Vivo City",
            "King Lious",
            "Pebble Whirlpool Water Feature",
            "Bubbleman Island",
            "Giant Red Demon",
            "Vivo City Mural",
            "Spiral Pilars at VivoCity",
            "Tropical Snowman",
            "Red and Green Bubblemen",
            "Marche at VivoCity"
    };

    public String[] PokeStopCoord ={
            "1.26456,103.82358",
            "1.26497,103.82362",
            "1.26371,103.82334",
            "1.26329,103.82309",
            "1.26346,103.82258",
            "1.26385,103.82255",
            "1.26426,103.82259",
            "1.26473,103.82296",
            "1.26331,103.82217",
            "1.26382,103.82151",
            "1.26382,103.82151"
    };


    public FloatingWindow() {
        super("FloatingWindow");
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        b=intent.getExtras();

    }



    @Override
    public void onStart(Intent intent, int startId) {

       super.onStart(intent, startId);

        b=intent.getExtras();
        String [] ReceiveVal={"a","b","c","d"};
        ReceiveVal=b.getStringArray("PassVal");


        CurrentLocation[0]=ReceiveVal[0];
        CurrentLocation[1]=ReceiveVal[1];
        UpdateIntervalVal=ReceiveVal[2];
        DelayMockVal=ReceiveVal[3];

        CoordinateHome=CurrentLocation;
        PreviousLocation =CurrentLocation;

        //Toast.makeText(getApplicationContext(),"-PokeSnipe @N3 is loading-", Toast.LENGTH_LONG).show();
        PokeSnipeONE =false;
        TimerUpdateLocRun=true;


        //add mock location
        int value = setMockLocationSettings();//toggle ALLOW_MOCK_LOCATION on
        try {
           lm.addTestProvider("gps", false, false, false, false, false, true, true, 1,1);
         lm.setTestProviderEnabled("gps",true);
        }catch (SecurityException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Allow mock location must be enabled in developer option", Toast.LENGTH_LONG).show();
            return;
        }finally{
            restoreMockLocationSettings(value);//toggle ALLOW_MOCK_LOCATION off
        }


        checkPokemonGoRunning();

    }


    public void onCreate(){


        super.onCreate();

         this.lm = ((LocationManager) getSystemService(LOCATION_SERVICE));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;


         file = new File(this.getFilesDir(),filename);

       // Toast.makeText(getApplicationContext(),"File DIR" + this.getFilesDir(), Toast.LENGTH_LONG).show();

         //set walking speed
         //walking = 0.00001
        //running =  0.00003
        //driving  = 0.00005
        walkingSpeed = 0.00003;

        wmDpadGPS=(WindowManager) getSystemService(WINDOW_SERVICE);
        wmMainIconSnipe=(WindowManager) getSystemService(WINDOW_SERVICE);
        wmSnipeMenu=(WindowManager) getSystemService(WINDOW_SERVICE);
        wmGetMenuPanel=(WindowManager) getSystemService(WINDOW_SERVICE);
        wmStatusBar = (WindowManager) getSystemService(WINDOW_SERVICE);
        wmListPokestop = (WindowManager) getSystemService(WINDOW_SERVICE);
        llMainIconSnipe =new LinearLayout(this);
        rlDpadGPS =new RelativeLayout(this);
        mMenuPanel = new RelativeLayout(this);
        stop = new Button(this);
        start=new Button (this);
        bhome =new Button(this);
        ClosePokeList = new Button(this);

        double wmIconSipex,wmIconSipey;
        int fwmIconSipex,fwmIconSipey;
        wmIconSipex =(double) screenWidth * 0.12;
        wmIconSipey = (double) screenHeight * 0.30;
        fwmIconSipex=(int) wmIconSipex;
        fwmIconSipey=(int) wmIconSipey;

        final WindowManager.LayoutParams parameterswmMainIconSnipe = new WindowManager.LayoutParams(fwmIconSipex,fwmIconSipey,WindowManager.LayoutParams.TYPE_PHONE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        parameterswmMainIconSnipe.x = -(screenWidth);
        parameterswmMainIconSnipe.y = 0;


        double wmDPadGPSx,wmDPadGPSy;
        int fwmDPadGPSx,fwmDPadGPSy;
        wmDPadGPSx =(double) screenWidth * 0.28;
        wmDPadGPSy = (double) screenHeight * 0.23;
        fwmDPadGPSx=(int) wmDPadGPSx;
        fwmDPadGPSy=(int) wmDPadGPSy;

        final WindowManager.LayoutParams parameterswmDpadGPS = new WindowManager.LayoutParams(fwmDPadGPSx,fwmDPadGPSy,WindowManager.LayoutParams.TYPE_PHONE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        parameterswmDpadGPS.x=(int) (0.41 * (double) screenWidth); //floating window position
        parameterswmDpadGPS.y=(int) (0.15 * (double) screenHeight);//floating window position

        double wmSnipeMenux,wmSnipeMenuy;
        int fwmSnipeMenux,fwmSnipeMenuy;
        wmSnipeMenux =(double) screenWidth * 0.90;
        wmSnipeMenuy = (double) screenHeight * 0.27;
        fwmSnipeMenux=(int) wmSnipeMenux;
        fwmSnipeMenuy=(int) wmSnipeMenuy;

        final WindowManager.LayoutParams parameterswmSnipeMenu = new WindowManager.LayoutParams(fwmSnipeMenux,fwmSnipeMenuy,WindowManager.LayoutParams.TYPE_PHONE,WindowManager.LayoutParams.ALPHA_CHANGED,PixelFormat.TRANSLUCENT);
        parameterswmSnipeMenu.x=0; //floating window position
        parameterswmSnipeMenu.y=0;//floating window position

        double wmMenuPanelx,wmMenuPanely;
        int fwmMenuPanelx,fwmMenuPanely;
        wmMenuPanelx =(double) screenWidth * 0.28;
        wmMenuPanely = (double) screenHeight * 0.06;
        fwmMenuPanelx=(int) wmMenuPanelx;
        fwmMenuPanely=(int) wmMenuPanely;

        final WindowManager.LayoutParams parameterswmMenuPanel = new WindowManager.LayoutParams(fwmMenuPanelx, fwmMenuPanely, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        parameterswmMenuPanel.x = (int) (0.41 * (double) screenWidth); //floating window position
        parameterswmMenuPanel.y = (int) (0.30 * (double) screenHeight);//floating window position

        double wmStatusBarx,wmStatusBary;
        int fwmStatusBarx,fwmStatusBary;
        wmStatusBarx =(double) screenWidth * 0.36;
        wmStatusBary = (double) screenHeight * 0.07;
        fwmStatusBarx=(int) wmStatusBarx;
        fwmStatusBary=(int) wmStatusBary;

        final WindowManager.LayoutParams parameterswmStatusBar = new WindowManager.LayoutParams(fwmStatusBarx, fwmStatusBary, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        parameterswmStatusBar.x = (int) (0.33 * (double) screenWidth); //floating window position
        parameterswmStatusBar.y = (int) (0.46 * (double) screenHeight);//floating window position//floating window position

        final double wmListPokeStopx,wmListPokeStopy;
        int fwmListPokeStopx,fwmListPokeStopy;
        wmListPokeStopx =(double) screenWidth * 0.7;
        wmListPokeStopy = (double) screenHeight * 0.7;
        fwmListPokeStopx=(int) wmListPokeStopx;
        fwmListPokeStopy=(int) wmListPokeStopy;

        final WindowManager.LayoutParams parameterswmPokeStopList = new WindowManager.LayoutParams(fwmListPokeStopx, fwmListPokeStopy, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        parameterswmPokeStopList.x = (int) 0; //floating window position
        parameterswmPokeStopList.y = (int) -(0.05 * (double) screenHeight);//floating window position//floating window position

        //set size of linear layout

        layoutinflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);


       //layouts ------------------------------------------------------------------------------------
       //rlDpadGPS ----------------------------------------------------------------------------------
        rlDpadGPS = (RelativeLayout) layoutinflater.inflate(R.layout.floating_joystick,null);

        //assign id from floating joystick layout
        moveButton = (ImageView) rlDpadGPS.findViewById(R.id.imageView2);
        MyJoyStick = (JoystickView) rlDpadGPS.findViewById(R.id.imageView4);
      //  dpadfl= (RelativeLayout) rlDpadGPS.findViewById(R.id.relativeFrame);


      //---------------------------------------------------------------------------------------------

      //llMainIconSnipe-------------------------------------------------------------------------------
        llMainIconSnipe=(LinearLayout) layoutinflater.inflate(R.layout.floating_snipe_icon_main,null);
        //assign id from floatin snipe icon layout
        Snipe = (ImageView) llMainIconSnipe.findViewById(R.id.imageView);
        HomeButton = (ImageView) llMainIconSnipe.findViewById(R.id.imageView5);
        NaviSpeed = (ImageView) llMainIconSnipe.findViewById(R.id.runningIcon);
        pokestopB = (ImageView) llMainIconSnipe.findViewById(R.id.pokeStopList);
      //---------------------------------------------------------------------------------------------

      //rlSnipeMenu
        rlSnipeMenu=(RelativeLayout) layoutinflater.inflate(R.layout.floating_window,null);
        SnipeCoord = (Button) rlSnipeMenu.findViewById(R.id.button1);
        CoordPokemonToSnipe=(EditText) rlSnipeMenu.findViewById(R.id.editText);
        cancelSnipeWin = (Button) rlSnipeMenu.findViewById(R.id.button);

      //rlStatusBar
        mStaturBar = (RelativeLayout) layoutinflater.inflate(R.layout.status_bar,null);
        angelVal = (TextView) mStaturBar.findViewById(R.id.textView5);
        LatLonVal = (TextView) mStaturBar.findViewById(R.id.textView9);
      //rlMainMenuPanel
        mMenuPanel = (RelativeLayout) layoutinflater.inflate(R.layout.main_menu_panel,null);
        moveJoynMenuP = (ImageView) mMenuPanel.findViewById(R.id.imageView6);
        CloseServiceButton = (ImageView) mMenuPanel.findViewById(R.id.imageView8);
        openMoreMenu = (ImageView) mMenuPanel.findViewById(R.id.imageView7);

        //llPokeStopList
        llPokeStopList = (LinearLayout) layoutinflater.inflate(R.layout.list_of_pokestop,null);
        ListPokeStop = (ListView) llPokeStopList.findViewById(R.id.listView);
        ClosePokeList = (Button) llPokeStopList.findViewById(R.id.closePokelist);
      //----------------------------------------------------------------------------------------------
        //close layouts --------------------------------------------------------------------------


        //List adapter
        ArrayAdapter adapter =new ArrayAdapter<String>(this,R.layout.activity_listofpokestop,PokeStopName);

        ListPokeStop.setAdapter(adapter);


        wmDpadGPS.addView(rlDpadGPS,parameterswmDpadGPS);
        wmStatusBar.addView(mStaturBar,parameterswmStatusBar);

        HomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        CurrentLocation = CoordinateHome;

        //angelVal.setText(String.format("%.12s",CurrentLocation[0]));
        //LatLonVal.setText(String.format("%.12s",CurrentLocation[1]));

                //get current location
              //  launchLocationListener(provider);
            }
        });

        Snipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                wmSnipeMenu.addView(rlSnipeMenu,parameterswmSnipeMenu);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                wmMainIconSnipe.removeView(llMainIconSnipe);

            }
        });

        SnipeCoord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);

               String GetSnipePokemonCoord = CoordPokemonToSnipe.getText().toString();

               String [] PokemonCoord = GetSnipePokemonCoord.split(",");

                CurrentLocation = PokemonCoord;

                //angelVal.setText(String.format("%.12s",CurrentLocation[0]));
                //LatLonVal.setText(String.format("%.12s",CurrentLocation[1]));

                wmSnipeMenu.removeView(rlSnipeMenu);
                stopSelf();
                wmMainIconSnipe.addView(llMainIconSnipe,parameterswmMainIconSnipe);

            }
        });

        cancelSnipeWin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wmSnipeMenu.removeView(rlSnipeMenu);
                stopSelf();
                wmMainIconSnipe.addView(llMainIconSnipe,parameterswmMainIconSnipe);
            }
        });

        openMoreMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mainSnipeMenuShow == false){

                wmSnipeMenu.addView(llMainIconSnipe,parameterswmMainIconSnipe);
                mainSnipeMenuShow=true;
                }else{
                wmSnipeMenu.removeView(llMainIconSnipe);
                    mainSnipeMenuShow=false;
                }
            }
        });

        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


              //  Toast.makeText(getApplicationContext(),dpad.getVisibility(), Toast.LENGTH_LONG).show();

                if(mainMenuShow == false) {
                    wmGetMenuPanel.addView(mMenuPanel, parameterswmMenuPanel);
                    mainMenuShow = true;
                } else{
                    wmGetMenuPanel.removeView(mMenuPanel);
                    mainMenuShow =false;
                }
            }
        });


        llMainIconSnipe.setOnTouchListener(new View.OnTouchListener(){

            private WindowManager.LayoutParams updatedParameters = parameterswmMainIconSnipe;
            int x, y;
            float touchedX,touchedY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:

                        x = updatedParameters.x;
                        y = updatedParameters.y;

                        touchedX = event.getRawX();
                        touchedY = event.getRawY();

                        break;

                    case MotionEvent.ACTION_MOVE:

                        updatedParameters.x=(int)(x + (event.getRawX() - touchedX));
                        updatedParameters.y=(int)(y + (event.getRawY() - touchedY));

                        wmMainIconSnipe.updateViewLayout(llMainIconSnipe,updatedParameters);


                    default:
                        break;

                }

                return false;
            }
        });


        moveJoynMenuP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        CloseServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(llMainIconSnipe.getParent() !=null) wmMainIconSnipe.removeView(llMainIconSnipe);
               if(rlDpadGPS.getParent() !=null) wmDpadGPS.removeView(rlDpadGPS);
               if(mMenuPanel.getParent() !=null) wmGetMenuPanel.removeView(mMenuPanel);
               if(mStaturBar.getParent() !=null) wmStatusBar.removeView(mStaturBar);

                //saveLastLocationCoord
                writeInFile(CurrentLocation);

                CheckPogoRunning = false;
                TimerUpdateLocRun=false;
                Toast.makeText(getApplicationContext(),"Close PokeSnipe @N3", Toast.LENGTH_LONG).show();
                stopSelf();

                removeListener();


            }
        });

        moveJoynMenuP.setOnTouchListener(new View.OnTouchListener(){

            private WindowManager.LayoutParams updatedParameters = parameterswmDpadGPS;
            private WindowManager.LayoutParams updatedParameters2 = parameterswmMenuPanel;
            int x, y,x1,y1;
            float touchedX,touchedY;



            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:

                        x = updatedParameters.x;
                        y = updatedParameters.y;
                        x1 = updatedParameters2.x;
                        y1 = updatedParameters2.y;

                        touchedX = event.getRawX();
                        touchedY = event.getRawY();

                        break;

                    case MotionEvent.ACTION_MOVE:

                        updatedParameters.x=(int)(x + (event.getRawX() - touchedX));
                        updatedParameters.y=(int)(y + (event.getRawY() - touchedY));

                        updatedParameters2.x=(int) (x1 + (event.getRawX() - touchedX));
                        updatedParameters2.y=(int) (y1 + (event.getRawY() - touchedY));

                        wmDpadGPS.updateViewLayout(rlDpadGPS,updatedParameters);
                        wmGetMenuPanel.updateViewLayout(mMenuPanel,updatedParameters2);

                    default:
                        break;

                }

                return false;
            }
        });



        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


            }
        });

        stop.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });



        MyJoyStick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override

            public void onMove(int angle, int strength) {

                //TimerUpdateLocRun=false;

                //if angle = 0    lat = 0 long = 1
                //if angle = 90   lat = 1 long = 0
                //if angle = 180  lat = 0 long = -1
                //if angle = 270  lat = -1 long =0

                double lat=0.00,lon=0.00;
                float ratioNavigate=0;

                if (-1 < angle && angle < 90) {

                    if (angle == 0) {
                        ratioNavigate = 1;
                    } else {
                        ratioNavigate = angle / 90.0f;
                    }

                    //if ratio == 1

                    if (ratioNavigate == 1) {
                        lat = 0;
                        lon = ratioNavigate * walkingSpeed;
                    } else {
                        lat = ratioNavigate * walkingSpeed;
                        lon = (1 - ratioNavigate) * walkingSpeed;
                    }

               }

                else if(89 < angle && angle < 180){
                   if(angle == 90) {
                       ratioNavigate =1;
                   }else{
                       ratioNavigate = (angle - 90) / 90.0f;
                   }

                    if (ratioNavigate == 1) {
                        lat = ratioNavigate * walkingSpeed;
                        lon = 0;
                    }else{
                        lat = (1-ratioNavigate) * walkingSpeed;
                        lon = -(ratioNavigate * walkingSpeed);
                    }


                }

                else if (179 < angle && angle < 270){

                    if(angle == 180){
                        ratioNavigate=1;
                    }else{
                        ratioNavigate =(angle - 180) /90.0f;
                    }

                    if(ratioNavigate==1){
                        lon = -(ratioNavigate * walkingSpeed);
                        lat = 0;
                    }else{
                        lat = -(ratioNavigate * walkingSpeed);
                        lon = -((1-ratioNavigate) * walkingSpeed);
                    }
                }

                else if(269 < angle && angle < 360){

                    if(angle ==270){
                        ratioNavigate=1;
                    }else{
                        ratioNavigate=(angle-270) / 90.0f;
                    }

                    if(ratioNavigate==1.0){
                        lat=-(ratioNavigate * walkingSpeed);
                        lon=0;
                    }else{
                        lon=ratioNavigate * walkingSpeed;
                        lat=-((1 - ratioNavigate) * walkingSpeed);
                    }
                }


                Double moveLocationX = Double.parseDouble(CurrentLocation[0]) + (lat);
                Double moveLocationY = Double.parseDouble(CurrentLocation[1]) + (lon);

                CurrentLocation[0] =String.valueOf(moveLocationX);
                CurrentLocation[1] = String.valueOf(moveLocationY);
            }


        },800);


       //Set icon for walking speed
        NaviSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (NaviMode){

                    case 0:
                        walkingSpeed = 0.00009;
                        NaviSpeed.setImageResource(R.drawable.running);
                        NaviModeInSwitch=1;
                        break;

                    case 1:
                        walkingSpeed = 0.0003;
                        NaviSpeed.setImageResource(R.drawable.driving);
                        NaviModeInSwitch=2;
                        break;
                    case 2:

                    walkingSpeed = 0.00003;
                    NaviSpeed.setImageResource(R.drawable.walking);
                    NaviModeInSwitch=0;
                    break;
                    default:
                }
                NaviMode=NaviModeInSwitch;
            }



        });

        pokestopB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wmListPokestop.addView(llPokeStopList,parameterswmPokeStopList);
                wmMainIconSnipe.removeView(llMainIconSnipe);

            }
        });

        ClosePokeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wmListPokestop.removeView(llPokeStopList);
                stopSelf();
                wmMainIconSnipe.addView(llMainIconSnipe,parameterswmMainIconSnipe);

            }
        });

        ListPokeStop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String GetPokeStopCoord = PokeStopCoord[position];

                String [] PokeStopCoordinate = GetPokeStopCoord.split(",");

                CurrentLocation=PokeStopCoordinate;

                Toast.makeText(getApplicationContext(),"Going to PokeStop :[" + ((TextView) view).getText().toString() + "]", Toast.LENGTH_SHORT).show();

                wmListPokestop.removeView(llPokeStopList);
                stopSelf();
                wmMainIconSnipe.addView(llMainIconSnipe,parameterswmMainIconSnipe);

            }
        });
    }



    private void SetCurrentLocation(){

       //add mock location
        try {

   //         lm.addTestProvider(provider, false, false, false, false, false, true, true, 0, 5);

        }catch (SecurityException e){
            //
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Allow mock location must be enabled in developer option", Toast.LENGTH_LONG).show();
            return;
        }

     //   lm.setTestProviderEnabled(provider, true);
      //  lm.setTestProviderLocation(provider, location);

        angelVal.setText(String.format("%.12s",CurrentLocation[0]));
        LatLonVal.setText(String.format("%.12s",CurrentLocation[1]));
    }



    private void launchLocationListener() {

    //    final String providerLocList = providerName;
      //  final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

         listener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {



                try{
                   restoreMockLocationSettings(0);

                }catch (Exception e){
                    e.printStackTrace();
                }

//                Toast.makeText(getApplicationContext(), "Loc : " + String.format("%.8f", location.getLatitude()) + "," + String.format("%.8f", location.getLongitude()), Toast.LENGTH_SHORT).show();
                CurrentLocation[0] = String.valueOf(location.getLatitude());
                CurrentLocation[1] = String.valueOf(location.getLongitude());

                angelVal.setText(String.format("%.12s", CurrentLocation[0]));
                LatLonVal.setText(String.format("%.12s", CurrentLocation[1]));


                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    removeListener();
                    lm.removeUpdates(listener);

                } catch (Exception e) {
                    e.printStackTrace();
                }



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

            lm.requestLocationUpdates("gps", 1000, 0, listener);

        } catch (Exception e) {
            Toast toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    private int setMockLocationSettings() {
        int value = 1;
        try {
            value = Settings.Secure.getInt(getContentResolver(),
                    Settings.Secure.ALLOW_MOCK_LOCATION);
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.ALLOW_MOCK_LOCATION, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private void restoreMockLocationSettings(int restore_value) {
        try {
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.ALLOW_MOCK_LOCATION, restore_value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeListener(){

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }


            lm.removeUpdates(listener);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void writeInFile(String[] Coord){

        FileOutputStream outputStream;

        String LastCoord;
        LastCoord=Coord[0] + "," + Coord[1];

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(LastCoord.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private void checkPokemonGoRunning (){

        CheckPogoRunning=true;

        handler = new Handler();

        checkPoGo = new Runnable() {
            int delay=Integer.parseInt(UpdateIntervalVal);



            @Override
            public void run() {
                handler.postDelayed(this,delay);



                String CurrentApp="";

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    // intentionally using string value as Context.USAGE_STATS_SERVICE was
                    // strangely only added in API 22 (LOLLIPOP_MR1)
                    @SuppressWarnings("WrongConstant")
                    UsageStatsManager usm = (UsageStatsManager) getSystemService("usagestats");
                    long time = System.currentTimeMillis();
                    List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                            time - 1000 * 1000, time);

                    if (appList != null && appList.size() > 0) {

                        SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                        for (UsageStats usageStats : appList) {
                            mySortedMap.put(usageStats.getLastTimeUsed(),
                                    usageStats);

                        }
                        if (mySortedMap != null && !mySortedMap.isEmpty()) {
                            CurrentApp = mySortedMap.get(
                                    mySortedMap.lastKey()).getPackageName();

                        }
                    }
                } else {
                    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningAppProcessInfo> tasks = am
                            .getRunningAppProcesses();
                    CurrentApp = tasks.get(0).processName;
                }

                if (CurrentApp.equals("com.nianticlabs.pokemongo")){
                    //if (PokeSnipeONE == false) {
                    if(Arrays.equals(PreviousLocation,CurrentLocation)){
                        delay=1500;
                    }else{
                        delay=Integer.parseInt(UpdateIntervalVal);
                    }
                        runUpdateMockLocation();
                    //}


                    //Toast.makeText(getApplicationContext(),"Curr App :" + CurrentApp + " delay : " + delay + " SnipeOne " + PokeSnipeONE + " UpdateMock " + TimerUpdateLocRun, Toast.LENGTH_SHORT).show();

                }else{
//                    PokeSnipeONE=false;

                    delay=2000;
                    //Toast.makeText(getApplicationContext(),"Curr App :" + CurrentApp + " delay : " + delay + " check " + PokeSnipeONE, Toast.LENGTH_SHORT).show();

                }

                if (CheckPogoRunning == false){
                    handler.removeCallbacks(checkPoGo);
                }
            }
        };

        handler.postDelayed(checkPoGo,1000);
    }

    private void runUpdateMockLocation (){
        //PokeSnipeONE=true;

        //handler = new Handler();

        //r = new Runnable() {
         //   @Override
          //  public void run() {
            //    handler.postDelayed(this,6000);

           // LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //set previous location



            Location location = new Location("gps");

            location.setLatitude(Double.parseDouble(CurrentLocation[0]));
            location.setLongitude(Double.parseDouble(CurrentLocation[1]));
            location.setAccuracy(100.0f);
            location.setAltitude(9.0);
            timeMockGPS =System.currentTimeMillis()+ Long.parseLong(DelayMockVal);
            location.setTime(timeMockGPS);
            location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());

        //add mock location
        int value = setMockLocationSettings();//toggle ALLOW_MOCK_LOCATION on
       try {

           lm.setTestProviderLocation("gps", location);

       }catch (SecurityException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Allow mock location must be enabled in developer option", Toast.LENGTH_LONG).show();
            return;
        }finally{
            restoreMockLocationSettings(value);//toggle ALLOW_MOCK_LOCATION off
        }

     //   List ProviderAda=lm.getAllProviders();

      //  System.out.println(ProviderAda);

        launchLocationListener();


     //   angelVal.setText(String.format("%.12s",CurrentLocation[0]));
        //LatLonVal.setText(String.format("%.12s",CurrentLocation[1]));

               // Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();

              //  if(TimerUpdateLocRun == false){

                //    handler.removeCallbacks(r);
                //}

          //  }
        //};
        //if(TimerUpdateLocRun == true){
        //handler.postDelayed(r,1000);
       // }

    }


}



