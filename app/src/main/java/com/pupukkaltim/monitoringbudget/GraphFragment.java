package com.pupukkaltim.monitoringbudget;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
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
import com.idunnololz.widgets.AnimatedExpandableListView;
import com.txusballesteros.widgets.FitChart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment {

    private RequestQueue mQueue;
    private Context context;
    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHash;
    private String fiscalYear, unitKerja;
    private TextView txtheaderfunds,txtheaderyear;
    private View RootView;
    private TextView[] percentageText,titleItem;
    private FitChart[] fitchart;
    private RecyclerView recyclerView;
    private List<Graph> graphList;
    private GraphAdapter adapter;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;

    public GraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View RootView = inflater.inflate(R.layout.fragment_graph, container, false);
        ((DashboardActivity) getActivity()).setActionBarTitle("Dashboard");
        recyclerView = (RecyclerView) RootView.findViewById(R.id.recycler_view);
        graphList = new ArrayList<>();

        txtheaderfunds = (TextView) RootView.findViewById(R.id.txtheaderfunds);
        txtheaderyear = (TextView) RootView.findViewById(R.id.txtheaderyear);
        txtheaderfunds.setText(SharedPrefManager.getInstance(getActivity().getApplicationContext()).getSelectedFundCenterName());
        txtheaderyear.setText(SharedPrefManager.getInstance(getActivity().getApplicationContext()).getSelectedFiscalYear());


        adapter = new GraphAdapter(this, graphList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Tunggu Sebentar...");
        swipeRefreshLayout = (SwipeRefreshLayout) RootView.findViewById(R.id.swipe_graph);
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

          return RootView;
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


    public void getDataLpaReport(){
        graphList.clear();
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
                progressDialog.dismiss();
                String firstChar = String.valueOf(response.charAt(0));
                ArrayList<String> myArrayList = new ArrayList<>();

                try {
                    if(firstChar.equalsIgnoreCase("[")){
                        JSONArray arrayResponse = sortJsonArray(new JSONArray(response));
                        for(int i=0; i<arrayResponse.length(); i++){
                            JSONObject dataLpa = arrayResponse.getJSONObject(i);
                            String CommitmentItem =  dataLpa.getString("CommitmentItemName");
                            String Persentase = dataLpa.getString("Persentase");

                            Graph a = new Graph(CommitmentItem, Integer.valueOf(Persentase));
                            graphList.add(a);
                        }
                        adapter.notifyDataSetChanged();

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
                params.put("tahun",fiscalYear);
                params.put("fundsCenter",unitKerja);
                params.put("bearer",SharedPrefManager.getInstance(context).getToken());
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);

    }
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
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
