package com.github.openeet.openeet;

import android.content.Context;
import android.os.Environment;
import android.support.v7.util.SortedList;
import android.util.Log;

import com.github.openeet.openeet.data.SaleSqlHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Really insanely stupid sale store - evry change overwrites alllllll the data. Every load reads the data into memory. No caching.
 * Created by rasekl on 8/22/16.
 */
public class SaleStoreFileImpl extends  SaleStore {
    //FIXME: implement sane form of persistence
    public static final String LOGTAG="SaleStoreFileImpl";

    public static final String BY_BKP = "by-bkp";
    private Context context;

    private static final class DatTrzbyComparator implements Comparator<SaleService.SaleEntry> {
        @Override
        public int compare(SaleService.SaleEntry saleEntry, SaleService.SaleEntry t1) {
            return saleEntry.saleData.dat_trzby.compareToIgnoreCase(t1.saleData.dat_trzby);
        }
    }


    /**
     *
     * @param context
     */
    public SaleStoreFileImpl(Context context){
        this.context=context;
    }

    /**
     *
     * @return
     * @throws SaleStoreException
     */
    protected File getFile(String type) throws SaleStoreException{
        File appdir = new File(Environment.getExternalStorageDirectory(), "openeet");
        if (!appdir.exists())  appdir.mkdir();
        return new File(appdir,String.format("salestore-%s.dat",type));
    }

    /**
     *
     * @return
     * @throws SaleStoreException
     */
    protected Map<String,SaleService.SaleEntry> load(String type) throws SaleStoreException {
        if (!getFile(type).exists()) return new HashMap<String, SaleService.SaleEntry>() ;
        ObjectInputStream is=null;
        try {
            is = new ObjectInputStream(new FileInputStream(getFile(type).getAbsoluteFile()));
            Object loaded=is.readObject();
            return (Map<String,SaleService.SaleEntry>) loaded;
        }
        catch (Exception e){
            throw new SaleStoreException("error loading",e);
        }
        finally {
            if (is!=null) {
                try {
                    is.close();
                }
                catch (IOException e){
                    throw new SaleStoreException("closing problem", e);
                }
            }
        }
    }

    /**
     *
     * @param entries
     * @throws SaleStoreException
     */
    protected void save(Map<String, SaleService.SaleEntry> entries, String type) throws SaleStoreException{
        ObjectOutputStream os=null;
        try {
            os = new ObjectOutputStream(new FileOutputStream(getFile(type).getAbsoluteFile()));
            os.writeObject(entries);
            os.close();
        }
        catch (Exception e){
            throw new SaleStoreException("error loading",e);
        }
        finally {
            if (os!=null) {
                try {
                    os.close();
                }
                catch (IOException e){
                    throw new SaleStoreException("closing problem", e);
                }
            }
        }

    }

    /**
     *
     */
    protected static final class SaleEntry extends SaleService.SaleEntry {
        boolean needInflate=true;
    }

    /**
     *
     * @param entry
     * @throws SaleStoreException
     */
    @Override
    public void saveSaleEntry(SaleService.SaleEntry entry) throws SaleStoreException {
        Log.d(LOGTAG, "Storing entry bkp:"+entry.saleData.bkp);
        if (entry==null) throw new NullPointerException("saved entry can;t be null");
        if (entry.saleData.bkp==null) throw new IllegalArgumentException("cant save entry with empty bkp");
        Map<String, SaleService.SaleEntry> byBkp=load(BY_BKP);
        SaleService.SaleEntry existing=  byBkp.get(entry.saleData.bkp);
        byBkp.put(entry.saleData.bkp,entry);
        save(byBkp,BY_BKP);
    }

    @Override
    public SaleService.SaleEntry loadSaleEntry(String bkp, boolean inflate) throws SaleStoreException {
        if (bkp==null) throw new NullPointerException("bkp can;t be null");
        Map<String, SaleService.SaleEntry> byBkp=load(BY_BKP);
        SaleService.SaleEntry existing=  byBkp.get(bkp);
        return existing;
    }

    @Override
    public SaleService.SaleEntry loadSaleEntry(String bkp) throws SaleStoreException {
        return loadSaleEntry(bkp, false);
    }

    @Override
    public void inflateSaleEntry(SaleService.SaleEntry entry) throws SaleStoreException {
        return;
    }

    @Override
    public SaleService.SaleEntry[] findAll(int offset, int limit, LimitType type) throws SaleStoreException {
        Map<String, SaleService.SaleEntry> byBkp=load(BY_BKP);
        SaleService.SaleEntry[] data=byBkp.values().toArray(new SaleService.SaleEntry[byBkp.size()]);
        Arrays.sort(data, new DatTrzbyComparator());
        return data;
    }

    @Override
    public SaleService.SaleEntry[] findOffline(int offset, int limit, LimitType type) throws SaleStoreException {
        Map<String,SaleService.SaleEntry> all=load(BY_BKP);
        List<SaleService.SaleEntry> selected=new ArrayList<SaleService.SaleEntry>();
        for (SaleService.SaleEntry e: all.values()){
            if (e.offline && !e.error && e.registered)
                selected.add(e);
        }
        SaleService.SaleEntry[] ret=selected.toArray(new SaleService.SaleEntry[selected.size()]);
        Arrays.sort(ret,new DatTrzbyComparator());
        return ret;
    }

    @Override
    public SaleService.SaleEntry[] findOnline(int offset, int limit, LimitType type) throws SaleStoreException {
        Map<String,SaleService.SaleEntry> all=load(BY_BKP);
        List<SaleService.SaleEntry> selected=new ArrayList<SaleService.SaleEntry>();
        for (SaleService.SaleEntry e: all.values()){
            if (!e.offline && !e.error)
                selected.add(e);
        }
        SaleService.SaleEntry[] ret=selected.toArray(new SaleService.SaleEntry[selected.size()]);
        Arrays.sort(ret,new DatTrzbyComparator());
        return ret;
    }

    @Override
    public SaleService.SaleEntry[] findUnregistered(int offset, int limit, LimitType type) throws SaleStoreException {
        Map<String,SaleService.SaleEntry> all=load(BY_BKP);
        List<SaleService.SaleEntry> selected=new ArrayList<SaleService.SaleEntry>();
        for (SaleService.SaleEntry e: all.values()){
            if (!e.registered && !e.error)
                selected.add(e);
        }
        SaleService.SaleEntry[] ret=selected.toArray(new SaleService.SaleEntry[selected.size()]);
        Arrays.sort(ret,new DatTrzbyComparator());
        return ret;
    }

    @Override
    public SaleService.SaleEntry[] findError(int offset, int limit, LimitType type) throws SaleStoreException {
        Map<String,SaleService.SaleEntry> all=load(BY_BKP);
        List<SaleService.SaleEntry> selected=new ArrayList<SaleService.SaleEntry>();
        for (SaleService.SaleEntry e: all.values()){
            if (e.error)
                selected.add(e);
        }
        SaleService.SaleEntry[] ret=selected.toArray(new SaleService.SaleEntry[selected.size()]);
        Arrays.sort(ret,new DatTrzbyComparator());
        return ret;
    }

    @Override
    public void clearStore() throws SaleStoreException {
        Map<String,SaleService.SaleEntry> empty=new HashMap<String,SaleService.SaleEntry>();
        save(empty,BY_BKP);
    }

    @Override
    public void removeSale(String bkp) throws SaleStoreException {
        Map<String,SaleService.SaleEntry> all=load(BY_BKP);
        all.remove(bkp);
        save(all,BY_BKP);
    }

}
