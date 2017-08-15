package zxclc.com.BLE_key_interface;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;

/**
 * Created by ljd on 2017/7/29.
 */

public class DVActivity extends AppCompatActivity{

    public static final int DISCONNECTED = 0;
    public static final int SCANNING = 1;
    public static final int KEYFOUND = 2;
    public static final int DISCOVERING = 3;
    public static final int DISCOVERED = 4;

    private BluetoothMgr btMgr;
    private Handler eventCheckHandler;
    boolean started = false;
    boolean paused = false;

    private DVLogMgr logMgr;

    public Handler msgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //myLog(">>>>>>> handleMessage"+msg.toString());
            handleMsg(msg.what, msg.arg1);
        }
    };

    Runnable delayedCheck = new Runnable() {
        @Override
        public void run() {
            if (btMgr != null && btMgr.connectionStatus == DISCOVERED) {
                exceptionally_disconnected = false;
                checkTick();
            } else if (paused && btMgr.connectionStatus == DISCONNECTED && exceptionally_disconnected) {
                dv_resume.setEnabled(false);
                btMgr.resumeConnection();
                myLog("reconnecting...");
            }

            eventCheckHandler.postDelayed(this, 50);
        }
    };

    //"试验开始时间,试验结束时间,BLE重连次数,按键变化次数,无线充电变化次数"
    public String dv_start_time = "";
    public String dv_end_time = "";
    public int BLE_reconnection_times =0;
    public int button_change_times = 0;
    public int wpc_change_times = 0;

    public long timeLastRead = 0;
    public static final long READ_INTERVAL_LIMIT = 200;

    public long timeLastReadLEDState = 0;
    public long timeLastReadButtonState = 0;
    public void checkTick(){
        if (btMgr.connectionStatus != BluetoothMgr.DISCOVERED) {
            return;
        }
        long now = Calendar.getInstance().getTimeInMillis();
        if (now - timeLastRead > READ_INTERVAL_LIMIT) {
            if (now - timeLastReadLEDState > 2000) {
                myLog("read LED State");
                btMgr.readCharacteristic(btMgr.DebugService_UUID, btMgr.LEDStatus_UUID);
                timeLastRead = now;
                timeLastReadLEDState = now;
                return;
            } else if (now - timeLastReadButtonState > 2000) {
                myLog("read Button State");
                btMgr.readCharacteristic(btMgr.DebugService_UUID, btMgr.ButtonStatus_UUID);
                timeLastRead = now;
                timeLastReadButtonState = now;
                return;
            }
        }
    }

    String led_state_cache = "N/A";
    String button_state_cache = "N/A";
    String charing_state_cache = "N/A";

    boolean exceptionally_disconnected = false;
    public Handler syncMsgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == BluetoothMgr.SYNC_EXCEPTION && msg.arg1 == BluetoothMgr.SYNC_EXCEPTION_DEVICE_NOT_FOUND) {
                myLog("Couldn't find device.");
            } else if(msg.what == BluetoothMgr.SYNC_EXCEPTION_DEVICE_CONNECTION_LOST){
                paused=true;
                exceptionally_disconnected = true;
            }
            else if (msg.what == BluetoothMgr.SYNC_CHARGING_STATE_CHANGED) {
                ((TextView)findViewById(R.id.textView_wpc_status)).setText("Wireless Charging Status: "+msg.arg1);
                if (!((msg.arg1 + "").equals(charing_state_cache)) ) {
                    charing_state_cache = msg.arg1 + "";
                    String logLine = getTimeString_Hms() + "," + charing_state_cache;
                    wpc_change_times++;
                    logMgr.writeLogLine(logLine, DVLogMgr.LOG_NOTIFY_VALUES);
                }
            } else if (msg.what == BluetoothMgr.SYNC_LED_STATE_READ) {
                myLog(">>>>" + Calendar.getInstance().getTimeInMillis()+"/led_state/" + intsToHexString(btMgr.ledState));

                led_state_cache = intsToHexString(btMgr.ledState);
                ((TextView)findViewById(R.id.textView_led_status)).setText("LED Status: "+led_state_cache);

            } else if (msg.what == BluetoothMgr.SYNC_BUTTON_STATE_READ) {
                myLog(">>>>" + Calendar.getInstance().getTimeInMillis()+"/button_state/" + btMgr.buttonState+"");
                if(!(button_state_cache.equals(btMgr.buttonState+""))){
                    button_change_times++;
                }
                button_state_cache = btMgr.buttonState+"";

                ((TextView)findViewById(R.id.textView_button_status)).setText("Button Status: "+button_state_cache);
                String logLine = getTimeString_YMD()+","+getTimeString_Hms()+","+button_state_cache+","+led_state_cache+","+(dv_stop.isEnabled()?1:0);
                logMgr.writeLogLine(logLine, DVLogMgr.LOG_READ_VALUES);

            }
        }
    };

    public static String getTimeString_YMD(){
        long time=System.currentTimeMillis();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        Date d1=new Date(time);
        return format1.format(d1);
    }

    public static String getTimeString_YYMMDD(){
        long time=System.currentTimeMillis();
        SimpleDateFormat format1 = new SimpleDateFormat("yyMMdd");
        Date d1=new Date(time);
        return format1.format(d1);
    }

    public static String getTimeString_Hms(){
        long time=System.currentTimeMillis();
        SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss");
        Date d1=new Date(time);
        return format2.format(d1);
    }
    public void showDialog(String msg){
        new AlertDialog.Builder(DVActivity.this).setTitle("").setMessage(msg).setPositiveButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public String intsToHexString(int[] c) {
        StringBuilder sb = new StringBuilder("0x");
        for (int i : c) {
            String s =Integer.toHexString(i).toUpperCase();
            if (s.length() == 1) {
                s="0"+s;
            }
            sb.append(s);
        }
        return sb.toString();
    }


    public void handleMsg(int what, int arg1){
        if (paused) {
            if (what == DISCONNECTED) {
                //myLog("Device not found or disconnected.");
                dv_start.setEnabled(false);
                dv_pause.setEnabled(false);
                dv_stop.setEnabled(true);
                dv_resume.setEnabled(true);
            } else if (what == DISCOVERED) {
                //myLog("All services discovered.");
                dv_start.setEnabled(false);
                dv_pause.setEnabled(true);
                dv_stop.setEnabled(true);
                dv_resume.setEnabled(false);
            }
        }else{
            if (what == DISCONNECTED) {
                //myLog("Device not found or disconnected.");
                dv_start.setEnabled(true);
                dv_pause.setEnabled(false);
                dv_stop.setEnabled(false);
                dv_resume.setEnabled(false);
            } else if (what == DISCOVERED) {
                //myLog("All services discovered.");
                dv_start.setEnabled(false);
                dv_pause.setEnabled(true);
                dv_stop.setEnabled(true);
                dv_resume.setEnabled(false);
            }
        }

    }

    private BluetoothAdapter mBluetoothAdapter;
    public void setBluetoothAdapter(BluetoothAdapter btAdapter) {
        this.mBluetoothAdapter = btAdapter;
        btMgr = new BluetoothMgr(this, btAdapter, msgHandler, syncMsgHandler);
    }

    void checkBLEFunction(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
        }
        mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {
                    setBluetoothAdapter(mBluetoothAdapter);
                } else {
                    //Bluetooth Advertisements are not supported
                }
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        } else {
            //Bluetooth is not supported
        }
    }

    Button dv_start;
    Button dv_stop;
    Button dv_pause;
    Button dv_resume;


    int connectedDeviceHash;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_dv);

        checkBLEFunction();
        askForPermission();
        logMgr = new DVLogMgr(this,
                "试验开始时间,试验结束时间,BLE重连次数,按键变化次数,无线充电变化次数",
                "日期,时间,按键状态特征值,LED状态特征值,BLE 连接状态 ",
                "时间,电池电量");

        eventCheckHandler = new Handler();
        eventCheckHandler.postDelayed(delayedCheck, 1000);

        dv_start = (Button)findViewById(R.id.button_dv_start);
        dv_stop = (Button)findViewById(R.id.button_dv_stop);
        dv_pause = (Button)findViewById(R.id.button_dv_pause);
        dv_resume = (Button)findViewById(R.id.button_dv_resume);

        dv_stop.setEnabled(false);
        dv_pause.setEnabled(false);
        dv_resume.setEnabled(false);

        dv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logMgr = new DVLogMgr(DVActivity.this,
                        "试验开始时间,试验结束时间,BLE重连次数,按键变化次数,无线充电变化次数",
                        "日期,时间,按键状态特征值,LED状态特征值,BLE 连接状态 ",
                        "时间,电池电量");
                dv_start_time = getTimeString_YYMMDD()+" " +getTimeString_Hms();
                dv_end_time = "";
                BLE_reconnection_times =0;
                button_change_times = 0;
                wpc_change_times = 0;

                myLog("Finding Devices...");
                dv_start.setEnabled(false);
                btMgr.startScanning(true, BluetoothMgr.DEVICE_TYPE_OLD_DEBUGGABLE);
            }
        });

        dv_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dv_stop.setEnabled(false);
                dv_end_time = getTimeString_YYMMDD()+" " +getTimeString_Hms();
                if (!paused) {
                    askForPermission();
                    logMgr.outputToStorage(DVLogMgr.LOG_READ_VALUES);
                    logMgr.outputToStorage(DVLogMgr.LOG_NOTIFY_VALUES);
                }
                //试验开始时间,试验结束时间,BLE重连次数,按键变化次数,无线充电变化次数
                logMgr.writeLogLine(dv_start_time+","+dv_end_time+","+BLE_reconnection_times+","+button_change_times+","+wpc_change_times,
                        DVLogMgr.LOG_SUMMARY);
                logMgr.outputToStorage(DVLogMgr.LOG_SUMMARY);
                paused = false;
                btMgr.onDestroy();
                btMgr = new BluetoothMgr(DVActivity.this, ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter(), msgHandler,
                        syncMsgHandler);
            }
        });

        dv_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dv_pause.setEnabled(false);
                askForPermission();
                logMgr.outputToStorage(DVLogMgr.LOG_READ_VALUES);
                logMgr.outputToStorage(DVLogMgr.LOG_NOTIFY_VALUES);
                paused = true;
                connectedDeviceHash = btMgr.bGatt.getDevice().hashCode(); // save hash code for reconnecting.
                /*btMgr.onDestroy();
                btMgr = new BluetoothMgr(DVActivity.this, ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter(), msgHandler,
                        syncMsgHandler);*/
                btMgr.suspendConnection();
            }
        });

        dv_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dv_resume.setEnabled(false);
                btMgr.resumeConnection();
            }
        });


    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public void askForPermission() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    Deque<String> logs = new ArrayDeque<String>();
    TextView dvLog;
    private void myLog(String s)
    {
        logs.addFirst(s);
        if (logs.size() > 10) {
            logs.removeLast();
        }
        if (dvLog == null) {
            dvLog = (TextView) findViewById(R.id.textView_dv_log);
        }
        StringBuilder logstring = new StringBuilder();
        for (String ss : logs) {
            logstring.append(ss);
            logstring.append("\n");
        }
        dvLog.setText(logstring.toString());
    }

    @Override
    protected void onDestroy() {
        btMgr.onDestroy();
        super.onDestroy();
    }



}
