package com.github.openeet.openeet;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Base64DataException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebView;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.StringWriter;

import openeet.lite.Base64;

public class SaleDetailActivity extends AppCompatActivity {
    public static final String LOGCAT="SaleDetailActivity";

    public static final String EXTRA_SALE_ENTRY="com.github.openeet.openeet.SaleDetailActivity.ExtraSaleEntry";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private static void setupVelocity(AssetManager assetManager) {
        Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, "com.github.openeet.openeet.velocity.Logger");
        Velocity.setProperty("resource.loader", "android");
        Velocity.setProperty("android.resource.loader.class", "com.github.openeet.openeet.velocity.AndroidResourceLoader");
        Velocity.setProperty("android.content.res.AssetManager",assetManager);
        Velocity.setProperty("android.content.res.AssetManager.path","com/github/openeet/templates");

        Velocity.init();
    }


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupVelocity(getAssets());

        setContentView(R.layout.activity_sale_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        SaleService.SaleEntry entry=(SaleService.SaleEntry)getIntent().getSerializableExtra(EXTRA_SALE_ENTRY);

        FloatingActionButton fab_print = (FloatingActionButton) findViewById(R.id.fab_print);
        fab_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Start printing", Snackbar.LENGTH_LONG).show();
            }
        });

        FloatingActionButton fab_share = (FloatingActionButton) findViewById(R.id.fab_share);
        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Start share", Snackbar.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sale_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class ReceiptFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public ReceiptFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ReceiptFragment newInstance(Bundle params) {
            ReceiptFragment fragment = new ReceiptFragment();
            fragment.setArguments(params);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_sale_detail_receipt, container, false);
            SaleService.SaleEntry entry=(SaleService.SaleEntry) getArguments().getSerializable(EXTRA_SALE_ENTRY);
            if(entry!=null) {
                WebView logWebView = (WebView) rootView.findViewById(R.id.receipt_web_view);
                String html = formatReceipt(entry);
                if (html!=null) {
                    //logWebView.loadDataWithBaseURL("file://android_assets/com/github/openeet/templates", string2base64(html), "text/html; charset=utf-8", "base64", null);
                    logWebView.loadDataWithBaseURL("file:///android_asset/com/github/openeet/templates/", (html), "text/html; charset=utf-8", "base64", null);
                }
                else {
                    logWebView.loadData(string2base64("<h1>Template Erroe</h1>"), "text/html; charset=utf-8", "base64");
                }
            }
            else {
                Log.e(LOGCAT,"No entry data arived");
            }
            return rootView;
        }

        private String formatReceipt(SaleService.SaleEntry entry) {
            try {
                VelocityContext context = new VelocityContext();
                context.put("sale",entry);
                Template template = Velocity.getTemplate("receipt.vm","utf-8");
                StringWriter sw = new StringWriter();
                template.merge(context, sw);
                return sw.toString();
            }
            catch (Exception e){
                Log.e(LOGCAT, "template exception", e);
            }
            return null;
        }
    }

    private static String string2base64(String s){
        try {
            return Base64.encodeToString(s.getBytes("utf-8"), Base64.NO_WRAP);
        }
        catch (Exception e){
            Log.e(LOGCAT,"exception whit encoding",e);
            return null;
        }
    }


    public static class LogFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public LogFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static LogFragment newInstance(Bundle params) {
            LogFragment fragment = new LogFragment();
            fragment.setArguments(params);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_sale_detail_log, container, false);
            SaleService.SaleEntry entry=(SaleService.SaleEntry) getArguments().getSerializable(EXTRA_SALE_ENTRY);
            if(entry!=null) {
                WebView logWebView = (WebView) rootView.findViewById(R.id.logWebView);
                String html = String.format("<h4 style='color: blue;'>Log</h4><p>FIK:%s</p>", entry.fik);
                logWebView.loadData(string2base64(html), "text/html; charset=utf-8", "base64");
            }
            else {
                Log.e(LOGCAT,"No entry data arived");
            }
            return rootView;
        }
    }

    public static class TechFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public TechFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static TechFragment newInstance(Bundle params) {
            Log.d(LOGCAT,"Techragment.newInstance");
            TechFragment fragment = new TechFragment();
            fragment.setArguments(params);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_sale_detail_tech, container, false);

            SaleService.SaleEntry entry=(SaleService.SaleEntry) getArguments().getSerializable(EXTRA_SALE_ENTRY);
            if(entry!=null) {
                WebView logWebView = (WebView) rootView.findViewById(R.id.techWebView);
                String html = String.format("<h4 style='color: blue;'>Technická diagnostika</h4><p>FIK:%s</p>", entry.fik);
                logWebView.loadData(string2base64(html), "text/html; charset=utf-8", "base64");
            }
            else {
                Log.e(LOGCAT,"No entry data arived");
            }

            return rootView;
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ReceiptFragment.newInstance(getIntent().getExtras());
                case 1:
                    return LogFragment.newInstance(getIntent().getExtras());
                case 2:
                    return TechFragment.newInstance(getIntent().getExtras());
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Účtenka";
                case 1:
                    return "Záznam odesílání";
                case 2:
                    return "Detaily odesílání";
            }
            return null;
        }
    }


}
