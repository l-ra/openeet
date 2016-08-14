package com.github.openeet.openeet;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

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
        View rowView = inflater.inflate(R.layout.sale_list_item, parent, false);
        TextView firstLine = (TextView) rowView.findViewById(R.id.sale_item_first);
        TextView secondLine = (TextView) rowView.findViewById(R.id.sale_item_second);
        TextView icon = (TextView) rowView.findViewById(R.id.sale_item_icon);
        icon.setTypeface(awesomeFont);
        icon.setText(values[position].registered?R.string.icon_done:values[position].error?R.string.icon_error:R.string.icon_exchange);
        firstLine.setText(values[position].saleData.dat_trzby+":"+values[position].saleData.celk_trzba);
        secondLine.setText(values[position].registered?values[position].fik:"neregistrováno");
        return rowView;
    }
}

