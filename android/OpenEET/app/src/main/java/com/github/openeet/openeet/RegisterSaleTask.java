package com.github.openeet.openeet;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import java.net.URL;
import java.util.Arrays;
import java.util.Date;

import openeet.lite.EetRegisterRequest;
import openeet.lite.EetSaleDTO;
import openeet.lite.Main;

/**
 * Created by rasekl on 8/10/16.
 */
public class RegisterSaleTask extends AsyncTask <EetSaleDTO,Integer, String> {
    private static final String LOGTAG="RegisterSaleTask";


    private Context context;

    protected RegisterSaleTask(Context context){
        this.context=context;
    }

    public static IntentFilter getMatchAllFilter(){
        IntentFilter ret=new IntentFilter();
        ret.addAction(MainBroadcastReceiver.ACTION_SALE_REGISTERED_CHANGE);
        return ret;
    }

    @Override
    protected String doInBackground(EetSaleDTO... dtoSales) {
        EetSaleDTO sale=dtoSales[0];

        //FIXME from settings
        if (sale.id_pokl==null) sale.id_pokl="an-"+ Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        sale.id_provoz="1";
        sale.porad_cis=String.format("%08x",System.currentTimeMillis());

        SaleStore store=SaleStore.getInstance(context.getApplicationContext());
        SaleService.getInstance(store).registerSale(sale,new SaleService.SaleServiceListener() {
            @Override
            public void saleDataUpdated(String[] bkpList) {
                Intent broadcast=new Intent(MainBroadcastReceiver.ACTION_SALE_REGISTERED_CHANGE);
                if (bkpList!=null)
                    broadcast.putExtra(MainBroadcastReceiver.ACTION_SALE_EXTRA_BKP_LIST,bkpList);
                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcast);
            }
        });
        return null;
    }
}
