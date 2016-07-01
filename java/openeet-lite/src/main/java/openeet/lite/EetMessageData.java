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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import com.sun.corba.se.spi.orbutil.fsm.Input;

public class EetMessageData {
	
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

	public static class Builder {
		protected PrivateKey _key;
		protected X509Certificate _certificate;
		protected Date _dat_odesl=new Date();
		protected PrvniZaslani _prvni_zaslani=PrvniZaslani.PRVNI;
		protected UUID _uuid_zpravy=UUID.randomUUID();
		protected Overeni _overeni=Overeni.PRODUKCNI;
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
		public Builder prvni_zaslani(PrvniZaslani val) {
			_prvni_zaslani = val;
			return this;
		}

		public Builder prvni_zaslani(boolean val) {
			_prvni_zaslani = PrvniZaslani.valueOf(val);
			return this;
		}

		public Builder prvni_zaslani(String val) {
			_prvni_zaslani = PrvniZaslani.valueOf(val);
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

		public Builder uuid_zpravy(String val) {
			_uuid_zpravy = UUID.fromString(val);
			return this;
		}

		/**
		 * Defaults to PRODUKCNI
		 * @param val
		 * @return
		 */
		public Builder overeni(Overeni val) {
			_overeni = val;
			return this;
		}

		public Builder overeni(boolean val) {
			_overeni = Overeni.valueOf(val);
			return this;
		}

		public Builder overeni(String val) {
			_overeni = Overeni.valueOf(val);
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

		public Builder dat_trzby(String val) {
			_dat_trzby = EetMessageData.parseDate(val);
			return this;
		}

		public Builder celk_trzba(Double val) {
			_celk_trzba = val;
			return this;
		}

		public Builder celk_trzba(String val) {
			_celk_trzba = Double.valueOf(val);
			return this;
		}

		public Builder zakl_nepodl_dph(Double val) {
			_zakl_nepodl_dph = val;
			return this;
		}

		public Builder zakl_nepodl_dph(String val) {
			_zakl_nepodl_dph = Double.valueOf(val);
			return this;
		}

		public Builder zakl_dan1(Double val) {
			_zakl_dan1 = val;
			return this;
		}

		public Builder zakl_dan1(String val) {
			_zakl_dan1 = Double.valueOf(val);
			return this;
		}

		public Builder dan1(Double val) {
			_dan1 = val;
			return this;
		}

		public Builder dan1(String val) {
			_dan1 = Double.valueOf(val);
			return this;
		}

		public Builder zakl_dan2(Double val) {
			_zakl_dan2 = val;
			return this;
		}

		public Builder zakl_dan2(String val) {
			_zakl_dan2 = Double.valueOf(val);
			return this;
		}

		public Builder dan2(Double val) {
			_dan2 = val;
			return this;
		}

		public Builder dan2(String val) {
			_dan2 = Double.valueOf(val);
			return this;
		}

		public Builder zakl_dan3(Double val) {
			_zakl_dan3 = val;
			return this;
		}

		public Builder zakl_dan3(String val) {
			_zakl_dan3 = Double.valueOf(val);
			return this;
		}

		public Builder dan3(Double val) {
			_dan3 = val;
			return this;
		}

		public Builder dan3(String val) {
			_dan3 = Double.valueOf(val);
			return this;
		}

		public Builder cest_sluz(Double val) {
			_cest_sluz = val;
			return this;
		}

		public Builder cest_sluz(String val) {
			_cest_sluz = Double.valueOf(val);
			return this;
		}

		public Builder pouzit_zboz1(Double val) {
			_pouzit_zboz1 = val;
			return this;
		}

		public Builder pouzit_zboz1(String val) {
			_pouzit_zboz1 = Double.valueOf(val);
			return this;
		}

		public Builder pouzit_zboz2(Double val) {
			_pouzit_zboz2 = val;
			return this;
		}

		public Builder pouzit_zboz2(String val) {
			_pouzit_zboz2 = Double.valueOf(val);
			return this;
		}

		public Builder pouzit_zboz3(Double val) {
			_pouzit_zboz3 = val;
			return this;
		}

		public Builder pouzit_zboz3(String val) {
			_pouzit_zboz3 = Double.valueOf(val);
			return this;
		}

		public Builder urceno_cerp_zuct(Double val) {
			_urceno_cerp_zuct = val;
			return this;
		}

		public Builder urceno_cerp_zuct(String val) {
			_urceno_cerp_zuct = Double.valueOf(val);
			return this;
		}

		public Builder cerp_zuct(Double val) {
			_cerp_zuct = val;
			return this;
		}

		public Builder cerp_zuct(String val) {
			_cerp_zuct = Double.valueOf(val);
			return this;
		}

		
		
		/*
		 * Defualts to Rezim.STANDARDNI
		 */
		public Builder rezim(Rezim val) {
			_rezim = val;
			return this;
		}

		public Builder rezim(int val) {
			_rezim = Rezim.valueOf(val);
			return this;
		}

		public Builder rezim(String val) {
			_rezim = Rezim.fromString(val);
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
		 * Parses string according to EET spec e.g 17796128-AED2BB9E-2301FF97-0A75656A-DF2B011D
		 * @param val hex splitted into 5 groups containing 8 digits
		 * @return
		 */
		public Builder bkp(String val) {
			_bkp=EetMessageData.parseBkp(val);
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
			return new EetMessageData(this);
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

	protected EetMessageData(Builder builder) {
		certificate = builder._certificate;
		dat_odesl = builder._dat_odesl;
		prvni_zaslani = builder._prvni_zaslani;
		uuid_zpravy = builder._uuid_zpravy;
		overeni=builder._overeni;
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
	
		if (builder._key!=null)
			computeCodes(builder._key);
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
	
	public String formatDate(Date date){
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
		StringBuilder sb=new StringBuilder();
		for (int i=0; i<_bkp.length; i++){
			sb.append(String.format("%02x",_bkp[i]));
		}
		return sb.toString().toUpperCase().replaceFirst("^([0-9A-F]{8})([0-9A-F]{8})([0-9A-F]{8})([0-9A-F]{8})([0-9A-F]{8})$","$1-$2-$3-$4-$5");
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
		return String.format("%.2f", amount);
	}

	public static String formatPkp(byte[] _pkp) {
		return Base64.getEncoder().encodeToString(_pkp);
	}

	public static byte[] parsePkp(String _pkp) {
		return Base64.getDecoder().decode(_pkp);
	}


	public String generateSoapRequest(PrivateKey key){
		try {
			String xmlTemplate=loadTemplateFromResource("/openeet/lite/templates/template.xml");
			String digestTemplate=loadTemplateFromResource("/openeet/lite/templates/digest-template");
			String signatureTemplate=loadTemplateFromResource("/openeet/lite/templates/signature-template");
			
			digestTemplate=replacePlaceholders(digestTemplate, null, null);
			digestTemplate=removeUnusedPlaceholders(digestTemplate);
			MessageDigest md=MessageDigest.getInstance("SHA-256");
			byte[] digestRaw=md.digest(digestTemplate.getBytes("utf-8"));
			String digest=Base64.getEncoder().encodeToString(digestRaw);
			
			signatureTemplate=replacePlaceholders(signatureTemplate, digest, null);
			signatureTemplate=removeUnusedPlaceholders(signatureTemplate);
			Signature signatureEngine = Signature.getInstance("SHA256withRSA");
	        signatureEngine.initSign(key);
	        signatureEngine.update(signatureTemplate.getBytes("UTF-8"));
	        byte[] signatureRaw=signatureEngine.sign();
	        String signature=Base64.getEncoder().encodeToString(signatureRaw);
	        
			xmlTemplate=replacePlaceholders(xmlTemplate, digest, signature);
			xmlTemplate=removeUnusedPlaceholders(xmlTemplate);

			return xmlTemplate;			
		}
		catch (Exception e){
			throw new RuntimeException("Error while generating soap request");
		}
	}
	
	private String replacePlaceholders(String src, String digest, String signature){
		try {
			if (certificate!=null) src=src.replace("${certb64}",Base64.getEncoder().encodeToString(certificate.getEncoded()));
			if (prvni_zaslani!=null) src=src.replace("${prvni_zaslani}",prvni_zaslani.toString());
			if (dat_odesl!=null) src=src.replace("${dat_odesl}",formatDate(dat_odesl));
			if (uuid_zpravy!=null) src=src.replace("${uuid_zpravy}",uuid_zpravy.toString());
			if (overeni!=null) src=src.replace("${overeni}",overeni.toString());
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

	private static byte[] loadStream(InputStream in) throws IOException{
		byte[] buf=new byte[1024];
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		int n=0;
		while ((n=in.read(buf))>0) bos.write(buf,0,n);
		in.close();
		return bos.toByteArray();
	}
	
	private String loadTemplateFromResource(String resource) throws IOException {
		byte[] streamData=loadStream(getClass().getResourceAsStream(resource));
		return new String(streamData,"UTF-8");
	}
	
	public void sendRequest(String requestBody, URL serviceUrl) throws IOException{
		byte[] content=requestBody.getBytes("utf-8");
		//FIXME: remove 
		Files.write(Paths.get("/tmp/openeet/eet-requuest.dump"), content);
		HttpURLConnection con=(HttpURLConnection)serviceUrl.openConnection();
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
		//FIXME: remove
		Files.write(Paths.get("/tmp/openeet/eet-response.dump"), response);
		String responseString=new String(response,"utf-8");
		String a=responseString;		
	}
	
	

}
