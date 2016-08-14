package com.github.openeet.openeet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import openeet.lite.EetSaleDTO;

public class MainActivity extends AppCompatActivity {
    private static final String LOGTAG="MainActivity";

    private static String[] STRING_ARRAY0 = new String[]{};
    private static final int REGISTER_SALE=0;

    final protected List<String> list = new ArrayList<String>();

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        broadcastReceiver=new MainBroadcastReceiver(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        updateList(list);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Adding item", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                //list.add(new Date().toString());
                //updateList(list);
                Intent registerSaleIntent = new Intent(MainActivity.this, RegisterSale.class);
                startActivityForResult(registerSaleIntent,REGISTER_SALE);
            }
        });
    }

    protected void updateList(List<String> list) {
        String[] items = list.toArray(STRING_ARRAY0);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        ListView salesList = (ListView) findViewById(R.id.salesList);
        salesList.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REGISTER_SALE: processRegisterSaleResult(resultCode,data);break;
        }
    }

    protected void processRegisterSaleResult(int resultCode, Intent data){
        EetSaleDTO dtoSale=(EetSaleDTO)data.getSerializableExtra(RegisterSale.RESULT);
        list.add("Registering:"+dtoSale);
        updateList(list);
        new RegisterSaleTask(getApplicationContext()).execute(dtoSale);
    }

    @Override
    protected void onStart() {
        Log.d(LOGTAG,"onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(LOGTAG,"onStop");
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.d(LOGTAG,"onResume");
        super.onResume();
        registerBroadcastReceivers();
    }

    @Override
    protected void onPause() {
        Log.d(LOGTAG,"onPause");
        super.onPause();
        unregisterBroadcastReceivers();
    }

    private void unregisterBroadcastReceivers() {
        Log.d(LOGTAG,"unregister br. recv");
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
    }

    private void registerBroadcastReceivers() {
        Log.d(LOGTAG,"register br. recv");
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver,RegisterSaleTask.getMatchAllFilter());
    }

    public void processBroadcast(Context context, Intent intent) {
        Log.d(LOGTAG,"onReceive: "+intent.getAction());
        if (intent.getAction().equals(RegisterSaleTask.ACTION_SALE_REGISTERED_SUCCES))
            list.add("Registed FIK:"+intent.getStringExtra(RegisterSaleTask.EXTRA_FIK));
        if (intent.getAction().equals(RegisterSaleTask.ACTION_SALE_REGISTERED_FAILURE))
            list.add("Error reg:"+intent.getStringExtra(RegisterSaleTask.EXTRA_ERROR));
        updateList(list);
    }
}
