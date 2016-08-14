/*
 * Copyright 2016 Luděk Rašek and other contributors as 
 * indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package openeet.lite;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Class implements everything what's needed to send sale registration request to the registration server. 
 * The code is tailored to version 2 of the registration interface as described <a href="http://www.etrzby.cz/cs/technicka-specifikace">in the technical specification</a> 
 * The code does not have any other dependencies than java runtime itself. As it requires SHA-256 hash function 
 * at least java 1.4.2 must be used.
 * <p>
 * The Builder is a tool for instance creation. For usage examples see tests. 
 * <p>
 * Basic usage:
 * <pre>
 * EetRegisterRequest data=EetRegisterRequest.builder()
	   .dic_popl("CZ1212121218")
	   .id_provoz("1")
	   .id_pokl("POKLADNA01")
	   .porad_cis("1")
	   .dat_trzby("2016-06-30T08:43:28+02:00")
	   .celk_trzba(100.0)
	   .rezim(0)
	   .certificate(cert)
	   .key(key)
	   .build();
	String requestBody=data.generateSoapRequest(key);
	String response=data.sendRequest(signed, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v2"));
 * 
 * </pre>
 *  
 * @author rasekl
 *
 */

public class EetRegisterRequest {

	/**
	 * Enumeration representing request header entry prvni_zaslani. 
	 * 
	 * @author rasekl
	 *
	 */
	public static enum PrvniZaslani {
		PRVNI(true), OPAKOVANE(false);
		private boolean _value;

		private PrvniZaslani(boolean value) {
			_value = value;
		}

		public boolean toBoolean() {
			return _value;
		}
		
		public static PrvniZaslani valueOf(boolean b){
			if (b) return PRVNI;
			else return OPAKOVANE;
		}
		
		@Override
		public String toString(){
			return String.valueOf(_value);
		}
	}

	/**
	 * Enumeration representing request header entry overeni. 
	 * 
	 * @author rasekl
	 *
	 */
	public static enum Overeni {
		OVEROVACI(true), PRODUKCNI(false);
		private boolean _value;

		private Overeni(boolean value) {
			_value = value;
		}

		public boolean toBoolean() {
			return _value;
		}
		
		public static Overeni valueOf(boolean b){
			if (b) return OVEROVACI;
			else return PRODUKCNI;
		}

		@Override
		public String toString(){
			return String.valueOf(_value);
		}
	}

	/**
	 * Enumeration representing request entry rezim. 
	 * 
	 * @author rasekl
	 *
	 */

	public static enum Rezim {
		STANDARDNI(0), ZJEDNODUSENY(1);
		private int _value;

		private Rezim(int value) {
			_value = value;
		}

		public int toInt() {
			return _value;
		}
		
		public static Rezim valueOf(int n){
			switch (n) {
				case 0: return STANDARDNI;
				case 1: return ZJEDNODUSENY;
				default: throw new IllegalArgumentException("requested value not allowed:"+n);
			}
		}

		/**
		 * merges 0,1 and names of enums together
		 * @param n 0,1,STANDARDNI,ZJEDNODUSENY are valid inputs
		 * @return enum member accordingly
		 */
		public static Rezim fromString(String n){
			if (n.matches("^[0-9]+$")) return valueOf(Integer.valueOf(n));
			return valueOf(n);
		}

		@Override
		public String toString(){
			return String.valueOf(_value);
		}
	}

	
	/**
	 * The Builder class is a tool for EetRegisterRequest creation. 
	 * Some of the fields has default values. See each builder method for default values.
	 * When a key property is set, PKP and BKP codes are computed during build phase.
	 * When a certificate property is set it is possible to create SOAP request from the resulting EetRegisterRequest object
	 * When null is passed to some of builder method, it is completely ignored like the method was not called      
	 * @author rasekl
	 *
	 */
	public static class Builder {
		
		//client setup
		protected byte[] _pkcs12bytes;
		protected char[] _pkcs12password;
		protected PrivateKey _key;
		protected X509Certificate _certificate;
		protected String _sslContextAlgorithm="TLSv1.1";
		protected KeyStore _trustKeyStore;
		
		//header
		protected Date _dat_odesl=new Date();
		protected PrvniZaslani _prvni_zaslani;
		protected UUID _uuid_zpravy;
		protected Overeni _overeni;		
		
		//data
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
			if (val==null) return this;
			_certificate = val;
			return this;
		}

		
		public Builder key(PrivateKey val) {
			if (val==null) return this;
			_key = val;
			return this;
		}
		
		
		/**
		 * Sets SSLContext.getInstance() algorithm. See JSSE reference for available algorithms.
		 * @param for which algorithm the context will be instantiated. When not set, defaults to "TLSv1.1"; when explicitly 
		 * set to null, default platform settings are used
		 * @return
		 */
		public Builder sslContextAlgorithm(String sslContextAlgorithm){
			if (sslContextAlgorithm==null) return this;
			_sslContextAlgorithm=sslContextAlgorithm;
			return this;
		}
		
		/**
		 * Sets keystore for TrustManager. The keystore must be loaded. When not set defaults to keystore loaded 
		 * from resources containing the right certificates for public EET service.
		 * @param keyStore the key store containing certificates needed to trrust the web service endpoint. The key store usually 
		 * contains root CA certificate and intermediate CA certificates.  
		 * @return
		 */
		public Builder trustKeyStore(KeyStore keyStore){
			if (keyStore==null) return this;
			_trustKeyStore=keyStore;
			return this;
		}

		/**
		 * When not set defaults to time of soap requeste generation 
		 * @param val
		 * @return
		 */
		public Builder dat_odesl(Date val) {
			if (val==null) return this;
			_dat_odesl = val;
			return this;
		}
		
		public Builder dat_odesl(String val) {
			if (val==null) return this;
			_dat_odesl = parseDate(val);
			return this;
		}
		

		/** 
		 * 
		 * @param val
		 * @return
		 */
		public Builder prvni_zaslani(PrvniZaslani val) {
			if (val==null) return this;
			_prvni_zaslani = val;
			return this;
		}

		public Builder prvni_zaslani(boolean val) {
			_prvni_zaslani = PrvniZaslani.valueOf(val);
			return this;
		}

		public Builder prvni_zaslani(String val) {
			if (val==null) return this;
			_prvni_zaslani = PrvniZaslani.valueOf(val);
			return this;
		}

		/**
		 * when not set defaults to random UUID when soap request is generated
		 * @param val
		 * @return
		 */
		public Builder uuid_zpravy(UUID val) {
			if (val==null) return this;
			_uuid_zpravy = val;
			return this;
		}

		public Builder uuid_zpravy(String val) {
			if (val==null) return this;
			_uuid_zpravy = UUID.fromString(val);
			return this;
		}

		/**
		 * when not set, defaults to PRODUKCNI during soap request generation
		 * @param val
		 * @return
		 */
		public Builder overeni(Overeni val) {
			if (val==null) return this;
			_overeni = val;
			return this;
		}

		public Builder overeni(boolean val) {
			_overeni = Overeni.valueOf(val);
			return this;
		}

		public Builder overeni(String val) {
			if (val==null) return this;
			_overeni = Overeni.valueOf(val);
			return this;
		}
		
		
		public Builder dic_popl(String val) {
			if (val==null) return this;
			_dic_popl = val;
			return this;
		}

		public Builder dic_poverujiciho(String val) {
			if (val==null) return this;
			_dic_poverujiciho = val;
			return this;
		}

		public Builder id_provoz(String val) {
			if (val==null) return this;
			_id_provoz = val;
			return this;
		}

		public Builder id_pokl(String val) {
			if (val==null) return this;
			_id_pokl = val;
			return this;
		}

		public Builder porad_cis(String val) {
			if (val==null) return this;
			_porad_cis = val;
			return this;
		}

		/**
		 * When not set defaults to builder creation time
		 * @param val
		 * @return
		 */
		public Builder dat_trzby(Date val) {
			if (val==null) return this;
			_dat_trzby = val;
			return this;
		}

		public Builder dat_trzby(String val) {
			if (val==null) return this;
			_dat_trzby = EetRegisterRequest.parseDate(val);
			return this;
		}

		public Builder celk_trzba(Double val) {
			if (val==null) return this;
			_celk_trzba = val;
			return this;
		}

		public Builder celk_trzba(String val) {
			if (val==null) return this;
			_celk_trzba = Double.valueOf(val);
			return this;
		}

		public Builder zakl_nepodl_dph(Double val) {
			if (val==null) return this;
			_zakl_nepodl_dph = val;
			return this;
		}

		public Builder zakl_nepodl_dph(String val) {
			if (val==null) return this;
			_zakl_nepodl_dph = Double.valueOf(val);
			return this;
		}

		public Builder zakl_dan1(Double val) {
			if (val==null) return this;
			_zakl_dan1 = val;
			return this;
		}

		public Builder zakl_dan1(String val) {
			if (val==null) return this;
			_zakl_dan1 = Double.valueOf(val);
			return this;
		}

		public Builder dan1(Double val) {
			if (val==null) return this;
			_dan1 = val;
			return this;
		}

		public Builder dan1(String val) {
			if (val==null) return this;
			_dan1 = Double.valueOf(val);
			return this;
		}

		public Builder zakl_dan2(Double val) {
			if (val==null) return this;
			_zakl_dan2 = val;
			return this;
		}

		public Builder zakl_dan2(String val) {
			if (val==null) return this;
			_zakl_dan2 = Double.valueOf(val);
			return this;
		}

		public Builder dan2(Double val) {
			if (val==null) return this;
			_dan2 = val;
			return this;
		}

		public Builder dan2(String val) {
			if (val==null) return this;
			_dan2 = Double.valueOf(val);
			return this;
		}

		public Builder zakl_dan3(Double val) {
			if (val==null) return this;
			_zakl_dan3 = val;
			return this;
		}

		public Builder zakl_dan3(String val) {
			if (val==null) return this;
			_zakl_dan3 = Double.valueOf(val);
			return this;
		}

		public Builder dan3(Double val) {
			if (val==null) return this;
			_dan3 = val;
			return this;
		}

		public Builder dan3(String val) {
			if (val==null) return this;
			_dan3 = Double.valueOf(val);
			return this;
		}

		public Builder cest_sluz(Double val) {
			if (val==null) return this;
			_cest_sluz = val;
			return this;
		}

		public Builder cest_sluz(String val) {
			if (val==null) return this;
			_cest_sluz = Double.valueOf(val);
			return this;
		}

		public Builder pouzit_zboz1(Double val) {
			if (val==null) return this;
			_pouzit_zboz1 = val;
			return this;
		}

		public Builder pouzit_zboz1(String val) {
			if (val==null) return this;
			_pouzit_zboz1 = Double.valueOf(val);
			return this;
		}

		public Builder pouzit_zboz2(Double val) {
			if (val==null) return this;
			_pouzit_zboz2 = val;
			return this;
		}

		public Builder pouzit_zboz2(String val) {
			if (val==null) return this;
			_pouzit_zboz2 = Double.valueOf(val);
			return this;
		}

		public Builder pouzit_zboz3(Double val) {
			if (val==null) return this;
			_pouzit_zboz3 = val;
			return this;
		}

		public Builder pouzit_zboz3(String val) {
			if (val==null) return this;
			_pouzit_zboz3 = Double.valueOf(val);
			return this;
		}

		public Builder urceno_cerp_zuct(Double val) {
			if (val==null) return this;
			_urceno_cerp_zuct = val;
			return this;
		}

		public Builder urceno_cerp_zuct(String val) {
			if (val==null) return this;
			_urceno_cerp_zuct = Double.valueOf(val);
			return this;
		}

		public Builder cerp_zuct(Double val) {
			if (val==null) return this;
			_cerp_zuct = val;
			return this;
		}

		public Builder cerp_zuct(String val) {
			if (val==null) return this;
			_cerp_zuct = Double.valueOf(val);
			return this;
		}

		/**
		 * Defualts to Rezim.STANDARDNI
		 * @param val
		 * @return
		 */
		public Builder rezim(Rezim val) {
			if (val==null) return this;
			_rezim = val;
			return this;
		}

		public Builder rezim(int val) {
			_rezim = Rezim.valueOf(val);
			return this;
		}

		public Builder rezim(String val) {
			if (val==null) return this;
			_rezim = Rezim.fromString(val);
			return this;
		}

		/**
		 * Computed when pkp available during build() call
		 * @param val
		 * @return
		 */
		public Builder bkp(byte[] val) {
			if (val==null) return this;
			_bkp = val;
			return this;
		}

		/**
		 * Parses string according to EET spec e.g 17796128-AED2BB9E-2301FF97-0A75656A-DF2B011D
		 * @param val hex splitted into 5 groups containing 8 digits
		 * @return
		 */
		public Builder bkp(String val) {
			if (val==null) return this;
			_bkp=EetRegisterRequest.parseBkp(val);
			return this;
		}
		
		public Builder fromDTO(EetSaleDTO dto){
			if (dto==null) return this;
			this
		    .dic_popl(dto.dic_popl)
		    .dic_poverujiciho(dto.dic_poverujiciho)
		    .id_provoz(dto.id_provoz)
		    .id_pokl(dto.id_pokl)
		    .porad_cis(dto.porad_cis)
		    .dat_trzby(dto.dat_trzby)
		    .celk_trzba(dto.celk_trzba)
		    .zakl_nepodl_dph(dto.zakl_nepodl_dph)
		    .zakl_dan1(dto.zakl_dan1)
		    .dan1(dto.dan1)
		    .zakl_dan2(dto.zakl_dan2)
		    .dan2(dto.dan2)
		    .zakl_dan3(dto.zakl_dan3)
		    .dan3(dto.dan3)
		    .cest_sluz(dto.cest_sluz)
		    .pouzit_zboz1(dto.pouzit_zboz1)
		    .pouzit_zboz2(dto.pouzit_zboz2)
		    .pouzit_zboz3(dto.pouzit_zboz3)
		    .urceno_cerp_zuct(dto.urceno_cerp_zuct)
		    .cerp_zuct(dto.cerp_zuct)
		    .rezim(dto.rezim);			
			return this;
		}
		
		public Builder fromDTO(EetHeaderDTO dto){
			if (dto==null) return this;
			this
		    .dat_odesl(dto.dat_odesl)
			.prvni_zaslani(dto.prvni_zaslani)
			.uuid_zpravy(dto.uuid_zpravy)
			.overeni(dto.overeni);
			return this;
		}


		/**
		 * Computed when private key available during nuild() call
		 * @param val
		 * @return
		 */
		public Builder pkp(byte[] val) {
			if (val==null) return this;
			_pkp = val;
			return this;
		}

		/** 
		 * file is loaded immediately
		 * @param p12Filename
		 * @return
		 */
		public Builder pkcs12(String p12Filename) throws IOException{
			if (p12Filename==null) return this;
			return pkcs12(loadStream(new FileInputStream(p12Filename),null));
		}
		
		public Builder pkcs12(byte[] p12bytes){
			if (p12bytes==null) return this;
			_pkcs12bytes=p12bytes;
			return this;
		}

		public Builder pkcs12password(String password){
			if (password==null) return this;
			return pkcs12password(password.toCharArray());
		}
		
		public Builder pkcs12password(char[] password){
			if (password==null) return this;
			_pkcs12password=password;
			return this;
		}
		
		public EetRegisterRequest build() {
			return new EetRegisterRequest(this);
		}		
	}

	protected X509Certificate certificate;
	protected Date dat_odesl;
	protected PrvniZaslani prvni_zaslani;
	protected UUID uuid_zpravy;
	protected Overeni overeni;
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
	
	protected PrivateKey key;
	protected String sslContextAlgorithm;
	protected KeyStore trustKeyStore;

	protected EetRegisterRequest(Builder builder) {

		//header
		dat_odesl = builder._dat_odesl;
		prvni_zaslani = builder._prvni_zaslani;
		uuid_zpravy = builder._uuid_zpravy;
		overeni=builder._overeni;
		
		//data
		certificate = builder._certificate;
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
		key= builder._key;
		certificate = builder._certificate;
		sslContextAlgorithm=builder._sslContextAlgorithm;
		trustKeyStore=builder._trustKeyStore;
		
		if ( builder._pkcs12bytes!=null ){
			if (builder._pkcs12password==null){
				throw new IllegalArgumentException("found pkcs12 data and missing pkcs12 password. use pkcs12password(\"pwd\") during the builder setup.") ;
			}
			loadP12(builder._pkcs12bytes, builder._pkcs12password);
		}
		
		if (key!=null)
			computeCodes(key);
	}

	private void computeCodes(PrivateKey key){
		try {
			if (pkp==null && key!=null){
				String toBeSigned=formatToBeSignedData();
				if (toBeSigned!=null){
					Signature signature = Signature.getInstance("SHA256withRSA");
			        signature.initSign(key);
			        signature.update(toBeSigned.getBytes("UTF-8"));
			        pkp=signature.sign();					
			    }
			}
	
			if ( bkp==null && pkp !=null){
				MessageDigest md=MessageDigest.getInstance("SHA-1");
				bkp=md.digest(pkp);
			}
		}
		catch (Exception e){
			throw new IllegalArgumentException("error while computing codes",e);
		}
	}
	
	public static Builder builder(){
		return new Builder();
	}
	
	public X509Certificate getCertificate() {
		return certificate;
	}

	public Date getDat_odesl() {
		return dat_odesl;
	}

	public PrvniZaslani getPrvni_zaslani() {
		return prvni_zaslani;
	}

	public UUID getUuid_zpravy() {
		return uuid_zpravy;
	}

	public Overeni getOvereni(){
		return overeni;
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

	/**
	 * Formats data to form ready to be signed for PKP computation based on data in this object 
	 * @return 
	 */
	public String formatToBeSignedData() {
		if (dic_popl == null || id_provoz==null || id_pokl==null || porad_cis==null || dat_trzby==null || celk_trzba==null ) 
			throw new NullPointerException(
					String.format("missing some of _dic_popl(%s), _id_provoz(%s), _id_pokl(%s), _porad_cis(%s), _celk_trzba(%s)",
							dic_popl, id_provoz, id_pokl, porad_cis, dat_trzby,celk_trzba));
		return String.format("%s|%s|%s|%s|%s|%s",dic_popl, id_provoz, id_pokl, porad_cis, formatDate(dat_trzby),formatAmount(celk_trzba));
	}
	
	public static String formatDate(Date date){
		String ret= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date);
		ret=ret.replaceFirst("\\+([0-9][0-9])([0-9][0-9])$","+$1:$2");
		return ret;
	}
	
	public static Date parseDate(String date){
		try {
			//replace 02:00 with 0200
			date=date.replaceFirst("\\+([0-9][0-9]):([0-9][0-9])$","+$1$2");
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(date);
		} catch (ParseException e) {
			throw new IllegalArgumentException("bad date",e);
		}
	}

	public String formatBkp(){
		return formatBkp(bkp);
	}
	
	public static String formatBkp(byte[] _bkp){
		String sb = byte2hex(_bkp);
		return sb.toString().toUpperCase().replaceFirst("^([0-9A-F]{8})([0-9A-F]{8})([0-9A-F]{8})([0-9A-F]{8})([0-9A-F]{8})$","$1-$2-$3-$4-$5");
	}

	private static String byte2hex(byte[] data) {
		StringBuilder sb=new StringBuilder();
		for (int i=0; i<data.length; i++){
			sb.append(String.format("%02x",data[i]));
		}
		return sb.toString();
	}
	
	public static byte[] parseBkp(String val){
		byte[] _bkp=new byte[20];
		val=val.replace("-","");
		
		if (val.length()!=40) 
			throw new IllegalArgumentException("Wrong length (~=!=40) of bkp string after dash removal:"+val);
		if (!val.toUpperCase().matches("^[A-F0-9]{40}$")) 
			throw new IllegalArgumentException("Wrong format, hexdump expected:"+val);
		
		for (int i=0; i<20; i++){
			_bkp[i]=(byte) Integer.parseInt(val.substring(i*2,i*2+1), 16);
		}
		return _bkp;  
	}

	private static String formatAmount(double amount){
		return String.format(Locale.US, "%.2f", amount);
	}

	public String formatPkp() {
		return formatPkp(pkp);
	}

	public static String formatPkp(byte[] _pkp) {
		return Base64.encodeToString(_pkp, Base64.NO_WRAP);
	}

	public static byte[] parsePkp(String _pkp) {
		return Base64.decode(_pkp, Base64.NO_WRAP);
	}


	/**
	 * Uses header data from builder, when not set, generate defaults as documented in builder
	 *  Sets lastHeader accordingly. 
	 * @return generated SOAP requesas stringt
	 */
	public String generateSoapRequest() {
		return generateSoapRequest(dat_odesl!=null?dat_odesl:new Date(),
								   prvni_zaslani!=null?prvni_zaslani:PrvniZaslani.PRVNI,
								   uuid_zpravy!=null?uuid_zpravy.toString():UUID.randomUUID().toString(),
								   overeni!=null?overeni:Overeni.PRODUKCNI);
	}
	
	
	/**
	 * Same as <pre>generateSoapRequest(Date dat_odesl_force, PrvniZaslani prvni_zaslani_force, String uuid_zpravy_force, Overeni overeni_force).</pre>
	 *  Sets lastHeader accordingly.
	 * @param header
	 * @return
	 */
	public String generateSoapRequest(EetHeaderDTO header){
		return generateSoapRequest(parseDate(header.dat_odesl), 
				                   PrvniZaslani.valueOf(header.prvni_zaslani),
				                   header.uuid_zpravy, 
				                   Overeni.valueOf(header.overeni));
	}
	
	
	/**
	 * Ignores header data from builder and sets according to params or default (if param is null). Sets lastHeader accordingly.
	 * @param dat_odesl_force if null, new Date is used
	 * @param prvni_zaslani_force if null, PRVNI is used
	 * @param uuid_zpravy_force if null random UUID is generated
	 * @param overeni_force if null, PRODUKCNI is used
	 * @return
	 */
	public String generateSoapRequest(Date dat_odesl_force, PrvniZaslani prvni_zaslani_force, String uuid_zpravy_force, Overeni overeni_force){
		return generateSoapRequestInternal(dat_odesl_force!=null?dat_odesl_force:new Date(),
				   prvni_zaslani_force!=null?prvni_zaslani_force:PrvniZaslani.PRVNI,
				   uuid_zpravy_force!=null?uuid_zpravy_force.toString():UUID.randomUUID().toString(),
				   overeni_force!=null?overeni_force:Overeni.PRODUKCNI);		
	}
	
	
	/**
	 * Returns last header data used to generate soap request. The value is rewiriten each time "generateSoapRequest" is called.
	 * Be careful when multithreading. The value is not thread safe - it is per EetRequestInstance and can be rewritten by another therad.
	 * @return
	 */
	public EetHeaderDTO getLastHeader(){
		return lastHeader;
	}
	
	protected EetHeaderDTO lastHeader;
	
	protected String generateSoapRequestInternal(Date dat_odesl_force, PrvniZaslani prvni_zaslani_force, String uuid_zpravy_force, Overeni overeni_force){
		try {
			lastHeader=new EetHeaderDTO();
			lastHeader.dat_odesl=formatDate(dat_odesl_force);
			lastHeader.prvni_zaslani=prvni_zaslani_force.toString();
			lastHeader.uuid_zpravy=uuid_zpravy_force;
			lastHeader.overeni=overeni_force.toString();
			
			String sha1sum=loadTemplateFromResource("/openeet/lite/templates/sha1sum.txt");
			BufferedReader  br=new BufferedReader(new StringReader(sha1sum));
			String ln;
			Map<String,String> hashes=new HashMap<String,String>();
			while ((ln=br.readLine())!=null){
				String[] items=ln.split("  ");
				hashes.put(items[1],items[0]);
			}
			br.close();
			
			String xmlTemplate=loadTemplateFromResource("/openeet/lite/templates/template.xml",hashes.get("template.xml"));
			String digestTemplate=loadTemplateFromResource("/openeet/lite/templates/digest-template",hashes.get("digest-template"));
			String signatureTemplate=loadTemplateFromResource("/openeet/lite/templates/signature-template",hashes.get("signature-template"));
					
			digestTemplate=replacePlaceholders(digestTemplate, null, null, dat_odesl_force, prvni_zaslani_force, uuid_zpravy_force, overeni_force);
			digestTemplate=removeUnusedPlaceholders(digestTemplate);
			MessageDigest md=MessageDigest.getInstance("SHA-256");
			byte[] digestRaw=md.digest(digestTemplate.getBytes("utf-8"));
			String digest=Base64.encodeToString(digestRaw, Base64.NO_WRAP);
			
			signatureTemplate=replacePlaceholders(signatureTemplate, digest, null, dat_odesl_force, prvni_zaslani_force, uuid_zpravy_force, overeni_force);
			signatureTemplate=removeUnusedPlaceholders(signatureTemplate);
			Signature signatureEngine = Signature.getInstance("SHA256withRSA");
	        signatureEngine.initSign(key);
	        signatureEngine.update(signatureTemplate.getBytes("UTF-8"));
	        byte[] signatureRaw=signatureEngine.sign();
	        String signature=Base64.encodeToString(signatureRaw, Base64.NO_WRAP);
	        
			xmlTemplate=replacePlaceholders(xmlTemplate, digest, signature, dat_odesl_force, prvni_zaslani_force, uuid_zpravy_force, overeni_force);
			xmlTemplate=removeUnusedPlaceholders(xmlTemplate);

			return xmlTemplate;			
		}
		catch (Exception e){
			throw new RuntimeException("Error while generating soap request",e);
		}
	}
	
	private String replacePlaceholders(String src, String digest, String signature, Date dat_odesl_force, PrvniZaslani prvni_zaslani_force, String uuid_zpravy_force, Overeni overeni_force){
		try {
			
			if (prvni_zaslani_force!=null) src=src.replace("${prvni_zaslani}",prvni_zaslani_force.toString());
			else if (prvni_zaslani!=null) src=src.replace("${prvni_zaslani}",prvni_zaslani.toString());
			
			if (dat_odesl_force!=null) src=src.replace("${dat_odesl}",formatDate(dat_odesl_force));
			else if (dat_odesl!=null) src=src.replace("${dat_odesl}",formatDate(dat_odesl));
			
			if (uuid_zpravy_force!=null) src=src.replace("${uuid_zpravy}",uuid_zpravy_force.toString());
			else if (uuid_zpravy!=null) src=src.replace("${uuid_zpravy}",uuid_zpravy.toString());
			
			if (overeni_force!=null) src=src.replace("${overeni}",overeni_force.toString());
			else if (overeni!=null) src=src.replace("${overeni}",overeni.toString());

			if (certificate!=null) src=src.replace("${certb64}",Base64.encodeToString(certificate.getEncoded(), Base64.NO_WRAP));
			if (dic_popl!=null) src=src.replace("${dic_popl}",dic_popl);
			if (dic_poverujiciho!=null) src=src.replace("${dic_poverujiciho}",dic_poverujiciho);
			if (id_provoz!=null) src=src.replace("${id_provoz}",id_provoz);
			if (id_pokl!=null) src=src.replace("${id_pokl}",id_pokl);
			if (porad_cis!=null) src=src.replace("${porad_cis}",porad_cis);
			if (dat_trzby!=null) src=src.replace("${dat_trzby}",formatDate(dat_trzby));
			if (celk_trzba!=null) src=src.replace("${celk_trzba}",formatAmount(celk_trzba));
			if (zakl_nepodl_dph!=null) src=src.replace("${zakl_nepodl_dph}",formatAmount(zakl_nepodl_dph));
			if (zakl_dan1!=null) src=src.replace("${zakl_dan1}",formatAmount(zakl_dan1));
			if (dan1!=null) src=src.replace("${dan1}",formatAmount(dan1));
			if (zakl_dan2!=null) src=src.replace("${zakl_dan2}",formatAmount(zakl_dan2));
			if (dan2!=null) src=src.replace("${dan2}",formatAmount(dan2));
			if (zakl_dan3!=null) src=src.replace("${zakl_dan3}",formatAmount(zakl_dan3));
			if (dan3!=null) src=src.replace("${dan3}",formatAmount(dan3));
			if (cest_sluz!=null) src=src.replace("${cest_sluz}",formatAmount(cest_sluz));
			if (pouzit_zboz1!=null) src=src.replace("${pouzit_zboz1}",formatAmount(pouzit_zboz1));
			if (pouzit_zboz2!=null) src=src.replace("${pouzit_zboz2}",formatAmount(pouzit_zboz2));
			if (pouzit_zboz3!=null) src=src.replace("${pouzit_zboz3}",formatAmount(pouzit_zboz3));
			if (urceno_cerp_zuct!=null) src=src.replace("${urceno_cerp_zuct}",formatAmount(urceno_cerp_zuct));
			if (cerp_zuct!=null) src=src.replace("${cerp_zuct}",formatAmount(cerp_zuct));
			if (rezim!=null) src=src.replace("${rezim}",rezim.toString());
			if (bkp!=null) src=src.replace("${bkp}",formatBkp(bkp));
			if (pkp!=null) src=src.replace("${pkp}",formatPkp(pkp));
			if (digest!=null) src=src.replace("${digest}",digest);
			if (signature!=null) src=src.replace("${signature}",signature);
			
			return src;
		}
		catch (Exception e){
			throw new IllegalArgumentException("replacement processing got wrong",e);
		}
	}
	
	private String removeUnusedPlaceholders(String src){
		src=src.replaceAll(" [a-z_0-9]+=\"\\$\\{[0-9_a-z]+\\}\"","");
		src=src.replaceAll("\\$\\{[a-b_0-9]+\\}","");
		return src;
	}
	
	
	public static byte[] loadStream(InputStream in) throws IOException {
		return loadStream(in,null);
	}

	public static byte[] loadStream(InputStream in, String sha1sum) throws IOException {
		byte[] buf=new byte[1024];
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		int n=0;
		while ((n=in.read(buf))>0) bos.write(buf,0,n);
		in.close();
		byte[] ret=bos.toByteArray();
		if ( sha1sum!=null ){
			try { 
				byte[] resourceSum=MessageDigest.getInstance("SHA1").digest(ret);
				String resourceSumStr=byte2hex(resourceSum);
				if (!resourceSumStr.toLowerCase().equals(sha1sum.toLowerCase())){
					throw new RuntimeException(String.format("Unexpected stream hash. Expected %s, computed %s",sha1sum, resourceSumStr));
				}
			} 
			catch (NoSuchAlgorithmException e ){
				throw new RuntimeException("fasiled to create message digest");
			}
		}
		return ret;
	}
	
	private String loadTemplateFromResource(String resource) throws IOException {
		return loadTemplateFromResource(resource,null);
	}
	
	private String loadTemplateFromResource(String resource, String sha1sum) throws IOException {
		byte[] streamData=loadStream(getClass().getResourceAsStream(resource), sha1sum);
		return new String(streamData,"UTF-8");
	}
	
	public String sendRequest(String requestBody, URL serviceUrl) throws Exception {
		byte[] content=requestBody.getBytes("utf-8");

		HttpURLConnection con=(HttpURLConnection)serviceUrl.openConnection();
		if (con instanceof HttpsURLConnection){
			HttpsURLConnection cons=(HttpsURLConnection)con;
			if (sslContextAlgorithm!=null){
				SSLContext sslCtx=SSLContext.getInstance(sslContextAlgorithm);
				if (trustKeyStore!=null) 
					sslCtx.init(null, new TrustManager[]{new EetTrustManager()}, null);
				else 
					sslCtx.init(null, new TrustManager[]{new EetTrustManager(trustKeyStore)}, null);
				cons.setSSLSocketFactory(sslCtx.getSocketFactory());
			}
		}
		
		con.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
		con.setRequestProperty("Content-Length",String.format("%d", content.length));
		con.setRequestProperty("SOAPAction", "http://fs.mfcr.cz/eet/OdeslaniTrzby");
		con.setUseCaches(false);
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestMethod("POST");
		
		OutputStream os=con.getOutputStream();
		os.write(content);
		os.flush();os.close();
		
		int responseCode=con.getResponseCode();
		InputStream is=con.getInputStream();
		byte[] response=loadStream(is);
		String responseString=new String(response,"utf-8");
		return responseString;
	}
	
	private void loadP12(byte[] p12data, char[] password)  {
		try {
			KeyStore ks=KeyStore.getInstance("PKCS12");
			ks.load(new ByteArrayInputStream(p12data), password);
			Enumeration<String> aliases= ks.aliases();
			while(aliases.hasMoreElements() ){
				String alias=aliases.nextElement();
				Key _key=ks.getKey(alias, password);
				if (_key!=null){
					Certificate _cert=ks.getCertificate(alias);
					if (_cert!=null){
						if (   _cert instanceof X509Certificate 
							&& _key instanceof RSAPrivateKey ){
							key=(PrivateKey) _key;
							certificate=(X509Certificate)_cert;
						}
					}
				}
			}
		}
		catch (Exception e ){
			throw new IllegalArgumentException("Exception while loading p12 data",e);
		}
		
		if (key==null || certificate==null) throw new IllegalArgumentException("key and/or certificate still missing after p12 processing");
	}
	
	public EetSaleDTO getSaleDTO(){
		EetSaleDTO dto=new EetSaleDTO();
		dto.dic_popl=dic_popl;
		dto.dic_poverujiciho=dic_poverujiciho;
		dto.id_provoz=id_provoz;
		dto.id_pokl=id_pokl;
		dto.porad_cis=porad_cis;
		dto.dat_trzby=dat_trzby!=null?formatDate(dat_trzby):null;
		dto.celk_trzba=celk_trzba!=null?formatAmount(celk_trzba):null;
		dto.zakl_nepodl_dph=zakl_nepodl_dph!=null?formatAmount(zakl_nepodl_dph):null;
		dto.zakl_dan1=zakl_dan1!=null?formatAmount(zakl_dan1):null;
		dto.dan1=dan1!=null?formatAmount(dan1):null;
		dto.zakl_dan2=zakl_dan2!=null?formatAmount(zakl_dan2):null;
		dto.dan2=dan2!=null?formatAmount(dan2):null;
		dto.zakl_dan3=zakl_dan3!=null?formatAmount(zakl_dan3):null;
		dto.dan3=dan3!=null?formatAmount(dan3):null;
		dto.cest_sluz=cest_sluz!=null?formatAmount(cest_sluz):null;
		dto.pouzit_zboz1=pouzit_zboz1!=null?formatAmount(pouzit_zboz1):null;
		dto.pouzit_zboz2=pouzit_zboz2!=null?formatAmount(pouzit_zboz2):null;
		dto.pouzit_zboz3=pouzit_zboz3!=null?formatAmount(pouzit_zboz3):null;
		dto.urceno_cerp_zuct=urceno_cerp_zuct!=null?formatAmount(urceno_cerp_zuct):null;
		dto.cerp_zuct=cerp_zuct!=null?formatAmount(cerp_zuct):null;
		dto.rezim=rezim.toString();		
		return dto;
	}
	
	public EetHeaderDTO getEetHeaderDTO(){
		EetHeaderDTO dto=new EetHeaderDTO();
	    dto.dat_odesl=formatDate(dat_odesl);
		dto.prvni_zaslani=prvni_zaslani.toString();
		dto.uuid_zpravy=uuid_zpravy.toString();
		dto.overeni=overeni.toString();
		return dto;
	}
}
