package zxclc.com.BLE_key_interface;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UserAreaActivity_2 extends AppCompatActivity {
    //Retrieve data from .php file
    /**********************************************/
   // private final String RETRIEVE_URL = "http://106.14.194.158:8080/retrieve_data.php";
 //   private final String RETRIEVE_URL = "http://192.168.0.111/retrieve_data.php";
    private final String RETRIEVE_URL = "http://106.14.194.158:8080/retrieve_data_5.php";
    private final String CHANGE_VEHICLE_URL = "http://106.14.194.158:8080/change_data_2.php";
    private final String CHANGE_VEHICLE_URL_2 = "http://106.14.194.158:8080/change_data_3.php";
    private final String CHANGE_VEHICLE_URL_3 = "http://106.14.194.158:8080/change_data_4.php";
    private final String CHANGE_VEHICLE_URL_4 = "http://106.14.194.158:8080/change_data_5.php";
  //  List<NameValuePair> parmeters = new ArrayList<NameValuePair>();
    /**********************************************/
    public static final int DISCONNECTED = 0;
    public static final int SCANNING = 1;
    public static final int KEYFOUND = 2;
    public static final int DISCOVERING = 3;
    public static final int DISCOVERED = 4;
    public static int UIState = 0;
    private Handler eventCheckHandler;

    BluetoothMgr btMgr;
    Button b1;
    TextView t1;
    TextView textViewChargeStatus;
    ProgressBar progressKeyBattery;

    ProgressBar progressBar5;

    public Handler msgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //myLog(">>>>>>> handleMessage"+msg.toString());
            ChangeUIState(msg.what, msg.arg1);
        }

    };

    public Handler syncMsgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == BluetoothMgr.SYNC_EXCEPTION && msg.arg1 == BluetoothMgr.SYNC_EXCEPTION_DEVICE_NOT_FOUND) {
                new AlertDialog.Builder(UserAreaActivity_2.this).setTitle("").setMessage("Couldn't find device.").setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        }

    };

    private BluetoothAdapter mBluetoothAdapter;

    public void setBluetoothAdapter(BluetoothAdapter btAdapter) {
        this.mBluetoothAdapter = btAdapter;
        btMgr = new BluetoothMgr(this, btAdapter, msgHandler, syncMsgHandler);
    }

    boolean LED_Enabled = false;
    boolean LED_EnabledChanged = false;
    int LED_RedValue = 0;
    int LED_GreenValue = 0;
    int LED_BlueValue = 0;
    boolean LED_RedFlagChanged = false;
    boolean LED_GreenFlagChanged = false;
    boolean LED_BlueFlagChanged = false;
    Switch RGBSwitch;
    SeekBar seekBarRed;
    SeekBar seekBarGreen;
    SeekBar seekBarBlue;
    TextView textViewRed;
    TextView textViewGreen;
    TextView textViewBlue;
  //  TextView textViewUsername;
    private void initRGBControl(){
        RGBSwitch = (Switch) findViewById(R.id.switch1);
        seekBarRed = (SeekBar) findViewById(R.id.seekBar4);
        seekBarGreen = (SeekBar) findViewById(R.id.seekBar5);
        seekBarBlue = (SeekBar) findViewById(R.id.seekBar6);
        textViewRed = (TextView) findViewById(R.id.textView13);
        textViewGreen = (TextView) findViewById(R.id.textView15);
        textViewBlue = (TextView) findViewById(R.id.textView17);
     //   textViewUsername = (TextView) findViewById(R.id.textView10);
        RGBSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LED_Enabled=b;
                LED_EnabledChanged=true;

                LED_RedValue=50;
                LED_BlueValue=50;
                LED_GreenValue=50;
                seekBarRed.setProgress(LED_RedValue);
                seekBarGreen.setProgress(LED_GreenValue);
                seekBarBlue.setProgress(LED_BlueValue);
                syncRGBControl();
            }
        });
        seekBarRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                LED_RedValue = i;
                LED_RedFlagChanged = true;
                syncRGBControl();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarRed.setMax(100);

        seekBarGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                LED_GreenValue = i;
                LED_GreenFlagChanged = true;
                syncRGBControl();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarGreen.setMax(100);

        seekBarBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                LED_BlueValue = i;
                LED_BlueFlagChanged = true;
                syncRGBControl();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarBlue.setMax(100);

    }
    private void resetRGBControl(){
        RGBSwitch.setChecked(false);
        seekBarRed.setProgress(0);
        seekBarGreen.setProgress(0);
        seekBarBlue.setProgress(0);
        textViewRed.setText("0");
        textViewGreen.setText("0");
        textViewBlue.setText("0");
    }
    private void syncRGBControl(){
        //RGBSwitch.setChecked(LED_Enabled);
        //seekBarRed.setProgress(LED_RedValue);
        //seekBarGreen.setProgress(LED_GreenValue);
        //seekBarBlue.setProgress(LED_BlueValue);
        textViewRed.setText(LED_RedValue+"");
        textViewGreen.setText(LED_GreenValue+"");
        textViewBlue.setText(LED_BlueValue+"");
    }
    private void syncRemoteLED(){
        if (LED_Enabled && UIState == DISCOVERED) {
            if (LED_EnabledChanged) {
                boolean writeSuccess = false;

                if(LED_Enabled){
                    myLog("ON (WriteSwitch)");
                    writeSuccess = btMgr.lightOn();
                }else{
                    myLog("OFF (WriteSwitch)");
                    writeSuccess = btMgr.lightOff();
                }
                LED_EnabledChanged=!writeSuccess;
                return;
            }
            if (LED_RedFlagChanged) {
                myLog(LED_RedValue+" (WriteR)");
                boolean writeSuccess = btMgr.WriteR(LED_RedValue);
                LED_RedFlagChanged=!writeSuccess;
                return;
            }
            if (LED_GreenFlagChanged) {
                myLog(LED_GreenValue+" (WriteG)");
                boolean writeSuccess = btMgr.WriteG(LED_GreenValue);
                LED_GreenFlagChanged=!writeSuccess;
                return;
            }
            if (LED_BlueFlagChanged) {
                myLog(LED_BlueValue+" (WriteB)");
                boolean writeSuccess = btMgr.WriteB(LED_BlueValue);
                LED_BlueFlagChanged=!writeSuccess;
                return;
            }

        }else if (!LED_Enabled && UIState == DISCOVERED) {
            if (LED_EnabledChanged) {
                boolean writeSuccess = false;
                myLog("OFF (WriteSwitch)");
                writeSuccess = btMgr.lightOff();
                LED_EnabledChanged=!writeSuccess;
                return;
            }
        }
    }

    Runnable delayedCheck = new Runnable() {
        @Override
        public void run() {
            syncRemoteLED();
            eventCheckHandler.postDelayed(this, 1200);
        }
    };



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_1);
     //   get_json();//user info (json)
        final Globalv var = (Globalv) getApplication();
        json_string = var.get_json2();
        //Go back to login page
        final TextView back = (TextView) findViewById(R.id.textView18);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(UserAreaActivity_2.this, MainActivity.class);
                UserAreaActivity_2.this.startActivity(registerIntent);}});


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
        }

        mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE))
                .getAdapter();
        eventCheckHandler = new Handler();
        eventCheckHandler.postDelayed(delayedCheck, 1000);

        // Is Bluetooth supported on this device?
        if (mBluetoothAdapter != null) {
            // Is Bluetooth turned on?
            if (mBluetoothAdapter.isEnabled()) {
                // Are Bluetooth Advertisements supported on this device?
                if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {
                    // Everything is supported and enabled
                    setBluetoothAdapter(mBluetoothAdapter);
                } else {

                    t1.append("\nBluetooth Advertisements are not supported.");
                }
            } else {
                // Prompt user to turn on Bluetooth (logic continues in onActivityResult()).
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        } else {

            t1.append("\nBluetooth is not supported");
        }

        setControlViewEnabled(false);
        progressBar5 = (ProgressBar) findViewById(R.id.progressBar5);
        progressBar5.setVisibility(View.INVISIBLE);
        b1 = (Button) findViewById(R.id.button3);
        b1.setOnClickListener(new Button.OnClickListener() {//创建监听
            public void onClick(View v) {
                b1.setEnabled(false);
                progressBar5.setVisibility(View.VISIBLE);
                if (UIState == DISCONNECTED) {
                    btMgr.startScanning(true, BluetoothMgr.DEVICE_TYPE_OLD_DEBUGGABLE);
                } else if (UIState == DISCOVERED) {
                    btMgr.onDestroy();
                    btMgr = new BluetoothMgr(UserAreaActivity_2.this, mBluetoothAdapter, msgHandler, syncMsgHandler);
                }

            }

        });
        t1 = (TextView) findViewById(R.id.textView4);
        textViewChargeStatus = (TextView) findViewById(R.id.textView8);
        progressKeyBattery = (ProgressBar) findViewById(R.id.progressBar4);
        initRGBControl();


        /****************************/
        //user info
     /*       Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    tran_json();
//                  runs a method every 2000ms
//       example    runThisEvery2seconds();
                }
            }, 2000);*/
     tran_json();


        /************************************/
        //open/close door (Hanxiang)
       // open_button = (Button) findViewById(R.id.imageButton6);
       // close_button = (Button) findViewById(R.id.imageButton5);
       // doTheAutoRefresh();
       // this.mHandler = new Handler();
       // this.mHandler.postDelayed(m_Runnable,5000);
    }     //end of OnCreate


    private void setControlViewEnabled(boolean flag){
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout1);
        for(int i = 0;i<linearLayout.getChildCount();i++) {
            View child = linearLayout.getChildAt(i);
            if (child instanceof ConstraintLayout) {
                for(int j = 0;j<((ConstraintLayout)child).getChildCount();j++) {
                    View childchild = ((ConstraintLayout)child).getChildAt(j);
                    childchild.setEnabled(flag);
                    childchild.setAlpha(flag?1f:0.4f);
                }
            }
        }
    }

    public void ChangeChargingState(boolean connected, boolean isCharging){
        if (!connected) {
            textViewChargeStatus.setText("钥匙电池：未知");
            progressKeyBattery.setProgress(0);
            return;
        }
        if (isCharging) {
            textViewChargeStatus.setText("钥匙正在充电...");
            progressKeyBattery.setProgress(100);
        }else{
            textViewChargeStatus.setText("钥匙电池：未充电");
            progressKeyBattery.setProgress(90);
        }

    }
    public void ChangeUIState(int state, int isCharging) {
      //  final Globalv app = (Globalv) getApplication();
     //   textViewUsername.setText("用户名："+app.get_username());
        ChangeChargingState(state == DISCOVERED, isCharging == 1);
        if (UIState == DISCONNECTED && state == DISCONNECTED) {
            b1.setEnabled(true);
            progressBar5.setVisibility(View.INVISIBLE);
            setControlViewEnabled(false);
            resetRGBControl();
            t1.setText("未连接");
            b1.setText("连接");
        } else if (state == DISCONNECTED) {
            btMgr.onDestroy();
            btMgr = new BluetoothMgr(UserAreaActivity_2.this, mBluetoothAdapter, msgHandler, syncMsgHandler);
            b1.setEnabled(true);
            progressBar5.setVisibility(View.INVISIBLE);
            setControlViewEnabled(false);
            resetRGBControl();
            t1.setText("已断开");
            b1.setText("连接");
        } else if (state == SCANNING) {
            b1.setEnabled(false);
            progressBar5.setVisibility(View.VISIBLE);
            setControlViewEnabled(false);
            t1.setText("连接中...");
        } else if (state == KEYFOUND) {
            t1.setText("设备已找到...");
        } else if (state == DISCOVERED) {
            t1.setText("已连接");
            setControlViewEnabled(true);
            b1.setEnabled(true);
            progressBar5.setVisibility(View.INVISIBLE);
            b1.setText("断开");
        }
        UIState = state;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void myLog(String s) {
        Log.i(">>>>>>>>>", s);
    }

    public void b1_click(View view) {
        btMgr.startScanning(true, BluetoothMgr.DEVICE_TYPE_OLD_DEBUGGABLE);
    }

    public void bA_click(View view) {
        btMgr.lightOn();
    }

    public void bB_click(View view) {
        btMgr.lightOff();
    }

    public void bC_click(View view) {
        btMgr.WriteR(20);
    }

    public void b2_click(View view) {
        btMgr.onDestroy();
    }

    /********************************/
    //User Info (Hanxiang)
    String JSON_STRING;

    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;
    ContactAdapter contactAdapter;
    ListView listView;


    public void parseJSON(View view){
        //   get_json();
        if(json_string == null)
        {
            Toast.makeText(getApplicationContext(),"First Get JSON", Toast.LENGTH_LONG).show();
        }
        else {
           //      Intent intent = new Intent(this,DisplayListView.class);
           //      intent.putExtra("json_data",json_string);
           //      startActivity(intent);
        }

    }
    /*********************************************/
    //open/close door (Hanxiang), open tailgate
  //  Button open_button = (Button) findViewById(R.id.imageButton6);
  //  Button close_button = (Button) findViewById(R.id.imageButton5);
    public void open_door(View view){
            final String username;
            final String window = "state_open";
            final Globalv app = (Globalv) getApplication();
            username = app.get_username();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, CHANGE_VEHICLE_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //      Toast.makeText(UserAreaActivity_2.this, response, Toast.LENGTH_LONG).show();
                            // System.out.println(response);
                            //if(response.equals("\"true\""))
                            //if(response.equals("true"))
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(UserAreaActivity_2.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", username);
                    params.put("window", window);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
            get_json();
            tran_json();

    }

    public void close_door(View view){
        final String username;
        final String window = "state_close";
        final String air_conditioner = "t-gate_close";
        final Globalv app = (Globalv) getApplication();
        username = app.get_username();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CHANGE_VEHICLE_URL_3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                 //       Toast.makeText(UserAreaActivity_2.this, response, Toast.LENGTH_LONG).show();
                        // System.out.println(response);
                        //if(response.equals("\"true\""))
                        //if(response.equals("true"))
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserAreaActivity_2.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("window", window);
                params.put("air_conditioner",air_conditioner);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        get_json();
        tran_json();
    }

    public void open_tailgate(View view){
        final String username;
        final String air_conditioner = "t-gate_open";
        final Globalv app = (Globalv) getApplication();
        username = app.get_username();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CHANGE_VEHICLE_URL_2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //      Toast.makeText(UserAreaActivity_2.this, response, Toast.LENGTH_LONG).show();
                        // System.out.println(response);
                        //if(response.equals("\"true\""))
                        //if(response.equals("true"))
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserAreaActivity_2.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("air_conditioner", air_conditioner);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        get_json();
        tran_json();
    }


    /**********************************************/
    //auto_refresh;
   /*
    private Handler mHandler;
    private final Runnable m_Runnable = new Runnable()
    {
        public void run()

        {
            Toast.makeText(UserAreaActivity_2.this,"in runnable",Toast.LENGTH_SHORT).show();

            UserAreaActivity_2.this.mHandler.postDelayed(m_Runnable, 5000);
        }

    };//runnable
*/
   /*******************************************************/
   //tran_json();
    public void tran_json(){
        listView = (ListView) findViewById(R.id.listview2);
        contactAdapter = new ContactAdapter(this,R.layout.raw_layout);
        listView.setAdapter(contactAdapter);
        // json_string = getIntent().getExtras().getString("json_data");
        final String id_username;
        final Globalv app = (Globalv) getApplication();
        id_username = app.get_username();
        if(json_string!=null) {
            try {
                jsonObject = new JSONObject(json_string);
                jsonArray = jsonObject.getJSONArray("result");
                int count = 0;
                String username, vid, fuel, window, ac;
                while (count < jsonArray.length()) {
                    JSONObject JO = jsonArray.getJSONObject(count);
                    username = JO.getString("username");
                    vid = JO.getString("vehicle_id");
                    fuel = JO.getString("fuel");
                    window = JO.getString("window");
                    ac = JO.getString("air_conditioner");
                    Contacts contacts = new Contacts(username, vid, fuel, window, ac);
                    if (username.equals(id_username)) {
                        contactAdapter.add(contacts);
                    }
                    count++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*********************************************************/
    //get_json();
    class BackgroundTask extends AsyncTask<Void,Void,String> {

        String json_url;
        @Override
        protected void onPreExecute() {
            //  json_url = "http://192.168.0.111:8080/retrieve_data_5.php";
            //  json_url = "http://192.168.1.10:8080/retrieve_data_5.php";
            json_url = "http://106.14.194.158:8080/retrieve_data_5.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while((JSON_STRING = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(JSON_STRING+"\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return  stringBuilder.toString().trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void...values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            //      TextView textView = (TextView) findViewById(R.id.textview);
            //      textView.setText(result);
            json_string = result;
        }

    }
    public void get_json(){
        new UserAreaActivity_2.BackgroundTask().execute();
    }

    /***************************************/
    //local_time();
    public void local_time(View view){
        long unixTime = System.currentTimeMillis() / 1000L;
        final String age = Long.toString(unixTime);
        final String username;
        final Globalv app = (Globalv) getApplication();
        username = app.get_username();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CHANGE_VEHICLE_URL_4,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //      Toast.makeText(UserAreaActivity_2.this, response, Toast.LENGTH_LONG).show();
                        // System.out.println(response);
                        //if(response.equals("\"true\""))
                        //if(response.equals("true"))
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserAreaActivity_2.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("age", age);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        get_json();
        tran_json();
    }

}
