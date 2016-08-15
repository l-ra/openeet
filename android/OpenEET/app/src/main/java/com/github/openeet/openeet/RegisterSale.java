package com.github.openeet.openeet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import openeet.lite.EetSaleDTO;

public class RegisterSale extends AppCompatActivity {
    private static final String LOGTAG="RegisterSale";
    public static final String RESULT="com.github.openeet.openeet.RegisterSale.RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_sale);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RegisterSaleTemplate template=RegisterSaleTemplate.getSimpleTemplate();
        template.applyAll(findViewById(R.id.register_sale_main));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_sale, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_done:
                onDone();
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    protected void onDone(){
        Intent resultIntent=new Intent();
        EetSaleDTO dtoSale=new EetSaleDTO();

        if (findViewById(R.id.dic_popl).getVisibility()==View.VISIBLE) dtoSale.dic_popl=((EditText)findViewById(R.id.dic_popl)).getText().toString();
        if (findViewById(R.id.dic_poverujiciho).getVisibility()==View.VISIBLE) dtoSale.dic_poverujiciho=((EditText)findViewById(R.id.dic_poverujiciho)).getText().toString();
        if (findViewById(R.id.id_provoz).getVisibility()==View.VISIBLE) dtoSale.id_provoz=((EditText)findViewById(R.id.id_provoz)).getText().toString();
        if (findViewById(R.id.id_pokl).getVisibility()==View.VISIBLE) dtoSale.id_pokl=((EditText)findViewById(R.id.id_pokl)).getText().toString();
        if (findViewById(R.id.porad_cis).getVisibility()==View.VISIBLE) dtoSale.porad_cis=((EditText)findViewById(R.id.porad_cis)).getText().toString();
        if (findViewById(R.id.dat_trzby).getVisibility()==View.VISIBLE) dtoSale.dat_trzby=((EditText)findViewById(R.id.dat_trzby)).getText().toString();
        if (findViewById(R.id.celk_trzba).getVisibility()==View.VISIBLE) dtoSale.celk_trzba=((EditText)findViewById(R.id.celk_trzba)).getText().toString();
        if (findViewById(R.id.zakl_nepodl_dph).getVisibility()==View.VISIBLE) dtoSale.zakl_nepodl_dph=((EditText)findViewById(R.id.zakl_nepodl_dph)).getText().toString();
        if (findViewById(R.id.zakl_dan1).getVisibility()==View.VISIBLE) dtoSale.zakl_dan1=((EditText)findViewById(R.id.zakl_dan1)).getText().toString();
        if (findViewById(R.id.dan1).getVisibility()==View.VISIBLE) dtoSale.dan1=((EditText)findViewById(R.id.dan1)).getText().toString();
        if (findViewById(R.id.zakl_dan2).getVisibility()==View.VISIBLE) dtoSale.zakl_dan2=((EditText)findViewById(R.id.zakl_dan2)).getText().toString();
        if (findViewById(R.id.dan2).getVisibility()==View.VISIBLE) dtoSale.dan2=((EditText)findViewById(R.id.dan2)).getText().toString();
        if (findViewById(R.id.zakl_dan3).getVisibility()==View.VISIBLE) dtoSale.zakl_dan3=((EditText)findViewById(R.id.zakl_dan3)).getText().toString();
        if (findViewById(R.id.dan3).getVisibility()==View.VISIBLE) dtoSale.dan3=((EditText)findViewById(R.id.dan3)).getText().toString();
        if (findViewById(R.id.cest_sluz).getVisibility()==View.VISIBLE) dtoSale.cest_sluz=((EditText)findViewById(R.id.cest_sluz)).getText().toString();
        if (findViewById(R.id.pouzit_zboz1).getVisibility()==View.VISIBLE) dtoSale.pouzit_zboz1=((EditText)findViewById(R.id.pouzit_zboz1)).getText().toString();
        if (findViewById(R.id.pouzit_zboz2).getVisibility()==View.VISIBLE) dtoSale.pouzit_zboz2=((EditText)findViewById(R.id.pouzit_zboz2)).getText().toString();
        if (findViewById(R.id.pouzit_zboz3).getVisibility()==View.VISIBLE) dtoSale.pouzit_zboz3=((EditText)findViewById(R.id.pouzit_zboz3)).getText().toString();
        if (findViewById(R.id.urceno_cerp_zuct).getVisibility()==View.VISIBLE) dtoSale.urceno_cerp_zuct=((EditText)findViewById(R.id.urceno_cerp_zuct)).getText().toString();
        if (findViewById(R.id.cerp_zuct).getVisibility()==View.VISIBLE) dtoSale.cerp_zuct=((EditText)findViewById(R.id.cerp_zuct)).getText().toString();
        if (findViewById(R.id.rezim).getVisibility()==View.VISIBLE) dtoSale.rezim=((EditText)findViewById(R.id.rezim)).getText().toString();
        
        
        resultIntent.putExtra(RESULT, dtoSale);
        Log.i(LOGTAG,"finish activity");
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
