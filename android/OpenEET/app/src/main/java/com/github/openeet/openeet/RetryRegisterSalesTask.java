package com.github.openeet.openeet;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import openeet.lite.EetSaleDTO;

/**
 * Created by rasekl on 8/10/16.
 */
public class RetryRegisterSalesTask extends AsyncTask <String ,Integer, String> {
    private static final String LOGTAG="RegisterSaleTask";
    public static final String ACTION_SALE_REGISTERED_CHANGE="com.github.openeet.openeet.action.SaleRegisteredChange";


    private Context context;

    protected RetryRegisterSalesTask(Context context){
        this.context=context;
    }

    public static IntentFilter getMatchAllFilter(){
        IntentFilter ret=new IntentFilter();
        ret.addAction(ACTION_SALE_REGISTERED_CHANGE);
        return ret;
    }

    @Override
    protected String doInBackground(String... dummy) {
        SaleService.getInstance().retryUnfinished(new SaleService.SaleServiceListener() {
            @Override
            public void saleDataUpdated() {
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(RetryRegisterSalesTask.ACTION_SALE_REGISTERED_CHANGE));
            }
        });
        return null;
    }
}
