package com.pupukkaltim.monitoringbudget;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import com.github.clans.fab.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionMenu;
import com.idunnololz.widgets.AnimatedExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment {

    private RequestQueue mQueue;
    private Context context;
    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader = new ArrayList<>();
    private HashMap<String,List<String>> listHash = new HashMap<>();
    private String fiscalYear, unitKerja, token;
    private View RootView;
    private TextView txtheaderfunds,txtheaderyear;
    private ProgressDialog progressDialog;
    ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;

    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingDownload, floatingShare, floatingDownloadDetail;

    public ReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View RootView = inflater.inflate(R.layout.fragment_report, container, false);

        mProgressDialog = new ProgressDialog(RootView.getContext());
        mProgressDialog.setMessage("Mendownload File");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        ((DashboardActivity) getActivity()).setActionBarTitle("Laporan Anggaran");
        listView = (ExpandableListView)RootView.findViewById(R.id.lvExp);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here
        }
        txtheaderfunds = (TextView) RootView.findViewById(R.id.txtheaderfunds);
        txtheaderyear = (TextView) RootView.findViewById(R.id.txtheaderyear);
        txtheaderfunds.setText(SharedPrefManager.getInstance(getActivity().getApplicationContext()).getSelectedFundCenterName());
        txtheaderyear.setText(SharedPrefManager.getInstance(getActivity().getApplicationContext()).getSelectedFiscalYear());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Tunggu Sebentar...");

        swipeRefreshLayout = (SwipeRefreshLayout) RootView.findViewById(R.id.swipe_report);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getDataLpaReport(RootView);
                    }
                },30);
            }
        });
        progressDialog.show();
        getDataLpaReport(RootView);

        materialDesignFAM = (FloatingActionMenu)RootView.findViewById(R.id.social_floating_menu);
        floatingDownload = (FloatingActionButton)RootView.findViewById(R.id.floating_download);
        floatingShare = (FloatingActionButton)RootView.findViewById(R.id.floating_share);
        floatingDownloadDetail = (FloatingActionButton)RootView.findViewById(R.id.floating_downloadDetail);
        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousGroup)
                    listView.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        floatingDownload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { new getRekapLaporanPDF("RekapLaporan.pdf",1).execute();  }
        });
        floatingShare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { new getRekapLaporanPDF("RekapLaporan.pdf",2).execute();  }
        });
        floatingDownloadDetail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { new getDetilLaporanPDF("DetilLaporan.pdf",1).execute();    }
        });

        return RootView;
    }

    public void getDataLpaReport(View RootView){

        listDataHeader.clear();
        listHash.clear();

        context = this.getContext();
        mQueue = Volley.newRequestQueue(context);
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();
        fiscalYear = SharedPrefManager.getInstance(getActivity().getApplicationContext()).getSelectedFiscalYear();
        unitKerja = SharedPrefManager.getInstance(getActivity().getApplicationContext()).getSelectedFundCenter();
        String url = Constants.ROOT_URL+"getLPA.php";
        StringRequest request = new StringRequest(Request.Method.POST, url
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                Log.v("st",response);
                progressDialog.dismiss();
                String firstChar = String.valueOf(response.charAt(0));
                ArrayList<String> myArrayList = new ArrayList<>();

                try {
                    if(firstChar.equalsIgnoreCase("[")){
                        JSONArray arrayResponse = sortJsonArray(new JSONArray(response));
                        for (int i = 0; i < arrayResponse.length(); i++){

                            JSONObject dataLpa = arrayResponse.getJSONObject(i);
                            String CommitmentItem =  dataLpa.getString("CommitmentItem");
                            Log.v("commitment",CommitmentItem);
                            myArrayList.add(CommitmentItem);


                            listDataHeader.add(dataLpa.getString("CommitmentItemName")+"|"+dataLpa.getString("Persentase"));
                            Log.v("data Header",dataLpa.getString("CommitmentItemName")+"|"+dataLpa.getString("Persentase"));
                            NumberFormat formatter = new DecimalFormat("#,###");

                            List<String> data = new ArrayList<>();
                            data.add("Anggaran ://|Rp."+formatter.format(Double.valueOf(dataLpa.getString("Anggaran"))));
                            data.add("Komitmen PR ://|Rp."+formatter.format(Double.valueOf(dataLpa.getString("KomitmenPR"))));
                            data.add("Komitmen PO ://|Rp."+formatter.format(Double.valueOf(dataLpa.getString("KomitmenPO"))));
                            data.add("Realisasi ://Rp."+formatter.format(Double.valueOf(dataLpa.getString("Realisasi"))));
                            data.add("Sisa Anggaran ://Rp."+formatter.format(Double.valueOf(dataLpa.getString("SisaAnggaran"))));


                            listHash.put(listDataHeader.get(i),data);

                            listAdapter = new ExpandableListAdapter(context, listDataHeader, listHash);
                            listView.setAdapter(listAdapter);

                            listAdapter.notifyDataSetChanged();

                        }
                        Bundle arguments = getArguments();
                        try{
                            Integer index_lv = arguments.getInt("report_index");
                            listView.expandGroup(index_lv);
                        }catch (Exception e){

                        }

                    }else{
                        JSONObject unautorizeObject = new JSONObject(response);
                        if(unautorizeObject.getBoolean("autorize") == false){
                            ((DashboardActivity)getActivity()).Logout("Sesi anda telah habis, Silahkan Login Kembali");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                progressDialog.dismiss();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("tahun",fiscalYear);
                params.put("fundsCenter",unitKerja);
                params.put("bearer",SharedPrefManager.getInstance(context).getToken());
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);

    }

    class getRekapLaporanPDF extends AsyncTask<String, String, String> {
        String fileName;
        int index;

        getRekapLaporanPDF(String fileName, int index) {
            // list all the parameters like in normal class define
            this.fileName = fileName;
            this.index = index;
        }
        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }
        protected void onProgressUpdate(String... progress) {
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }
        @Override
        protected String doInBackground(String... strings) {

            fiscalYear = SharedPrefManager.getInstance(getActivity().getApplicationContext()).getSelectedFiscalYear();
            unitKerja = SharedPrefManager.getInstance(getActivity().getApplicationContext()).getSelectedFundCenter();
            token = SharedPrefManager.getInstance(getActivity().getApplicationContext()).getToken();
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "pdf");
            folder.mkdir();
            File file = new File(folder, fileName);
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            new Downloader(getContext()).DownloadFile(Constants.ROOT_URL+"getRekapLaporanAnggaran.php", file,fiscalYear,unitKerja,token);
            if(index == 1){
                File files = new File(Environment.getExternalStorageDirectory()+"/pdf/"+fileName);
                PackageManager packageManager = getActivity().getPackageManager();
                Intent testIntent = new Intent(Intent.ACTION_VIEW);
                testIntent.setType("application/pdf");
                List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(files);
                intent.setDataAndType(uri, "application/pdf");
                startActivity(intent);
            }else if(index == 2){
                File outputFile = new File(Environment.getExternalStorageDirectory()+"/pdf/"+fileName);
                Uri uri = Uri.fromFile(outputFile);
                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.setType("application/pdf");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                //share.setPackage("com.whatsapp");
                startActivity(share);
            }
            return null;
        }
        protected void onPostExecute(String file_url) {
            mProgressDialog.dismiss();
        }
    }

    class getDetilLaporanPDF extends AsyncTask<String, String, String> {
        String fileName;
        int index;

        getDetilLaporanPDF(String fileName, int index) {
            // list all the parameters like in normal class define
            this.fileName = fileName;
            this.index = index;
        }
        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            fiscalYear = SharedPrefManager.getInstance(getActivity().getApplicationContext()).getSelectedFiscalYear();
            unitKerja = SharedPrefManager.getInstance(getActivity().getApplicationContext()).getSelectedFundCenter();
            token = SharedPrefManager.getInstance(getActivity().getApplicationContext()).getToken();
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "pdf");
            folder.mkdir();
            File file = new File(folder, fileName);
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            new Downloader(getContext()).DownloadFile(Constants.ROOT_URL+"getDetilLaporanAnggaran.php", file,fiscalYear,unitKerja,token);
            File files = new File(Environment.getExternalStorageDirectory()+"/pdf/"+fileName);
            PackageManager packageManager = getActivity().getPackageManager();
            Intent testIntent = new Intent(Intent.ACTION_VIEW);
            testIntent.setType("application/pdf");
            List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(files);
            intent.setDataAndType(uri, "application/pdf");
            startActivity(intent);

            return null;
        }
        protected void onPostExecute(String file_url) {
            mProgressDialog.dismiss();
        }
    }


    public void getDetilLaporanPDF(){

    }


    public static Intent getOpenFacebookIntent(Context context) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("fb://page/376227335860239")); //Trys to make intent with FB's URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/karthikofficialpage")); //catches and opens a url to the desired page
        }
    }
    public static JSONArray sortJsonArray(JSONArray array) {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            try {
                jsons.add(array.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                Integer lid = 0;
                try {
                    lid = Integer.valueOf(lhs.getString("Persentase"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Integer rid = 0;
                try {
                    rid = Integer.valueOf(rhs.getString("Persentase"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return rid.compareTo(lid);
            }
        });
        return new JSONArray(jsons);
    }

    public static Intent getOpenTwitterIntent(Context context) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.twitter.android", 0); //Checks if Twitter is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/drkarthiik")); //Trys to make intent with Twitter's's URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/drkarthiik")); //catches and opens a url to the desired page
        }
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
