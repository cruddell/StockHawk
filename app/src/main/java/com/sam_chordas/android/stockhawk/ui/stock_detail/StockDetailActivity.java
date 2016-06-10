package com.sam_chordas.android.stockhawk.ui.stock_detail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sam_chordas.android.stockhawk.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class StockDetailActivity extends AppCompatActivity {
    private static final String TAG = "StockDetailActivity";
    private static final boolean DEBUG_LOG = true;

    public static final String ARG_STOCK_SYMBOL = "stock_symbol";

    private String mStockSymbol;
    private LineChart mLineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        mLineChart = (LineChart)findViewById(R.id.lineChart);

        mStockSymbol = getIntent().getStringExtra(ARG_STOCK_SYMBOL);
        getStockData(mStockSymbol);
    }

    private void getStockData(String stockSymbol) {
        ApiHelper.getStockDetails(stockSymbol, new ApiHelper.NetworkCallback() {
            @Override
            public void onResponseReceived(final ApiHelper.HttpResponse response) {
                try {
                    JSONObject jsonObject = (new JSONObject(response.responseBody)).getJSONObject("query");
                    Iterator<?> keys = jsonObject.keys();

                    while( keys.hasNext() ) {
                        String key = (String)keys.next();
                        if (DEBUG_LOG) Log.d(TAG, "key found:" + key);
                    }

                    JSONArray quote = jsonObject.getJSONObject("results").getJSONArray("quote");

                    ArrayList<Entry> prices = new ArrayList<Entry>();
                    ArrayList<String> xVals = new ArrayList<String>();
                    int xIndex = 0;
                    for (int i=quote.length()-1; i>=0; i--) {
                        JSONObject thisQuote = quote.getJSONObject(i);
                        String date = thisQuote.getString("Date");
                        xVals.add(date);
                        double closePrice = Double.parseDouble(thisQuote.getString("Close"));
                        Entry entry = new Entry((float)closePrice, xIndex);
                        prices.add(entry);
                        xIndex++;
                    }

                    LineDataSet dataSet = new LineDataSet(prices, "Prices");
                    ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                    dataSets.add(dataSet);

                    LineData data = new LineData(xVals, dataSets);
                    mLineChart.setData(data);
                    mLineChart.animateX(3000);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, StockDetailActivity.this);
    }
}
