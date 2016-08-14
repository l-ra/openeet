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
    
}
