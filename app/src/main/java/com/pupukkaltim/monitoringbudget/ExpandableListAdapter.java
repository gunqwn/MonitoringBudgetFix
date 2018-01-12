package com.pupukkaltim.monitoringbudget;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by ROG-STRIX on 25/12/2017.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter{
    private Context context;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHashMap;

    public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listHashMap) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listHashMap.get(listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String)getGroup(groupPosition);
        StringTokenizer st = new StringTokenizer(headerTitle, "|");
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group,null);
        }
        TwoLineListItem bevGroup = (TwoLineListItem)convertView.findViewById(R.id.groupLayout);



        if(isExpanded){
            bevGroup.setBackgroundColor(context.getResources().getColor(R.color.green));

        }else{
            bevGroup.setBackgroundColor(context.getResources().getColor(R.color.lightgray));

        }
        TextView lbListHeader = (TextView)convertView.findViewById(R.id.lbListHeader);
        lbListHeader.setTypeface(null, Typeface.BOLD);
        lbListHeader.setText(st.nextToken());

        TextView lbListHeader2 = (TextView)convertView.findViewById(R.id.lbListHeader2);
        lbListHeader2.setTypeface(null, Typeface.BOLD);
        lbListHeader2.setText(st.nextToken()+"%");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String)getChild(groupPosition,childPosition);
        StringTokenizer st = new StringTokenizer(childText, "//|");
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem,null);
        }
        TextView txtListChild = (TextView)convertView.findViewById(R.id.lbListItem);
        TextView txtListChild2 = (TextView)convertView.findViewById(R.id.lbListItem2);
        txtListChild.setText(st.nextToken());
        txtListChild2.setText(st.nextToken());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true ;
    }
}
