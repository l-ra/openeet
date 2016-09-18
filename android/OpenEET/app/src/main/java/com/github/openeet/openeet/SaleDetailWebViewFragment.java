package com.github.openeet.openeet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.github.openeet.openeet.velocity.TemplateManager;

import org.apache.velocity.VelocityContext;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by rasekl on 9/18/16.
 */
public class SaleDetailWebViewFragment extends Fragment {
    private static final String LOGTAG="SaleDetailFragment";

    private static final String TEMPLATE_NAME_PARAM="com.github.openeet.openeet.SaleDetailWebViewFragment.templateName";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public SaleDetailWebViewFragment() {
        super();
    }

    public static SaleDetailWebViewFragment newInstance(String templateName, Bundle params){
        SaleDetailWebViewFragment fragment=new SaleDetailWebViewFragment();
        params.putString(TEMPLATE_NAME_PARAM,templateName);
        fragment.setArguments(params);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sale_detail_receipt, container, false);

        SaleService.SaleEntry entry=(SaleService.SaleEntry) getArguments().getSerializable(SaleDetailActivity.EXTRA_SALE_ENTRY);
        String templateName= getArguments().getString(TEMPLATE_NAME_PARAM);

        if(entry!=null) {
            Log.d(LOGTAG,"Displaying sale entry");

            WebView logWebView = (WebView) rootView.findViewById(R.id.receipt_web_view);
            VelocityContext ctx=new VelocityContext();
            ctx.put("sale",entry);

            TemplateManager templateManager=new TemplateManager(getActivity().getAssets());
            String html=templateManager.processTemplate(templateName, ctx);

            logWebView.loadDataWithBaseURL(templateManager.getTemplatesLocation(), (html), "text/html; charset=utf-8", "base64", null);
        }
        else {
            Log.e(LOGTAG,"No entry data arived");
        }
        return rootView;
    }
}
