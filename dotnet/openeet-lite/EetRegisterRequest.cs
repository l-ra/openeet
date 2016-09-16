/*
 * Copyright 2016 Luděk Rašek and other contributors as 
 * indicated by the @author tags.
 * Upravil Vlastimil Čoček, Jakub Cermoch
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
using System.Text.RegularExpressions;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.IO;
using System.Net;
// ReSharper disable MemberInitializerValueIgnored

namespace openeet_lite
{
    /// <summary>
    /// EET Request
    /// </summary>
    public class EetRegisterRequest
    {
        /// <summary>
        /// Gets or sets the X509 Certificate.
        /// </summary>
        /// <value>
        /// The certificate.
        /// </value>
        public X509Certificate2 Certificate { get; private set; }

        /// <summary>
        /// Gets or sets Datum a cas odeslani zpravy na server.
        /// </summary>
        /// <value>
        /// The dat odesl.
        /// </value>
        public DateTime DatOdesl { get; set; } = DateTime.Now;

        /// <summary>
        /// Gets or sets a value indicating whether is zprava odeslana poprve, nebo se jedna o dalsi odeslani.
        /// </summary>
        /// <value>
        /// Prvni zaslani.
        /// </value>
        public PrvniZaslaniEnum? PrvniZaslani { get; set; } = PrvniZaslaniEnum.Prvni;

        /// <summary>
        /// Gets or sets UUID zpravy. Identifikator zpravy, ne e-trzbu.
        /// </summary>
        /// <value>
        /// UUID zpravy.
        /// </value>
        public Guid UuidZpravy { get; set; } = Guid.NewGuid();

        /// <summary>
        /// Gets or sets a value indicating whether is zprava overovaciho typu nebo jestli se jedna o ostrou zpravu.
        /// </summary>
        /// <value>
        /// Prvni zaslani.
        /// </value>
        public OvereniEnum? Overeni { get; set; } = OvereniEnum.Produkcni;

        /// <summary>
        /// Gets or sets DIC poplatnika, ktery ke kteremu se ma uctenka zapocitat.
        /// </summary>
        /// <value>
        /// DIC poplatnika
        /// </value>
        public string DicPopl { get; set; }

        /// <summary>
        /// Gets or sets DIC poverene osoby, ktera odesila nahradou za poplatnika (DIC poplatnika).
        /// </summary>
        /// <value>
        /// DIC poverujici osoby.
        /// </value>
        public string DicPoverujiciho { get; set; }

        /// <summary>
        /// Gets or sets Identifikace provozovny, ktera byla pridelena portalem EET.
        /// </summary>
        /// <value>
        /// Identifikace provozovny.
        /// </value>
        public string IdProvoz { get; set; }

        /// <summary>
        /// Gets or sets Unikatni ID zarizeni, ktere odesila uctenku online.
        /// </summary>
        /// <value>
        /// Identifier pokladny.
        /// </value>
        public string IdPokl { get; set; }


        /// <summary>
        /// Gets or sets poradove cislo dokladu.
        /// </summary>
        /// <value>
        /// Poradove cislo dokladu.
        /// </value>
        public string PoradCis { get; set; }

        /// <summary>
        /// Gets or sets datum provedeni trzby.
        /// </summary>
        /// <value>
        /// Datum provedeni trzby.
        /// </value>
        public DateTime DatTrzby { get; set; } = DateTime.Now;


        /// <summary>
        /// Gets or sets Celkova castka trzby v Kc.
        /// </summary>
        /// <value>
        /// Celkova castka trzby v Kc.
        /// </value>
        public double? CelkTrzba { get; set; }

        /// <summary>
        /// Gets or sets Celkova castka nepodlehajici zdaneni.
        /// </summary>
        /// <value>
        /// Celkova castka nepodlehajici zdaneni
        /// </value>
        public double? ZaklNepodlDph { get; set; }

        /// <summary>
        /// Gets or sets Celkovy zaklad dane se zakladni sazbou DPH.
        /// </summary>
        /// <value>
        /// Celkovy zaklad dane se zakladni sazbou DPH.
        /// </value>
        public double? ZaklDan1 { get; set; }

        /// <summary>
        /// Gets or sets the Celkova DPH v zakladni sazbe.
        /// </summary>
        /// <value>
        /// Celkova DPH v zakladni sazbe.
        /// </value>
        public double? Dan1 { get; set; }

        /// <summary>
        /// Gets or sets Celkovy zaklad dane s prvni snizenou sazbou DPH.
        /// </summary>
        /// <value>
        /// Celkovy zaklad dane s prvni snizenou sazbou DPH.
        /// </value>
        public double? ZaklDan2 { get; set; }

        /// <summary>
        /// Gets or sets the Celkova DPH v prvni snizene sazbe.
        /// </summary>
        /// <value>
        /// Celkova DPH v prvni snizene sazbe.
        /// </value>
        public double? Dan2 { get; set; }

        /// <summary>
        /// Gets or sets Celkovy zaklad dane s druhou snizenou sazbou DPH.
        /// </summary>
        /// <value>
        /// Celkovy zaklad dane s druhou snizenou sazbou DPH.
        /// </value>
        public double? ZaklDan3 { get; set; }

        /// <summary>
        /// Gets or sets the Celkova DPH v druhe snizene sazbe.
        /// </summary>
        /// <value>
        /// Celkova DPH v druhe snizene sazbe.
        /// </value>
        public double? Dan3 { get; set; }

        /// <summary>
        /// Gets or sets Celkova castka DPH pro cestovni sluzbu.
        /// </summary>
        /// <value>
        /// Celkova castka DPH pro cestovni sluzbu.
        /// </value>
        public double? CestSluz { get; set; }

        /// <summary>
        /// Gets or sets Celkovy zaklad dane v zakladni sazbe DPH z <c>Pouziteho</c> zbozi.
        /// </summary>
        /// <value>
        /// Celkovy zaklad dane v zakladni sazbe DPH z <c>Pouziteho</c> zbozi.
        /// </value>
        public double? PouzitZboz1 { get; set; }

        /// <summary>
        /// Gets or sets Celkovy zaklad dane v prvni snizene sazbe DPH z <c>Pouziteho</c> zbozi.
        /// </summary>
        /// <value>
        /// Celkovy zaklad dane v prvni snizene sazbe DPH z <c>Pouziteho</c> zbozi.
        /// </value>
        public double? PouzitZboz2 { get; set; }

        /// <summary>
        /// Gets or sets Celkovy zaklad dane ve druhe snizene sazbe DPH z <c>Pouziteho</c> zbozi.
        /// </summary>
        /// <value>
        /// Celkovy zaklad dane ve druhe snizene sazbe DPH z <c>Pouziteho</c> zbozi.
        /// </value>
        public double? PouzitZboz3 { get; set; }

        /// <summary>
        /// Gets or sets Celkova castka plateb urcena k naslednemu cerpani nebo zuctovani.
        /// </summary>
        /// <value>
        /// Celkova castka plateb urcena k naslednemu cerpani nebo zuctovani.
        /// </value>
        public double? UrcenoCerpZuct { get; set; }

        /// <summary>
        /// Gets or sets Celkova castka plateb, ktere jsou naslednym cerpanim nebo zuctovanim platby.
        /// </summary>
        /// <value>
        /// Celkova castka plateb, ktere jsou naslednym cerpanim nebo zuctovanim platby.
        /// </value>
        public double? CerpZuct { get; set; }

        /// <summary>
        /// Gets or sets the Rezim trzby. Standardni bezny rezim nebo zjednoduseny rezim.
        /// </summary>
        /// <value>
        /// Rezim trzby.
        /// </value>
        public RezimEnum? Rezim { get; set; } = RezimEnum.Standardni;

        /// <summary>
        /// Gets or sets Bezpecnostni kod poplatnika. Jedna se o hash Kodu PKP.
        /// </summary>
        /// <value>
        /// Bezpecnostni kod poplatnika.
        /// </value>
        public byte[] Bkp { get; set; }

        /// <summary>
        /// Gets or sets Podpisovy kod poplatnika. Jedna se o podpis vybranych dat.
        /// </summary>
        /// <value>
        /// Podpisovy kod poplatnika.
        /// </value>
        public byte[] Pkp { get; set; }

        /// <summary>
        /// Gets or sets the RSA Key
        /// </summary>
        /// <value>
        /// The key.
        /// </value>
        public RSACryptoServiceProvider Key { get; private set; }

        /// <summary>
        /// Initializes a new instance of the <see cref="EetRegisterRequest"/> class.
        /// </summary>
        /// <param name="eetRequestBuilder">The eet request builder.</param>
        /// <exception cref="ArgumentException">Found pkcs12 data and missing pkcs12 password. use pkcs12password(\"pwd\") during the eetRequestBuilder setup.</exception>
        internal EetRegisterRequest(EetRequestBuilder eetRequestBuilder)
        {
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
                if (eetRequestBuilder.Pkcs12Password == null)
                {
                    throw new ArgumentException("Found pkcs12 data and missing pkcs12 password. use pkcs12password(\"pwd\") during the eetRequestBuilder setup.");
                }
                LoadP12(eetRequestBuilder.Pkcs12, eetRequestBuilder.Pkcs12Password);
            }

            if (Key != null)
                ComputeCodes(Key);
        }

        /// <summary>
        /// Get Builder.
        /// </summary>
        /// <returns>Request builder.</returns>
        public static EetRequestBuilder Builder()
        {
            return new EetRequestBuilder();
        }

        /// <summary>
        /// Computes PKP and BKP.
        /// </summary>
        /// <param name="key">The key.</param>
        /// <exception cref="ArgumentException">Error while computing codes</exception>
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
                        byte[] data = Encoding.UTF8.GetBytes(toBeSigned);
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
                throw new ArgumentException("Error while computing codes: ", e);
            }
        }

        /// <summary>
        /// Formats data to form ready to be signed for PKP computation based on data in this object.
        /// </summary>
        /// <returns></returns>
        /// <exception cref="NullReferenceException">missing some of DicPopl({DicPopl}), IdProvoz({IdProvoz}), IdPokl({IdPokl}), PoradCis({PoradCis}), DatTrzby({DatTrzby}), CelkTrzba({CelkTrzba})");</exception>
        public string FormatToBeSignedData()
        {
            if (DicPopl == null || IdProvoz == null || IdPokl == null || PoradCis == null || DatTrzby == null || CelkTrzba == null)
                throw new ArgumentNullException($"missing some of DicPopl({DicPopl}), IdProvoz({IdProvoz}), IdPokl({IdPokl}), PoradCis({PoradCis}), DatTrzby({DatTrzby}), CelkTrzba({CelkTrzba})");
            return $"{DicPopl}|{IdProvoz}|{IdPokl}|{PoradCis}|{FormatDate(DatTrzby)}|{FormatAmount(CelkTrzba.GetValueOrDefault())}";
        }

        /// <summary>
        /// Formats the date to EET form.
        /// </summary>
        /// <param name="date">The date.</param>
        /// <returns>Formated date</returns>
        public string FormatDate(DateTime date)
        {
            string ret = date.ToString("yyyy-MM-dd'T'HH:mm:sszzz");
            return ret;
        }

        /// <summary>
        /// Parses the date.
        /// </summary>
        /// <param name="date">The date.</param>
        /// <returns>The date</returns>
        public static DateTime ParseDate(string date)
        {
            return DateTime.Parse(date);
        }

        /// <summary>
        /// Formats the BKP.
        /// </summary>
        /// <returns></returns>
        public string FormatBkp()
        {
            return FormatBkp(Bkp);
        }

        /// <summary>
        /// Convert byte data to hexa string.
        /// </summary>
        /// <param name="data">Byte data.</param>
        /// <returns>Hexa data in string.</returns>
        public static string Byte2Hex(byte[] data)
        {
            StringBuilder sb = new StringBuilder();
            foreach (byte t in data)
            {
                sb.Append($"{t:X2}");
            }
            return sb.ToString();
        }

        /// <summary>
        /// Formats the BKP.
        /// </summary>
        /// <param name="bkp">The BKP.</param>
        /// <returns></returns>
        public static string FormatBkp(byte[] bkp)
        {
            Regex re = new Regex("^([0-9A-F]{8})([0-9A-F]{8})([0-9A-F]{8})([0-9A-F]{8})([0-9A-F]{8})$");
            return re.Replace(Byte2Hex(bkp).ToUpper(), @"$1-$2-$3-$4-$5");
        }

        /// <summary>
        /// Retrieve byte BKP from string form.
        /// </summary>
        /// <param name="val">String BKP.</param>
        /// <returns>Bytes BKP.</returns>
        /// <exception cref="ArgumentException">
        /// Wrong length or wrong format.
        /// </exception>
        public static byte[] ParseBkp(string val)
        {
            byte[] bkp = new byte[20];
            val = val.Replace("-", "");
            Regex re = new Regex("^[A-F0-9]{40}$");

            if (val.Length != 40)
                throw new ArgumentException("Wrong length (~=!=40) of bkp string after dash removal:" + val);
            if (re.Matches(val.ToUpper()).Count == 0)
                throw new ArgumentException("Wrong format, hexdump expected:" + val);

            for (int i = 0; i < 20; i++)
            {
                bkp[i] = (byte)Convert.ToUInt32(val.Substring(i * 2, 2), 16);
            }
            return bkp;
        }

        /// <summary>
        /// Formats the amount.
        /// </summary>
        /// <param name="amount">The amount.</param>
        /// <returns></returns>
        private static string FormatAmount(double amount)
        {
            return string.Format(System.Globalization.NumberFormatInfo.InvariantInfo, "{0:F2}", amount);
        }

        /// <summary>
        /// Formats the PKP.
        /// </summary>
        /// <returns></returns>
        public string FormatPkp()
        {
            return FormatPkp(Pkp);
        }

        /// <summary>
        /// Formats the PKP.
        /// </summary>
        /// <param name="pkp">The PKP.</param>
        /// <returns></returns>
        public static string FormatPkp(byte[] pkp)
        {
            return Convert.ToBase64String(pkp);
        }

        /// <summary>
        /// Parses the PKP.
        /// </summary>
        /// <param name="pkp">The PKP.</param>
        /// <returns></returns>
        public static byte[] ParsePkp(string pkp)
        {
            return Convert.FromBase64String(pkp);
        }

        /// <summary>
        /// Generates the SOAP request.
        /// </summary>
        /// <returns></returns>
        public string GenerateSoapRequest()
        {
            string sha1Sum = templates.sha1sum;
            StringReader rd = new StringReader(sha1Sum);
            Dictionary<string, string> hashes = new Dictionary<string, string>();

            string ln;
            while ((ln = rd.ReadLine()) != null)
            {
                string[] fields = ln.Split(new[] { " " }, StringSplitOptions.RemoveEmptyEntries);
                hashes[fields[1]] = fields[0];
            }

            if (!Byte2Hex(SHA1.Create().ComputeHash(templates.template)).ToLower().Equals(hashes["template.xml"]))
                throw new ArgumentException("template.xml checksum verification failed");
            if (!Byte2Hex(SHA1.Create().ComputeHash(templates.digest_template)).ToLower().Equals(hashes["digest-template"]))
                throw new ArgumentException("digest-template checksum verification failed");
            if (!Byte2Hex(SHA1.Create().ComputeHash(templates.signature_template)).ToLower().Equals(hashes["signature-template"]))
                throw new ArgumentException("signature-template checksum verification failed");

            string xmlTemplate = Encoding.UTF8.GetString(templates.template);
            string digestTemplate = Encoding.UTF8.GetString(templates.digest_template);
            string signatureTemplate = Encoding.UTF8.GetString(templates.signature_template);

            digestTemplate = ReplacePlaceholders(digestTemplate, null, null);
            digestTemplate = RemoveUnusedPlaceholders(digestTemplate);
            SHA256Managed md = new SHA256Managed();
            byte[] digestRaw = md.ComputeHash(Encoding.UTF8.GetBytes(digestTemplate));
            string digest = Convert.ToBase64String(digestRaw);


            signatureTemplate = ReplacePlaceholders(signatureTemplate, digest, null);
            signatureTemplate = RemoveUnusedPlaceholders(signatureTemplate);

            SHA256 sha256 = SHA256.Create();
            byte[] data = Encoding.UTF8.GetBytes(signatureTemplate);
            byte[] hash = sha256.ComputeHash(data);
            RSAPKCS1SignatureFormatter fmt = new RSAPKCS1SignatureFormatter(Key);
            fmt.SetHashAlgorithm("SHA256");
            byte[] signatureRaw = fmt.CreateSignature(hash);
            string signature = Convert.ToBase64String(signatureRaw);

            xmlTemplate = ReplacePlaceholders(xmlTemplate, digest, signature);
            xmlTemplate = RemoveUnusedPlaceholders(xmlTemplate);

            return xmlTemplate;
        }

        /// <summary>
        /// Replaces the placeholders.
        /// </summary>
        /// <param name="src">The source.</param>
        /// <param name="digest">The digest.</param>
        /// <param name="signature">The signature.</param>
        /// <returns></returns>
        /// <exception cref="ArgumentException">replacement processing got wrong</exception>
        private string ReplacePlaceholders(string src, string digest, string signature)
        {
            try
            {
                if (Certificate != null) src = src.Replace("${certb64}", Convert.ToBase64String(Certificate.GetRawCertData()));
                if (PrvniZaslani != null) src = src.Replace("${prvni_zaslani}", FormatPrvniZaslani(PrvniZaslani.GetValueOrDefault()));
                src = src.Replace("${dat_odesl}", FormatDate(DatOdesl));
                src = src.Replace("${uuid_zpravy}", UuidZpravy.ToString());
                if (Overeni != null) src = src.Replace("${overeni}", FormatOvereni(Overeni.GetValueOrDefault()));
                if (DicPopl != null) src = src.Replace("${dic_popl}", DicPopl);
                if (DicPoverujiciho != null) src = src.Replace("${dic_poverujiciho}", DicPoverujiciho);
                if (IdProvoz != null) src = src.Replace("${id_provoz}", IdProvoz);
                if (IdPokl != null) src = src.Replace("${id_pokl}", IdPokl);
                if (PoradCis != null) src = src.Replace("${porad_cis}", PoradCis);
                src = src.Replace("${dat_trzby}", FormatDate(DatTrzby));
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

        /// <summary>
        /// Removes the unused placeholders.
        /// </summary>
        /// <param name="src">The source.</param>
        /// <returns></returns>
        private string RemoveUnusedPlaceholders(string src)
        {
            src = Regex.Replace(src, " [a-z_0-9]+=\"\\$\\{[0-9_a-z]+\\}\"", "");
            src = Regex.Replace(src, "\\$\\{[a-b_0-9]+\\}", "");
            return src;
        }


        /// <summary>
        /// Sends the request.
        /// </summary>
        /// <param name="requestBody">The request body.</param>
        /// <param name="serviceUrl">The service URL.</param>
        /// <returns></returns>
        /// <exception cref="NullReferenceException">When cannot obtain response stream.</exception>
        public string SendRequest(string requestBody, string serviceUrl)
        {
            //enable minimal versions of TLS required by EET
            ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls12 | SecurityProtocolType.Tls11;
            byte[] content = Encoding.UTF8.GetBytes(requestBody);
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
            if (respStream == null)
            {
                throw new NullReferenceException();
            }
            StreamReader rdr = new StreamReader(respStream, Encoding.UTF8);
            string responseString = rdr.ReadToEnd();
            return responseString;
        }


        /// <summary>
        /// Loads the P12.
        /// </summary>
        /// <param name="p12Data">The p12data.</param>
        /// <param name="password">The password.</param>
        /// <exception cref="ArgumentException">key and/or certificate still missing after p12 processing</exception>
        void LoadP12(byte[] p12Data, string password)
        {
            X509Certificate2Collection col = new X509Certificate2Collection();
            col.Import(p12Data, password, X509KeyStorageFlags.Exportable);
            foreach (X509Certificate2 cert in col)
            {
                if (cert.HasPrivateKey)
                {
                    Certificate = cert;
                    RSACryptoServiceProvider tmpKey = (RSACryptoServiceProvider)cert.PrivateKey;
                    RSAParameters keyParams = tmpKey.ExportParameters(true);
                    CspParameters p = new CspParameters { ProviderName = "Microsoft Enhanced RSA and AES Cryptographic Provider" };
                    Key = new RSACryptoServiceProvider(p);
                    Key.ImportParameters(keyParams);
                }

            }

            if (Key == null || Certificate == null) throw new ArgumentException("key and/or certificate still missing after p12 processing");
        }

        /// <summary>
        /// Transformuje enum do stringu.
        /// </summary>
        /// <param name="val">The value.</param>
        /// <returns></returns>
        protected string FormatPrvniZaslani(PrvniZaslaniEnum val)
        {
            return val == PrvniZaslaniEnum.Prvni ? "true" : "false";
        }

        /// <summary>
        /// Transformuje enum do stringu.
        /// </summary>
        /// <param name="val">The value.</param>
        /// <returns></returns>
        protected string FormatOvereni(OvereniEnum val)
        {
            return val == OvereniEnum.Overovaci ? "true" : "false";
        }

        /// <summary>
        /// Transformuje enum do stringu.
        /// </summary>
        /// <param name="val">The value.</param>
        /// <returns></returns>
        protected string FormatRezim(RezimEnum val)
        {
            return val == RezimEnum.Standardni ? "0" : "1";
        }
    }
}
