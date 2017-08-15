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
 * Created by hxlee on 2017/6/17.
 */


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button b_register;
    //   private final String REGISTER_URL_3 = "http://192.168.1.10:8080/Register.php";
    // private final String REGISTER_URL_3 = "http://localhost:8080/Register.php";
    // private final String REGISTER_URL_3 = "http://192.168.56.1:8080/Register.php";
    private final String REGISTER_URL_3 = "http://106.14.194.158:8080/Register.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        b_register = (Button) findViewById(R.id.bb_register);
        b_register.setOnClickListener(this);
    }
    private void registerUser(){
        EditText b_age = (EditText) findViewById(R.id.age);
        EditText b_name = (EditText) findViewById(R.id.name);
        EditText b_username = (EditText) findViewById(R.id.username);
        EditText b_password = (EditText) findViewById(R.id.password);
        EditText b_email = (EditText) findViewById(R.id.email);
        final String name = b_name.getText().toString();
        final String username = b_username.getText().toString();
        final String password = b_password.getText().toString();
        final String email = b_email.getText().toString();
        //final int age = Integer.parseInt(b_age.getText().toString());
        final String age = b_age.getText().toString();
        //addition
        /**********/
        final String vehicle_id = "default";
        final String fuel = "100%";
        final String window = "state_close";
        final String air_conditioner = "state_close";
        /**********/

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL_3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(RegisterActivity.this,response,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("name",name);
                params.put("username",username);
                params.put("age",age);
                params.put("password",password);
                params.put("email",email);
                params.put("vehicle_id",vehicle_id);
                params.put("fuel",fuel);
                params.put("window",window);
                params.put("air_conditioner",air_conditioner);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        if(v == b_register)
        {
            registerUser();
        }
    }
}


  /*      b_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = b_name.getText().toString();
                final String username = b_username.getText().toString();
                final String password = b_password.getText().toString();
                final int age = Integer.parseInt(b_age.getText().toString());

           //     System.out.println(name);
           //     System.out.println(username);
           //     System.out.println(password);
           //     System.out.println(age);

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if(success){
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                RegisterActivity.this.startActivity(intent);
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("Register Failed")
                                        .setNegativeButton("Retry",null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };



                RegisterRequest registerRequest = new RegisterRequest(name,username,age,password,responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
                System.out.println("Adding request to queue.");
            }
        });

    }

}
*/