package com.pupukkaltim.monitoringbudget;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by ROG-STRIX on 09/01/2018.
 */

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.MyViewHolder> {


        private List<faq> faqList;
        ProgressDialog mProgressDialog;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView caption, filename;
            public RelativeLayout faqlist;

            public MyViewHolder(View view) {
                super(view);
                caption = (TextView) view.findViewById(R.id.caption);
                filename = (TextView) view.findViewById(R.id.filename);
                faqlist = (RelativeLayout) view.findViewById(R.id.faqList);

            }
        }


    public FaqAdapter(List<faq> moviesList) {
        this.faqList = moviesList;
    }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faq_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            faq faq = faqList.get(position);
        holder.caption.setText(faq.getCaption());
        holder.filename.setText("Name File: "+faq.getFilename());

            holder.faqlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mProgressDialog = new ProgressDialog(v.getContext());
                    mProgressDialog.setMessage("Mendownload File");
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setCancelable(true);

                    faq faq = faqList.get(position);
                    new getFaqPdf(faq.getFilename(),v.getContext()).execute();
                }
            });
        }

        @Override
        public int getItemCount() {
        return faqList.size();
    }
    class getFaqPdf extends AsyncTask<String, String, String> {
        String fileName;
        Context context;

        getFaqPdf(String fileName, Context context) {
            // list all the parameters like in normal class define
            this.fileName = fileName;
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }
            @Override
        protected String doInBackground(String... strings) {
                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                File folder = new File(extStorageDirectory, "pdf");
                folder.mkdir();
                File file = new File(folder, fileName);
                try {
                    file.createNewFile();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                new DownloaderFaq(context).DownloadFile(Constants.MAIN_URL+"/files/"+fileName, file);

                File files = new File(Environment.getExternalStorageDirectory()+"/pdf/"+fileName);
                PackageManager packageManager = context.getPackageManager();
                Intent testIntent = new Intent(Intent.ACTION_VIEW);
                testIntent.setType("application/pdf");
                List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(files);
                intent.setDataAndType(uri, "application/pdf");
                context.startActivity(intent);

                return null;
        }
        @Override
        protected void onPostExecute(String file_url) {
            mProgressDialog.dismiss();
        }

    }
}


