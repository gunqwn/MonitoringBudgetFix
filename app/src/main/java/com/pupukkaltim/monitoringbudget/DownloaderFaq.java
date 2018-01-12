package com.pupukkaltim.monitoringbudget;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DownloaderFaq {
    private static Context context;


    public DownloaderFaq(Context context){

        this.context = context;
    }

    public static void DownloadFile(String fileURL, File directory) {
        try {

            FileOutputStream f = new FileOutputStream(directory);
            URL u = new URL(fileURL);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("POST");
            c.setDoInput(true);
            c.setDoOutput(true);
            Uri.Builder builder = new Uri.Builder();

            OutputStream os = c.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.flush();
            writer.close();
            os.close();

            c.connect();
            InputStream in = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            float total = 0;
            while ((len1 = in.read(buffer)) > 0) {
                total = total + len1;
                f.write(buffer, 0, len1);
            }
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}