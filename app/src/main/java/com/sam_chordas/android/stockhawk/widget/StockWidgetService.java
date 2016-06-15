package com.sam_chordas.android.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by ChristopherRuddell, Museum of the Bible, on 6/14/16.
 */

public class StockWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new StockHawkRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StockHawkRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Cursor mCursor;
    private Context mContext;
    private int mAppWidgetId;

    public StockHawkRemoteViewsFactory(final Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        mCursor = mContext.getContentResolver().query(StockQuery.CONTENT_URI, StockQuery.PROJECTION, QuoteColumns.ISCURRENT + " = ?", new String[]{"1"}, null);
    }

    @Override
    public void onDataSetChanged() {
        mCursor = mContext.getContentResolver().query(StockQuery.CONTENT_URI, StockQuery.PROJECTION, QuoteColumns.ISCURRENT + " = ?", new String[]{"1"}, null);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(final int position) {
        mCursor.moveToPosition(position);
        String symbol = mCursor.getString(StockQuery.SYMBOL);
        Log.d("StockWidgetService","showing symbol:" + symbol);
        String bidPrice = mContext.getResources().getString(R.string.dollarSign) + mCursor.getString(StockQuery.BIDPRICE);
        // Construct a RemoteViews item based on the app widget item XML file, and set the
        // text based on the position.
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        rv.setTextViewText(R.id.stock_symbol, symbol);
        rv.setTextViewText(R.id.bid_price, bidPrice);

        // Next, set a fill-intent, which will be used to fill in the pending intent template
        // that is set on the collection view in StackWidgetProvider.
//        Bundle extras = new Bundle();
//        extras.putInt(StockHawkWidget.EXTRA_ITEM, position);
//        Intent fillInIntent = new Intent();
//        fillInIntent.putExtras(extras);
        // Make it possible to distinguish the individual on-click
        // action of a given item
//        rv.setOnClickFillInIntent(R.id.stock_symbol, fillInIntent);

        //Return the RemoteViews object.
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(final int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }



    //make cursor query available to class
    protected interface StockQuery {
        int _TOKEN = 0x0;

        Uri CONTENT_URI = QuoteProvider.Quotes.CONTENT_URI;

        String[] PROJECTION = {
                QuoteColumns._ID,
                QuoteColumns.SYMBOL,
                QuoteColumns.BIDPRICE,
                QuoteColumns.PERCENT_CHANGE,
                QuoteColumns.CHANGE,
                QuoteColumns.ISUP
        };

        // COLUMN INDEXES
        int _ID = 0;
        int SYMBOL = 1;
        int BIDPRICE = 2;
        int PERCENT_CHANGE = 3;
        int CHANGE = 4;
        int ISUP = 5;

    }

}
