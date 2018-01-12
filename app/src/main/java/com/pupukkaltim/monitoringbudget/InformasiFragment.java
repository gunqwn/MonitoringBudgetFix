package com.pupukkaltim.monitoringbudget;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InformasiFragment extends Fragment {
    public InformasiFragment() {
        // Required empty public constructor
    }
    private ProgressDialog progressDialog;
    ExpandableListView expandableListView;
    android.widget.ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    public HashMap<String, List<String>> expandableListDetail = new HashMap<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View RootView = inflater.inflate(R.layout.fragment_informasi, container, false);
        ((DashboardActivity) getActivity()).setActionBarTitle("Informasi Terkini");
        expandableListView = (ExpandableListView) RootView.findViewById(R.id.expandableListView);

        expandableListDetail = new HashMap<String, List<String>>();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Tunggu Sebentar...");
        swipeRefreshLayout = (SwipeRefreshLayout) RootView.findViewById(R.id.swipe_info);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getInformation();
                    }
                },30);
            }
        });
        progressDialog.show();
        getInformation();



      //  expandableListDetail = expandableListDetails;
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });
            return RootView;
    }
    public void getInformation(){
        expandableListTitle.clear();
        expandableListDetail.clear();
        String url = Constants.ROOT_URL+"getInformationUpdate.php";
        StringRequest request = new StringRequest(Request.Method.POST, url
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                progressDialog.dismiss();
                String firstChar = String.valueOf(response.charAt(0));

                HashMap<String, List<String>> expandableListDetails = new HashMap<String, List<String>>();

                try {
                    if(firstChar.equalsIgnoreCase("[")){
                        JSONArray arrayResponse = new JSONArray(response);

                        for (int i = 0; i < arrayResponse.length(); i++){

                            JSONObject dataInfo = arrayResponse.getJSONObject(i);
                            Integer infoId =  dataInfo.getInt("ID");
                            String infoCaption = dataInfo.getString("Caption");
                            String informasi = dataInfo.getString("Informasi");
                            String infoFund = getFundCenterName(dataInfo.getString("FundCenter"));
                            String tanggal = dataInfo.getString("CreatedOn");
                            if(!tanggal.equals("null")){
                                String strCurrentDate = tanggal;
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                                Date newDate = format.parse(strCurrentDate);

                                format = new SimpleDateFormat("dd/mm/yyyy");
                                tanggal = format.format(newDate);
                            }else{
                                tanggal = " ";
                            }
                            List<String> data = new ArrayList<String>();
                            data.add(informasi);
                            expandableListDetails.put(infoCaption+"|"+infoFund+"|"+tanggal, data);
                        }
                        expandableListDetail = expandableListDetails;
                        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
                        expandableListAdapter = new InfoExpandableListAdapter(getContext(), expandableListTitle, expandableListDetail);
                        expandableListView.setAdapter(expandableListAdapter);

                    }else{
                        JSONObject unautorizeObject = new JSONObject(response);
                        if(unautorizeObject.getBoolean("autorize") == false){
                            DashboardActivity dashboardActivity = new DashboardActivity();
                            dashboardActivity.Logout("Sesi anda telah habis, Silahkan Login Kembali");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                Toast.makeText(getActivity(), "Gagal mengambil data, Silahkan coba lagi",
                        Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                String fundCenterParam ="";
                try{
                    JSONArray arrayResponse = new JSONArray(SharedPrefManager.getInstance(getActivity().getApplicationContext()).getFundsCenterData());
                    for (int i = 0; i < arrayResponse.length(); i++){

                        JSONObject dataLpa = arrayResponse.getJSONObject(i);
                            String FundsCenterName =  dataLpa.getString("FundsCenterName");
                            String FundsCenter =  dataLpa.getString("FundsCenter");
                            fundCenterParam += FundsCenter;
                            if(i != arrayResponse.length()-1){
                                fundCenterParam += ",";
                            }
                    }
                }catch (JSONException e){

                }
                params.put("fundsCenter",fundCenterParam);
                params.put("bearer",SharedPrefManager.getInstance(getContext()).getToken());
                return params;
            }
        };
        RequestHandler.getInstance(getContext()).addToRequestQueue(request);

    }
    public String getFundCenterName(String fundCenter){
        try{
            JSONArray arrayResponse = new JSONArray(SharedPrefManager.getInstance(getActivity().getApplicationContext()).getFundsCenterData());
            for (int i = 0; i < arrayResponse.length(); i++){

                JSONObject dataLpa = arrayResponse.getJSONObject(i);
                String FundsCenterName =  dataLpa.getString("FundsCenterName");
                String FundsCenter =  dataLpa.getString("FundsCenter");
                if(FundsCenter.equals(fundCenter)){
                    return FundsCenterName;
                }
            }
        }catch (JSONException e){

        }
        return fundCenter;
    }
    @Override
    public void onPause() {
        super.onPause();
        if (swipeRefreshLayout!=null) {
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.destroyDrawingCache();
            swipeRefreshLayout.clearAnimation();
        }
    }

}
