package openeet.lite;

import java.io.Serializable;

/**
 * DTO for sale data - no checks, no verifications, just a data holder. 
 * All data is expected in the form usable for corresponding builder method in EetRegisterRequest.
 * @author rasekl
 *
 */
public class EetSaleDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	public String dic_popl;
    public String dic_poverujiciho;
    public String id_provoz;
    public String id_pokl;
    public String porad_cis;
    public String dat_trzby;
    public String celk_trzba;
    public String zakl_nepodl_dph;
    public String zakl_dan1;
    public String dan1;
    public String zakl_dan2;
    public String dan2;
    public String zakl_dan3;
    public String dan3;
    public String cest_sluz;
    public String pouzit_zboz1;
    public String pouzit_zboz2;
    public String pouzit_zboz3;
    public String urceno_cerp_zuct;
    public String cerp_zuct;
    public String rezim; 
    public String bkp;
    public String pkp;
    
    
	public String getDicPopl(){ return dic_popl; }
    public String getDicPoverujiciho(){ return dic_poverujiciho; }
    public String getIdProvoz(){ return id_provoz; }
    public String getIdPokl(){ return id_pokl; }
    public String getPoradCis(){ return porad_cis; }
    public String getDatTrzby(){ return dat_trzby; }
    public String getCelkTrzba(){ return celk_trzba; }
    public String getZaklNepodlDph(){ return zakl_nepodl_dph; }
    public String getZaklDan1(){ return zakl_dan1; }
    public String getDan1(){ return dan1; }
    public String getZaklDan2(){ return zakl_dan2; }
    public String getDan2(){ return dan2; }
    public String getZaklDan3(){ return zakl_dan3; }
    public String getDan3(){ return dan3; }
    public String getCestSluz(){ return cest_sluz; }
    public String getPouzitZboz1(){ return pouzit_zboz1; }
    public String getPouzitZboz2(){ return pouzit_zboz2; }
    public String getPouzitZboz3(){ return pouzit_zboz3; }
    public String getUrcenoCerpZuct(){ return urceno_cerp_zuct; }
    public String getCerpZuct(){ return cerp_zuct; }
    public String getRezim(){ return rezim; } 
    public String getBkp(){ return bkp; }
    public String getPkp(){ return pkp; }    
    
    
    @Override
    public String toString() {
    	return String.format("%s|%s|%s|%s|%s|%s",dic_popl, id_provoz, id_pokl, porad_cis, dat_trzby,celk_trzba);
    }   
}
