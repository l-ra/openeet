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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterSale extends AppCompatActivity {
    private static final String LOGTAG="RegisterSale";
    public static final String RESULT="com.github.openeet.openeet.RegisterSale.RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_sale);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        EditText amount=(EditText)findViewById(R.id.amount);


        amount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onDone();
                    return true;
                }
                return false;
            }
        });


        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        amount.requestFocus();
        imm.showSoftInput(amount, InputMethodManager.SHOW_FORCED);


        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(amount, InputMethodManager.SHOW_IMPLICIT);
        //amount.requestFocus();
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
        TextView dicView=(TextView) findViewById(R.id.dic);
        TextView amountView=(TextView) findViewById(R.id.amount);
        Intent resultIntent=new Intent();
        resultIntent.putExtra(RESULT, amountView.getText().toString());
        Log.i(LOGTAG,"finish activity");
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
