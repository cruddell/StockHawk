package com.sam_chordas.android.stockhawk.ui.stock_detail;

import java.util.Calendar;

/**
 * Created by ChristopherRuddell, Museum of the Bible, on 6/9/16.
 */
public class UrlBuilder {
    private static final boolean DEBUG_LOG = true;
    private static final String TAG = "UrlBuilder";


    enum HttpMethods {
        GET,
        POST,
        PUT,
        DELETE
    }

    public static String detailStockUrl(String symbol) {
        int yearNow = Calendar.getInstance().get(Calendar.YEAR);
        int monthNow = Calendar.getInstance().get(Calendar.MONTH);
        int dayNow = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        String endDate = String.format("%04d-%02d-%02d",yearNow,monthNow, dayNow);
        String startDate = String.format("%04d-%02d-%02d",yearNow-1,monthNow,dayNow);

//        String startDate = "2009-09-11";
//        String endDate = "2010-03-10";
        String apiUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20%22" + symbol
                + "%22%20and%20startDate%20%3D%20%22" + startDate + "%22%20and%20endDate%20%3D%20%22" + endDate
                + "%22&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

        return apiUrl;
    }


}
