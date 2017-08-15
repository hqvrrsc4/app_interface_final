package zxclc.com.BLE_key_interface;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothMgr {

    int connectedDeviceHash = -1;
    public static final int DISCONNECTED = 0;
    public static final int SCANNING = 1;
    public static final int KEYFOUND = 2;
    public static final int DISCOVERING = 3;
    public static final int DISCOVERED = 4;
    public boolean debugServiceFound = false;
    public boolean requestServiceFound = false;
    public int connectionStatus = 0;
    public boolean isCharging = false;
    boolean found = false;
    BluetoothGatt bGatt;
    public UUID LEDSwitch_UUID;
    public UUID R_UUID;
    public UUID G_UUID;
    public UUID B_UUID;
    public UUID Charge_UUID = UUID.fromString("A20018A0-6461-4F7F-AC4D-EC1AC6531BC4");
    public UUID DebugService_UUID = UUID.fromString("ad9c2f74-5994-424e-af18-8bf15f303590");
    public UUID LEDStatus_UUID = UUID.fromString("a20018a0-6461-4f7f-ac4d-ec1ac6531bc3");
    public UUID ButtonStatus_UUID = UUID.fromString("a20018a0-6461-4f7f-ac4d-ec1ac6531bc2");
    public UUID Request_UUID = UUID.fromString("a20018a0-6461-4f7f-ac4d-ec1ac611aa00");
    public UUID Response_UUID = UUID.fromString("a20018a0-6461-4f7f-ac4d-ec1ac611aa11");
    public UUID NIO_UUID = UUID.fromString("ad9c2f74-5944-424e-af18-8bf15f11aabb");



    boolean normally_disconnected = false;
    public int[] ledState = new int[7];
    public int buttonState = -1;
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            myLog("onConnectionStateChange Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    myLog("gattCallback STATE_CONNECTED");
                    connectionStatus = DISCOVERING;
//                    if (connectedDeviceHash != -1) {
//                        connectionStatus = DISCOVERED;
//                    }else{
                    if(shouldDiscoverServices){
                        gatt.discoverServices();
                    }else{
                        connectionStatus = DISCOVERED;
                        connectedDeviceHash = gatt.getDevice().hashCode();
                        myLog("skipped discovering");
                    }
//                    }
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    if(normally_disconnected){
                        return;
                    }
                    myLog("gattCallback STATE_DISCONNECTED");
                    sendSyncMessage(SYNC_EXCEPTION_DEVICE_CONNECTION_LOST, 1);
                    connectionStatus = DISCONNECTED;
                    break;
                default:
                    myLog("gattCallback STATE_OTHER");
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            for (BluetoothGattService s : services) {
                myLog("onServicesDiscovered uuid=" + s.getUuid().toString());

                List<BluetoothGattCharacteristic> characteristics = s.getCharacteristics();
                myLog("@@"+characteristics.toString());
                for (BluetoothGattCharacteristic c : characteristics) {
                    if (c.getUuid().toString().equalsIgnoreCase("A20018A0-6461-4F7F-AC4D-EC1AC6531BC4")) {
                        debugServiceFound = true; // old version, can be debugged.
                        myLog("Found Wireless Charge Characteristic");
                        Charge_UUID=c.getUuid();
                        DebugService_UUID = s.getUuid();
                        UUID UUID_CCC = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
                        BluetoothGattDescriptor config = c.getDescriptor(UUID_CCC);
                        config.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        bGatt.writeDescriptor(config);
                        bGatt.setCharacteristicNotification(c, true);
                    } else if (c.getUuid().toString().equalsIgnoreCase( "a20018a0-6461-4f7f-ac4d-ec1ac6531bd0")) {
                        myLog("Found LED Switch Characteristic");
                        LEDSwitch_UUID=c.getUuid();
                        DebugService_UUID =s.getUuid();
                    } else if (c.getUuid().toString().equalsIgnoreCase( "a20018a0-6461-4f7f-ac4d-ec1ac611aa00")) {
                        myLog("Found Request Characteristic");
                        requestServiceFound=true;
                        Request_UUID=c.getUuid();
                        NIO_UUID=s.getUuid();
                    } else if (c.getUuid().toString().equalsIgnoreCase( "a20018a0-6461-4f7f-ac4d-ec1ac6531bd1")) {
                        myLog("Found R Characteristic");
                        R_UUID=c.getUuid();
                        DebugService_UUID =s.getUuid();
                    }   else if (c.getUuid().toString().equalsIgnoreCase( "a20018a0-6461-4f7f-ac4d-ec1ac6531bd2")) {
                        myLog("Found G Characteristic");
                        G_UUID=c.getUuid();
                        DebugService_UUID =s.getUuid();
                    }else if (c.getUuid().toString().equalsIgnoreCase( "a20018a0-6461-4f7f-ac4d-ec1ac6531bd3")) {
                        myLog("Found B Characteristic");
                        B_UUID=c.getUuid();
                        DebugService_UUID =s.getUuid();
                    }
                    myLog(c.getUuid().toString());
                }

            }
            connectionStatus = DISCOVERED;
            if(!debugServiceFound && !requestServiceFound){
                //should not happen
            }else{
                connectedDeviceHash = gatt.getDevice().hashCode();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            myLog("onCharacteristicRead "+ characteristic.toString());
            if (characteristic.getUuid() == Charge_UUID) {
                myLog("cc"+characteristic.getValue().toString());
            }else if (characteristic.getUuid().equals(LEDStatus_UUID)) {
                for( int i = 0;i<7;i++) {
                    ledState[i] = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, i);
                }
                sendSyncMessage(SYNC_LED_STATE_READ, 1);

            } else if (characteristic.getUuid().equals(ButtonStatus_UUID)) {
                buttonState = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                sendSyncMessage(SYNC_BUTTON_STATE_READ, 0);
            } else if (characteristic.getUuid().equals(Response_UUID)) {
                //log
            }
            //gatt.disconnect();
        }


        @Override
        public void onCharacteristicChanged( BluetoothGatt gatt,
                                             BluetoothGattCharacteristic characteristic) {
            if (characteristic.getUuid() == Charge_UUID) {
                int chargeStateRaw = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,0);
                //myLog(characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,0).toString());
                isCharging = chargeStateRaw<3;
                sendSyncMessage(SYNC_CHARGING_STATE_CHANGED, chargeStateRaw);
            }
        }

    };


    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanCallback mScanCallback;
    private Handler mHandler;
    private Handler eventCheckHandler;
    private Handler responseCheckHandler;
    private Context cont;
    private Handler contextAsyncMsgHandler;
    private Handler contextSyncMsgHandler;

    public static final int SYNC_LOG = 0;
    public static final int SYNC_EXCEPTION = 1;
    public static final int SYNC_CHARGING_STATE_CHANGED = 2;
    public static final int SYNC_LED_STATE_READ = 3;
    public static final int SYNC_BUTTON_STATE_READ = 4;

    public static final int SYNC_EXCEPTION_DEVICE_NOT_FOUND = 8;
    public static final int SYNC_EXCEPTION_DEVICE_CONNECTION_LOST = 9;

    private void sendSyncMessage(int messageType, int argType){
        Message message = contextSyncMsgHandler.obtainMessage();
        message.what = messageType;
        message.arg1 = argType;
        contextSyncMsgHandler.sendMessage(message);
    }

    Runnable delayedCheck = new Runnable() {
        @Override
        public void run() {
            if (!found && mScanCallback == null) {
                connectionStatus = DISCONNECTED;
            }
            Message message = contextAsyncMsgHandler.obtainMessage();
            message.what = connectionStatus;
            message.arg1 = isCharging?1:0;
            contextAsyncMsgHandler.sendMessage(message);
            //myLog(">>>>>snedMessage"+message.toString());
            eventCheckHandler.postDelayed(this, 1000);
        }
    };

    Runnable readResponse = new Runnable() {
        @Override
        public void run() {
            if (!found || mScanCallback == null || connectionStatus!=DISCOVERED || !requestServiceFound ) {
                return;
            }
            readCharacteristic(NIO_UUID, Response_UUID);
            responseCheckHandler.postDelayed(this, 200);
        }
    };


    private int scanTimeout = 0;
    public BluetoothMgr(Context cont, BluetoothAdapter bAdapter, Handler d, Handler d_sync) {
        this.cont = cont;
        mHandler = new Handler();
        if(d != null){
            contextAsyncMsgHandler = d;
        }
        if(d_sync != null){
            contextSyncMsgHandler = d_sync;
        }
        eventCheckHandler = new Handler();
        eventCheckHandler.postDelayed(delayedCheck, 1000);
        responseCheckHandler = new Handler();
        responseCheckHandler.postDelayed(delayedCheck, 100);
        mBluetoothAdapter = bAdapter;
        setBluetoothAdapter(mBluetoothAdapter);


    }

    public void setBluetoothAdapter(BluetoothAdapter btAdapter) {
        this.mBluetoothAdapter = btAdapter;
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    private List<ScanFilter> buildScanFilters() {
        List<ScanFilter> scanFilters = new ArrayList<>();
        ScanFilter.Builder builder = new ScanFilter.Builder();
        // builder.setServiceUuid(Constants.Service_UUID);
        // builder.setDeviceName("UAES NIO C1725");
        scanFilters.add(builder.build());
        return scanFilters;
    }

    private ScanSettings buildScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
        return builder.build();
    }

    boolean shouldDiscoverServices = true;

    public static final int DEVICE_TYPE_OLD_DEBUGGABLE = 0;
    public static final int DEVICE_TYPE_NEW_REQUEST_RESPONSE = 1;

    public int deviceType = 0;
    public void startScanning(boolean discoverServices, int device_type) {
        deviceType = device_type;
        normally_disconnected = false;
        shouldDiscoverServices = discoverServices;
        if(connectionStatus == DISCOVERED || connectionStatus == DISCOVERING){
            myLog("not dc'ed, cant start scanning");
            return;
        }
        if (mScanCallback == null) {
            found = false;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScanning();
                }
            }, 5000);

            mScanCallback = new BLEScanCallback();
            mBluetoothLeScanner.startScan(buildScanFilters(), buildScanSettings(), mScanCallback);
            connectionStatus = SCANNING;
            myLog("\nstart scanning");
        } else {
            myLog("\nalready scanning");
        }
    }

    public void resumeConnection(){
        startScanning(true, deviceType);
    }

    public void stopScanning() {
        myLog("\nstop scanning");
        if (connectionStatus == SCANNING) {
            sendSyncMessage(SYNC_EXCEPTION, SYNC_EXCEPTION_DEVICE_NOT_FOUND);
        }
        mBluetoothLeScanner.stopScan(mScanCallback);
        mScanCallback = null;
    }

    protected void onDestroy() {
        eventCheckHandler.removeCallbacks(delayedCheck);

        normally_disconnected = true;
        if (bGatt == null) {
            return;
        }
        bGatt.close();
        bGatt = null;
    }

    public void suspendConnection(){
        if (bGatt != null) {
            bGatt.disconnect();
            normally_disconnected = true;
            connectionStatus = DISCONNECTED;
        }
    }

    public void myLog(String s) {
        Log.i(">>>>>>>>>", s);

    }

    public void checkResult(ScanResult result) {
        if (found) {
            return;
        }
        if(connectedDeviceHash!=-1){
            if(result.getDevice().hashCode() == connectedDeviceHash){
                connectionStatus = KEYFOUND;
                myLog("\nKey found, resuming...");
                found = true;
                bGatt = result.getDevice().connectGatt(cont, true, gattCallback);
                return;
            }
        }else if (result.getDevice().getName()!= null && result.getDevice().getName().startsWith("UAES NIO C0RGB")) {
            myLog(result.getDevice().hashCode()+" (hashcode)");

            myLog("\nKey found, connecting...");
            connectionStatus = KEYFOUND;
            found = true;
            bGatt = result.getDevice().connectGatt(cont, true, gattCallback);
            return;
        }
    }

    public boolean writeGatt(BluetoothGattService s, BluetoothGattCharacteristic c, byte[] b) {
        if(connectionStatus!=DISCOVERED){return false;}
        if (bGatt == null) {
            return false;
        }
        c.setValue(b);
        boolean flag = bGatt.writeCharacteristic(c);
        if(flag){
            myLog("write success");
        }else{
            myLog("write failed");
        }
        return flag;
    }


    public boolean lightOn_old() {
        BluetoothGattService s = bGatt.getService(DebugService_UUID);
        BluetoothGattCharacteristic c = s.getCharacteristic(LEDSwitch_UUID);
        byte[] on = {0x2};
        return writeGatt(s,c,on);
    }

    public void readCharacteristic(UUID service_uuid, UUID char_uuid) {
//        myLog(service_uuid.toString());
//        myLog(char_uuid.toString());
        BluetoothGattService s = bGatt.getService(service_uuid);
        BluetoothGattCharacteristic c = null;
        if (s != null) {
            c = bGatt.getService(service_uuid).getCharacteristic(char_uuid);
        }
        if (c != null) {
            bGatt.readCharacteristic(c);
        }

    }

    public boolean lightOn() {
        return lightOn_old();
    }

    public boolean lightOff_old() {
        BluetoothGattService s = bGatt.getService(DebugService_UUID);
        BluetoothGattCharacteristic c = s.getCharacteristic(LEDSwitch_UUID);
        byte[] on = {0x0};
        return writeGatt(s,c,on);
    }

    public boolean lightOff() {
        return lightOff_old();
    }

    public boolean WriteR(int val) {
        BluetoothGattService s = bGatt.getService(DebugService_UUID);
        BluetoothGattCharacteristic c = s.getCharacteristic(R_UUID);
        byte[] perc = {(byte)val};
        return writeGatt(s,c,perc);
    }

    public boolean WriteG(int val) {
        BluetoothGattService s = bGatt.getService(DebugService_UUID);
        BluetoothGattCharacteristic c = s.getCharacteristic(G_UUID);
        byte[] perc = {(byte)val};
        return writeGatt(s,c,perc);
    }

    public boolean WriteB(int val) {
        BluetoothGattService s = bGatt.getService(DebugService_UUID);
        BluetoothGattCharacteristic c = s.getCharacteristic(B_UUID);
        byte[] perc = {(byte)val};
        return writeGatt(s,c,perc);
    }

    private class BLEScanCallback extends ScanCallback {
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (ScanResult result : results) {
                checkResult(result);
            }
        }
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            checkResult(result);
        }
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            // Toast.makeText(getActivity(), "Scan failed with error: " + errorCode, Toast.LENGTH_LONG).show();
        }
    }

}


