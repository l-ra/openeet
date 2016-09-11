package com.github.openeet.openeet.velocity;

/**
 * Created by rasekl on 9/6/16.
 */
import java.io.InputStream;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

import android.content.res.AssetManager;

public class AndroidResourceLoader extends FileResourceLoader {
    private AssetManager assets;
    private String packageName;

    public void commonInit(RuntimeServices rs, ExtendedProperties configuration) {
        super.commonInit(rs,configuration);
        this.assets = (AssetManager) rs.getProperty("android.content.res.AssetManager");
    }

    public long getLastModified(Resource resource) {
        return 0;
    }

    public InputStream getResourceStream(String templateName) {
        return null; //assets.;
    }

    public boolean  isSourceModified(Resource resource) {
        return false;
    }

    public boolean  resourceExists(String templateName) {
        return false;//resources.getIdentifier(templateName, "raw", this.packageName) != 0;
    }
}
