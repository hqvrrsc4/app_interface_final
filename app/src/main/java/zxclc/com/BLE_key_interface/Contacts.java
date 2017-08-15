package zxclc.com.BLE_key_interface;

/**
 * Created by hxlee on 2017/8/6.
 */

public class Contacts {
    private String username,vid,fuel,window,ac;

    public Contacts(String username, String vid, String fuel, String window, String ac)
    {
        this.setUsername(username);
        this.setVid(vid);
        this.setFuel(fuel);
        this.setWindow(window);
        this.setAc(ac);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public String getWindow() {
        return window;
    }

    public void setWindow(String window) {
        this.window = window;
    }

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }
}
