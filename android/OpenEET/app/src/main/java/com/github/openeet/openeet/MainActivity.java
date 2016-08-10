package com.github.openeet.openeet;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static String[] STRING_ARRAY0 = new String[]{};
    private static final int REGISTER_SALE=0;

    final List<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            case REGISTER_SALE: RegisterSaleResult(resultCode,data);break;
        }
    }

    protected void RegisterSaleResult(int resultCode, Intent data){
        String amount=data.getStringExtra(RegisterSale.RESULT);
        list.add("Registering:"+amount);
        updateList(list);
        new RegisterSaleTask().execute(amount);
    }
}
