package zxclc.com.BLE_key_interface;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hxlee on 2017/8/6.
 */

public class ContactAdapter extends ArrayAdapter {
    List list = new ArrayList();
    public ContactAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    public void add(Contacts object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row;
        row = convertView;
        ContactHolder contactHolder;
        if(row==null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.raw_layout,parent,false);
            contactHolder = new ContactHolder();
            contactHolder.tx_username = (TextView) row.findViewById(R.id.tx_username);
            contactHolder.tx_vid = (TextView) row.findViewById(R.id.tx_vid);
            contactHolder.tx_fuel = (TextView) row.findViewById(R.id.tx_fuel);
            contactHolder.tx_window = (TextView) row.findViewById(R.id.tx_window);
            contactHolder.tx_ac = (TextView) row.findViewById(R.id.tx_ac);
            row.setTag(contactHolder);
        }
        else
        {
            contactHolder = (ContactHolder)row.getTag();
        }
        Contacts contacts = (Contacts) this.getItem(position);
        contactHolder.tx_username.setText("用户名："+contacts.getUsername());
        contactHolder.tx_vid.setText("车牌号："+contacts.getVid());
        contactHolder.tx_fuel.setText("油量："+contacts.getFuel());
        contactHolder.tx_window.setText("车门："+contacts.getWindow());
        contactHolder.tx_ac.setText("后备箱："+contacts.getAc());
        return row;
    }

    class ContactHolder
    {
        TextView tx_username,tx_vid,tx_fuel,tx_window,tx_ac;
    }
}
