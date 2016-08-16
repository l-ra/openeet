package com.github.openeet.openeet;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

import openeet.lite.Base64;
import openeet.lite.EetRegisterRequest;
import openeet.lite.EetSaleDTO;

/**
 * Created by rasekl on 8/14/16.
 */
public class SaleListArrayAdapter extends ArrayAdapter<SaleService.SaleEntry> {
    private final Context context;
    private final SaleService.SaleEntry[] values;
    private Typeface awesomeFont;

    public SaleListArrayAdapter(Context context, SaleService.SaleEntry[] values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
        awesomeFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/fontawesome-webfont.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView=null;
        if (convertView!=null && convertView.findViewById(R.id.sale_item_first)!=null ){
            //reuse view
            rowView=convertView;
        }
        else {
            rowView=inflater.inflate(R.layout.sale_list_item, parent, false);
        }
        TextView firstLine = (TextView) rowView.findViewById(R.id.sale_item_first);
        TextView secondLine = (TextView) rowView.findViewById(R.id.sale_item_second);
        TextView icon = (TextView) rowView.findViewById(R.id.sale_item_icon);
        icon.setTypeface(awesomeFont);
        SaleService.SaleEntry entry=values[position];

        firstLine.setText(String.format("%s Kč, %s",entry.saleData.celk_trzba, reformatDate(entry.saleData.dat_trzby)));
        if (entry.inProgress){
            icon.setText(R.string.icon_gears);
            secondLine.setText(String.format("probíhá zpracování pokus %d",entry.attempts.size()));
        }
        else {
            if (entry.registered) {
                icon.setText(R.string.icon_done);
                secondLine.setText(String.format("fik: %s",entry.fik));
            } else {
                if (entry.error) {
                    icon.setText(R.string.icon_error);
                    secondLine.setText(String.format("chyba, nebude zpracováno"));
                } else {
                    icon.setText(R.string.icon_exchange);
                    secondLine.setText(String.format("bkp: %s\npokusů o odeslání:%d\nodloženo: %s",
                            shortenBkp(entry.saleData.bkp), entry.attempts.size(), entry.attempts.get(entry.attempts.size()-1).throwable.getMessage()));
                }
            }
        }
        String easter=easter(entry.saleData.celk_trzba);
        if ( easter!=null) {
            firstLine.setText(easter);
            firstLine.setTypeface(awesomeFont);
        }
        return rowView;
    }

    private String shortenBkp(String bkp){
        return String.format("%s...%s",bkp.substring(0,10),bkp.substring(bkp.lastIndexOf("-")));
    }

    private String reformatDate(String date){
        if (date==null) return "no date yet";
        Date d=EetRegisterRequest.parseDate(date);
        Date now=new Date();
        long ageSec=(now.getTime()-d.getTime())/1000;
        if (ageSec<60) return "před méně než minutou";
        if (ageSec<(60*60)) return String.format("před méně než hodinou");
        if (ageSec<(60*60*48)) return String.format("před %d hodinami",Math.round(ageSec/3600.0));
        return (new SimpleDateFormat("d.M.")).format(d);
    }

    private String easter(String celk_trzba){
        String LOGCAT="Easter";
        try {
            String hash=Base64.encodeToString(MessageDigest.getInstance("MD5").digest(celk_trzba.getBytes("utf-8")),Base64.NO_WRAP);
            //Log.d(LOGCAT, celk_trzba+":"+hash);
            if (hash.equals("IB7it4ev1NeWRrFD9mfLWQ==")){
                return "\uF1B0 \uF1B0 \uF1B0 \uF1B0 \uF1B0 \uF1B0 \uF1B0 \uF1B0 \uF1B0 \uF1B0";
            }
        }
        catch (Exception e){
            Log.e(LOGCAT,"wooooops",e);
        }
        return null;
    }
}

