package com.github.openeet.openeet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import openeet.lite.EetSaleDTO;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOGTAG="MainActivity";

    private enum ListViewContent {
        ALL,
        ONLINE,
        OFFLINE,
        UNREGISTERED,
        ERROR
    }

    private ListViewContent listViewContent=ListViewContent.ALL;

    private static final int REGISTER_SALE=0;

    final protected List<EetSaleDTO> list = new ArrayList<EetSaleDTO>();

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        broadcastReceiver=new MainBroadcastReceiver(this);

        setContentView(R.layout.activity_main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        updateList();
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_retry_register:
                new RetryRegisterSalesTask(getBaseContext()).execute("");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_last_receipts) {
            listViewContent=ListViewContent.ALL;
            Snackbar.make(findViewById(R.id.content_main_activity),"Zobrazeny všechny účtenky",3000).show();
        } else if (id == R.id.nav_offline_receipts) {
            listViewContent=ListViewContent.OFFLINE;
            Snackbar.make(findViewById(R.id.content_main_activity),"Zobrazeny offline registrované účtenky",3000).show();
        } else if (id == R.id.nav_online_receipts) {
            listViewContent=ListViewContent.ONLINE;
            Snackbar.make(findViewById(R.id.content_main_activity),"Zobrazeny online registrované účtenky",3000).show();
        } else if (id == R.id.nav_unregistereg_receipts) {
            listViewContent=ListViewContent.UNREGISTERED;
            Snackbar.make(findViewById(R.id.content_main_activity),"Zobrazeny pouze místně uložené účtenky",3000).show();
        } else if (id == R.id.nav_error_receipts) {
            listViewContent=ListViewContent.ERROR;
            Snackbar.make(findViewById(R.id.content_main_activity),"Zobrazeny účtenky s chybou",3000).show();
        } else if (id == R.id.nav_settings) {
            Snackbar.make(findViewById(R.id.content_main_activity),"Nastavení není implementováno",3000).show();
        } else if (id == R.id.nav_about) {
            Intent showAboutIntent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(showAboutIntent);
        }
        updateList();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void listProviders() {
        try {
            Provider p[] = Security.getProviders();
            for (int i = 0; i < p.length; i++) {
                Log.d(LOGTAG,p[i].toString());
                for (Enumeration e = p[i].keys(); e.hasMoreElements();)
                    Log.d(LOGTAG,"      " + e.nextElement().toString());
            }
        } catch (Exception e) {
            Log.e(LOGTAG,"error listing providers/algs");
        }
    }

    protected void updateList() {
        SaleStore store=SaleStore.getInstance(getBaseContext().getApplicationContext());
        SaleService.SaleEntry[] items=null;

        try {
            switch (listViewContent) {
                case ALL:
                    items = store.findAll(-1, -1, SaleStore.LimitType.COUNT);
                    break;
                case ONLINE:
                    items = store.findOnline(-1, -1, SaleStore.LimitType.COUNT);
                    break;
                case OFFLINE:
                    items = store.findOffline(-1, -1, SaleStore.LimitType.COUNT);
                    break;
                case UNREGISTERED:
                    items = store.findUnregistered(-1, -1, SaleStore.LimitType.COUNT);
                    break;
                case ERROR:
                    items = store.findError(-1, -1, SaleStore.LimitType.COUNT);
                    break;
                default:
                    items = store.findAll(-1, -1, SaleStore.LimitType.COUNT);
                    break;
            }
        }
        catch (Exception e){
            Log.e(LOGTAG,"Error while updating data from storaqe",e);
            Snackbar.make(findViewById(R.id.content_main_activity),"Chyba úložiště",3000).show();
        }

        if (items!=null) {
            ArrayAdapter<SaleService.SaleEntry> adapter = new SaleListArrayAdapter(this, items);
            ListView salesList = (ListView) findViewById(R.id.salesList);
            salesList.setAdapter(adapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REGISTER_SALE: processRegisterSaleResult(resultCode,data);break;
        }
    }

    protected void processRegisterSaleResult(int resultCode, Intent data){
        if (resultCode==RESULT_OK && data!=null) {
            EetSaleDTO dtoSale = (EetSaleDTO) data.getSerializableExtra(RegisterSale.RESULT);
            new RegisterSaleTask(getBaseContext()).execute(dtoSale);
        }
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
        updateList();
    }



}
