package com.github.openeet.openeet;

import android.provider.Settings;
import android.util.Log;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

import openeet.lite.EetHeaderDTO;
import openeet.lite.EetRegisterRequest;
import openeet.lite.EetSaleDTO;
import openeet.lite.Main;

/**
 * Created by rasekl on 8/14/16.
 */
public class SaleService {
    private static final String LOGTAG="SaleService";

    public interface SaleServiceListener {
        void saleDataUpdated();
    }

    public static class SaleRegisterAttempt {
        public SaleRegisterAttempt(){
            start=new Date();
        }
        public Date start;
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
            inProgress=false;
        }
        public SaleRegisterAttempt startAttempt(){
            SaleRegisterAttempt ret=new SaleRegisterAttempt();
            attempts.add(ret);
            inProgress=true;
            currentAttempt=ret;
            return ret;
        }

        public void finishAttempt(boolean registered, boolean error, String fik, String info, Throwable throwable){
            this.registered=registered;
            this.error=error;
            this.fik=fik;
            currentAttempt.fik=fik;
            currentAttempt.info=info;
            currentAttempt.throwable=throwable;
            inProgress=false;
            currentAttempt=null;
        }

        public SaleRegisterAttempt currentAttempt;
        public EetSaleDTO saleData;
        public boolean registered;
        public boolean error;
        public String fik;
        public List<SaleRegisterAttempt> attempts;
        public boolean inProgress;
    }

    private static SaleService service=new SaleService();
    public static SaleService getInstance(){
        return service;
    }

    private List<SaleEntry> sales;
    private Set<SaleServiceListener> listeners;

    public SaleService(){
        sales=new ArrayList<SaleEntry>();
        listeners=new HashSet<SaleServiceListener>();
    }

    protected void addListener(SaleServiceListener listener){
        listeners.add(listener);
    }

    protected void removeListener(SaleServiceListener listener){
        listeners.remove(listener);
    }

    private void notifyListeners(){
        for (SaleServiceListener listener: listeners ) {
            listener.saleDataUpdated();
        }
    }

    public void registerSale(EetSaleDTO dto, SaleServiceListener listener){
        addListener(listener);
        SaleEntry entry=new SaleEntry();
        entry.saleData=dto;
        entry.startAttempt();
        sales.add(entry);
        notifyListeners();
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
            notifyListeners();

            String requestBody=request.generateSoapRequest();
            entry.currentAttempt.soapRequest=requestBody;
            entry.currentAttempt.header=request.getLastHeader();

            long prepareTime=System.currentTimeMillis();
            String response=request.sendRequest(requestBody, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v2"));
            entry.currentAttempt.soapResponse=response;
            //Log.d(LOGTAG,"response received: \n"+response);
            //System.out.printf("===== BEGIN EET RESPONSE =====\n%s\n===== END EET RESPONSE =====\n",response);
            long registeredTime=System.currentTimeMillis();

            String fikPattern="eet2:Potvrzeni fik=\"";
            if (response.contains(fikPattern)) {
                int fikIdx=response.indexOf(fikPattern)+fikPattern.length();
                String fik=response.substring(fikIdx,fikIdx+39);
                String msg=String.format("prepare time: %d, sedning time: %d, fik: %s",(prepareTime-startTime),(registeredTime-prepareTime),fik);
                Log.d(LOGTAG, msg);
                entry.finishAttempt(true,false,fik,msg, null);
            }
            else {
                //FIXME: better error handling
                Log.e(LOGTAG, "fik not found in response");
                entry.finishAttempt(false,false,null,"fik not found in response", null);
            }
            notifyListeners();
        }
        catch (Exception e) {
            Log.e(LOGTAG, "exception while regfistering",e);
            entry.finishAttempt(false,true,null,"exception while registering",e);
            notifyListeners();
        }
        //FIXME maybe ad to finally would be better
        removeListener(listener);
    }

    public void retryUnfinished(SaleServiceListener listener){
        addListener(listener);
        for (int i=0; i<sales.size(); i++){
            SaleService.SaleEntry entry=sales.get(i);
            if (!entry.registered && !entry.error){
                entry.startAttempt();
                notifyListeners();
                long startTime=System.currentTimeMillis();

                try {
                    EetRegisterRequest.Builder builder=EetRegisterRequest.builder();
                    builder
                            .fromDTO(entry.saleData)
                            .pkcs12(EetRegisterRequest.loadStream(Main.class.getResourceAsStream("/openeet/lite/01000003.p12")))
                            .pkcs12password("eet");
                    EetRegisterRequest request=builder.build();

                    String soapRequest=request.generateSoapRequest(null, EetRegisterRequest.PrvniZaslani.OPAKOVANE, null,null);
                    entry.currentAttempt.header=request.getLastHeader();
                    entry.currentAttempt.soapRequest=soapRequest;
                    long prepareTime=System.currentTimeMillis();

                    String soapResponse=request.sendRequest(soapRequest, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v2"));
                    entry.currentAttempt.soapResponse=soapResponse;
                    long registeredTime=System.currentTimeMillis();
                    String fikPattern="eet2:Potvrzeni fik=\"";
                    if (soapResponse.contains(fikPattern)) {
                        int fikIdx=soapResponse.indexOf(fikPattern)+fikPattern.length();
                        String fik=soapResponse.substring(fikIdx,fikIdx+39);
                        String msg=String.format("prepare time: %d, sedning time: %d, fik: %s",(prepareTime-startTime),(registeredTime-prepareTime),fik);
                        Log.d(LOGTAG, msg);
                        entry.finishAttempt(true,false,fik,msg,null);
                        notifyListeners();
                    }
                    else {
                        //FIXME: better error handling
                        Log.e(LOGTAG, "fik not found in response");
                        entry.finishAttempt(false,false,null,"fik not found in response",null);
                        notifyListeners();
                    }
                }
                catch (Exception e){
                    Log.e(LOGTAG, "exception while regfistering",e);
                    entry.finishAttempt(false,true,null,"exception while registering",e);
                    notifyListeners();
                }
            }
        }
        //FIXME maybe ad to finally would be better
        removeListener(listener);
    }

    public SaleEntry[] getLastRegistered() {
        return sales.toArray(new SaleEntry[sales.size()]);
    }
}
