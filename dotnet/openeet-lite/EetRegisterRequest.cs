/*
 * Copyright 2016 Luděk Rašek and other contributors as 
 * indicated by the @author tags.
 * Upravil Vlastimil Čoček 
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
    public enum PrvniZaslaniEnum { PRVNI = 1, OPAKOVANE = 0 }
    public enum OvereniEnum { OVEROVACI = 1, PRODUKCNI = 0 }
    public enum RezimEnum { STANDARDNI = 0, ZJEDNODUSENY = 1 }

    public class EetRequestBuilder
    {

        #region Properties

        public byte[] Pkcs12 { get; set; }
        public string Pkcs12password { get; set; }
        public RSACryptoServiceProvider Key { get; set; }
        public X509Certificate2 Certificate { get; set; }
        public DateTime DatOdesl { get; set; } = DateTime.Now;
        public PrvniZaslaniEnum? PrvniZaslani { get; set; } = PrvniZaslaniEnum.PRVNI;
        public Guid UuidZpravy { get; set; } = Guid.NewGuid();
        public OvereniEnum? Overeni { get; set; } = OvereniEnum.PRODUKCNI;
        public string DicPopl { get; set; }
        public string DicPoverujiciho { get; set; }
        public string IdProvoz { get; set; }
        public string IdPokl { get; set; }
        public string PoradCis { get; set; }
        public DateTime DatTrzby { get; set; } = DateTime.Now;
        public double? CelkTrzba { get; set; }
        public double? ZaklNepodlDph { get; set; }
        public double? ZaklDan1 { get; set; }
        public double? Dan1 { get; set; }
        public double? ZaklDan2 { get; set; }
        public double? Dan2 { get; set; }
        public double? ZaklDan3 { get; set; }
        public double? Dan3 { get; set; }
        public double? CestSluz { get; set; }
        public double? PouzitZboz1 { get; set; }
        public double? PouzitZboz2 { get; set; }
        public double? PouzitZboz3 { get; set; }
        public double? UrcenoCerpZuct { get; set; }
        public double? CerpZuct { get; set; }
        public RezimEnum? Rezim { get; set; } = RezimEnum.STANDARDNI;
        public byte[] Bkp { get; set; }
        public byte[] Pkp { get; set; }

        #endregion

        #region Public Methods

        public EetRequestBuilder SetCertificate(X509Certificate2 val)
        {
            Certificate = val;
            return this;
        }

        public EetRequestBuilder SetKey(RSACryptoServiceProvider val)
        {
            Key = val;
            return this;
        }


        /**
		 * Defaults to time of eetRequestBuilder creation 
		 * @param val
		 * @return
		 */
        public EetRequestBuilder SetDatOdesl(DateTime val)
        {
            DatOdesl = val;
            return this;
        }

        /** 
		 * Defaults to PrvniZaslani.PRVNI
		 * @param val
		 * @return
		 */
        public EetRequestBuilder SetPrvniZaslani(PrvniZaslaniEnum val)
        {
            PrvniZaslani = val;
            return this;
        }

        public EetRequestBuilder SetPrvniZaslani(bool val)
        {
            PrvniZaslani = val ? PrvniZaslaniEnum.PRVNI : PrvniZaslaniEnum.OPAKOVANE;
            return this;
        }

        /*
		public EetRequestBuilder prvni_zaslani(string val) {
			_prvni_zaslani = PrvniZaslani.valueOf(val);
			return this;
		}
        */

        /**
		 * Defaults to random UUID
		 * @param val
		 * @return
		 */
        public EetRequestBuilder SetUuidZpravy(Guid val)
        {
            UuidZpravy = val;
            return this;
        }

        public EetRequestBuilder SetUuidZpravy(string val)
        {
            UuidZpravy = new Guid(val);
            return this;
        }

        /**
		 * Defaults to PRODUKCNI
		 * @param val
		 * @return
		 */
        public EetRequestBuilder SetOvereni(OvereniEnum val)
        {
            Overeni = val;
            return this;
        }

        public EetRequestBuilder SetOvereni(bool val)
        {
            Overeni = val ? OvereniEnum.OVEROVACI : OvereniEnum.PRODUKCNI;
            return this;
        }

        /*
		public EetRequestBuilder overeni(string val) {
			_overeni = Overeni.valueOf(val);
			return this;
		}
		*/

        public EetRequestBuilder SetDicPopl(string val)
        {
            DicPopl = val;
            return this;
        }

        public EetRequestBuilder SetDicPoverujiciho(string val)
        {
            DicPoverujiciho = val;
            return this;
        }

        public EetRequestBuilder SetIdProvoz(string val)
        {
            IdProvoz = val;
            return this;
        }

        public EetRequestBuilder SetIdPokl(string val)
        {
            IdPokl = val;
            return this;
        }

        public EetRequestBuilder SetPoradCis(string val)
        {
            PoradCis = val;
            return this;
        }

        /**
		 * Defaults to eetRequestBuilder creation time
		 * @param val
		 * @return
		 */
        public EetRequestBuilder SetDatTrzby(DateTime val)
        {
            DatTrzby = val;
            return this;
        }

        public EetRequestBuilder SetDatTrzbys(string val)
        { // v delphi lze vlozit datum v textovem tvaru
            DatTrzby = EetRegisterRequest.ParseDate(val);
            return this;
        }

        public EetRequestBuilder SetCelkTrzba(double val)
        {
            CelkTrzba = val;
            return this;
        }

        public EetRequestBuilder SetCelkTrzba(string val)
        {
            CelkTrzba = double.Parse(val);
            return this;
        }

        public EetRequestBuilder SetZaklNepodlDph(double val)
        {
            ZaklNepodlDph = val;
            return this;
        }

        public EetRequestBuilder SetZaklNepodlDph(string val)
        {
            ZaklNepodlDph = double.Parse(val);
            return this;
        }

        public EetRequestBuilder SetZaklDan1(double val)
        {
            ZaklDan1 = val;
            return this;
        }

        public EetRequestBuilder SetZaklDan1(string val)
        {
            ZaklDan1 = double.Parse(val);
            return this;
        }

        public EetRequestBuilder SetDan1(double val)
        {
            Dan1 = val;
            return this;
        }

        public EetRequestBuilder SetDan1(string val)
        {
            Dan1 = double.Parse(val);
            return this;
        }

        public EetRequestBuilder SetZaklDan2(double val)
        {
            ZaklDan2 = val;
            return this;
        }

        public EetRequestBuilder SetZaklDan2(string val)
        {
            ZaklDan2 = double.Parse(val);
            return this;
        }

        public EetRequestBuilder SetDan2(double val)
        {
            Dan2 = val;
            return this;
        }

        public EetRequestBuilder SetDan2(string val)
        {
            Dan2 = double.Parse(val);
            return this;
        }

        public EetRequestBuilder SetZaklDan3(double val)
        {
            ZaklDan3 = val;
            return this;
        }

        public EetRequestBuilder SetZaklDan3(string val)
        {
            ZaklDan3 = double.Parse(val);
            return this;
        }

        public EetRequestBuilder SetDan3(double val)
        {
            Dan3 = val;
            return this;
        }

        public EetRequestBuilder SetDan3(string val)
        {
            Dan3 = double.Parse(val);
            return this;
        }

        public EetRequestBuilder SetCestSluz(double val)
        {
            CestSluz = val;
            return this;
        }

        public EetRequestBuilder SetCestSluz(string val)
        {
            CestSluz = double.Parse(val);
            return this;
        }

        public EetRequestBuilder SetPouzitZboz1(double val)
        {
            PouzitZboz1 = val;
            return this;
        }

        public EetRequestBuilder SetPouzitZboz1(string val)
        {
            PouzitZboz1 = double.Parse(val);
            return this;
        }

        public EetRequestBuilder SetPouzitZboz2(double val)
        {
            PouzitZboz2 = val;
            return this;
        }

        public EetRequestBuilder SetPouzitZboz2(string val)
        {
            PouzitZboz2 = double.Parse(val);
            return this;
        }

        public EetRequestBuilder SetPouzitZboz3(double val)
        {
            PouzitZboz3 = val;
            return this;
        }

        public EetRequestBuilder SetPouzitZboz3(string val)
        {
            PouzitZboz3 = double.Parse(val);
            return this;
        }

        public EetRequestBuilder SetUrcenoCerpZuct(double val)
        {
            UrcenoCerpZuct = val;
            return this;
        }

        public EetRequestBuilder SetUrcenoCerpZuct(string val)
        {
            UrcenoCerpZuct = double.Parse(val);
            return this;
        }

        public EetRequestBuilder SetCerpZuct(double val)
        {
            CerpZuct = val;
            return this;
        }

        public EetRequestBuilder SetCerpZuct(string val)
        {
            CerpZuct = double.Parse(val);
            return this;
        }

        /**
		 * Defualts to Rezim.STANDARDNI
		 * @param val
		 * @return
		 */
        public EetRequestBuilder SetRezim(RezimEnum val)
        {
            Rezim = val;
            return this;
        }


        public EetRequestBuilder SetRezim(int val)
        {
            if (val == 0)
                return SetRezim(RezimEnum.STANDARDNI);
            if (val == 1)
                return SetRezim(RezimEnum.ZJEDNODUSENY);
            throw new ArgumentException("only 0 and 1 is allowed as int value");
        }

        public EetRequestBuilder SetRezim(string val)
        {
            return SetRezim(int.Parse(val));
        }

        /**
		 * Computed when pkp available during build() call
		 * @param val
		 * @return
		 */
        public EetRequestBuilder SetBkp(byte[] val)
        {
            Bkp = val;
            return this;
        }

        /**
		 * Parses string according to EET spec e.g 17796128-AED2BB9E-2301FF97-0A75656A-DF2B011D
		 * @param val hex splitted into 5 groups containing 8 digits
		 * @return
		 */
        public EetRequestBuilder SetBkps(string val)
        {// v delphi lze vlozit bkp v textovem tvaru
            Bkp = EetRegisterRequest.ParseBkp(val);
            return this;
        }

        /**
		 * Computed when private key available during nuild() call
		 * @param val
		 * @return
		 */
        public EetRequestBuilder SetPkp(byte[] val)
        {
            Pkp = val;
            return this;
        }
        public EetRequestBuilder SetPkps(string val)
        {// v delphi lze vlozit pkp v textovem tvaru
            Pkp = EetRegisterRequest.ParsePkp(val);
            return this;
        }


        /** 
		 * file is loaded immediately
		 * @param p12Filename
		 * @return
		 */
        public EetRequestBuilder SetPkcs12s(string p12Filename)
        {// v delphi lze vlozit nazev souboru 'xxxxxx.p12'
            return SetPkcs12(File.ReadAllBytes(p12Filename));
        }

        public EetRequestBuilder SetPkcs12(byte[] p12bytes)
        {
            Pkcs12 = p12bytes;
            return this;
        }

        public EetRequestBuilder SetPkcs12password(string password)
        {
            Pkcs12password = password;
            return this;
        }

        public EetRegisterRequest Build()
        {
            return new EetRegisterRequest(this);
        }

        #endregion

    }

    public class EetRegisterRequest
    {
        public X509Certificate2 Certificate { get; private set; }
        public DateTime DatOdesl { get; private set; }
        public PrvniZaslaniEnum? PrvniZaslani { get; private set; }
        public Guid UuidZpravy { get; private set; }
        public OvereniEnum? Overeni { get; private set; }
        public string DicPopl { get; private set; }
        public string DicPoverujiciho { get; private set; }
        public string IdProvoz { get; private set; }
        public string IdPokl { get; private set; }
        public string PoradCis { get; private set; }
        public DateTime DatTrzby { get; private set; }
        public double? CelkTrzba { get; private set; }
        public double? ZaklNepodlDph { get; private set; }
        public double? ZaklDan1 { get; private set; }
        public double? Dan1 { get; private set; }
        public double? ZaklDan2 { get; private set; }
        public double? Dan2 { get; private set; }
        public double? ZaklDan3 { get; private set; }
        public double? Dan3 { get; private set; }
        public double? CestSluz { get; private set; }
        public double? PouzitZboz1 { get; private set; }
        public double? PouzitZboz2 { get; private set; }
        public double? PouzitZboz3 { get; private set; }
        public double? UrcenoCerpZuct { get; private set; }
        public double? CerpZuct { get; private set; }
        public RezimEnum? Rezim { get; private set; }

        public byte[] Bkp { get; private set; }
        public byte[] Pkp { get; private set; }

        public RSACryptoServiceProvider Key { get; private set; }

        internal EetRegisterRequest(EetRequestBuilder eetRequestBuilder)
        {
            Certificate = eetRequestBuilder.Certificate;
            DatOdesl = eetRequestBuilder.DatOdesl;
            PrvniZaslani = eetRequestBuilder.PrvniZaslani;
            UuidZpravy = eetRequestBuilder.UuidZpravy;
            Overeni = eetRequestBuilder.Overeni;
            DicPopl = eetRequestBuilder.DicPopl;
            DicPoverujiciho = eetRequestBuilder.DicPoverujiciho;
            IdProvoz = eetRequestBuilder.IdProvoz;
            IdPokl = eetRequestBuilder.IdPokl;
            PoradCis = eetRequestBuilder.PoradCis;
            DatTrzby = eetRequestBuilder.DatTrzby;
            CelkTrzba = eetRequestBuilder.CelkTrzba;
            ZaklNepodlDph = eetRequestBuilder.ZaklNepodlDph;
            ZaklDan1 = eetRequestBuilder.ZaklDan1;
            Dan1 = eetRequestBuilder.Dan1;
            ZaklDan2 = eetRequestBuilder.ZaklDan2;
            Dan2 = eetRequestBuilder.Dan2;
            ZaklDan3 = eetRequestBuilder.ZaklDan3;
            Dan3 = eetRequestBuilder.Dan3;
            CestSluz = eetRequestBuilder.CestSluz;
            PouzitZboz1 = eetRequestBuilder.PouzitZboz1;
            PouzitZboz2 = eetRequestBuilder.PouzitZboz2;
            PouzitZboz3 = eetRequestBuilder.PouzitZboz3;
            UrcenoCerpZuct = eetRequestBuilder.UrcenoCerpZuct;
            CerpZuct = eetRequestBuilder.CerpZuct;
            Rezim = eetRequestBuilder.Rezim;
            Bkp = eetRequestBuilder.Bkp;
            Pkp = eetRequestBuilder.Pkp;
            Key = eetRequestBuilder.Key;
            Certificate = eetRequestBuilder.Certificate;

            if (eetRequestBuilder.Pkcs12 != null)
            {
                if (eetRequestBuilder.Pkcs12password == null)
                {
                    throw new ArgumentException("found pkcs12 data and missing pkcs12 password. use pkcs12password(\"pwd\") during the eetRequestBuilder setup.");
                }
                LoadP12(eetRequestBuilder.Pkcs12, eetRequestBuilder.Pkcs12password);
            }

            if (Key != null)
                ComputeCodes(Key);
        }

        public static EetRequestBuilder Builder()
        {
            return new EetRequestBuilder();
        }

        private void ComputeCodes(RSACryptoServiceProvider key)
        {
            try
            {
                if (Pkp == null && key != null)
                {
                    string toBeSigned = FormatToBeSignedData();
                    if (toBeSigned != null)
                    {
                        SHA256 sha256 = SHA256.Create();
                        byte[] data = UTF8Encoding.UTF8.GetBytes(toBeSigned);
                        byte[] hash = sha256.ComputeHash(data);
                        RSAPKCS1SignatureFormatter fmt = new RSAPKCS1SignatureFormatter(key);
                        fmt.SetHashAlgorithm("SHA256");
                        Pkp = fmt.CreateSignature(hash);
                    }
                }

                if (Bkp == null && Pkp != null)
                {
                    HashAlgorithm sha1 = new SHA1Managed();
                    Bkp = sha1.ComputeHash(Pkp);
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
        public string FormatToBeSignedData()
        {
            if (DicPopl == null || IdProvoz == null || IdPokl == null || PoradCis == null || DatTrzby == null || CelkTrzba == null)
                throw new NullReferenceException(
                    $"missing some of DicPopl({DicPopl}), IdProvoz({IdProvoz}), IdPokl({IdPokl}), PoradCis({PoradCis}), DatTrzby({DatTrzby}), CelkTrzba({CelkTrzba})");
            return string.Format("{0}|{1}|{2}|{3}|{4}|{5}", DicPopl, IdProvoz, IdPokl, PoradCis, FormatDate(DatTrzby), FormatAmount(CelkTrzba.GetValueOrDefault()));
        }

        public string FormatDate(DateTime date)
        {
            string ret = date.ToString("yyyy-MM-dd'T'HH:mm:sszzz");
            return ret;
        }

        public static DateTime ParseDate(string date)
        {
            return DateTime.Parse(date);
        }

        public string FormatBkp()
        {
            return FormatBkp(Bkp);
        }

        public static string Byte2hex(byte[] data)
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.Length; i++)
            {
                sb.Append(string.Format("{0:X2}", data[i]));
            }
            return sb.ToString();
        }

        public static string FormatBkp(byte[] _bkp)
        {
            Regex re = new Regex("^([0-9A-F]{8})([0-9A-F]{8})([0-9A-F]{8})([0-9A-F]{8})([0-9A-F]{8})$");
            return re.Replace(Byte2hex(_bkp).ToUpper(), @"$1-$2-$3-$4-$5"); ;
        }

        public static byte[] ParseBkp(string val)
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

        private static string FormatAmount(double amount)
        {
            return string.Format(System.Globalization.NumberFormatInfo.InvariantInfo, "{0:F2}", amount);
        }

        public string FormatPkp()
        {
            return FormatPkp(Pkp);
        }

        public static string FormatPkp(byte[] _pkp)
        {
            return Convert.ToBase64String(_pkp);
        }

        public static byte[] ParsePkp(string _pkp)
        {
            return Convert.FromBase64String(_pkp);
        }


        public string GenerateSoapRequest()
        {
            try
            {
                string sha1sum = templates.sha1sum;
                StringReader rd = new StringReader(sha1sum);
                Dictionary<string, string> hashes = new Dictionary<string, string>();

                string ln;
                while ((ln = rd.ReadLine()) != null)
                {
                    string[] fields = ln.Split(new string[] { " " }, StringSplitOptions.RemoveEmptyEntries);
                    hashes[fields[1]] = fields[0];
                }

                if (!Byte2hex(SHA1.Create().ComputeHash(templates.template)).ToLower().Equals(hashes["template.xml"]))
                    throw new ArgumentException("template.xml checksum verification failed");
                if (!Byte2hex(SHA1.Create().ComputeHash(templates.digest_template)).ToLower().Equals(hashes["digest-template"]))
                    throw new ArgumentException("digest-template checksum verification failed");
                if (!Byte2hex(SHA1.Create().ComputeHash(templates.signature_template)).ToLower().Equals(hashes["signature-template"]))
                    throw new ArgumentException("signature-template checksum verification failed");

                string xmlTemplate = UTF8Encoding.UTF8.GetString(templates.template);
                string digestTemplate = UTF8Encoding.UTF8.GetString(templates.digest_template);
                string signatureTemplate = UTF8Encoding.UTF8.GetString(templates.signature_template);

                digestTemplate = ReplacePlaceholders(digestTemplate, null, null);
                digestTemplate = RemoveUnusedPlaceholders(digestTemplate);
                SHA256Managed md = new SHA256Managed();
                byte[] digestRaw = md.ComputeHash(UTF8Encoding.UTF8.GetBytes(digestTemplate));
                string digest = Convert.ToBase64String(digestRaw);


                signatureTemplate = ReplacePlaceholders(signatureTemplate, digest, null);
                signatureTemplate = RemoveUnusedPlaceholders(signatureTemplate);

                SHA256 sha256 = SHA256.Create();
                byte[] data = UTF8Encoding.UTF8.GetBytes(signatureTemplate);
                byte[] hash = sha256.ComputeHash(data);
                RSAPKCS1SignatureFormatter fmt = new RSAPKCS1SignatureFormatter(Key);
                fmt.SetHashAlgorithm("SHA256");
                byte[] signatureRaw = fmt.CreateSignature(hash);
                string signature = Convert.ToBase64String(signatureRaw);

                xmlTemplate = ReplacePlaceholders(xmlTemplate, digest, signature);
                xmlTemplate = RemoveUnusedPlaceholders(xmlTemplate);

                return xmlTemplate;
            }
            catch (Exception e)
            {
                throw new ArgumentException("Error while generating soap request", e);
            }
        }

        private string ReplacePlaceholders(string src, string digest, string signature)
        {
            try
            {
                if (Certificate != null) src = src.Replace("${certb64}", Convert.ToBase64String(Certificate.GetRawCertData()));
                if (PrvniZaslani != null) src = src.Replace("${prvni_zaslani}", FormatPrvniZaslani(PrvniZaslani.GetValueOrDefault()));
                if (DatOdesl != null) src = src.Replace("${dat_odesl}", FormatDate(DatOdesl));
                if (UuidZpravy != null) src = src.Replace("${uuid_zpravy}", UuidZpravy.ToString());
                if (Overeni != null) src = src.Replace("${overeni}", FormatOvereni(Overeni.GetValueOrDefault()));
                if (DicPopl != null) src = src.Replace("${dic_popl}", DicPopl);
                if (DicPoverujiciho != null) src = src.Replace("${dic_poverujiciho}", DicPoverujiciho);
                if (IdProvoz != null) src = src.Replace("${id_provoz}", IdProvoz);
                if (IdPokl != null) src = src.Replace("${id_pokl}", IdPokl);
                if (PoradCis != null) src = src.Replace("${porad_cis}", PoradCis);
                if (DatTrzby != null) src = src.Replace("${dat_trzby}", FormatDate(DatTrzby));
                if (CelkTrzba != null) src = src.Replace("${celk_trzba}", FormatAmount(CelkTrzba.GetValueOrDefault()));
                if (ZaklNepodlDph != null) src = src.Replace("${zakl_nepodl_dph}", FormatAmount(ZaklNepodlDph.GetValueOrDefault()));
                if (ZaklDan1 != null) src = src.Replace("${zakl_dan1}", FormatAmount(ZaklDan1.GetValueOrDefault()));
                if (Dan1 != null) src = src.Replace("${dan1}", FormatAmount(Dan1.GetValueOrDefault()));
                if (ZaklDan2 != null) src = src.Replace("${zakl_dan2}", FormatAmount(ZaklDan2.GetValueOrDefault()));
                if (Dan2 != null) src = src.Replace("${dan2}", FormatAmount(Dan2.GetValueOrDefault()));
                if (ZaklDan3 != null) src = src.Replace("${zakl_dan3}", FormatAmount(ZaklDan3.GetValueOrDefault()));
                if (Dan3 != null) src = src.Replace("${dan3}", FormatAmount(Dan3.GetValueOrDefault()));
                if (CestSluz != null) src = src.Replace("${cest_sluz}", FormatAmount(CestSluz.GetValueOrDefault()));
                if (PouzitZboz1 != null) src = src.Replace("${pouzit_zboz1}", FormatAmount(PouzitZboz1.GetValueOrDefault()));
                if (PouzitZboz2 != null) src = src.Replace("${pouzit_zboz2}", FormatAmount(PouzitZboz2.GetValueOrDefault()));
                if (PouzitZboz3 != null) src = src.Replace("${pouzit_zboz3}", FormatAmount(PouzitZboz3.GetValueOrDefault()));
                if (UrcenoCerpZuct != null) src = src.Replace("${urceno_cerp_zuct}", FormatAmount(UrcenoCerpZuct.GetValueOrDefault()));
                if (CerpZuct != null) src = src.Replace("${cerp_zuct}", FormatAmount(CerpZuct.GetValueOrDefault()));
                if (Rezim != null) src = src.Replace("${rezim}", FormatRezim(Rezim.GetValueOrDefault()));
                if (Bkp != null) src = src.Replace("${bkp}", FormatBkp(Bkp));
                if (Pkp != null) src = src.Replace("${pkp}", FormatPkp(Pkp));
                if (digest != null) src = src.Replace("${digest}", digest);
                if (signature != null) src = src.Replace("${signature}", signature);

                return src;
            }
            catch (Exception e)
            {
                throw new ArgumentException("replacement processing got wrong", e);
            }
        }

        private string RemoveUnusedPlaceholders(string src)
        {
            src = Regex.Replace(src, " [a-z_0-9]+=\"\\$\\{[0-9_a-z]+\\}\"", "");
            src = Regex.Replace(src, "\\$\\{[a-b_0-9]+\\}", "");
            return src;
        }


        public string SendRequest(string requestBody, string serviceUrl)
        {
            //enable minimal versions of TLS required by EET
            System.Net.ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls12 | SecurityProtocolType.Tls11;
            byte[] content = UTF8Encoding.UTF8.GetBytes(requestBody);
            WebRequest req = WebRequest.Create(serviceUrl);
            req.ContentType = "text/xml;charset=UTF-8";
            req.ContentLength = content.Length;
            req.Headers.Add("SOAPAction", "http://fs.mfcr.cz/eet/OdeslaniTrzby");
            req.Method = "POST";
            Stream reqStream = req.GetRequestStream();
            reqStream.Write(content, 0, content.Length);
            reqStream.Close();

            WebResponse resp = req.GetResponse();
            Stream respStream = resp.GetResponseStream();
            StreamReader rdr = new StreamReader(respStream, Encoding.UTF8);
            string responseString = rdr.ReadToEnd();
            return responseString;
        }


        void LoadP12(byte[] p12data, string password)
        {
            X509Certificate2Collection col = new X509Certificate2Collection();
            col.Import(p12data, password, X509KeyStorageFlags.Exportable);
            foreach (X509Certificate2 cert in col)
            {
                if (cert.HasPrivateKey)
                {
                    Certificate = cert;
                    RSACryptoServiceProvider tmpKey = (RSACryptoServiceProvider)cert.PrivateKey;
                    RSAParameters keyParams = tmpKey.ExportParameters(true);
                    CspParameters p = new CspParameters();
                    p.ProviderName = "Microsoft Enhanced RSA and AES Cryptographic Provider";
                    Key = new RSACryptoServiceProvider(p);
                    Key.ImportParameters(keyParams);
                }

            }

            if (Key == null || Certificate == null) throw new ArgumentException("key and/or certificate still missing after p12 processing");
        }

        protected string FormatPrvniZaslani(PrvniZaslaniEnum val)
        {
            return val == PrvniZaslaniEnum.PRVNI ? "true" : "false";
        }

        protected string FormatOvereni(OvereniEnum val)
        {
            return val == OvereniEnum.OVEROVACI ? "true" : "false";
        }

        protected string FormatRezim(RezimEnum val)
        {
            return val == RezimEnum.STANDARDNI ? "0" : "1";
        }
    }
}
