package com.github.openeet.openeet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by rasekl on 8/14/16.
 */
public class MainBroadcastReceiver extends BroadcastReceiver {

    protected MainActivity mainActivity;

    protected MainBroadcastReceiver(MainActivity mainActivity){
        this.mainActivity=mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mainActivity.processBroadcast(context,intent);
    }
}
