package com.example.ritika.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AsyncTaskValues extends AsyncTask<String, Integer, String> {

    private static final String TAG = "StockSyncTask";
    private MainActivity mainActivity;
    public static String STOCK_URL = "https://cloud.iexapis.com/stable/stock/",  appendURL="/quote?token=sk_d6dde12fa405401b85634f2e2daf98f4";
    public static boolean running = false;
    public String REQUIRED_URL;
    private ArrayList<Stock> stockValues = new ArrayList<>();
    private String symbol;


    public AsyncTaskValues(MainActivity mainact, String sym) {
        mainActivity = mainact;
        this.symbol = sym;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... params) {
        REQUIRED_URL = STOCK_URL + symbol + appendURL;
        Uri stockDetailUri = Uri.parse(REQUIRED_URL);
        String urlToFinalUse = stockDetailUri.toString();
        StringBuilder wantedData = new StringBuilder();
        String sentence;
        try {
            URL urlNew = new URL(urlToFinalUse);
            HttpURLConnection conn1 = (HttpURLConnection) urlNew.openConnection();
            conn1.setRequestMethod("GET");
            InputStream is1 = conn1.getInputStream();
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(is1));
            while ((sentence = reader1.readLine()) != null) {
                wantedData.append(sentence).append("/n");
            }

        } catch (Exception e) {
        }
        return wantedData.toString();
    }

    public ArrayList<Stock> parseJSON(String s){
        ArrayList<Stock> stockList = new ArrayList<>();
        try{
            if(!s.equals("httpserver.cc: Response Code 404")) {
                JSONObject jobj = new JSONObject(s);
                    String symbol = jobj.getString("symbol");
                        String cName = jobj.getString("companyName");
                        String latestPrice = jobj.getString("latestPrice");
                        Double latestStockVal = Double.parseDouble(latestPrice);
                        String change = jobj.getString("change");
                        Double stockChange = Double.parseDouble(change);
                        String stockDiffPercent = jobj.getString("changePercent");
                        Double stockPercentDiff = Double.parseDouble(stockDiffPercent);

                        stockList.add(
                                new Stock(symbol, cName, latestStockVal, stockChange, stockPercentDiff));

            }
        }
         catch (JSONException e1)
        {
            e1.printStackTrace();
        }
        return stockList;
    }

    @Override
    protected void onPostExecute(String s) {
        ArrayList<Stock> stockFullList = parseJSON(s);
        if (stockFullList != null)
            mainActivity.whenAsyncTaskStockDone(stockFullList);
            running = false;
    }
}
