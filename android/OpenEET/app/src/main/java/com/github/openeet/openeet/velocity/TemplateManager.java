package com.github.openeet.openeet.velocity;


import android.app.Application;
import android.content.res.AssetManager;
import android.util.Log;

import com.github.openeet.openeet.SaleService;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Template manager is a central point for velocity templates useage in the app.
 * It provides templates, information on templates to all other parts.
 * Templates are used in two ways. Template file is used to be processed by Velocity engine to produce html.
 * The html is then displayed in WebView and the generated html is opened with base of the template file.
 * All other resources (templated includes, images etc) should be referenced relative in the template.
 *
 * Templates are structured as a tree. Several trees can be used. The default tree is marked as default.
 *
 * Created by rasekl on 9/18/16.
 */
public class TemplateManager {
    static final String LOGTAG="TemplateManager";
    static final String defaultTemplatesLocation = "file:///android_asset/templates/default";

    private String templatesLocation;
    private VelocityEngine engine;
    private AssetManager manager;

    public TemplateManager(AssetManager manager){
        this(manager, defaultTemplatesLocation);
    }

    public TemplateManager(AssetManager manager, String templatesLocation){
        Log.d(LOGTAG,"Template Managere created for location:"+templatesLocation);
        this.templatesLocation=templatesLocation;

        engine=new VelocityEngine();

        engine.setProperty("resource.loader", "asset");
        engine.setProperty("asset.resource.loader.class", "com.github.openeet.openeet.velocity.AndroidAssetLoader");
        engine.setProperty("asset.resource.loader.path", templatesLocation);
        engine.setProperty("asset.resource.loader.manager", manager);

        engine.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, "com.github.openeet.openeet.velocity.Logger");
        engine.init();
        Log.d(LOGTAG,"Velocity inited");
    }

    public String getTemplatesLocation(){
        return templatesLocation;
    }

    public String processTemplate(String templateName, VelocityContext context){
        try {
            String templateFile=templateName;
            Log.i(LOGTAG,"Using template: "+templateFile);
            Template template = engine.getTemplate(templateFile,"utf-8");
            StringWriter sw = new StringWriter();
            template.merge(context, sw);
            return sw.toString();
        }
        catch (Exception e){
            Log.e(LOGTAG, "template exception", e);
            StringWriter sw=new StringWriter();
            PrintWriter wr=new PrintWriter(sw);
            e.printStackTrace(wr);
            wr.close();
            return String.format("<h1 color='red'>Tempate error</h1><p>Error while processing template:<br>%s<br>%s</p>",e.getMessage(), sw.toString());
        }
    }

}
