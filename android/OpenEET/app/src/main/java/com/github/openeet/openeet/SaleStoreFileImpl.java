package com.github.openeet.openeet;

import android.support.v7.util.SortedList;

/**
 * Created by rasekl on 8/22/16.
 */
public class SaleStoreFileImpl implements  SaleStore {

    protected static final class SaleEntry extends SaleService.SaleEntry {
        boolean needInflate=true;
    }

    private SortedList<SaleEntry> list

    @Override
    public void saveSaleEntry(SaleService.SaleEntry entry) throws SaleStoreException {


    }

    @Override
    public SaleService.SaleEntry loadSaleEntry(String bkp, boolean inflate) throws SaleStoreException {
        return null;
    }

    @Override
    public SaleService.SaleEntry loadSaleEntry(String bkp) throws SaleStoreException {
        return null;
    }

    @Override
    public void inflateSaleEntry(SaleService.SaleEntry entry) throws SaleStoreException {

    }

    @Override
    public SaleService.SaleEntry[] findAll(int offset, int limit, LimitType type) throws SaleStoreException {
        return new SaleService.SaleEntry[0];
    }

    @Override
    public SaleService.SaleEntry[] findOffline(int offset, int limit, LimitType type) throws SaleStoreException {
        return new SaleService.SaleEntry[0];
    }

    @Override
    public SaleService.SaleEntry[] findUnregistered(int offset, int limit, LimitType type) throws SaleStoreException {
        return new SaleService.SaleEntry[0];
    }

    @Override
    public SaleService.SaleEntry[] findError(int offset, int limit, LimitType type) throws SaleStoreException {
        return new SaleService.SaleEntry[0];
    }
}
