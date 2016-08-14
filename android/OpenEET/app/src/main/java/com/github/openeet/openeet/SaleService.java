package com.github.openeet.openeet;

import android.provider.Settings;
import android.util.Log;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import openeet.lite.EetHeaderDTO;
import openeet.lite.EetRegisterRequest;
import openeet.lite.EetSaleDTO;
import openeet.lite.Main;

/**
 * Created by rasekl on 8/14/16.
 */
public class SaleService {
    private static final String LOGTAG="SaleService";
    private static final SaleEntry[] SALE_ENTRY_ARRAY0=new SaleEntry[]{};

    public static class SaleRegisterAttempt {
        public String soapRequest;
        public String soapResponse;
        public EetHeaderDTO header;
        public String fik;
        public Throwable throwable;
        public String info;
    }

    public static class SaleEntry {
        public SaleEntry(){
            attempts=new ArrayList<SaleRegisterAttempt>();
            registered=false;
            error=false;
            fik=null;
            saleData=null;
        }
        public EetSaleDTO saleData;
        public boolean registered;
        public boolean error;
        public String fik;
        public List<SaleRegisterAttempt> attempts;
    }

    private static SaleService service=new SaleService();

    private List<SaleEntry> sales;

    public static SaleService getInstance(){
        return service;
    }

    public SaleService(){
        sales=new ArrayList<SaleEntry>();
    }

    public void registerSale(EetSaleDTO dto){
        SaleEntry entry=new SaleEntry();
        SaleRegisterAttempt attempt=new SaleRegisterAttempt();
        entry.saleData=dto;
        try {
            long startTime=System.currentTimeMillis();
            Log.i(LOGTAG,"started registration for sale:"+dto);
            EetRegisterRequest.Builder builder = EetRegisterRequest.builder()
                    .fromDTO(dto)
                    .pkcs12(EetRegisterRequest.loadStream(Main.class.getResourceAsStream("/openeet/lite/01000003.p12")))
                    .pkcs12password("eet");

            //use andorid_id as identifier of device
            //FIXME use android id
            //if (dto.id_pokl==null) builder.id_pokl("an-"+ Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            if (dto.id_pokl==null) builder.id_pokl("an-xxxxxxxxx");
            builder.id_provoz("1");
            builder.porad_cis(String.format("%08x",System.currentTimeMillis()));
            if (dto.celk_trzba==null) builder.celk_trzba("0.00");

            EetRegisterRequest request=builder.build();
            entry.saleData=request.getSaleDTO();
            Log.d(LOGTAG,"RegisterRequest built:"+entry.saleData.toString());

            String requestBody=request.generateSoapRequest();
            attempt.soapRequest=requestBody;
            attempt.header=request.getLastHeader();

            long prepareTime=System.currentTimeMillis();
            String response=request.sendRequest(requestBody, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v2"));
            attempt.soapResponse=response;
            //Log.d(LOGTAG,"response received: \n"+response);
            //System.out.printf("===== BEGIN EET RESPONSE =====\n%s\n===== END EET RESPONSE =====\n",response);
            long registeredTime=System.currentTimeMillis();

            String fikPattern="eet2:Potvrzeni fik=\"";
            if (response.contains(fikPattern)) {
                int fikIdx=response.indexOf(fikPattern)+fikPattern.length();
                String fik=response.substring(fikIdx,fikIdx+39);
                String msg=String.format("prepare time: %d, sedning time: %d, fik: %s",(prepareTime-startTime),(registeredTime-prepareTime),fik);
                Log.d(LOGTAG, msg);
                entry.fik=fik;
                attempt.fik=fik;
                attempt.info=msg;
                entry.registered=true;
                entry.error=false;
                entry.attempts.add(attempt);
                sales.add(entry);
            }
            else {
                //FIXME: better error handling
                Log.e(LOGTAG, "fik not found in response");
                entry.error=true;
                entry.registered=false;
                entry.attempts.add(attempt);
                sales.add(entry);
            }
        }
        catch (Exception e) {
            Log.e(LOGTAG, "exception while regfistering",e);
            entry.registered=false;
            attempt.throwable=e;
            entry.attempts.add(attempt);
            sales.add(entry);
        }
    }

    public void retryUnfinished(){
        for (int i=0; i<sales.size(); i++){
            SaleService.SaleEntry entry=sales.get(i);
            if (!entry.registered && !entry.error){
                long startTime=System.currentTimeMillis();
                SaleService.SaleRegisterAttempt attempt=new SaleRegisterAttempt();
                try {
                    EetRegisterRequest.Builder builder=EetRegisterRequest.builder();
                    builder
                            .fromDTO(entry.saleData)
                            .pkcs12(EetRegisterRequest.loadStream(Main.class.getResourceAsStream("/openeet/lite/01000003.p12")))
                            .pkcs12password("eet");
                    EetRegisterRequest request=builder.build();

                    String soapRequest=request.generateSoapRequest(null, EetRegisterRequest.PrvniZaslani.OPAKOVANE, null,null);
                    attempt.header=request.getLastHeader();
                    attempt.soapRequest=soapRequest;
                    long prepareTime=System.currentTimeMillis();

                    String soapResponse=request.sendRequest(soapRequest, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v2"));
                    attempt.soapResponse=soapResponse;
                    long registeredTime=System.currentTimeMillis();
                    String fikPattern="eet2:Potvrzeni fik=\"";
                    if (soapResponse.contains(fikPattern)) {
                        int fikIdx=soapResponse.indexOf(fikPattern)+fikPattern.length();
                        String fik=soapResponse.substring(fikIdx,fikIdx+39);
                        String msg=String.format("prepare time: %d, sedning time: %d, fik: %s",(prepareTime-startTime),(registeredTime-prepareTime),fik);
                        Log.d(LOGTAG, msg);
                        entry.fik=fik;
                        attempt.fik=fik;
                        attempt.info=msg;
                        entry.registered=true;
                        entry.error=false;
                        entry.attempts.add(attempt);
                    }
                    else {
                        //FIXME: better error handling
                        Log.e(LOGTAG, "fik not found in response");
                        entry.error=true;
                        entry.registered=false;
                        entry.attempts.add(attempt);
                    }
                }
                catch (Exception e){
                    Log.e(LOGTAG, "exception while regfistering",e);
                    entry.registered=false;
                    attempt.throwable=e;
                    entry.attempts.add(attempt);
                }
            }
        }
    }

    public SaleEntry[] getLastRegistered() {
        return sales.toArray(SALE_ENTRY_ARRAY0);
    }
}
