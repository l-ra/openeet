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

using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;
using System.Text.RegularExpressions;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.IO;
using System.Net;

namespace openeet_lite
{
    public enum PrvniZaslani {	PRVNI=1, OPAKOVANE=0 }
    public enum Overeni { OVEROVACI=1, PRODUKCNI=0 }
  	public enum Rezim {STANDARDNI=0, ZJEDNODUSENY=1 }

    public class Builder {
		internal byte[] _pkcs12bytes;
		internal String _pkcs12password;
		internal RSACryptoServiceProvider _key;
		internal X509Certificate2 _certificate;
		internal DateTime _dat_odesl=DateTime.Now;
		internal PrvniZaslani? _prvni_zaslani=PrvniZaslani.PRVNI;
		internal Guid _uuid_zpravy=Guid.NewGuid();
		internal Overeni? _overeni=Overeni.PRODUKCNI;
		internal String _dic_popl;
		internal String _dic_poverujiciho;
		internal String _id_provoz;
		internal String _id_pokl;
		internal String _porad_cis;
		internal DateTime _dat_trzby=DateTime.Now;
		internal Double? _celk_trzba;
		internal Double? _zakl_nepodl_dph;
		internal Double? _zakl_dan1;
		internal Double? _dan1;
		internal Double? _zakl_dan2;
		internal Double? _dan2;
		internal Double? _zakl_dan3;
		internal Double? _dan3;
		internal Double? _cest_sluz;
		internal Double? _pouzit_zboz1;
		internal Double? _pouzit_zboz2;
		internal Double? _pouzit_zboz3;
		internal Double? _urceno_cerp_zuct;
		internal Double? _cerp_zuct;
		internal Rezim? _rezim=Rezim.STANDARDNI;
        internal byte[] _bkp;
		internal byte[] _pkp;

		public Builder certificate(X509Certificate2 val) {
			_certificate = val;
			return this;
		}

		
		public Builder key(RSACryptoServiceProvider val) {
			_key = val;
			return this;
		}

		
		/**
		 * Defaults to time of builder creation 
		 * @param val
		 * @return
		 */
		public Builder dat_odesl(DateTime val) {
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

		public Builder prvni_zaslani(bool val) {
            if (val) _prvni_zaslani=PrvniZaslani.PRVNI;
            else _prvni_zaslani=PrvniZaslani.OPAKOVANE;
            return this;
		}

        /*
		public Builder prvni_zaslani(String val) {
			_prvni_zaslani = PrvniZaslani.valueOf(val);
			return this;
		}
        */

		/**
		 * Defaults to random UUID
		 * @param val
		 * @return
		 */
		public Builder uuid_zpravy(Guid val) {
			_uuid_zpravy = val;
			return this;
		}

		public Builder uuid_zpravy(String val) {
			_uuid_zpravy = new Guid(val);
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
		
		public Builder overeni(bool val) {
		   if (val) _overeni=Overeni.OVEROVACI;
		   else _overeni = Overeni.PRODUKCNI;
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
		public Builder dat_trzby(DateTime val) {
			_dat_trzby = val;
			return this;
		}

		public Builder dat_trzby(String val) {
			_dat_trzby = EetRegisterRequest.parseDate(val);
			return this;
		}

		public Builder celk_trzba(Double val) {
			_celk_trzba = val;
			return this;
		}

		public Builder celk_trzba(String val) {
			_celk_trzba = Double.Parse(val);
			return this;
		}

		public Builder zakl_nepodl_dph(Double val) {
			_zakl_nepodl_dph = val;
			return this;
		}

		public Builder zakl_nepodl_dph(String val) {
			_zakl_nepodl_dph =  Double.Parse(val);
			return this;
		}

		public Builder zakl_dan1(Double val) {
			_zakl_dan1 = val;
			return this;
		}

		public Builder zakl_dan1(String val) {
			_zakl_dan1 =  Double.Parse(val);
			return this;
		}

		public Builder dan1(Double val) {
			_dan1 = val;
			return this;
		}

		public Builder dan1(String val) {
			_dan1 =  Double.Parse(val);
			return this;
		}

		public Builder zakl_dan2(Double val) {
			_zakl_dan2 = val;
			return this;
		}

		public Builder zakl_dan2(String val) {
			_zakl_dan2 =  Double.Parse(val);
			return this;
		}

		public Builder dan2(Double val) {
			_dan2 = val;
			return this;
		}

		public Builder dan2(String val) {
			_dan2 =  Double.Parse(val);
			return this;
		}

		public Builder zakl_dan3(Double val) {
			_zakl_dan3 = val;
			return this;
		}

		public Builder zakl_dan3(String val) {
			_zakl_dan3 =  Double.Parse(val);
			return this;
		}

		public Builder dan3(Double val) {
			_dan3 = val;
			return this;
		}

		public Builder dan3(String val) {
			_dan3 =  Double.Parse(val);
			return this;
		}

		public Builder cest_sluz(Double val) {
			_cest_sluz = val;
			return this;
		}

		public Builder cest_sluz(String val) {
			_cest_sluz =  Double.Parse(val);
			return this;
		}

		public Builder pouzit_zboz1(Double val) {
			_pouzit_zboz1 = val;
			return this;
		}

		public Builder pouzit_zboz1(String val) {
			_pouzit_zboz1 =  Double.Parse(val);
			return this;
		}

		public Builder pouzit_zboz2(Double val) {
			_pouzit_zboz2 = val;
			return this;
		}

		public Builder pouzit_zboz2(String val) {
			_pouzit_zboz2 =  Double.Parse(val);
			return this;
		}

		public Builder pouzit_zboz3(Double val) {
			_pouzit_zboz3 = val;
			return this;
		}

		public Builder pouzit_zboz3(String val) {
			_pouzit_zboz3 =  Double.Parse(val);
			return this;
		}

		public Builder urceno_cerp_zuct(Double val) {
			_urceno_cerp_zuct = val;
			return this;
		}

		public Builder urceno_cerp_zuct(String val) {
			_urceno_cerp_zuct =  Double.Parse(val);
			return this;
		}

		public Builder cerp_zuct(Double val) {
			_cerp_zuct = val;
			return this;
		}

		public Builder cerp_zuct(String val) {
			_cerp_zuct =  Double.Parse(val);
			return this;
		}

		/**
		 * Defualts to Rezim.STANDARDNI
		 * @param val
		 * @return
		 */
		public Builder rezim(Rezim val) {
			_rezim = val;
			return this;
		}

        
		public Builder rezim(int val) {
            if (val==0) return rezim(Rezim.STANDARDNI);
            else if (val==1) return rezim(Rezim.ZJEDNODUSENY);
            else throw new ArgumentException("only 0 and 1 is allowed as int value");
		}

		public Builder rezim(String val) {
			return rezim(int.Parse(val));
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
			_bkp=EetRegisterRequest.parseBkp(val);
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

		/** 
		 * file is loaded immediately
		 * @param p12Filename
		 * @return
		 */
		public Builder pkcs12(String p12Filename) {
			return pkcs12(File.ReadAllBytes(p12Filename));
		}
		
		public Builder pkcs12(byte[] p12bytes){
			_pkcs12bytes=p12bytes;
			return this;
		}

		public Builder pkcs12password(String password){
			_pkcs12password=password;
            return this;
		}
		
		public EetRegisterRequest build() {
			return new EetRegisterRequest(this);
		}		

    }

    public class EetRegisterRequest
    {
        X509Certificate2 _certificate; public X509Certificate2 certificate { get { return _certificate; } }
        DateTime _dat_odesl; public DateTime dat_odesl { get { return _dat_odesl; } }
        PrvniZaslani? _prvni_zaslani; public PrvniZaslani? prvni_zaslani { get { return _prvni_zaslani; } }
        Guid _uuid_zpravy; public Guid uuid_zpravy { get { return _uuid_zpravy; } }
        Overeni? _overeni; public Overeni? overeni { get { return _overeni; } }
        String _dic_popl; public String dic_popl { get { return _dic_popl; } }
        String _dic_poverujiciho; public String dic_poverujiciho { get { return _dic_poverujiciho; } }
        String _id_provoz; public String id_provoz { get { return _id_provoz; } }
        String _id_pokl; public String id_pokl { get { return _id_pokl; } }
        String _porad_cis; public String porad_cis { get { return _porad_cis; } }
        DateTime _dat_trzby; public DateTime dat_trzby { get { return _dat_trzby; } }
        Double? _celk_trzba; public Double? celk_trzba { get { return _celk_trzba; } }
        Double? _zakl_nepodl_dph; public Double? zakl_nepodl_dph { get { return _zakl_nepodl_dph; } }
        Double? _zakl_dan1; public Double? zakl_dan1 { get { return _zakl_dan1; } }
        Double? _dan1; public Double? dan1 { get { return _dan1; } }
        Double? _zakl_dan2; public Double? zakl_dan2 { get { return _zakl_dan2; } }
        Double? _dan2; public Double? dan2 { get { return _dan2; } }
        Double? _zakl_dan3; public Double? zakl_dan3 { get { return _zakl_dan3; } }
        Double? _dan3; public Double? dan3 { get { return _dan3; } }
        Double? _cest_sluz; public Double? cest_sluz { get { return _cest_sluz; } }
        Double? _pouzit_zboz1; public Double? pouzit_zboz1 { get { return _pouzit_zboz1; } }
        Double? _pouzit_zboz2; public Double? pouzit_zboz2 { get { return _pouzit_zboz2; } }
        Double? _pouzit_zboz3; public Double? pouzit_zboz3 { get { return _pouzit_zboz3; } }
        Double? _urceno_cerp_zuct; public Double? urceno_cerp_zuct { get { return _urceno_cerp_zuct; } }
        Double? _cerp_zuct; public Double? cerp_zuct { get { return _cerp_zuct; } }
        Rezim? _rezim; public Rezim? rezim { get { return _rezim; } }

        byte[] _bkp; public byte[] bkp { get { return _bkp; } }
        byte[] _pkp; public byte[] pkp { get { return _pkp; } }

        RSACryptoServiceProvider _key; RSACryptoServiceProvider key { get { return _key; } }

        internal EetRegisterRequest(Builder builder)
        {
            _certificate = builder._certificate;
            _dat_odesl = builder._dat_odesl;
            _prvni_zaslani = builder._prvni_zaslani;
            _uuid_zpravy = builder._uuid_zpravy;
            _overeni = builder._overeni;
            _dic_popl = builder._dic_popl;
            _dic_poverujiciho = builder._dic_poverujiciho;
            _id_provoz = builder._id_provoz;
            _id_pokl = builder._id_pokl;
            _porad_cis = builder._porad_cis;
            _dat_trzby = builder._dat_trzby;
            _celk_trzba = builder._celk_trzba;
            _zakl_nepodl_dph = builder._zakl_nepodl_dph;
            _zakl_dan1 = builder._zakl_dan1;
            _dan1 = builder._dan1;
            _zakl_dan2 = builder._zakl_dan2;
            _dan2 = builder._dan2;
            _zakl_dan3 = builder._zakl_dan3;
            _dan3 = builder._dan3;
            _cest_sluz = builder._cest_sluz;
            _pouzit_zboz1 = builder._pouzit_zboz1;
            _pouzit_zboz2 = builder._pouzit_zboz2;
            _pouzit_zboz3 = builder._pouzit_zboz3;
            _urceno_cerp_zuct = builder._urceno_cerp_zuct;
            _cerp_zuct = builder._cerp_zuct;
            _rezim = builder._rezim;
            _bkp = builder._bkp;
            _pkp = builder._pkp;
            _key = builder._key;
            _certificate = builder._certificate;

            if (builder._pkcs12bytes != null)
            {
                if (builder._pkcs12password == null)
                {
                    throw new ArgumentException("found pkcs12 data and missing pkcs12 password. use pkcs12password(\"pwd\") during the builder setup.");
                }
                loadP12(builder._pkcs12bytes, builder._pkcs12password);
            }

            if (key != null)
                computeCodes(key);
        }

        public static Builder builder()
        {
            return new Builder();
        }

        private void computeCodes(RSACryptoServiceProvider key)
        {
            try
            {
                if (pkp == null && key != null)
                {
                    String toBeSigned = formatToBeSignedData();
                    if (toBeSigned != null)
                    {
                        SHA256 sha256 = SHA256.Create();
                        byte[] data = UTF8Encoding.UTF8.GetBytes(toBeSigned);
                        byte[] hash = sha256.ComputeHash(data);
                        RSAPKCS1SignatureFormatter fmt = new RSAPKCS1SignatureFormatter(key);
                        fmt.SetHashAlgorithm("SHA256");
                        _pkp = fmt.CreateSignature(hash);
                    }
                }

                if (_bkp == null && _pkp != null)
                {
                    HashAlgorithm sha1 = new SHA1Managed();
                    _bkp = sha1.ComputeHash(_pkp);
                }
            }
            catch (Exception e)
            {
                throw new ArgumentException("error while computing codes", e);
            }
        }



        /**
         * Formats data to form ready to be signed for PKP computation based on data in this object 
         * @return 
         */
        public String formatToBeSignedData()
        {
            if (dic_popl == null || id_provoz == null || id_pokl == null || porad_cis == null || dat_trzby == null || celk_trzba == null)
                throw new NullReferenceException(
                        String.Format("missing some of _dic_popl({0}), _id_provoz({1}), _id_pokl({2}), _porad_cis({3}), _celk_trzba({4})",
                                dic_popl, id_provoz, id_pokl, porad_cis, dat_trzby, celk_trzba));
            return String.Format("{0}|{1}|{2}|{3}|{4}|{5}", dic_popl, id_provoz, id_pokl, porad_cis, formatDate(dat_trzby), formatAmount(celk_trzba.GetValueOrDefault()));
        }

        public String formatDate(DateTime date)
        {
            string ret = date.ToString("yyyy-MM-dd'T'HH:mm:sszzz");
            return ret;
        }

        public static DateTime parseDate(String date)
        {
            return DateTime.Parse(date);
        }

        public String formatBkp()
        {
            return formatBkp(bkp);
        }

        public static string byte2hex(byte[] data)
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.Length; i++)
            {
                sb.Append(String.Format("{0:X2}", data[i]));
            }
            return sb.ToString();
        }

        public static String formatBkp(byte[] _bkp)
        {
            Regex re = new Regex("^([0-9A-F]{8})([0-9A-F]{8})([0-9A-F]{8})([0-9A-F]{8})([0-9A-F]{8})$");
            return re.Replace(byte2hex(_bkp).ToUpper(), @"$1-$2-$3-$4-$5"); ;
        }

        public static byte[] parseBkp(String val)
        {
            byte[] _bkp = new byte[20];
            val = val.Replace("-", "");
            Regex re = new Regex("^[A-F0-9]{40}$");

            if (val.Length != 40)
                throw new ArgumentException("Wrong length (~=!=40) of bkp string after dash removal:" + val);
            if (re.Matches(val.ToUpper()).Count == 0)
                throw new ArgumentException("Wrong format, hexdump expected:" + val);

            for (int i = 0; i < 20; i++)
            {
                _bkp[i] = (byte)Convert.ToUInt32(val.Substring(i * 2, 2), 16);
            }
            return _bkp;
        }

        private static String formatAmount(double amount)
        {
            return String.Format(System.Globalization.NumberFormatInfo.InvariantInfo, "{0:F2}", amount);
        }

        public String formatPkp()
        {
            return formatPkp(pkp);
        }

        public static String formatPkp(byte[] _pkp)
        {
            return Convert.ToBase64String(_pkp);
        }

        public static byte[] parsePkp(String _pkp)
        {
            return Convert.FromBase64String(_pkp);
        }


        public String generateSoapRequest()
        {
            try
            {
                String sha1sum = templates.sha1sum;
                StringReader rd = new StringReader(sha1sum);
                Dictionary<String,String> hashes=new Dictionary<string,string>();

                string ln;
                while ((ln = rd.ReadLine()) != null)
                {
                    string[] fields=ln.Split(new string[]{" "},StringSplitOptions.RemoveEmptyEntries);
                    hashes[fields[1]] = fields[0];
                }

                if (!byte2hex(SHA1.Create().ComputeHash(templates.template)).ToLower().Equals(hashes["template.xml"]))
                    throw new ArgumentException("template.xml checksum verification failed") ;
                if ( ! byte2hex(SHA1.Create().ComputeHash(templates.digest_template)).ToLower().Equals(hashes["digest-template"]) )
                    throw new ArgumentException("digest-template checksum verification failed") ;
                if (!byte2hex(SHA1.Create().ComputeHash(templates.signature_template)).ToLower().Equals(hashes["signature-template"]))
                    throw new ArgumentException("signature-template checksum verification failed") ;

                String xmlTemplate = UTF8Encoding.UTF8.GetString(templates.template);
                String digestTemplate = UTF8Encoding.UTF8.GetString(templates.digest_template);
                String signatureTemplate = UTF8Encoding.UTF8.GetString(templates.signature_template);

                digestTemplate = replacePlaceholders(digestTemplate, null, null);
                digestTemplate = removeUnusedPlaceholders(digestTemplate);
                SHA256Managed md = new SHA256Managed();
                byte[] digestRaw = md.ComputeHash(UTF8Encoding.UTF8.GetBytes(digestTemplate));
                String digest = Convert.ToBase64String(digestRaw);


                signatureTemplate = replacePlaceholders(signatureTemplate, digest, null);
                signatureTemplate = removeUnusedPlaceholders(signatureTemplate);
                
                SHA256 sha256 = SHA256.Create();
                byte[] data = UTF8Encoding.UTF8.GetBytes(signatureTemplate);
                byte[] hash = sha256.ComputeHash(data);
                RSAPKCS1SignatureFormatter fmt = new RSAPKCS1SignatureFormatter(key);
                fmt.SetHashAlgorithm("SHA256");
                byte[] signatureRaw = fmt.CreateSignature(hash);
                String signature = Convert.ToBase64String(signatureRaw);

                xmlTemplate = replacePlaceholders(xmlTemplate, digest, signature);
                xmlTemplate = removeUnusedPlaceholders(xmlTemplate);

                return xmlTemplate;
            }
            catch (Exception e)
            {
                throw new ArgumentException("Error while generating soap request",e);
            }
        }

        private String replacePlaceholders(String src, String digest, String signature)
        {
            try
            {
                if (certificate != null) src = src.Replace("${certb64}", Convert.ToBase64String(certificate.GetRawCertData()));
                if (prvni_zaslani != null) src = src.Replace("${prvni_zaslani}", formatPrvniZaslani(prvni_zaslani.GetValueOrDefault())); 
                if (dat_odesl != null) src = src.Replace("${dat_odesl}", formatDate(dat_odesl));
                if (uuid_zpravy != null) src = src.Replace("${uuid_zpravy}", uuid_zpravy.ToString());
                if (overeni != null) src = src.Replace("${overeni}", formatOvereni(overeni.GetValueOrDefault()));
                if (dic_popl != null) src = src.Replace("${dic_popl}", dic_popl);
                if (dic_poverujiciho != null) src = src.Replace("${dic_poverujiciho}", dic_poverujiciho);
                if (id_provoz != null) src = src.Replace("${id_provoz}", id_provoz);
                if (id_pokl != null) src = src.Replace("${id_pokl}", id_pokl);
                if (porad_cis != null) src = src.Replace("${porad_cis}", porad_cis);
                if (dat_trzby != null) src = src.Replace("${dat_trzby}", formatDate(dat_trzby));
                if (celk_trzba != null) src = src.Replace("${celk_trzba}", formatAmount(celk_trzba.GetValueOrDefault()));
                if (zakl_nepodl_dph != null) src = src.Replace("${zakl_nepodl_dph}", formatAmount(zakl_nepodl_dph.GetValueOrDefault()));
                if (zakl_dan1 != null) src = src.Replace("${zakl_dan1}", formatAmount(zakl_dan1.GetValueOrDefault()));
                if (dan1 != null) src = src.Replace("${dan1}", formatAmount(dan1.GetValueOrDefault()));
                if (zakl_dan2 != null) src = src.Replace("${zakl_dan2}", formatAmount(zakl_dan2.GetValueOrDefault()));
                if (dan2 != null) src = src.Replace("${dan2}", formatAmount(dan2.GetValueOrDefault()));
                if (zakl_dan3 != null) src = src.Replace("${zakl_dan3}", formatAmount(zakl_dan3.GetValueOrDefault()));
                if (dan3 != null) src = src.Replace("${dan3}", formatAmount(dan3.GetValueOrDefault()));
                if (cest_sluz != null) src = src.Replace("${cest_sluz}", formatAmount(cest_sluz.GetValueOrDefault()));
                if (pouzit_zboz1 != null) src = src.Replace("${pouzit_zboz1}", formatAmount(pouzit_zboz1.GetValueOrDefault()));
                if (pouzit_zboz2 != null) src = src.Replace("${pouzit_zboz2}", formatAmount(pouzit_zboz2.GetValueOrDefault()));
                if (pouzit_zboz3 != null) src = src.Replace("${pouzit_zboz3}", formatAmount(pouzit_zboz3.GetValueOrDefault()));
                if (urceno_cerp_zuct != null) src = src.Replace("${urceno_cerp_zuct}", formatAmount(urceno_cerp_zuct.GetValueOrDefault()));
                if (cerp_zuct != null) src = src.Replace("${cerp_zuct}", formatAmount(cerp_zuct.GetValueOrDefault()));
                if (rezim != null) src = src.Replace("${rezim}", formatRezim(rezim.GetValueOrDefault()));
                if (bkp != null) src = src.Replace("${bkp}", formatBkp(bkp));
                if (pkp != null) src = src.Replace("${pkp}", formatPkp(pkp));
                if (digest != null) src = src.Replace("${digest}", digest);
                if (signature != null) src = src.Replace("${signature}", signature);

                return src;
            }
            catch (Exception e)
            {
                throw new ArgumentException("replacement processing got wrong", e);
            }
        }

        private String removeUnusedPlaceholders(String src)
        {
            src = Regex.Replace(src, " [a-z_0-9]+=\"\\$\\{[0-9_a-z]+\\}\"", "");
            src = Regex.Replace(src, "\\$\\{[a-b_0-9]+\\}", "");
            return src;
        }

        
        public String sendRequest(String requestBody, String serviceUrl) {
            //enable minimal versions of TLS required by EET
            System.Net.ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls12 | SecurityProtocolType.Tls11;
            byte[] content=UTF8Encoding.UTF8.GetBytes(requestBody);
            WebRequest req=WebRequest.Create(serviceUrl);
            req.ContentType="text/xml;charset=UTF-8";
            req.ContentLength=content.Length;
            req.Headers.Add("SOAPAction", "http://fs.mfcr.cz/eet/OdeslaniTrzby");
            req.Method="POST";
		    Stream reqStream=req.GetRequestStream();
            reqStream.Write(content,0,content.Length);
            reqStream.Close();

            WebResponse resp=req.GetResponse();
            Stream respStream=resp.GetResponseStream();
            StreamReader rdr=new StreamReader(respStream,Encoding.UTF8);
            String responseString = rdr.ReadToEnd();
            return responseString;
        }
    

        void loadP12(byte[] p12data, string password)
        {
            X509Certificate2Collection col = new X509Certificate2Collection();
            col.Import(p12data, password, X509KeyStorageFlags.Exportable);
            foreach (X509Certificate2 cert in col)
            {
                if (cert.HasPrivateKey)
                {
                    _certificate = cert;
                    RSACryptoServiceProvider tmpKey = (RSACryptoServiceProvider)cert.PrivateKey;
                    RSAParameters keyParams = tmpKey.ExportParameters(true);
                    CspParameters p = new CspParameters();
                    p.ProviderName = "Microsoft Enhanced RSA and AES Cryptographic Provider";
                    _key = new RSACryptoServiceProvider(p);
                    _key.ImportParameters(keyParams);
                }

            }

            if (_key == null || _certificate == null) throw new ArgumentException("key and/or certificate still missing after p12 processing");
        }

        protected String formatPrvniZaslani(PrvniZaslani val)
        {
            if (val == PrvniZaslani.PRVNI)
                return "true";
            else
                return "false";
        }

        protected String formatOvereni(Overeni val)
        {
            if (val == Overeni.OVEROVACI)
                return "true";
            else
                return "false";
        }

        protected String formatRezim(Rezim val)
        {
            if (val == Rezim.STANDARDNI)
                return "0";
            else
                return "1";
        }



    }
}
