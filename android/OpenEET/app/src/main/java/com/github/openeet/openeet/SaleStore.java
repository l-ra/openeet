package com.github.openeet.openeet;

import android.content.Context;

/**
 * Created by rasekl on 8/21/16.
 */
public abstract class SaleStore {
    enum LimitType {
        COUNT,
        DAY,
        WEEK,
        MONTH
    }

    public static SaleStore store;
    public static synchronized SaleStore getInstance(Context context) {
        if (store==null) store=new SaleStoreFileImpl(context);
        return store;
    }

    /**
     * Persists sale entry. Uses uppercase BKP as primary key. When BKP exists in the DB, update is performed overwriting previous data.
     * @param entry
     */
    abstract public void saveSaleEntry(SaleService.SaleEntry entry) throws SaleStoreException;

    /**
     *
     * Loads base data from store - attempts do not contain req/res when inflate is false
     * @param bkp the key
     * @param inflate when true, all data is loaded
     * @return loaded entry
     */
    abstract public SaleService.SaleEntry loadSaleEntry(String bkp, boolean inflate) throws SaleStoreException; //FIXME: really needed?

    /**
     * Same as loadSaleEntry(String bkp, boolean inflate) but inflate defaults to false
     * @param bkp
     * @return loaded entry
     * @throws SaleStoreException
     */
    abstract public SaleService.SaleEntry loadSaleEntry(String bkp) throws SaleStoreException;

    /**
     * Loads details of the entry (attempt req/res)
     * @param entry the same entry but with additional data
     */
    abstract public void inflateSaleEntry(SaleService.SaleEntry entry) throws SaleStoreException;


    /**
     * Always sorted by dat_trzby newest frist, always details are not loaded (need to call inflate to get all details)
     * @param offset paging
     * @param limit count
     * @return
     * @throws SaleStoreException
     */
    abstract public SaleService.SaleEntry[] findAll(int offset, int limit, LimitType type) throws SaleStoreException;

    abstract public SaleService.SaleEntry[] findOnline(int offset, int limit, LimitType type) throws SaleStoreException;

    abstract public SaleService.SaleEntry[] findOffline(int offset, int limit, LimitType type) throws SaleStoreException;

    abstract public SaleService.SaleEntry[] findUnregistered(int offset, int limit, LimitType type) throws SaleStoreException;

    abstract public SaleService.SaleEntry[] findError(int offset, int limit, LimitType type) throws SaleStoreException;

    abstract public void clearStore() throws SaleStoreException;

    abstract public void removeSale(String bkp) throws SaleStoreException;

}
