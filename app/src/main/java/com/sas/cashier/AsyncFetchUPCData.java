package com.sas.cashier;

import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.HttpGet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;


public class AsyncFetchUPCData extends AsyncTask<String, Void, Boolean> {
    String barcodeValue;
    String count;
    public static String TAG="ASYNCFETCHUPC";

    public AsyncFetchUPCData(String barcodevalue) {
        this.barcodeValue=barcodevalue;
    }
    private String upcUri = "https://api.upcitemdb.com/prod/trial/lookup?upc="+barcodeValue;

    protected Boolean doInBackground(String... params) {

        System.setProperty("https.protocols", "TLSv1.1");
        try {

            HttpGet httpGet = new HttpGet(upcUri);
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httpGet);
            int status = response.getStatusLine().getStatusCode();

            if (status == 200) {
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);


                JSONObject jsono = new JSONObject(data);
                Log.d(TAG,"json data:  " + jsono);

                return true;
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {

            e.printStackTrace();
        }
        return false;
    }



    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

    }

    public String processResult(String result) {

        return result;
    }

}