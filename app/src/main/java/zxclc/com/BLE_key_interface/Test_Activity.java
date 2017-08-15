package zxclc.com.BLE_key_interface;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class Test_Activity extends AppCompatActivity {
    String JSON_STRING;

    String json_string;

//    String[] data;
//    ListView lv;
//    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
      //  post_username();
        get_json();
        //  clear_json();
    }

  /*  public void getJSON(View view){
        new BackgroundTask().execute();

    }*/

    public void get_json(){
        new BackgroundTask().execute();
    }

    public void post_username(){
        final String username;
        final Globalv app = (Globalv) getApplication();
        username = app.get_username();
        String json_url;
        json_url = "http://106.14.194.158:8080/retrieve_data_5.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, json_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Test_Activity.this,response,Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Test_Activity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
                 protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username",username);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void clear_json(){
        Intent intent = new Intent(this,Test_Activity.class);
        startActivity(intent);
    }

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
    public void parseJSON(View view){
        //   get_json();
        if(json_string == null)
        {
            Toast.makeText(getApplicationContext(),"First Get JSON", Toast.LENGTH_LONG).show();
        }
        else
        {
            Intent intent = new Intent(this,DisplayListView.class);
            intent.putExtra("json_data",json_string);
            startActivity(intent);
        }
    }
    //   public void clearJSON(View view){
    //  Intent intent = new Intent(this,MainActivity.class);
    //  startActivity(intent);
    //  Toast.makeText(getApplicationContext(),"First Get JSON", Toast.LENGTH_LONG).show();
    //   }
}