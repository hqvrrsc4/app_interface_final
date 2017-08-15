package zxclc.com.BLE_key_interface;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DisplayListView extends AppCompatActivity {
    String JSON_STRING;
    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;
    ContactAdapter contactAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.display_listview_layout);
        setContentView(R.layout.main_1);
        listView = (ListView) findViewById(R.id.listview2);
        contactAdapter = new ContactAdapter(this,R.layout.raw_layout);
        listView.setAdapter(contactAdapter);
        json_string = getIntent().getExtras().getString("json_data");
        final String id_username;
        final Globalv app = (Globalv) getApplication();
        id_username = app.get_username();
        try {
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("result");
            int count=0;
            String username,vid,fuel,window,ac;
            while(count<jsonArray.length()){
                JSONObject JO = jsonArray.getJSONObject(count);
                username = JO.getString("username");
                vid = JO.getString("vehicle_id");
                fuel = JO.getString("fuel");
                window = JO.getString("window");
                ac = JO.getString("air_conditioner");
                Contacts contacts = new Contacts(username,vid,fuel,window,ac);
                if(username.equals(id_username)){
                contactAdapter.add(contacts);}
                count++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    /************************/
    public void parseJSON(View view){
        Intent intent = new Intent(this,UserAreaActivity_2.class);
        startActivity(intent);
        //Toast.makeText(getApplicationContext(),"First Get JSON", Toast.LENGTH_LONG).show();
    }
}
