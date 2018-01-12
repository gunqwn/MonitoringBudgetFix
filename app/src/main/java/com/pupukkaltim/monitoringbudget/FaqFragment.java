package com.pupukkaltim.monitoringbudget;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pupukkaltim.monitoringbudget.GraphFragment.sortJsonArray;


/**
 * A simple {@link Fragment} subclass.
 */
public class FaqFragment extends Fragment {
    private RequestQueue mQueue;
    private List<faq> faqList = new ArrayList<>();
    private RecyclerView recyclerView;
    private FaqAdapter mAdapter;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    public FaqFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_faq, container, false);
        // Inflate the layout for this fragment
        ((DashboardActivity) getActivity()).setActionBarTitle("Faq");
        super.onCreate(savedInstanceState);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);

        mAdapter = new FaqAdapter(faqList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Tunggu Sebentar...");
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_faq);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getDataLpaReport();
                    }
                },30);
            }
        });
        progressDialog.show();
        getDataLpaReport();

        return v;
    }
    public void getDataLpaReport(){
        faqList.clear();
        Context context = this.getContext();
        mQueue = Volley.newRequestQueue(context);
        String url = Constants.ROOT_URL+"getFAQ.php";
        StringRequest request = new StringRequest(Request.Method.POST, url
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                progressDialog.dismiss();
                String firstChar = String.valueOf(response.charAt(0));
                faq dataFaq;
                try {
                    if(firstChar.equalsIgnoreCase("[")){
                        JSONArray arrayResponse = sortJsonArray(new JSONArray(response));
                        for(int i=0; i<arrayResponse.length(); i++){
                            JSONObject datafaq = arrayResponse.getJSONObject(i);
                            String Caption =  datafaq.getString("Caption");
                            String Path = datafaq.getString("Path");
                            dataFaq = new faq(Caption, Path);
                            faqList.add(dataFaq);
                        }
                        mAdapter.notifyDataSetChanged();

                    }else{
                        JSONObject unautorizeObject = new JSONObject(response);
                        if(unautorizeObject.getBoolean("autorize") == false){
                            ((DashboardActivity)getActivity()).Logout
                                    ("Sesi anda telah habis, Silahkan Login Kembali");
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
                Toast.makeText(getActivity(), "Gagal mengambil data, Silahkan coba lagi",
                        Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("bearer",SharedPrefManager.getInstance(getContext()).getToken());
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);

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
