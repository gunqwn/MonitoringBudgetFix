package com.pupukkaltim.monitoringbudget;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private AppBarLayout appBarLayout;
    private Context context;

    NavigationView navigationView = null;
    Toolbar toolbar = null;
    private TextView textViewUsername, textViewVersion, textViewUnitkerjaMenu;
    private ArrayList<String> fundsCenterArrayList,fundsCenterIdArrayList,fiscalYearArrayList;
    private String idFundsCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        context = this;
        getFundCenter();
        fundsCenterArrayList = new ArrayList<>();
        fiscalYearArrayList = new ArrayList<>();
        fundsCenterIdArrayList = new ArrayList<>();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        fiscalYearArrayList.add("2017");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MainMenuFragment fragment = new MainMenuFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer,fragment);
        fragmentTransaction.commit();






        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        View headerView = navigationView.getHeaderView(0);

        textViewUnitkerjaMenu = (TextView) headerView.findViewById(R.id.unitKerjaMenu);
        textViewUsername = (TextView) headerView.findViewById(R.id.userNameTextView);
        textViewVersion = (TextView) headerView.findViewById(R.id.versionTextView);

        textViewVersion.setText(Constants.VERSION);
        textViewUsername.setText(SharedPrefManager.getInstance(this).getUsername());

    }
    public void Logout(String msg){
        Toast.makeText(
                getApplicationContext(),
                msg,
                Toast.LENGTH_SHORT
        ).show();

        SharedPrefManager.getInstance(this).logout();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
    public void getFundCenter(){
        String url = Constants.ROOT_URL+"getUserFundsCenter.php";
        StringRequest request = new StringRequest(Request.Method.POST, url
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String firstChar = String.valueOf(response.charAt(0));


                fundsCenterArrayList = new ArrayList<>();

                try {
                    if(firstChar.equalsIgnoreCase("[")){
                        JSONArray arrayResponse = new JSONArray(response);
                        for (int i = 0; i < arrayResponse.length(); i++){

                            JSONObject dataLpa = arrayResponse.getJSONObject(i);
                            String FundsCenterName =  dataLpa.getString("FundsCenterName");
                            String FundsCenter =  dataLpa.getString("FundsCenter");
                            fundsCenterArrayList.add(FundsCenterName);
                            fundsCenterIdArrayList.add(FundsCenter);
                        }
                    }else{
                        JSONObject unautorizeObject = new JSONObject(response);
                        if(unautorizeObject.getBoolean("autorize") == false){
                            Logout("Sesi anda telah habis, Silahkan Login Kembali");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("userid",String.valueOf(SharedPrefManager.getInstance(context).getUserId()));
                params.put("bearer",SharedPrefManager.getInstance(context).getToken());
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(request);

    }
    @Override
    public void onBackPressed() {
        Fragment f = this.getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (f instanceof MainMenuFragment){
                confirmDialog(getApplicationContext());
            }else{
                showHome();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    private void confirmDialog(Context context){
            new AlertDialog.Builder(this)
                    .setMessage("Apakah anda ingin keluar?")
                    .setCancelable(false)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DashboardActivity.this.finish();
                        }
                    })
                    .setNegativeButton("Tidak", null)
                    .show();

    }
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
    Fragment fragment = null;
    private void showHome(){
        fragment = new MainMenuFragment();
        if(fragment != null){
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer,fragment);
            fragmentTransaction.commit();
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            fragment = new MainMenuFragment();

        } else if (id == R.id.nav_report) {
            fragment = new ReportFragment();

        } else if (id == R.id.nav_dashboard) {
            fragment = new GraphFragment();
        } else if (id == R.id.nav_update) {
            fragment = new InformasiFragment();

        } else if (id == R.id.nav_about) {
            fragment = new FaqFragment();


        } else if (id == R.id.nav_logout) {
            SharedPrefManager.getInstance(this).logout();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }

        if(fragment != null){
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer,fragment);
            fragmentTransaction.commit();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
