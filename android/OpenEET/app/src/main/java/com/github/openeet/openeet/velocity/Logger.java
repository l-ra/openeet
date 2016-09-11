package com.github.openeet.openeet.velocity;

/**
 * Created by rasekl on 9/6/16.
 */
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

import android.util.Log;

public class Logger implements LogChute {
    private final static String tag = "Velocity";

    @Override
    public void init(RuntimeServices arg0) throws Exception {
    }

    @Override
    public boolean isLevelEnabled(int level) {
        return level > LogChute.DEBUG_ID;
    }

    @Override
    public void log(int level, String msg) {
        switch(level) {
            case LogChute.DEBUG_ID:
                Log.d(tag,msg);
                break;
            case LogChute.ERROR_ID:
                Log.e(tag,msg);
                break;
            case LogChute.INFO_ID:
                Log.i(tag,msg);
                break;
            case LogChute.TRACE_ID:
                Log.d(tag,msg);
                break;
            case LogChute.WARN_ID:
                Log.w(tag,msg);
        }
    }

    @Override
    public void log(int level, String msg, Throwable t) {
        switch(level) {
            case LogChute.DEBUG_ID:
                Log.d(tag,msg,t);
                break;
            case LogChute.ERROR_ID:
                Log.e(tag,msg,t);
                break;
            case LogChute.INFO_ID:
                Log.i(tag,msg,t);
                break;
            case LogChute.TRACE_ID:
                Log.d(tag,msg,t);
                break;
            case LogChute.WARN_ID:
                Log.w(tag,msg,t);
        }
    }

}
