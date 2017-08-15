package zxclc.com.BLE_key_interface;

import android.app.Application;

/**
 * Created by hxlee on 2017/7/13.
 */

public class Globalv extends Application {
    //private Random r = new Random();
    //private int code_number = r.nextInt(9999-1000)+1000;
    //private String code = Integer.toString(code_number);
    private String code;
    public String get_code(){
        return this.code;
    }
    public void set_code(String c){
        this.code = c;
    }
    private String forget_email;
    public String get_email(){
        return this.forget_email;
    }
    public void set_email(String c){
        this.forget_email = c;
    }
    private String username;
    public String get_username() {return this.username;}
    public void set_username(String c){
        this.username = c;
    }
    private String json;
    public String get_json2() {return this.json;}
    public void set_json(String c){
        this.json = c;
    }
}
