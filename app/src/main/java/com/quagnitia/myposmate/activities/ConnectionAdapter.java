package com.quagnitia.myposmate.activities;

import android.content.Context;
import android.preference.Preference;
import android.support.v4.app.FragmentActivity;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.internal.$Gson$Preconditions;
import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.arke.RequestParameters;
import com.quagnitia.myposmate.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.prefs.Preferences;

public class ConnectionAdapter extends BaseAdapter {
    ArrayList connectionList;
    Context context;
    PreferencesManager preferenceManager;
 //  int selectedPosition;

    public ConnectionAdapter(Context context, ArrayList connectionList, int selectedPosition) {
        this.context=context;
        this.connectionList=connectionList;
    //    this.selectedPosition=selectedPosition;
        preferenceManager=new PreferencesManager(context);
    }

    @Override
    public int getCount() {
        return connectionList.size();
    }

    @Override
    public Object getItem(int position) {
        return connectionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
RadioButton rbLive;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.connection_list_item, null);
            RadioButton r = (RadioButton)v.findViewById(R.id.rbLive);
        }
        /*TextView tv = (TextView)v.findViewById(R.id.textview);*/

        RadioButton r = (RadioButton)v.findViewById(R.id.rbLive);
        r.setText(""+connectionList.get(position));
        r.setChecked(position == preferenceManager.getSelectedPosition());
        r.setTag(position);
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selectedPosition = (Integer)view.getTag();
                preferenceManager.setSelectedPosition( (Integer)view.getTag());
                Log.v("SelectedPos","on Select "+preferenceManager.getSelectedPosition()+"  "+ (Integer)view.getTag());
                notifyDataSetChanged();
            }
        });
        return v;
/*        final ViewHolder view;
        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView == null) {

            view = new ViewHolder();

            convertView = inflater.inflate(R.layout.connection_list_item, null);
            view.rbLive = (RadioButton) convertView
                    .findViewById(R.id.rbLive);

            view.rbLive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (isChecked) {

                        connectionList.get((Integer) buttonView.getTag()).put(
                                "checked", "true");
                        for (int i = 0; i < connectionList.size(); i++) {
                            if (i != (Integer) buttonView.getTag()) {
                                if (connectionList.get(i).containsKey("checked"))
                                    connectionList.get(i).remove("checked");
                            }
                        }
                    } else {
                        connectionList.get((Integer) buttonView.getTag()).remove(
                                "checked");
                    }

                    notifyDataSetChanged();
                }
            });

            convertView.setTag(R.id.selection_checkbox, view.rbLive);
            convertView.setTag(view);

        }

        else {
            view = (ViewHolder) convertView.getTag();
        }
        view.rbLive.setTag(position);

        view.rbLive.setText(connectionList.get(position).get("name"));

        if (connectionList.get(position).containsKey("checked")) {
            view.rbLive.setChecked(true);
        } else
            view.rbLive.setChecked(false);

        return convertView;*/
    /* ViewHolder holder = null;

        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView == null) {
           convertView = inflater.inflate(
                    R.layout.connection_list_item, null);
            holder = new ViewHolder();

            holder.rbLive = (RadioButton) convertView.findViewById(R.id.rbLive);





            convertView.setTag(holder);
       } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }



        return convertView;*/
    }
}
