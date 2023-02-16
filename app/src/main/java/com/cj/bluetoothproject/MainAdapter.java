package com.cj.bluetoothproject;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainAdapter extends BaseAdapter {
    private Context context;
    public List<BluetoothDevice> deviceList;

    public MainAdapter(Context context) {
        this.context = context;
        deviceList = new ArrayList<>();
    }


    public void setData(BluetoothDevice device) {
        if (device.getName() == null) {
            return;
        }
        if (device.getName().isEmpty()) {
            return;
        }
        for (int i = 0; i < deviceList.size(); i++) {
            if (device.getAddress().equals(deviceList.get(i).getAddress())) {
                return;
            }
        }
        deviceList.add(device);
        notifyDataSetChanged();
    }

    public void setClearData() {
        if (deviceList != null) {
            deviceList.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return deviceList == null ? 0 : deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceList == null ? null : deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_main_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (deviceList.get(position).getBondState() == BluetoothDevice.BOND_BONDED) {
            viewHolder.adapterMainItemState.setVisibility(View.VISIBLE);
        } else {
            viewHolder.adapterMainItemState.setVisibility(View.GONE);
        }
        viewHolder.adapterMainItemTitle.setText(deviceList.get(position).getName() == null ?
                "" : deviceList.get(position).getName());
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.adapter_main_item_title)
        TextView adapterMainItemTitle;
        @BindView(R.id.adapter_main_item_state)
        TextView adapterMainItemState;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
