package openeet.lite;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.UUID;

public class EetMessageData {
	public static enum PrvniOdeslani {
		PRVNI(true), OPAKOVANE(false);
		private boolean _value;

		private PrvniOdeslani(boolean value) {
			_value = value;
		}

		public boolean toBoolean() {
			return _value;
		}
	}

	public static enum Rezim {
		STANDARDNI(0), ZJEDNODUSENY(1);
		private int _value;

		private Rezim(int value) {
			_value = value;
		}

		public int toInt() {
			return _value;
		}
	}

	public static class Builder {
		protected PrivateKey _key;
		protected X509Certificate _certificate;
		protected Date _dat_odesl=new Date();
		protected PrvniOdeslani _prvni_zaslani=PrvniOdeslani.PRVNI;
		protected UUID _uuid_zpravy=UUID.randomUUID();
		protected String _dic_popl;
		protected String _dic_poverujiciho;
		protected String _id_provoz;
		protected String _id_pokl;
		protected String _porad_cis;
		protected Date _dat_trzby=new Date();
		protected Double _celk_trzba;
		protected Double _zakl_nepodl_dph;
		protected Double _zakl_dan1;
		protected Double _dan1;
		protected Double _zakl_dan2;
		protected Double _dan2;
		protected Double _zakl_dan3;
		protected Double _dan3;
		protected Double _cest_sluz;
		protected Double _pouzit_zboz1;
		protected Double _pouzit_zboz2;
		protected Double _pouzit_zboz3;
		protected Double _urceno_cerp_zuct;
		protected Double _cerp_zuct;
		protected Rezim _rezim=Rezim.STANDARDNI;

		protected byte[] _bkp;
		protected byte[] _pkp;

		public Builder certificate(X509Certificate val) {
			_certificate = val;
			return this;
		}

		
		public Builder key(PrivateKey val) {
			_key = val;
			return this;
		}

		
		/**
		 * Defaults to time of builder creation 
		 * @param val
		 * @return
		 */
		public Builder dat_odesl(Date val) {
			_dat_odesl = val;
			return this;
		}

		/** 
		 * Defaults to PrvniZaslani.PRVNI
		 * @param val
		 * @return
		 */
		public Builder prvni_zaslani(PrvniOdeslani val) {
			_prvni_zaslani = val;
			return this;
		}

		/**
		 * Defaults to random UUID
		 * @param val
		 * @return
		 */
		public Builder uuid_zpravy(UUID val) {
			_uuid_zpravy = val;
			return this;
		}

		public Builder dic_popl(String val) {
			_dic_popl = val;
			return this;
		}

		public Builder dic_poverujiciho(String val) {
			_dic_poverujiciho = val;
			return this;
		}

		public Builder id_provoz(String val) {
			_id_provoz = val;
			return this;
		}

		public Builder id_pokl(String val) {
			_id_pokl = val;
			return this;
		}

		public Builder porad_cis(String val) {
			_porad_cis = val;
			return this;
		}

		/**
		 * Defaults to builder creation time
		 * @param val
		 * @return
		 */
		public Builder dat_trzby(Date val) {
			_dat_trzby = val;
			return this;
		}

		public Builder celk_trzba(Double val) {
			_celk_trzba = val;
			return this;
		}

		public Builder zakl_nepodl_dph(Double val) {
			_zakl_nepodl_dph = val;
			return this;
		}

		public Builder zakl_dan1(Double val) {
			_zakl_dan1 = val;
			return this;
		}

		public Builder dan1(Double val) {
			_dan1 = val;
			return this;
		}

		public Builder zakl_dan2(Double val) {
			_zakl_dan2 = val;
			return this;
		}

		public Builder dan2(Double val) {
			_dan2 = val;
			return this;
		}

		public Builder zakl_dan3(Double val) {
			_zakl_dan3 = val;
			return this;
		}

		public Builder dan3(Double val) {
			_dan3 = val;
			return this;
		}

		public Builder cest_sluz(Double val) {
			_cest_sluz = val;
			return this;
		}

		public Builder pouzit_zboz1(Double val) {
			_pouzit_zboz1 = val;
			return this;
		}

		public Builder pouzit_zboz2(Double val) {
			_pouzit_zboz2 = val;
			return this;
		}

		public Builder pouzit_zboz3(Double val) {
			_pouzit_zboz3 = val;
			return this;
		}

		public Builder urceno_cerp_zuct(Double val) {
			_urceno_cerp_zuct = val;
			return this;
		}

		public Builder cerp_zuct(Double val) {
			_cerp_zuct = val;
			return this;
		}

		/*
		 * Defualts to Rezim.STANDARDNI
		 */
		public Builder rezim(Rezim val) {
			_rezim = val;
			return this;
		}

		/**
		 * Computed when pkp available during build() call
		 * @param val
		 * @return
		 */
		public Builder bkp(byte[] val) {
			_bkp = val;
			return this;
		}

		/**
		 * Computed when private key available during nuild() call
		 * @param val
		 * @return
		 */
		public Builder pkp(byte[] val) {
			_pkp = val;
			return this;
		}

		public EetMessageData build() {
			try {
				if (_pkp==null && _key!=null){
					String toBeSigned=formatToBeSignedData();
					if (toBeSigned!=null){
						Signature signature = Signature.getInstance("SHA256withRSA");
				        signature.initSign(_key);
				        signature.update(toBeSigned.getBytes("UTF-8"));
				        _pkp=signature.sign();					}
				}

				if ( _bkp!=null && _pkp !=null){
					MessageDigest md=MessageDigest.getInstance("SHA-1");
					_bkp=md.digest(_pkp);
				}
				return new EetMessageData(this);
			}
			catch(Exception e){
				throw new RuntimeException("Problems with cryptosetup",e);
			} 
		}


		private String formatToBeSignedData() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	protected X509Certificate certificate;
	protected Date dat_odesl;
	protected PrvniOdeslani prvni_zaslani;
	protected UUID uuid_zpravy;
	protected String dic_popl;
	protected String dic_poverujiciho;
	protected String id_provoz;
	protected String id_pokl;
	protected String porad_cis;
	protected Date dat_trzby;
	protected Double celk_trzba;
	protected Double zakl_nepodl_dph;
	protected Double zakl_dan1;
	protected Double dan1;
	protected Double zakl_dan2;
	protected Double dan2;
	protected Double zakl_dan3;
	protected Double dan3;
	protected Double cest_sluz;
	protected Double pouzit_zboz1;
	protected Double pouzit_zboz2;
	protected Double pouzit_zboz3;
	protected Double urceno_cerp_zuct;
	protected Double cerp_zuct;
	protected Rezim rezim;

	protected byte[] bkp;
	protected byte[] pkp;

	protected EetMessageData(Builder builder) {
		certificate = builder._certificate;
		dat_odesl = builder._dat_odesl;
		prvni_zaslani = builder._prvni_zaslani;
		uuid_zpravy = builder._uuid_zpravy;
		dic_popl = builder._dic_popl;
		dic_poverujiciho = builder._dic_poverujiciho;
		id_provoz = builder._id_provoz;
		id_pokl = builder._id_pokl;
		porad_cis = builder._porad_cis;
		dat_trzby = builder._dat_trzby;
		celk_trzba = builder._celk_trzba;
		zakl_nepodl_dph = builder._zakl_nepodl_dph;
		zakl_dan1 = builder._zakl_dan1;
		dan1 = builder._dan1;
		zakl_dan2 = builder._zakl_dan2;
		dan2 = builder._dan2;
		zakl_dan3 = builder._zakl_dan3;
		dan3 = builder._dan3;
		cest_sluz = builder._cest_sluz;
		pouzit_zboz1 = builder._pouzit_zboz1;
		pouzit_zboz2 = builder._pouzit_zboz2;
		pouzit_zboz3 = builder._pouzit_zboz3;
		urceno_cerp_zuct = builder._urceno_cerp_zuct;
		cerp_zuct = builder._cerp_zuct;
		rezim = builder._rezim;
		bkp = builder._bkp;
		pkp = builder._pkp;
	}

	public X509Certificate getCertificate() {
		return certificate;
	}

	public Date getDat_odesl() {
		return dat_odesl;
	}

	public PrvniOdeslani getPrvni_zaslani() {
		return prvni_zaslani;
	}

	public UUID getUuid_zpravy() {
		return uuid_zpravy;
	}

	public String getDic_popl() {
		return dic_popl;
	}

	public String getDic_poverujiciho() {
		return dic_poverujiciho;
	}

	public String getId_provoz() {
		return id_provoz;
	}

	public String getId_pokl() {
		return id_pokl;
	}

	public String getPorad_cis() {
		return porad_cis;
	}

	public Date getDat_trzby() {
		return dat_trzby;
	}

	public Double getCelk_trzba() {
		return celk_trzba;
	}

	public Double getZakl_nepodl_dph() {
		return zakl_nepodl_dph;
	}

	public Double getZakl_dan1() {
		return zakl_dan1;
	}

	public Double getDan1() {
		return dan1;
	}

	public Double getZakl_dan2() {
		return zakl_dan2;
	}

	public Double getDan2() {
		return dan2;
	}

	public Double getZakl_dan3() {
		return zakl_dan3;
	}

	public Double getDan3() {
		return dan3;
	}

	public Double getCest_sluz() {
		return cest_sluz;
	}

	public Double getPouzit_zboz1() {
		return pouzit_zboz1;
	}

	public Double getPouzit_zboz2() {
		return pouzit_zboz2;
	}

	public Double getPouzit_zboz3() {
		return pouzit_zboz3;
	}

	public Double getUrceno_cerp_zuct() {
		return urceno_cerp_zuct;
	}

	public Double getCerp_zuct() {
		return cerp_zuct;
	}

	public Rezim getRezim() {
		return rezim;
	}

	public byte[] getBkp() {
		return bkp;
	}

	public byte[] getPkp() {
		return pkp;
	}
}
