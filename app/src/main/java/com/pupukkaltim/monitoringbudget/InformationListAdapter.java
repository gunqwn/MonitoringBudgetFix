package com.pupukkaltim.monitoringbudget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ROG-STRIX on 01/01/2018.
 */

public class InformationListAdapter extends BaseAdapter {


    private Context mContext;
    private List<Information> mProductList;

    //Constructor

    public InformationListAdapter(Context mContext, List<Information> mProductList) {
        this.mContext = mContext;
        this.mProductList = mProductList;
    }

    @Override
    public int getCount() {
        return mProductList.size();
    }

    @Override
    public Object getItem(int position) {
        return mProductList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.information_list, null);
        TextView ifJudul = (TextView)v.findViewById(R.id.info_judul);
        TextView iffund = (TextView)v.findViewById(R.id.info_fundCenter);
        TextView ifDesc = (TextView)v.findViewById(R.id.info_Description);
        TextView ifDate = (TextView)v.findViewById(R.id.date_message);
        //Set text for TextView
        ifJudul.setText(mProductList.get(position).getCaption());
        iffund.setText(mProductList.get(position).getFundCenter());
        ifDesc.setText(mProductList.get(position).getInformasi());
        ifDate.setText(mProductList.get(position).getTanggal());

        //Save product id to tag
        v.setTag(mProductList.get(position).getId());

        return v;
    }
}
