package com.git.openeet.openeet;

import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;
import java.util.Date;

import openeet.lite.EetRegisterRequest;
import openeet.lite.Main;

/**
 * Created by rasekl on 8/10/16.
 */
public class RegisterSaleTask extends AsyncTask <String,Integer, String> {
    private static final String LOGTAG="RegisterSaleTask";

    @Override
    protected String doInBackground(String... amount) {
        try {
            long startTime=System.currentTimeMillis();
            Log.i(LOGTAG,"started registration for amount:"+amount);
            EetRegisterRequest request = EetRegisterRequest.builder()
                    .dic_popl("CZ1212121218")
                    .id_provoz("1")
                    .id_pokl("POKLADNA01")
                    .porad_cis("1")
                    .dat_trzby(EetRegisterRequest.formatDate(new Date()))
                    .celk_trzba(Double.parseDouble(amount[0]))
                    .rezim(0)
                    .pkcs12(EetRegisterRequest.loadStream(Main.class.getResourceAsStream("/openeet/lite/01000003.p12")))
                    .pkcs12password("eet")
                    .build();

            Log.d(LOGTAG,"RegisterRequest built:");

            String requestBody=request.generateSoapRequest();
            ///Log.d(LOGTAG,"soap request generated: \n"+requestBody);
            //System.out.printf("===== BEGIN EET REQUEST =====\n%s\n===== END EET REQUEST =====\n",requestBody);

            long prepareTime=System.currentTimeMillis();
            String response=request.sendRequest(requestBody, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v2"));
            //Log.i(LOGTAG,"response received: \n"+response);
            //System.out.printf("===== BEGIN EET RESPONSE =====\n%s\n===== END EET RESPONSE =====\n",response);
            long registeredTime=System.currentTimeMillis();

            String fikPattern="eet2:Potvrzeni fik=\"";
            if (response.contains(fikPattern)) {
                int fikIdx=response.indexOf(fikPattern)+fikPattern.length();
                String fik=response.substring(fikIdx,fikIdx+39);
                String msg=String.format("prepare time: %d, sedning time: %d, fik: %s",(prepareTime-startTime),(registeredTime-prepareTime),fik);
                Log.d(LOGTAG, msg);
                return msg;
            }
            else {
                Log.e(LOGTAG, "fik not found in response");
                return "NOOK";
            }
        }
        catch (Exception e) {
            Log.e(LOGTAG, "exception while regfistering",e);
            return "ERROR";
        }
    }
}
