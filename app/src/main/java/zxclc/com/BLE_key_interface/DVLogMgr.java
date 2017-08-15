package zxclc.com.BLE_key_interface;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class DVLogMgr {
    public ArrayList<String> log_summary;
    public ArrayList<String> log_read_values;
    public ArrayList<String> log_notify_values;

    public String summary_columns;
    public String read_values_columns;
    public String notify_values_columns;

    public Context context;
    public DVLogMgr(Context c, String summary_columns_string,
                    String read_values_columns_string, String notify_values_columns_string){
        if(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).exists()){
            deleteAllFiles(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS));
        }
        context = c;
        log_summary = new ArrayList<String>();
        log_read_values = new ArrayList<String>();
        log_notify_values = new ArrayList<String>();
        summary_columns = summary_columns_string;
        read_values_columns = read_values_columns_string;
        notify_values_columns = notify_values_columns_string;

        writeLogLine(summary_columns, LOG_SUMMARY);
        writeLogLine(read_values_columns, LOG_READ_VALUES);
        writeLogLine(notify_values_columns, LOG_NOTIFY_VALUES);
    }

    static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) {
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }

    public static final int LOG_SUMMARY = 0;
    public static final int LOG_READ_VALUES = 1;
    public static final int LOG_NOTIFY_VALUES = 2;

    public int read_value_sequence = 0;
    public int notify_value_sequence = 0;

    public void writeLogLine(String line, int LOG_TYPE) {
        if (LOG_TYPE == LOG_SUMMARY) {
            log_summary.add(line);
        }else if (LOG_TYPE == LOG_READ_VALUES) {
            log_read_values.add(line);
        }else if (LOG_TYPE == LOG_NOTIFY_VALUES) {
            log_notify_values.add(line);
        }
    }

    public void outputToStorage(int LOG_TYPE){
        if (LOG_TYPE > 2 || LOG_TYPE < 0) {
            return;
        }
        String filename = "myfile";
        String string = "Hello world!";
        ArrayList<String> log = null;
        FileOutputStream outputStream;

        switch (LOG_TYPE) {
            case LOG_SUMMARY:
                filename = "BLE_KEY_DV_TEST_REPORT_"+DVActivity.getTimeString_YYMMDD()+".csv";
                log = log_summary;
                break;
            case LOG_READ_VALUES:
                read_value_sequence++;
                filename = "BLE_KEY_DV_DATA_RECORD_PERIOD_"+String.format("%04d",read_value_sequence)+".csv";
                log = log_read_values;
                break;
            case LOG_NOTIFY_VALUES:
                notify_value_sequence++;
                filename = "BLE_KEY_DV_DATA_RECORD_EVENT_"+String.format("%04d",notify_value_sequence)+".csv";
                log = log_notify_values;
                break;
        }
        StringBuilder sb = new StringBuilder();
        for (String s : log) {
            sb.append(s);
            sb.append("\n");
        }
        switch (LOG_TYPE) {
            case LOG_SUMMARY:
                log_summary.clear();
                break;
            case LOG_READ_VALUES:
                log_read_values.clear();
                break;
            case LOG_NOTIFY_VALUES:
                log_notify_values.clear();
                break;
        }

        if (log_summary.isEmpty()) {
            writeLogLine(summary_columns, LOG_SUMMARY);
        }
        if (log_read_values.isEmpty()) {
            writeLogLine(read_values_columns, LOG_READ_VALUES);
        }
        if (log_notify_values.isEmpty()) {
            writeLogLine(notify_values_columns, LOG_NOTIFY_VALUES);
        }
        try {

            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).mkdirs();

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), filename);
            //file.mkdirs();
            Log.e("123",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString());
            file.createNewFile();
            outputStream = new FileOutputStream(file, true);
            outputStream.write(sb.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

}
