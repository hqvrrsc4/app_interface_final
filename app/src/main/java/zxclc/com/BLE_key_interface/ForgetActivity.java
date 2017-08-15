package zxclc.com.BLE_key_interface;

import android.content.Intent;
import android.net.Uri;
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
import java.util.Random;

/**
 * Created by hxlee on 2017/7/4.
 */

public class ForgetActivity extends AppCompatActivity implements View.OnClickListener{
    private Button b_email;
   // private final String EMAIL_URL = "http://localhost:8080/send_mail_2.0.php";
  // private final String EMAIL_URL = "http://192.168.56.1:8080/send_mail_2.0.php";
   private final String EMAIL_URL = "http://106.14.194.158:8080/send_mail_2.0.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        b_email = (Button) findViewById(R.id.code_button);
        b_email.setOnClickListener(this);
    }

    public void send_message() {
        Intent intent = null;
        Intent chooser = null;
        intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        //String[] to = {"lihanxiang0309@gmail.com","hamish@sjtu.edu.cn","1571963633@qq.com","hxlee@umich.edu"};
        String[] to = {"hxlee@umich.edu"};
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        intent.putExtra(Intent.EXTRA_SUBJECT, "hello, this is sent from my app");
        intent.putExtra(Intent.EXTRA_TEXT, "how are you doing?");
        intent.setType("message/rfc822");
        // intent.setType("text/plain");
        chooser = Intent.createChooser(intent, "hamish@sjtu.edu.cn");
        startActivity(chooser);
        Toast.makeText(ForgetActivity.this, "Success", Toast.LENGTH_LONG).show();
    }

    public void forget_pw() {
        //setContentView(R.layout.activity_forget_2);
        Intent resetIntent = new Intent(ForgetActivity.this, ResetActivity.class);
        ForgetActivity.this.startActivity(resetIntent);
    }

    public void send_email() {
           EditText s_email = (EditText) findViewById(R.id.textView3);
           Random r = new Random();
           int code_number = r.nextInt(9999-1000)+1000;
        final String code = Integer.toString(code_number);
       final String forget_email = s_email.getText().toString();
        final Globalv app = (Globalv) getApplication();
        app.set_code(code);
        app.set_email(forget_email);
        StringRequest stringRequest_code = new StringRequest(Request.Method.POST, EMAIL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ForgetActivity.this,response,Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ForgetActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("code",code);
                params.put("email",forget_email);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest_code);
    }

    @Override
    public void onClick(View v) {
        if(v == b_email)
        {
            forget_pw();
            send_email();
        }
    }
}
