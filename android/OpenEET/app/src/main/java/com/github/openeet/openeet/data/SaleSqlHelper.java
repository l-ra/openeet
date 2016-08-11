package com.github.openeet.openeet.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import openeet.lite.EetRegisterRequest;

/**
 * Created by rasekl on 8/10/16.
 */
public final class SaleSqlHelper extends SQLiteOpenHelper  {
    private static final String LOGTAG="SaleSqlHelper";
    public static final String DATABASE_NAME = "sale";
    public static final int DATABASE_VERSION = 1;

    public static abstract class Sale implements BaseColumns {
        public static final String TABLE_NAME = "sale";
        public static final String COLUMN_NAME_DIC_POPL = "dic_popl";
        public static final String COLUMN_NAME_DIC_POVERUJICIHO = "dic_poverujiciho";
        public static final String COLUMN_NAME_ID_PROVOZ = "id_provoz";
        public static final String COLUMN_NAME_ID_POKL = "id_pokl";
        public static final String COLUMN_NAME_PORAD_CIS = "porad_cis";
        public static final String COLUMN_NAME_DAT_TRZBY = "dat_trzby";
        public static final String COLUMN_NAME_CELK_TRZBA = "celk_trzba";
        public static final String COLUMN_NAME_ZAKL_NEPODL_DPH = "zakl_nepodl_dph";
        public static final String COLUMN_NAME_ZAKL_DAN1 = "zakl_dan1";
        public static final String COLUMN_NAME_DAN1 = "dan1";
        public static final String COLUMN_NAME_ZAKL_DAN2 = "zakl_dan2";
        public static final String COLUMN_NAME_DAN2 = "dan2";
        public static final String COLUMN_NAME_ZAKL_DAN3 = "zakl_dan3";
        public static final String COLUMN_NAME_DAN3 = "dan3";
        public static final String COLUMN_NAME_CEST_SLUZ = "cest_sluz";
        public static final String COLUMN_NAME_POUZIT_ZBOZ1 = "pouzit_zboz1";
        public static final String COLUMN_NAME_POUZIT_ZBOZ2 = "pouzit_zboz2";
        public static final String COLUMN_NAME_POUZIT_ZBOZ3 = "pouzit_zboz3";
        public static final String COLUMN_NAME_URCENO_CERP_ZUCT = "urceno_cerp_zuct";
        public static final String COLUMN_NAME_CERP_ZUCT = "cerp_zuct";
        public static final String COLUMN_NAME_REZIM = "rezim";
    }

    public static abstract class Send implements BaseColumns {
        public static final String TABLE_NAME = "send";
        public static final String COLUMN_NAME_SALE_ID = "sale_id";
        public static final String COLUMN_NAME_DAT_ODESLANI = "dat_odesl";
        public static final String COLUMN_NAME_PRVNI_ZASLANI = "prvni_zaslani";
        public static final String COLUMN_NAME_UUID_ZPRAVY = "uuid_zpravy";
        public static final String COLUMN_NAME_OVERENI = "overeni";
    }



    public SaleSqlHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(LOGTAG, "Creating database");

        String CREATE_SALE_TABLE="CREATE TABLE "+Sale.TABLE_NAME+" ("
                +Sale._ID + " INTEGER PRIMARY KEY AUTOINCREMENT "
                +Sale.COLUMN_NAME_DIC_POPL+" TEXT "
                +Sale.COLUMN_NAME_DIC_POVERUJICIHO+" TEXT "
                +Sale.COLUMN_NAME_ID_PROVOZ+" TEXT "
                +Sale.COLUMN_NAME_ID_POKL+" TEXT "
                +Sale.COLUMN_NAME_PORAD_CIS+" TEXT "
                +Sale.COLUMN_NAME_DAT_TRZBY+" TEXT "
                +Sale.COLUMN_NAME_CELK_TRZBA+" TEXT "
                +Sale.COLUMN_NAME_ZAKL_NEPODL_DPH+" TEXT "
                +Sale.COLUMN_NAME_ZAKL_DAN1+" TEXT "
                +Sale.COLUMN_NAME_DAN1+" TEXT "
                +Sale.COLUMN_NAME_ZAKL_DAN2+" TEXT "
                +Sale.COLUMN_NAME_DAN2+" TEXT "
                +Sale.COLUMN_NAME_ZAKL_DAN3+" TEXT "
                +Sale.COLUMN_NAME_DAN3+" TEXT "
                +Sale.COLUMN_NAME_CEST_SLUZ+" TEXT "
                +Sale.COLUMN_NAME_POUZIT_ZBOZ1+" TEXT "
                +Sale.COLUMN_NAME_POUZIT_ZBOZ2+" TEXT "
                +Sale.COLUMN_NAME_POUZIT_ZBOZ3+" TEXT "
                +Sale.COLUMN_NAME_URCENO_CERP_ZUCT+" TEXT "
                +Sale.COLUMN_NAME_CERP_ZUCT+" TEXT "
                +Sale.COLUMN_NAME_REZIM+" TEXT "
                +")";
        db.execSQL(CREATE_SALE_TABLE);

        String CREATE_SEND_TABLE="CREATE TABLE "+Send.TABLE_NAME+" ("
                +Send._ID + " INTEGER PRIMARY KEY AUTOINCREMENT "
                +Send.COLUMN_NAME_DAT_ODESLANI+" TEXT "
                +Send.COLUMN_NAME_OVERENI+" TEXT "
                +Send.COLUMN_NAME_PRVNI_ZASLANI+" TEXT "
                +Send.COLUMN_NAME_SALE_ID+" INTEGER "
                +Send.COLUMN_NAME_UUID_ZPRAVY+" TEXT "
                +")";
        db.execSQL(CREATE_SEND_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long insertSale(EetRegisterRequest req){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues val=new ContentValues();
        if (req.getDic_popl()!=null) val.put(Sale.COLUMN_NAME_DIC_POPL,req.getDic_popl());
        if (req.getDic_poverujiciho()!=null) val.put(Sale.COLUMN_NAME_DIC_POVERUJICIHO,req.getDic_poverujiciho());
        if (req.getId_provoz()!=null) val.put(Sale.COLUMN_NAME_ID_PROVOZ,req.getId_provoz());
        if (req.getId_pokl()!=null) val.put(Sale.COLUMN_NAME_ID_POKL,req.getId_pokl());
        if (req.getPorad_cis()!=null) val.put(Sale.COLUMN_NAME_PORAD_CIS,req.getPorad_cis());
        if (req.getDat_trzby()!=null) val.put(Sale.COLUMN_NAME_DAT_TRZBY,EetRegisterRequest.formatDate(req.getDat_trzby()));
        if (req.getCelk_trzba()!=null) val.put(Sale.COLUMN_NAME_CELK_TRZBA,req.getCelk_trzba());
        if (req.getZakl_nepodl_dph()!=null) val.put(Sale.COLUMN_NAME_ZAKL_NEPODL_DPH,req.getZakl_nepodl_dph());
        if (req.getZakl_dan1()!=null) val.put(Sale.COLUMN_NAME_ZAKL_DAN1,req.getZakl_dan1());
        if (req.getDan1()!=null) val.put(Sale.COLUMN_NAME_DAN1,req.getDan1());
        if (req.getZakl_dan2()!=null) val.put(Sale.COLUMN_NAME_ZAKL_DAN2,req.getZakl_dan2());
        if (req.getDan2()!=null) val.put(Sale.COLUMN_NAME_DAN2,req.getDan2());
        if (req.getZakl_dan3()!=null) val.put(Sale.COLUMN_NAME_ZAKL_DAN3,req.getZakl_dan3());
        if (req.getDan3()!=null) val.put(Sale.COLUMN_NAME_DAN3,req.getDan3());
        if (req.getCest_sluz()!=null) val.put(Sale.COLUMN_NAME_CEST_SLUZ,req.getCest_sluz());
        if (req.getPouzit_zboz1()!=null) val.put(Sale.COLUMN_NAME_POUZIT_ZBOZ1,req.getPouzit_zboz1());
        if (req.getPouzit_zboz2()!=null) val.put(Sale.COLUMN_NAME_POUZIT_ZBOZ2,req.getPouzit_zboz2());
        if (req.getPouzit_zboz3()!=null) val.put(Sale.COLUMN_NAME_POUZIT_ZBOZ3,req.getPouzit_zboz3());
        if (req.getUrceno_cerp_zuct()!=null) val.put(Sale.COLUMN_NAME_URCENO_CERP_ZUCT,req.getUrceno_cerp_zuct());
        if (req.getCerp_zuct()!=null) val.put(Sale.COLUMN_NAME_CERP_ZUCT,req.getCerp_zuct());
        if (req.getRezim()!=null) val.put(Sale.COLUMN_NAME_REZIM,req.getRezim().toString());
        return db.insert(Sale.TABLE_NAME,null,val);
    }

    public void insertSend(EetRegisterRequest req) {
        SQLiteDatabase db = getWritableDatabase();
    }

    /*
    public void buildSale(EetRegisterRequest.Builder bldr,){
        SQLiteDatabase db=getReadableDatabase();
        db.query(false,Sale.TABLE_NAME,null, )
    }
    */

}



