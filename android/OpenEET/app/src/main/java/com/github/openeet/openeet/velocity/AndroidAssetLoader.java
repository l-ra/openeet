package com.github.openeet.openeet.velocity;

/**
 * Created by rasekl on 9/6/16.
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

import android.content.res.AssetManager;
import android.util.Log;


public class AndroidAssetLoader extends FileResourceLoader {
    private static final String LOGTAG="AndroidAssetLoader";
    private static final String ASSET_PREFIX="file:///android_asset/";
    private AssetManager assets;
    private String path;
    private String[] list;

    public AndroidAssetLoader(){
        super();
        Log.i(LOGTAG, "Instance created");
    }

    public void commonInit(RuntimeServices rs, ExtendedProperties configuration) {
        super.commonInit(rs,configuration);
        Log.i(LOGTAG,"commonInit");
        try {
            this.assets = (AssetManager) rs.getProperty("asset.resource.loader.manager");
            setPath((String) rs.getProperty("asset.resource.loader.path"));
            Log.d(LOGTAG,"Assets Path:"+path);
            //list = assets.list(path);
        }
        catch (Exception e){
            Log.e(LOGTAG,"Exception wile initing",e);
            throw new RuntimeException("Exception while initing", e);
        }
    }

    private void setPath(String path){
        if (!path.startsWith(ASSET_PREFIX))
            throw new IllegalArgumentException("Path for templates must start with "+ASSET_PREFIX+". Bad path:"+path);
        this.path=path.substring(ASSET_PREFIX.length());
        Log.d(LOGTAG, "Path for templates set:"+this.path);
    }

    public long getLastModified(Resource resource) {
        return 0;
    }

    public InputStream getResourceStream(String templateName) {
        Log.d(LOGTAG,"Getting resource:"+templateName);
        try {
            return assets.open(path+"/"+templateName);
        }
        catch (FileNotFoundException fnfe) {
            Log.i(LOGTAG,"Resource not found:"+path+"/"+templateName);
            return null;
        }
        catch (IOException ioe ){
            Log.e(LOGTAG,"Exception while openning asset:"+path+"/"+templateName, ioe);
            return null;
        }
    }

    public boolean  isSourceModified(Resource resource) {
        return false;
    }

    public boolean  resourceExists(String templateName) {
        Log.d(LOGTAG, "Checking asset exist:"+templateName);
        InputStream s=null;
        try {
            s=getResourceStream(templateName);
            if (s!=null) Log.d(LOGTAG, "Asset exist:"+templateName);
            else Log.w(LOGTAG, "Asset not found:"+templateName);
            return s!=null;
        }
        finally {
            try {
                if (s != null) s.close();
            }
            catch(IOException e){
                Log.w(LOGTAG,"exception while closing stream",e);
            }
        }
    }
}
