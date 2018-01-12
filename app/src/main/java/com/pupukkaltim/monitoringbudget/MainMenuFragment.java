package com.pupukkaltim.monitoringbudget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainMenuFragment extends Fragment {

    private Context context;
    public MainMenuFragment() {
        // Required empty public constructor
    }
    private ArrayList<String> fundsCenterArrayList,fundsCenterIdArrayList,fiscalYearArrayList;
    private String idFundsCenter;
    private NavigationView navigationView = null;

    private TextView textViewUnitkerjaMenu,textViewFiscalYearMenu,textViewselamtDatang;
    private View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main_menu, container, false);
        ((DashboardActivity) getActivity()).setActionBarTitle("Menu Utama");
        Boolean state = true;
        textViewselamtDatang = (TextView) v.findViewById(R.id.selamat_datang);
        textViewUnitkerjaMenu = (TextView) v.findViewById(R.id.unitKerjaMenu);
        textViewFiscalYearMenu = (TextView) v.findViewById(R.id.fiscalYearMenu);
        textViewselamtDatang.setText("Selamat Datang \n"+SharedPrefManager.getInstance(getContext()).getUsername());
        textViewFiscalYearMenu.setText("Tahun: "+SharedPrefManager.getInstance(getActivity().getApplicationContext()).getSelectedFiscalYear());
        textViewUnitkerjaMenu.setText(SharedPrefManager.getInstance(getActivity().getApplicationContext()).getSelectedFundCenterName().trim());
        navigationView = (NavigationView)v.findViewById(R.id.nav_view);
        final NavigationView navigationView = ((DashboardActivity) getActivity()).navigationView;

        fundsCenterArrayList = new ArrayList<>();
        fiscalYearArrayList = new ArrayList<>();
        fundsCenterIdArrayList = new ArrayList<>();
        getFundCenter();
        if (checkFundFiscal()){
            CardView cardView = (CardView)v.findViewById(R.id.settingCard);
            cardView.setOnClickListener(
                    new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                            View mview = getActivity().getLayoutInflater().inflate(R.layout.dialog_spinner,null );
                            mBuilder.setTitle("Pilih Funds Center dan Fiscal Year");
                            final Spinner fundCenterSpinner = (Spinner) mview.findViewById(R.id.fundsCenterSpinner);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                    android.R.layout.simple_spinner_item,fundsCenterArrayList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            fundCenterSpinner.setAdapter(adapter);
                            for (int position = 0; position < adapter.getCount(); position++) {
                                if(adapter.getItem(position) == SharedPrefManager.getInstance(getActivity().getApplicationContext()).getSelectedFundCenterName().toString()) {
                                    fundCenterSpinner.setSelection(position);
                                }
                            }
                            final Spinner fiscalYearSpinner = (Spinner) mview.findViewById(R.id.fiscalYearSpinner);
                            ArrayAdapter<String> adapterf = new ArrayAdapter<String>(getActivity(),
                                    android.R.layout.simple_spinner_item,fiscalYearArrayList);
                            adapterf.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            fiscalYearSpinner.setAdapter(adapterf);

                            fundCenterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                    idFundsCenter = fundsCenterIdArrayList.get(position);//This will be the student id.
                                    fiscalYearArrayList = getFiscalYear(idFundsCenter);
                                    ArrayAdapter<String>adapterf = new ArrayAdapter<String>(getActivity(),
                                            android.R.layout.simple_spinner_item,fiscalYearArrayList);
                                    adapterf.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    fiscalYearSpinner.setAdapter(adapterf);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parentView) {
                                    // your code here
                                }
                            });
                            mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(!fundCenterSpinner.getSelectedItem().toString().equals("")){

                                        SharedPrefManager.getInstance(getActivity().getApplicationContext()).changeFundsCenter(
                                                idFundsCenter,
                                                fundCenterSpinner.getSelectedItem().toString(),
                                                fiscalYearSpinner.getSelectedItem().toString()
                                        );

                                        @SuppressLint("RestrictedApi") MainMenuFragment fragment = (MainMenuFragment) getFragmentManager().getFragments().get(0);
                                        fragment.RefreshMenu(fiscalYearSpinner.getSelectedItem().toString(),fundCenterSpinner.getSelectedItem().toString());
                                    }
                                }
                            });
                            mBuilder.setView(mview);
                            AlertDialog dialog = mBuilder.create();
                            dialog.show();
                        }
                    }
            );
            CardView cardLaporanView = (CardView)v.findViewById(R.id.laporanCard);
            cardLaporanView.setOnClickListener(
                    new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            navigationView.getMenu().getItem(1).setChecked(true);
                            ReportFragment fragment = new ReportFragment();
                            android.support.v4.app.FragmentTransaction fragmentTransaction =
                                    getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragmentContainer,fragment);
                            fragmentTransaction.commit();
                        }
                    });

            CardView cardDashboardView = (CardView)v.findViewById(R.id.dasboardCard);
            cardDashboardView.setOnClickListener(
                    new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            navigationView.getMenu().getItem(2).setChecked(true);
                            GraphFragment fragment = new GraphFragment();
                            android.support.v4.app.FragmentTransaction fragmentTransaction =
                                    getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragmentContainer,fragment);
                            fragmentTransaction.commit();
                        }
                    });
            CardView cardInformationView = (CardView)v.findViewById(R.id.infoCard);
            cardInformationView.setOnClickListener(
                    new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            navigationView.getMenu().getItem(3).setChecked(true);
                            InformasiFragment fragment = new InformasiFragment();
                            android.support.v4.app.FragmentTransaction fragmentTransaction =
                                    getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragmentContainer,fragment);
                            fragmentTransaction.commit();
                        }
                    });
            CardView cardFaqView = (CardView)v.findViewById(R.id.faqCard);
            cardFaqView.setOnClickListener(
                    new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            navigationView.getMenu().getItem(4).setChecked(true);
                            FaqFragment fragment = new FaqFragment();
                            android.support.v4.app.FragmentTransaction fragmentTransaction =
                                    getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragmentContainer,fragment);
                            fragmentTransaction.commit();
                        }
                    });
        }

        return v;
    }
    public void getFundCenter(){
        try{
            JSONArray arrayResponse = new JSONArray(SharedPrefManager.getInstance(getActivity().getApplicationContext()).getFundsCenterData());
            for (int i = 0; i < arrayResponse.length(); i++){

                JSONObject dataLpa = arrayResponse.getJSONObject(i);
                JSONArray arrayFiscal = dataLpa.getJSONArray("fiscalYear");
                if(arrayFiscal.length() > 0){
                    String FundsCenterName =  dataLpa.getString("FundsCenterName");
                    String FundsCenter =  dataLpa.getString("FundsCenter");
                    fundsCenterArrayList.add(FundsCenterName.trim());
                    fundsCenterIdArrayList.add(FundsCenter);
                }
            }
        }catch (JSONException e){

        }

    }
    public ArrayList<String> getFiscalYear(String FundsCenterSelected){
        ArrayList<String> fiscalYearsArrayList = new ArrayList<>();
        try{
            JSONArray arrayResponse = new JSONArray(SharedPrefManager.getInstance(getActivity().getApplicationContext()).getFundsCenterData());
            for (int i = 0; i < arrayResponse.length(); i++){

                JSONObject dataLpa = arrayResponse.getJSONObject(i);
                String FundsCenter =  dataLpa.getString("FundsCenter");
                if(FundsCenter.equals(FundsCenterSelected) ){
                    fiscalYearsArrayList.clear();
                   JSONArray arrayFiscal = dataLpa.getJSONArray("fiscalYear");
                    for (int x = 0; x < arrayFiscal.length(); x++){
                        JSONObject objFiscal = arrayFiscal.getJSONObject(x);
                        fiscalYearsArrayList.add(objFiscal.getString("FiscalYear"));
                    }
                    break;
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return fiscalYearsArrayList;
    }
    public Boolean checkFundFiscal(){
        if(Integer.valueOf(fundsCenterIdArrayList.size()).equals(0)){
            return false;
        }else if(Integer.valueOf(fundsCenterIdArrayList.size()).equals(1)){
            getFiscalYear( String.valueOf(getFiscalYear(fundsCenterIdArrayList.get(0).toString())) );
            if(Integer.valueOf(getFiscalYear(fundsCenterIdArrayList.get(0).toString()).size()).equals(1)){
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }
    }


    public Boolean RefreshMenu(String fiscalYear, String fundsCenter){
        textViewUnitkerjaMenu = (TextView) v.findViewById(R.id.unitKerjaMenu);
        textViewFiscalYearMenu = (TextView) v.findViewById(R.id.fiscalYearMenu);
        textViewFiscalYearMenu.setText("Tahun: "+SharedPrefManager.getInstance(getActivity().getApplicationContext()).getSelectedFiscalYear());
        textViewUnitkerjaMenu.setText(SharedPrefManager.getInstance(getActivity().getApplicationContext()).getSelectedFundCenterName());
        return  true;
    }

}
