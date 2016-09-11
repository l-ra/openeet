package com.github.openeet.openeet.velocity;

/**
 * Created by rasekl on 9/6/16.
 */
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

import android.content.res.AssetManager;
import android.util.Log;

public class AndroidResourceLoader extends FileResourceLoader {
    private static final String LOGTAG="AndroidResourceLoader";
    private AssetManager assets;
    private String path;
    private String[] list;

    public AndroidResourceLoader(){
        super();
        Log.i(LOGTAG, "Instance created");
    }

    public void commonInit(RuntimeServices rs, ExtendedProperties configuration) {
        super.commonInit(rs,configuration);
        Log.i(LOGTAG,"commonInit");
        try {
            this.assets = (AssetManager) rs.getProperty("android.content.res.AssetManager");
            this.path = (String) rs.getProperty("android.content.res.AssetManager.path");
            Log.d(LOGTAG,"Assets Path:"+path);
            list = assets.list(path);
        }
        catch (Exception e){
            Log.e(LOGTAG,"Exception wile initing",e);
            throw new RuntimeException("Exception while initing", e);
        }
    }

    public long getLastModified(Resource resource) {
        return 0;
    }

    public InputStream getResourceStream(String templateName) {
        try {
            return assets.open(path+"/"+templateName);
        }
        catch (IOException e){
            Log.e(LOGTAG,"Exception while openning asset:"+path+"/"+templateName,e);
            return null;
        }
    }

    public boolean  isSourceModified(Resource resource) {
        return false;
    }

    public boolean  resourceExists(String templateName) {
        InputStream s=null;
        try {
            s=getResourceStream(templateName);
            return s!=null;
        }
        finally {
            try {
                if (s != null) s.close();
            }
            catch(IOException e){
                Log.i(LOGTAG,"exception while closing stream",e);
            }
        }
    }
}
