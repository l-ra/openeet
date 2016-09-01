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



    private Context context;

    protected RetryRegisterSalesTask(Context context){
        this.context=context;
    }

    public static IntentFilter getMatchAllFilter(){
        IntentFilter ret=new IntentFilter();
        ret.addAction(MainBroadcastReceiver.ACTION_SALE_REGISTERED_CHANGE);
        return ret;
    }

    @Override
    protected String doInBackground(String... dummy) {
        SaleStore store=SaleStore.getInstance(context.getApplicationContext());
        SaleService.getInstance(store).retryUnfinished(new SaleService.SaleServiceListener() {
            @Override
            public void saleDataUpdated(String[] bkpList) {
                Intent broadcast=new Intent(MainBroadcastReceiver.ACTION_SALE_REGISTERED_CHANGE);
                if (bkpList!=null)
                    broadcast.putExtra(MainBroadcastReceiver.ACTION_SALE_EXTRA_BKP_LIST, bkpList);
                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcast);
            }
        });
        return null;
    }
}
