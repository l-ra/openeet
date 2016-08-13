package com.github.openeet.openeet;

import android.view.View;
import android.widget.EditText;

/**
 * Created by rasekl on 8/14/16.
 */
public class RegisterSaleTemplate {
    
    public boolean hide_dic_popl=true;
    public boolean hide_dic_poverujiciho=true;
    public boolean hide_id_provoz=true;
    public boolean hide_id_pokl=true;
    public boolean hide_porad_cis=true;
    public boolean hide_dat_trzby=true;
    public boolean hide_celk_trzba=true;
    public boolean hide_zakl_nepodl_dph=true;
    public boolean hide_zakl_dan1=true;
    public boolean hide_dan1=true;
    public boolean hide_zakl_dan2=true;
    public boolean hide_dan2=true;
    public boolean hide_zakl_dan3=true;
    public boolean hide_dan3=true;
    public boolean hide_cest_sluz=true;
    public boolean hide_pouzit_zboz1=true;
    public boolean hide_pouzit_zboz2=true;
    public boolean hide_pouzit_zboz3=true;
    public boolean hide_urceno_cerp_zuct=true;
    public boolean hide_cerp_zuct=true;
    public boolean hide_rezim=true;

    public String default_dic_popl=null;
    public String default_dic_poverujiciho=null;
    public String default_id_provoz=null;
    public String default_id_pokl=null;
    public String default_porad_cis=null;
    public String default_dat_trzby=null;
    public String default_celk_trzba=null;
    public String default_zakl_nepodl_dph=null;
    public String default_zakl_dan1=null;
    public String default_dan1=null;
    public String default_zakl_dan2=null;
    public String default_dan2=null;
    public String default_zakl_dan3=null;
    public String default_dan3=null;
    public String default_cest_sluz=null;
    public String default_pouzit_zboz1=null;
    public String default_pouzit_zboz2=null;
    public String default_pouzit_zboz3=null;
    public String default_urceno_cerp_zuct=null;
    public String default_cerp_zuct=null;
    public String default_rezim=null;

    public Integer focused;

    public void showAll() {
        hide_dic_popl = false;
        hide_dic_poverujiciho = false;
        hide_id_provoz = false;
        hide_id_pokl = false;
        hide_porad_cis = false;
        hide_dat_trzby = false;
        hide_celk_trzba = false;
        hide_zakl_nepodl_dph = false;
        hide_zakl_dan1 = false;
        hide_dan1 = false;
        hide_zakl_dan2 = false;
        hide_dan2 = false;
        hide_zakl_dan3 = false;
        hide_dan3 = false;
        hide_cest_sluz = false;
        hide_pouzit_zboz1 = false;
        hide_pouzit_zboz2 = false;
        hide_pouzit_zboz3 = false;
        hide_urceno_cerp_zuct = false;
        hide_cerp_zuct = false;
        hide_rezim = false;
    }

    public void hideAll() {
        hide_dic_popl = true;
        hide_dic_poverujiciho = true;
        hide_id_provoz = true;
        hide_id_pokl = true;
        hide_porad_cis = true;
        hide_dat_trzby = true;
        hide_celk_trzba = true;
        hide_zakl_nepodl_dph = true;
        hide_zakl_dan1 = true;
        hide_dan1 = true;
        hide_zakl_dan2 = true;
        hide_dan2 = true;
        hide_zakl_dan3 = true;
        hide_dan3 = true;
        hide_cest_sluz = true;
        hide_pouzit_zboz1 = true;
        hide_pouzit_zboz2 = true;
        hide_pouzit_zboz3 = true;
        hide_urceno_cerp_zuct = true;
        hide_cerp_zuct = true;
        hide_rezim = true;
    }

    public void applyVisibility(View v){
        if (hide_dic_popl) { v.findViewById(R.id.dic_popl).setVisibility(View.GONE); v.findViewById(R.id.lbl_dic_popl).setVisibility(View.GONE); }
        if (hide_dic_poverujiciho) { v.findViewById(R.id.dic_poverujiciho).setVisibility(View.GONE); v.findViewById(R.id.lbl_dic_poverujiciho).setVisibility(View.GONE); }
        if (hide_id_provoz) { v.findViewById(R.id.id_provoz).setVisibility(View.GONE); v.findViewById(R.id.lbl_id_provoz).setVisibility(View.GONE); }
        if (hide_id_pokl) { v.findViewById(R.id.id_pokl).setVisibility(View.GONE); v.findViewById(R.id.lbl_id_pokl).setVisibility(View.GONE); }
        if (hide_porad_cis) { v.findViewById(R.id.porad_cis).setVisibility(View.GONE); v.findViewById(R.id.lbl_porad_cis).setVisibility(View.GONE); }
        if (hide_dat_trzby) { v.findViewById(R.id.dat_trzby).setVisibility(View.GONE); v.findViewById(R.id.lbl_dat_trzby).setVisibility(View.GONE); }
        if (hide_celk_trzba) { v.findViewById(R.id.celk_trzba).setVisibility(View.GONE); v.findViewById(R.id.lbl_celk_trzba).setVisibility(View.GONE); }
        if (hide_zakl_nepodl_dph) { v.findViewById(R.id.zakl_nepodl_dph).setVisibility(View.GONE); v.findViewById(R.id.lbl_zakl_nepodl_dph).setVisibility(View.GONE); }
        if (hide_zakl_dan1) { v.findViewById(R.id.zakl_dan1).setVisibility(View.GONE); v.findViewById(R.id.lbl_zakl_dan1).setVisibility(View.GONE); }
        if (hide_dan1) { v.findViewById(R.id.dan1).setVisibility(View.GONE); v.findViewById(R.id.lbl_dan1).setVisibility(View.GONE); }
        if (hide_zakl_dan2) { v.findViewById(R.id.zakl_dan2).setVisibility(View.GONE); v.findViewById(R.id.lbl_zakl_dan2).setVisibility(View.GONE); }
        if (hide_dan2) { v.findViewById(R.id.dan2).setVisibility(View.GONE); v.findViewById(R.id.lbl_dan2).setVisibility(View.GONE); }
        if (hide_zakl_dan3) { v.findViewById(R.id.zakl_dan3).setVisibility(View.GONE); v.findViewById(R.id.lbl_zakl_dan3).setVisibility(View.GONE); }
        if (hide_dan3) { v.findViewById(R.id.dan3).setVisibility(View.GONE); v.findViewById(R.id.lbl_dan3).setVisibility(View.GONE); }
        if (hide_cest_sluz) { v.findViewById(R.id.cest_sluz).setVisibility(View.GONE); v.findViewById(R.id.lbl_cest_sluz).setVisibility(View.GONE); }
        if (hide_pouzit_zboz1) { v.findViewById(R.id.pouzit_zboz1).setVisibility(View.GONE); v.findViewById(R.id.lbl_pouzit_zboz1).setVisibility(View.GONE); }
        if (hide_pouzit_zboz2) { v.findViewById(R.id.pouzit_zboz2).setVisibility(View.GONE); v.findViewById(R.id.lbl_pouzit_zboz2).setVisibility(View.GONE); }
        if (hide_pouzit_zboz3) { v.findViewById(R.id.pouzit_zboz3).setVisibility(View.GONE); v.findViewById(R.id.lbl_pouzit_zboz3).setVisibility(View.GONE); }
        if (hide_urceno_cerp_zuct) { v.findViewById(R.id.urceno_cerp_zuct).setVisibility(View.GONE); v.findViewById(R.id.lbl_urceno_cerp_zuct).setVisibility(View.GONE); }
        if (hide_cerp_zuct) { v.findViewById(R.id.cerp_zuct).setVisibility(View.GONE); v.findViewById(R.id.lbl_cerp_zuct).setVisibility(View.GONE); }
        if (hide_rezim) { v.findViewById(R.id.rezim).setVisibility(View.GONE); v.findViewById(R.id.lbl_rezim).setVisibility(View.GONE); }
    }
    
    public void applyDefaults(View v){
        if (default_dic_popl!=null)  ((EditText)v.findViewById(R.id.dic_popl)).setText(default_dic_popl);
        if (default_dic_poverujiciho!=null)  ((EditText)v.findViewById(R.id.dic_poverujiciho)).setText(default_dic_poverujiciho);
        if (default_id_provoz!=null)  ((EditText)v.findViewById(R.id.id_provoz)).setText(default_id_provoz);
        if (default_id_pokl!=null)  ((EditText)v.findViewById(R.id.id_pokl)).setText(default_id_pokl);
        if (default_porad_cis!=null)  ((EditText)v.findViewById(R.id.porad_cis)).setText(default_porad_cis);
        if (default_dat_trzby!=null)  ((EditText)v.findViewById(R.id.dat_trzby)).setText(default_dat_trzby);
        if (default_celk_trzba!=null)  ((EditText)v.findViewById(R.id.celk_trzba)).setText(default_celk_trzba);
        if (default_zakl_nepodl_dph!=null)  ((EditText)v.findViewById(R.id.zakl_nepodl_dph)).setText(default_zakl_nepodl_dph);
        if (default_zakl_dan1!=null)  ((EditText)v.findViewById(R.id.zakl_dan1)).setText(default_zakl_dan1);
        if (default_dan1!=null)  ((EditText)v.findViewById(R.id.dan1)).setText(default_dan1);
        if (default_zakl_dan2!=null)  ((EditText)v.findViewById(R.id.zakl_dan2)).setText(default_zakl_dan2);
        if (default_dan2!=null)  ((EditText)v.findViewById(R.id.dan2)).setText(default_dan2);
        if (default_zakl_dan3!=null)  ((EditText)v.findViewById(R.id.zakl_dan3)).setText(default_zakl_dan3);
        if (default_dan3!=null)  ((EditText)v.findViewById(R.id.dan3)).setText(default_dan3);
        if (default_cest_sluz!=null)  ((EditText)v.findViewById(R.id.cest_sluz)).setText(default_cest_sluz);
        if (default_pouzit_zboz1!=null)  ((EditText)v.findViewById(R.id.pouzit_zboz1)).setText(default_pouzit_zboz1);
        if (default_pouzit_zboz2!=null)  ((EditText)v.findViewById(R.id.pouzit_zboz2)).setText(default_pouzit_zboz2);
        if (default_pouzit_zboz3!=null)  ((EditText)v.findViewById(R.id.pouzit_zboz3)).setText(default_pouzit_zboz3);
        if (default_urceno_cerp_zuct!=null)  ((EditText)v.findViewById(R.id.urceno_cerp_zuct)).setText(default_urceno_cerp_zuct);
        if (default_cerp_zuct!=null)  ((EditText)v.findViewById(R.id.cerp_zuct)).setText(default_cerp_zuct);
        if (default_rezim!=null)  ((EditText)v.findViewById(R.id.rezim)).setText(default_rezim);
    }

    public void applyFocused(View v){
        if (focused!=null) v.findViewById(focused).requestFocus();
    }

    public void applyAll(View v){
        applyVisibility(v);
        applyDefaults(v);
        applyFocused(v);
    }
    
    public static RegisterSaleTemplate getSimpleTemplate(){
        RegisterSaleTemplate tpl=new RegisterSaleTemplate();
        tpl.hideAll();
        tpl.hide_celk_trzba=false;
        tpl.hide_dic_popl=false;
        tpl.default_dic_popl="CZ1212121218";
        tpl.focused=R.id.celk_trzba;
        return tpl;
    }
}
