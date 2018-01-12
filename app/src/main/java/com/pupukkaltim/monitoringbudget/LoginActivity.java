package com.pupukkaltim.monitoringbudget;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.franmontiel.fullscreendialog.FullScreenDialogFragment;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button login;
    EditText usernameText, passwordText;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        //jika sebelumnya sudah login maka langsung ke Profile Activity.
        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this,DashboardActivity.class));
            return;
        }
        login = (Button) findViewById(R.id.loginButton);
        usernameText = (EditText) findViewById(R.id.userEmail);
        passwordText = (EditText) findViewById(R.id.userPassword);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Tunggu Sebentar...");
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == login){
            if(isConnectedToServer("http://apimonita.pupukkaltim.com/phpver.php",1000)){
                userLogin();
            }else{
                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                alertDialog.setTitle("Maaf");
                alertDialog.setMessage("Tidak bisa menghubungkan ke server");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

            }
        }
    }
    private  void userLogin(){
        //menyimpan EditText kedalam sebuah variabel String dengan di trim(menghapus spasi diawal dan di akhir)
        final String username = usernameText.getText().toString().trim();
        final String password = passwordText.getText().toString().trim();

        //menampilkan loading
        progressDialog.show();
        //StringRequest adalah objek dari volley
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, Constants.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String dataInformasi = "";
                progressDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);
                    if(!obj.getBoolean("error")){
                        ReportFragment reportFragment = new ReportFragment();

                        JSONArray fundCenterArray = reportFragment.sortJsonArray(obj.getJSONArray("fundCenter"));
                        JSONObject fundObject = fundCenterArray.getJSONObject(0);
                        JSONArray fiscalYearArray = fundObject.getJSONArray("fiscalYear");
                        JSONObject fiscalYearObject = fiscalYearArray.getJSONObject(0);

                        Log.v("dataInfo",dataInformasi);
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(
                                obj.getString("id"),
                                obj.getString("username"),
                                obj.getString("email"),
                                fundObject.getString("FundsCenter"),
                                fundObject.getString("FundsCenterName"),
                                fiscalYearObject.getString("FiscalYear"),
                                //yearObject.getString("FiscalYear"),
                                obj.getString("token"),
                                fundCenterArray.toString()
                        );
                        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                        finish();
                    }else{
                        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                        alertDialog.setTitle("Maaf");
                        alertDialog.setMessage("Username dan Password tidak cocok");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(
                        getApplicationContext(),
                        error.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username",username);
                params.put("password",password);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    public boolean isConnectedToServer(String url, int timeout) {
        try{
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();
            return true;

        } catch (Exception e) {
            Log.v("not konek",e.toString());
            return false;
        }
    }

}
