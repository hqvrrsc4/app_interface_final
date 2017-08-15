package zxclc.com.BLE_key_interface;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by hxlee on 2017/6/18.
 */

public class UserAreaActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);
        final EditText username = (EditText) findViewById(R.id.username);
        final EditText age = (EditText) findViewById(R.id.age);
        final TextView welcome_msg = (TextView) findViewById(R.id.welcome_msg);
    }
}
