package zxclc.com.BLE_key_interface;

import android.content.Intent;
import android.os.AsyncTask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity{
 //   String JSON_STRING;

 //   String json_string;
    private Button b_login;
    //private final String LOGIN_URL = "http://192.168.1.10:8080/Login.php";
  // private final String LOGIN_URL = "http://localhost:8080/Login.php";
    // private final String LOGIN_URL = "http://192.168.56.1:8080/Login.php";
    private final String LOGIN_URL = "http://106.14.194.158:8080/Login.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b_login = (Button) findViewById(R.id.login);
        final TextView register = (TextView) findViewById(R.id.register);
        final TextView forget = (TextView) findViewById(R.id.forget_password);
        final Button DVButton = (Button) findViewById(R.id.button_dv);

        DVButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent DVIntent = new Intent(MainActivity.this, DVActivity.class);
                startActivity(DVIntent);
            }
        });

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgetIntent = new Intent(MainActivity.this, ForgetActivity.class);
                MainActivity.this.startActivity(forgetIntent);
            }
     /*       public void onClick(View v) {
                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                MainActivity.this.startActivity(registerIntent);
            }*/
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                MainActivity.this.startActivity(registerIntent);
            }
        });



    }

    public void loginUser(View view) {
        EditText l_username = (EditText) findViewById(R.id.username);
        EditText l_password = (EditText) findViewById(R.id.password);
        final Globalv app = (Globalv) getApplication();
        app.set_username(l_username.getText().toString());
        //   final String username = app.get_username();
        final String username = l_username.getText().toString();
        final String password = l_password.getText().toString();
        get_json();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                     //   Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                        // System.out.println(response);
                        //if(response.equals("\"true\""))
                        //if(response.equals("true"))
                        if (true) {
                            Intent intent = new Intent(MainActivity.this, UserAreaActivity_2.class);
                     //       intent.putExtra("json_data",json_string);
                            app.set_json(json_string);
                            startActivity(intent);
                            //    setContentView(R.layout.test);
                            //      Intent intent = new Intent(MainActivity.this,Test_Activity.class);
                            //      startActivity(intent);
                        } else {
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
        /********************************/
        String JSON_STRING;

    String json_string;

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
            new MainActivity.BackgroundTask().execute();
        }

   //     String RR = "Hello World";
   //     System.out.print(RR);

     //           Intent intent = new Intent(this,UserAreaActivity.class);
     //           startActivity(intent);


 /*   public void login_register(View views){
        String button_text;
        button_text = ((Button) views).getText().toString();
        if(button_text.equals("Login"))
        {
            Intent intent = new Intent(this,UserAreaActivity.class);
            startActivity(intent);
        }

    }*/





}
