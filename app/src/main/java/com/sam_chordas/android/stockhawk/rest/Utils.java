package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.util.Log;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utils {

  private static String LOG_TAG = Utils.class.getSimpleName();

  public static boolean showPercent = true;

  public static ArrayList quoteJsonToContentVals(String JSON, Context context){
    //output json string to log for debug purposes
    Log.d(LOG_TAG, "quoteJsonToContentVals:\n" + JSON);
    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
    JSONObject jsonObject = null;
    JSONArray resultsArray = null;
    try{
      jsonObject = new JSONObject(JSON);

      if (jsonObject != null && jsonObject.length() != 0){
        jsonObject = jsonObject.getJSONObject("query");
        int count = Integer.parseInt(jsonObject.getString("count"));
        if (count == 1){
          jsonObject = jsonObject.getJSONObject("results")
              .getJSONObject("quote");
          batchOperations.add(buildBatchOperation(jsonObject, context));
        } else{
          resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

          if (resultsArray != null && resultsArray.length() != 0){
            for (int i = 0; i < resultsArray.length(); i++){
              jsonObject = resultsArray.getJSONObject(i);
              batchOperations.add(buildBatchOperation(jsonObject, context));
            }
          }
        }
      }
    } catch (JSONException e){
      Log.e(LOG_TAG, "String to JSON failed: " + e);
    }
    return batchOperations;
  }

  public static String truncateBidPrice(String bidPrice){
    //perform additional error handling here.  Sometimes a valid stock ticker was entered but no bid price is available.
    //in such cases, return "N/A" for the bid price.
    try {
      bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
    }
    catch (Exception e) {
      bidPrice = "N/A";
    }

    return bidPrice;
  }

  public static String truncateChange(String change, boolean isPercentChange, Context context){
    String weight = change.substring(0,1);
    String ampersand = "";
    if (isPercentChange){
      ampersand = change.substring(change.length() - 1, change.length());
      change = change.substring(0, change.length() - 1);
    }
    change = change.substring(1, change.length());
    double changeVal = 0;
    try {
      changeVal = Double.parseDouble(change);
    }
    catch (Exception e) {
      Log.e("Utils", "Unable to parse string into Double:" + change);
      return context.getResources().getString(R.string.not_available);  //if change comes back null, display appropriate "not available" text string
    }

    double round = (double) Math.round(changeVal * 100) / 100;
    change = String.format("%.2f", round);
    StringBuffer changeBuffer = new StringBuffer(change);
    changeBuffer.insert(0, weight);
    changeBuffer.append(ampersand);
    change = changeBuffer.toString();
    return change;
  }

  public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject, Context context){
    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
        QuoteProvider.Quotes.CONTENT_URI);
    try {
      String change = jsonObject.getString("Change");
      builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol"));
      builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
      builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
          jsonObject.getString("ChangeinPercent"), true, context));
      builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false, context));
      builder.withValue(QuoteColumns.ISCURRENT, 1);
      if (change.charAt(0) == '-'){
        builder.withValue(QuoteColumns.ISUP, 0);
      }else{
        builder.withValue(QuoteColumns.ISUP, 1);
      }

    } catch (JSONException e){
      e.printStackTrace();
    }
    return builder.build();
  }

  public static boolean stockIsValid(String jsonString) {
    boolean isValid = false;
    try {
      JSONObject jsonObject = new JSONObject(jsonString);
      String bookValue = jsonObject.getJSONObject("query").getJSONObject("results").getJSONObject("quote").getString("BookValue");
      if (bookValue!=null && !bookValue.equals("null")) isValid = true;
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return isValid;
  }
}
