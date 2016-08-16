package com.github.openeet.openeet;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import openeet.lite.EetHeaderDTO;
import openeet.lite.EetRegisterRequest;
import openeet.lite.EetSaleDTO;
import openeet.lite.Main;

/**
 * Created by rasekl on 8/14/16.
 */
public class SaleService {
    private static final String LOGTAG="SaleService";
    private static final String FIK_PATTERN ="eet:Potvrzeni fik=\"";

    public interface SaleServiceListener {
        void saleDataUpdated();
    }

    public static class SaleRegisterAttempt {
        public SaleRegisterAttempt(){
            startTime=System.currentTimeMillis();
        }
        public String soapRequest;
        public String soapResponse;
        public EetHeaderDTO header;
        public String fik;
        public Throwable throwable;
        public String info;
        long startTime;
        long startSendingTime;
        long finishTime;
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
            currentAttempt.finishTime=System.currentTimeMillis();
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
        sales.add(entry);

        processEntry(entry, true);

        //FIXME maybe ad to finally would be better
        removeListener(listener);
    }

    public void retryUnfinished(SaleServiceListener listener){
        addListener(listener);
        for (int i=0; i<sales.size(); i++){
            SaleService.SaleEntry entry=sales.get(i);
            if (!entry.registered && !entry.error) {
                processEntry(entry, false);
            }
        }
        //FIXME maybe ad to finally would be better
        removeListener(listener);
    }

    protected void processEntry(SaleEntry entry, boolean firstAttempt){
        try {
            if (entry.registered) return; //already registered

            entry.startAttempt();
            notifyListeners();
            EetRegisterRequest.Builder builder = EetRegisterRequest.builder()
                    .fromDTO(entry.saleData)
                    .pkcs12(EetRegisterRequest.loadStream(Main.class.getResourceAsStream("/openeet/lite/01000003.p12")))
                    .pkcs12password("eet");

            EetRegisterRequest request=builder.build();
            if (firstAttempt)
                entry.saleData=request.getSaleDTO();
            notifyListeners();

            String soapRequest;
            if (firstAttempt)
                soapRequest=request.generateSoapRequest(null, EetRegisterRequest.PrvniZaslani.PRVNI, null,null);
            else
                soapRequest=request.generateSoapRequest(null, EetRegisterRequest.PrvniZaslani.OPAKOVANE, null,null);
            writeStringToFile(soapRequest,"request");

            entry.currentAttempt.header=request.getLastHeader();
            entry.currentAttempt.soapRequest=soapRequest;
            entry.currentAttempt.startSendingTime= System.currentTimeMillis();

            String soapResponse=request.sendRequest(soapRequest, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3"));
            entry.currentAttempt.soapResponse=soapResponse;
            entry.currentAttempt.startSendingTime=System.currentTimeMillis();
            writeStringToFile(soapResponse,"response");

            if (soapResponse.contains(FIK_PATTERN)) {
                int fikIdx=soapResponse.indexOf(FIK_PATTERN)+FIK_PATTERN.length();
                String fik=soapResponse.substring(fikIdx,fikIdx+39);
                entry.finishAttempt(true,false,fik,null, null);
            }
            else {
                //FIXME: better error handling
                Log.e(LOGTAG, "fik not found in response");
                entry.finishAttempt(false,true,null,"fik not found in response", null);
            }
            notifyListeners();
        }
        catch (Exception e) {
            Log.e(LOGTAG, "exception while regfistering",e);
            entry.finishAttempt(false,false,null,"exception while registering",e);
            notifyListeners();
        }
    }

    public SaleEntry[] getLastRegistered() {
        return sales.toArray(new SaleEntry[sales.size()]);
    }

    private void writeStringToFile(String data, String filename){
        try {
            File appdir = new File(Environment.getExternalStorageDirectory(), "openeet");
            if (!appdir.exists()) appdir.mkdir();
            File outFile=new File(appdir,String.format("%x016-%s",System.currentTimeMillis(),filename));
            Log.d(LOGTAG, "Writing file to "+outFile.getAbsolutePath());
            FileWriter fw=new FileWriter(outFile);
            fw.write(data);
            fw.close();
        }
        catch (Exception e){
            Log.e(LOGTAG, "failed to write file", e);
        }
    }
}
