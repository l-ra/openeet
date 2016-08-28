package com.github.openeet.openeet;

/**
 * Created by rasekl on 8/22/16.
 */
public class SaleStoreException extends Exception {

    public SaleStoreException(String message){
        super(message);
    }

    public SaleStoreException(String message, Throwable t){
        super(message, t);
    }
}
