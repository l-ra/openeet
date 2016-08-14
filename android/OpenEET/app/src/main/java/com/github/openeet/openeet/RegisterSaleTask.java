package com.github.openeet.openeet;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.net.URL;
import java.util.Date;

import openeet.lite.EetRegisterRequest;
import openeet.lite.EetSaleDTO;
import openeet.lite.Main;

/**
 * Created by rasekl on 8/10/16.
 */
public class RegisterSaleTask extends AsyncTask <EetSaleDTO,Integer, String> {
    private static final String LOGTAG="RegisterSaleTask";
    public static final String ACTION_SALE_REGISTERED_SUCCES="com.github.openeet.openeet.action.SaleRegisteredSuccess";
    public static final String EXTRA_FIK="com.github.openeet.openeet.extra.fik";
    public static final String ACTION_SALE_REGISTERED_FAILURE="com.github.openeet.openeet.action.SaleRegisteredFailure";
    public static final String EXTRA_ERROR="com.github.openeet.openeet.extra.error";

    private Context context;

    protected RegisterSaleTask(Context context){
        this.context=context;
    }

    public static IntentFilter getMatchAllFilter(){
        IntentFilter ret=new IntentFilter();
        ret.addAction(ACTION_SALE_REGISTERED_FAILURE);
        ret.addAction(ACTION_SALE_REGISTERED_SUCCES);
        return ret;
    }

    @Override
    protected String doInBackground(EetSaleDTO... dtoSales) {
        try {
            EetSaleDTO dto=dtoSales[0];
            long startTime=System.currentTimeMillis();
            Log.i(LOGTAG,"started registration for sale:"+dto);
            EetRegisterRequest.Builder builder = EetRegisterRequest.builder()
                    .fromDTO(dto)
                    .pkcs12(EetRegisterRequest.loadStream(Main.class.getResourceAsStream("/openeet/lite/01000003.p12")))
                    .pkcs12password("eet");

            //use andorid_id as identifier of device
            if (dto.id_pokl==null) builder.id_pokl(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            builder.id_provoz("1");
            builder.porad_cis(String.format("%08x",System.currentTimeMillis()));
            if (dto.celk_trzba==null) builder.celk_trzba("0.00");

            EetRegisterRequest request=builder.build();
            Log.d(LOGTAG,"RegisterRequest built:");

            String requestBody=request.generateSoapRequest();
            //Log.d(LOGTAG,"soap request generated: \n"+requestBody);
            //System.out.printf("===== BEGIN EET REQUEST =====\n%s\n===== END EET REQUEST =====\n",requestBody);

            long prepareTime=System.currentTimeMillis();
            String response=request.sendRequest(requestBody, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v2"));
            //Log.d(LOGTAG,"response received: \n"+response);
            //System.out.printf("===== BEGIN EET RESPONSE =====\n%s\n===== END EET RESPONSE =====\n",response);
            long registeredTime=System.currentTimeMillis();

            String fikPattern="eet2:Potvrzeni fik=\"";
            if (response.contains(fikPattern)) {
                int fikIdx=response.indexOf(fikPattern)+fikPattern.length();
                String fik=response.substring(fikIdx,fikIdx+39);
                String msg=String.format("prepare time: %d, sedning time: %d, fik: %s",(prepareTime-startTime),(registeredTime-prepareTime),fik);
                Log.d(LOGTAG, msg);
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(RegisterSaleTask.ACTION_SALE_REGISTERED_SUCCES).putExtra(RegisterSaleTask.EXTRA_FIK,fik));
                return msg;
            }
            else {
                Log.e(LOGTAG, "fik not found in response");
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(RegisterSaleTask.ACTION_SALE_REGISTERED_FAILURE).putExtra(RegisterSaleTask.EXTRA_ERROR,"fik not found in response"));
                return "NOOK";
            }
        }
        catch (Exception e) {
            Log.e(LOGTAG, "exception while regfistering",e);
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(RegisterSaleTask.ACTION_SALE_REGISTERED_FAILURE).putExtra(RegisterSaleTask.EXTRA_ERROR,"failed to send request"));
            return "ERROR";
        }
    }
}
