package zxclc.com.BLE_key_interface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hxlee on 2017/7/5.
 */

public class ResetActivity extends AppCompatActivity implements View.OnClickListener{
    private Button b_reset;
 //   private final String DATA_URL = "http://192.168.56.1:8080/change_data.php";
 private final String DATA_URL = "http://106.14.194.158:8080/change_data.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_2);
        b_reset = (Button) findViewById(R.id.reset_button);
        b_reset.setOnClickListener(this);
    }
    public void reset_pw() {
        //setContentView(R.layout.activity_forget_2);
        EditText code_i = (EditText) findViewById(R.id.textView5);
        EditText pass_1 = (EditText) findViewById(R.id.textView6);
        EditText pass_2 = (EditText) findViewById(R.id.textView7);
        String pw_1 = pass_1.getText().toString();
        String pw_2 = pass_2.getText().toString();
        String code_input = code_i.getText().toString();
        final Globalv app = (Globalv) getApplication();
        String code = app.get_code();
        final String email = app.get_email();
        if(!code_input.equals(code)){
            Toast.makeText(ResetActivity.this, "Wrong code, please re-enter your email address.", Toast.LENGTH_LONG).show();
            Intent againIntent = new Intent(ResetActivity.this, ForgetActivity.class);
            ResetActivity.this.startActivity(againIntent);
        }
        else if (!pw_1.equals(pw_2)) {
            Toast.makeText(ResetActivity.this, "Confirmation of password failed.", Toast.LENGTH_LONG).show();
            Intent againIntent = new Intent(ResetActivity.this, ResetActivity.class);
            ResetActivity.this.startActivity(againIntent);
        } else {
            final String password = pw_1;
            StringRequest stringRequest_reset = new StringRequest(Request.Method.POST, DATA_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(ResetActivity.this, response, Toast.LENGTH_LONG).show();
                            Intent resetIntent = new Intent(ResetActivity.this, MainActivity.class);
                            ResetActivity.this.startActivity(resetIntent);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ResetActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("password", password);
                   params.put("email",email);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest_reset);
            //  Toast.makeText(ResetActivity.this, "Success", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onClick(View v) {
        if(v == b_reset)
        {
            reset_pw();
        }
    }
}
