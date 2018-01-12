package com.pupukkaltim.monitoringbudget;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

public class SharedPrefManager {
    private static SharedPrefManager mInstance;
    private static Context mCtx;
    private static final String SHARED_PREF_NAME = "mysharedpref12";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_EMAIL = "useremail";
    private static final String KEY_USER_ID = "userid";
    private static final String KEY_FUND_CENTER = "fundcenter";
    private static final String KEY_SELECTED_FUND_CENTER = "selectedfundcenter";
    private static final String KEY_SELECTED_FUND_CENTER_NAME = "selectedfundcentername";
    private static final String KEY_SELECTED_FISCAL_YEAR = "selectedfiscalyear";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_DATA_FUNDS = "fundcenterdata";
    private static final String KEY_INFO_DATA = "infodata";


    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public boolean userLogin(String id, String username, String email,
                             String selectedfundCenter, String selectedfundCenterName,
                             String selectedFiscalYear, String token, String fundsCenterData){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, id);
        editor.putString(KEY_USER_EMAIL,email);
        editor.putString(KEY_USERNAME,username);
        editor.putString(KEY_SELECTED_FUND_CENTER, selectedfundCenter);
        editor.putString(KEY_SELECTED_FUND_CENTER_NAME, selectedfundCenterName);
        editor.putString(KEY_SELECTED_FISCAL_YEAR, selectedFiscalYear);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_DATA_FUNDS, fundsCenterData);
        editor.apply();

        return  true;
    }

    public boolean changeFundsCenter(String selectedfundCenter, String selectedfundCenterName, String selectedFiscalYear){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SELECTED_FUND_CENTER, selectedfundCenter);
        editor.putString(KEY_SELECTED_FUND_CENTER_NAME, selectedfundCenterName);
        editor.putString(KEY_SELECTED_FISCAL_YEAR, selectedFiscalYear);
        editor.apply();

        return  true;
    }

    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences.getString(KEY_USERNAME, null) != null){
            return true;
        }else{
            return false;
        }
    }

    public boolean logout(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }

    public String getUsername(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null);
    }
    public String getUserId(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_ID,null);
    }
    public String getUserEmail(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }
    public String getToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TOKEN, null);
    }
    public String getSelectedFundCenter(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_SELECTED_FUND_CENTER, "");
    }
    public String getSelectedFundCenterName(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_SELECTED_FUND_CENTER_NAME, "");
    }
    public String getSelectedFiscalYear(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_SELECTED_FISCAL_YEAR, "");
    }
    public String getFundsCenterData(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DATA_FUNDS, "");
    }
    public String getInfoData(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_INFO_DATA, "");
    }
}