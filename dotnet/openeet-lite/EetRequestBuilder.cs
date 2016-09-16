using System;
using System.IO;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;

namespace openeet_lite
{
    /// <summary>
    /// Jedna se o prvni zaslani nebo opakovane zaslani...
    /// </summary>
    public enum PrvniZaslaniEnum
    {
        /// <summary>
        /// Prvni zaslani
        /// </summary>
        /// 
        Prvni = 1,

        /// <summary>
        /// Opakovane zaslani pro pripad, ze z prvniho neodesla odpoved
        /// </summary>
        Opakovane = 0
    }

    /// <summary>
    /// Jedna se o overovaci zpravu...
    /// </summary>
    public enum OvereniEnum
    {
        /// <summary>
        /// Je to overovaci zprava
        /// </summary>
        Overovaci = 1,

        /// <summary>
        /// The produkcni
        /// </summary>
        Produkcni = 0
    }

    /// <summary>
    /// Standardni rezim nebo zjednoduseny rezim.
    /// </summary>
    public enum RezimEnum
    {

        /// <summary>
        /// Standardni rezim
        /// </summary>
        Standardni = 0,

        /// <summary>
        /// Zjednoduseny rezim
        /// </summary>
        Zjednoduseny = 1
    }

    /// <summary>
    /// Request Builder
    /// </summary>
    public class EetRequestBuilder
    {

        #region Properties

        /// <summary>
        /// Gets or sets the byte form PKCS12 certificate.
        /// </summary>
        /// <value>
        /// The PKCS12.
        /// </value>
        public byte[] Pkcs12 { get; set; }

        /// <summary>
        /// Gets or sets the PKCS12 password.
        /// </summary>
        /// <value>
        /// The PKCS12 password.
        /// </value>
        public string Pkcs12Password { get; set; }

        /// <summary>
        /// Gets or sets the RSA Key
        /// </summary>
        /// <value>
        /// The key.
        /// </value>
        public RSACryptoServiceProvider Key { get; set; }

        /// <summary>
        /// Gets or sets the X509 Certificate.
        /// </summary>
        /// <value>
        /// The certificate.
        /// </value>
        public X509Certificate2 Certificate { get; set; }

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
        /// Gets or sets Celkova castka nepodlehajici zdaneni v Kc.
        /// </summary>
        /// <value>
        /// Celkova castka nepodlehajici zdaneni v Kc.
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
        /// Gets or sets the Celkova DPH v zakladni sazbe v Kc.
        /// </summary>
        /// <value>
        /// Celkova DPH v zakladni sazbe v Kc.
        /// </value>
        public double? Dan1 { get; set; }

        /// <summary>
        /// Gets or sets Celkovy zaklad dane s prvni snizenou sazbou DPH v Kc.
        /// </summary>
        /// <value>
        /// Celkovy zaklad dane s prvni snizenou sazbou DPH v Kc.
        /// </value>
        public double? ZaklDan2 { get; set; }

        /// <summary>
        /// Gets or sets the Celkova DPH v prvni snizene sazbe v Kc.
        /// </summary>
        /// <value>
        /// Celkova DPH v prvni snizene sazbe v Kc.
        /// </value>
        public double? Dan2 { get; set; }

        /// <summary>
        /// Gets or sets Celkovy zaklad dane s druhou snizenou sazbou DPH v Kc.
        /// </summary>
        /// <value>
        /// Celkovy zaklad dane s druhou snizenou sazbou DPH v Kc.
        /// </value>
        public double? ZaklDan3 { get; set; }

        /// <summary>
        /// Gets or sets the Celkova DPH v druhe snizene sazbe v Kc.
        /// </summary>
        /// <value>
        /// Celkova DPH v druhe snizene sazbe v Kc.
        /// </value>
        public double? Dan3 { get; set; }

        /// <summary>
        /// Gets or sets Celkova castka DPH pro cestovni sluzbu v Kc.
        /// </summary>
        /// <value>
        /// Celkova castka DPH pro cestovni sluzbu v Kc.
        /// </value>
        public double? CestSluz { get; set; }

        /// <summary>
        /// Gets or sets Celkovy zaklad dane v zakladni sazbe DPH z <c>Pouziteho</c> zbozi v Kc.
        /// </summary>
        /// <value>
        /// Celkovy zaklad dane v zakladni sazbe DPH z <c>Pouziteho</c> zbozi v Kc.
        /// </value>
        public double? PouzitZboz1 { get; set; }

        /// <summary>
        /// Gets or sets Celkovy zaklad dane v prvni snizene sazbe DPH z <c>Pouziteho</c> zbozi v Kc.
        /// </summary>
        /// <value>
        /// Celkovy zaklad dane v prvni snizene sazbe DPH z <c>Pouziteho</c> zbozi v Kc.
        /// </value>
        public double? PouzitZboz2 { get; set; }

        /// <summary>
        /// Gets or sets Celkovy zaklad dane ve druhe snizene sazbe DPH z <c>Pouziteho</c> zbozi v Kc.
        /// </summary>
        /// <value>
        /// Celkovy zaklad dane ve druhe snizene sazbe DPH z <c>Pouziteho</c> zbozi v Kc.
        /// </value>
        public double? PouzitZboz3 { get; set; }

        /// <summary>
        /// Gets or sets Celkova castka plateb urcena k naslednemu cerpani nebo zuctovani v Kc.
        /// </summary>
        /// <value>
        /// Celkova castka plateb urcena k naslednemu cerpani nebo zuctovani v Kc.
        /// </value>
        public double? UrcenoCerpZuct { get; set; }

        /// <summary>
        /// Gets or sets Celkova castka plateb, ktere jsou naslednym cerpanim nebo zuctovanim platby v Kc.
        /// </summary>
        /// <value>
        /// Celkova castka plateb, ktere jsou naslednym cerpanim nebo zuctovanim platby v Kc.
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

        #endregion

        #region Public Methods

        /// <summary>
        /// Sets X509 certificate.
        /// </summary>
        /// <param name="val">X509 certificate.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetCertificate(X509Certificate2 val)
        {
            Certificate = val;
            return this;
        }

        /// <summary>
        /// Sets RSA key.
        /// </summary>
        /// <param name="val">RSA key.</param>
        /// <returns>This builder.</returns>
        public EetRequestBuilder SetKey(RSACryptoServiceProvider val)
        {
            Key = val;
            return this;
        }

        /// <summary>
        /// Sets Datum a cas odeslani zpravy na server.
        /// </summary>
        /// <param name="val">Datum a cas odeslani zpravy na server.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetDatOdesl(DateTime val)
        {
            DatOdesl = val;
            return this;
        }

        /// <summary>
        /// Sets value indicating whether is zprava odeslana poprve, nebo se jedna o dalsi odeslani. Default je Prvni.
        /// </summary>
        /// <param name="val">Value indicating whether is zprava odeslana poprve, nebo se jedna o dalsi odeslani.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetPrvniZaslani(PrvniZaslaniEnum val)
        {
            PrvniZaslani = val;
            return this;
        }

        /// <summary>
        /// Sets value indicating whether is zprava odeslana poprve, nebo se jedna o dalsi odeslani. Default je Prvni.
        /// </summary>
        /// <param name="val">Value indicating whether is zprava odeslana poprve, nebo se jedna o dalsi odeslani.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetPrvniZaslani(bool val)
        {
            PrvniZaslani = val ? PrvniZaslaniEnum.Prvni : PrvniZaslaniEnum.Opakovane;
            return this;
        }

        /// <summary>
        /// Sets UUID zpravy. Identifikator zpravy, ne e-trzbu. Default is Randomly generated GUID.
        /// </summary>
        /// <param name="val">Identifikator zpravy, ne e-trzbu. Default is Randomly generated GUID.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetUuidZpravy(Guid val)
        {
            UuidZpravy = val;
            return this;
        }

        /// <summary>
        /// Sets UUID zpravy. Identifikator zpravy, ne e-trzbu. Default is Randomly generated GUID.
        /// </summary>
        /// <param name="val">Identifikator zpravy, ne e-trzbu. Default is Randomly generated GUID.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetUuidZpravy(string val)
        {
            UuidZpravy = new Guid(val);
            return this;
        }

        /// <summary>
        /// Sets Value indicating whether is zprava overovaciho typu nebo jestli se jedna o ostrou zpravu. Default je Produkcni.
        /// </summary>
        /// <param name="val">Value indicating whether is zprava overovaciho typu nebo jestli se jedna o ostrou zpravu.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetOvereni(OvereniEnum val)
        {
            Overeni = val;
            return this;
        }

        /// <summary>
        /// Sets Value indicating whether is zprava overovaciho typu nebo jestli se jedna o ostrou zpravu. Default je Produkcni.
        /// </summary>
        /// <param name="val">Value indicating whether is zprava overovaciho typu nebo jestli se jedna o ostrou zpravu.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetOvereni(bool val)
        {
            Overeni = val ? OvereniEnum.Overovaci : OvereniEnum.Produkcni;
            return this;
        }

        /// <summary>
        /// Sets DIC poplatnika, ktery ke kteremu se ma uctenka zapocitat.
        /// </summary>
        /// <param name="val">DIC poplatnika, ktery ke kteremu se ma uctenka zapocitat.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetDicPopl(string val)
        {
            DicPopl = val;
            return this;
        }

        /// <summary>
        /// Sets DIC poverene osoby, ktera odesila nahradou za poplatnika (DIC poplatnika).
        /// </summary>
        /// <param name="val">DIC poverene osoby, ktera odesila nahradou za poplatnika (DIC poplatnika).</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetDicPoverujiciho(string val)
        {
            DicPoverujiciho = val;
            return this;
        }

        /// <summary>
        /// Sets Identifikace provozovny, ktera byla pridelena portalem EET.
        /// </summary>
        /// <param name="val">Identifikace provozovny, ktera byla pridelena portalem EET.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetIdProvoz(string val)
        {
            IdProvoz = val;
            return this;
        }

        /// <summary>
        /// Sets Unikatni ID zarizeni, ktere odesila uctenku online.
        /// </summary>
        /// <param name="val">Unikatni ID zarizeni, ktere odesila uctenku online.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetIdPokl(string val)
        {
            IdPokl = val;
            return this;
        }

        /// <summary>
        /// Sets poradove cislo dokladu.
        /// </summary>
        /// <param name="val">Poradove cislo dokladu.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetPoradCis(string val)
        {
            PoradCis = val;
            return this;
        }

        /// <summary>
        /// Sets datum provedeni trzby.
        /// </summary>
        /// <param name="val">Datum provedeni trzby.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetDatTrzby(DateTime val)
        {
            DatTrzby = val;
            return this;
        }

        /// <summary>
        /// Sets datum provedeni trzby.
        /// </summary>
        /// <param name="val">Datum provedeni trzby.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetDatTrzbys(string val)
        { // v delphi lze vlozit datum v textovem tvaru
            DatTrzby = EetRegisterRequest.ParseDate(val);
            return this;
        }


        /// <summary>
        /// Sets Celkova castka trzby v Kc.
        /// </summary>
        /// <param name="val">Celkova castka trzby v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetCelkTrzba(double val)
        {
            CelkTrzba = val;
            return this;
        }

        /// <summary>
        /// Sets Celkova castka trzby v Kc.
        /// </summary>
        /// <param name="val">Celkova castka trzby v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetCelkTrzba(string val)
        {
            CelkTrzba = double.Parse(val);
            return this;
        }

        /// <summary>
        /// Sets Celkova castka nepodlehajici zdaneni v Kc.
        /// </summary>
        /// <param name="val">Celkova castka nepodlehajici zdaneni v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetZaklNepodlDph(double val)
        {
            ZaklNepodlDph = val;
            return this;
        }

        /// <summary>
        /// Sets Celkova castka nepodlehajici zdaneni v Kc.
        /// </summary>
        /// <param name="val">Celkova castka nepodlehajici zdaneni v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetZaklNepodlDph(string val)
        {
            ZaklNepodlDph = double.Parse(val);
            return this;
        }

        /// <summary>
        /// Sets Celkovy zaklad dane se zakladni sazbou DPH v Kc.
        /// </summary>
        /// <param name="val">Celkovy zaklad dane se zakladni sazbou DPH v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetZaklDan1(double val)
        {
            ZaklDan1 = val;
            return this;
        }

        /// <summary>
        /// Sets Celkovy zaklad dane se zakladni sazbou DPH v Kc.
        /// </summary>
        /// <param name="val">Celkovy zaklad dane se zakladni sazbou DPH v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetZaklDan1(string val)
        {
            ZaklDan1 = double.Parse(val);
            return this;
        }

        /// <summary>
        /// Sets Celkova DPH v zakladni sazbe v Kc.
        /// </summary>
        /// <param name="val">Celkova DPH v zakladni sazbe v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetDan1(double val)
        {
            Dan1 = val;
            return this;
        }

        /// <summary>
        /// Sets Celkova DPH v zakladni sazbe v Kc.
        /// </summary>
        /// <param name="val">Celkova DPH v zakladni sazbe v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetDan1(string val)
        {
            Dan1 = double.Parse(val);
            return this;
        }

        /// <summary>
        /// Sets Celkovy zaklad dane s prvni snizenou sazbou DPH v Kc.
        /// </summary>
        /// <param name="val">Celkovy zaklad dane s prvni snizenou sazbou DPH v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetZaklDan2(double val)
        {
            ZaklDan2 = val;
            return this;
        }

        /// <summary>
        /// Sets Celkovy zaklad dane s prvni snizenou sazbou DPH v Kc.
        /// </summary>
        /// <param name="val">Celkovy zaklad dane s prvni snizenou sazbou DPH v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetZaklDan2(string val)
        {
            ZaklDan2 = double.Parse(val);
            return this;
        }

        /// <summary>
        /// Sets Celkova DPH v prvni snizene sazbe v Kc.
        /// </summary>
        /// <param name="val">Celkova DPH v prvni snizene sazbe v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetDan2(double val)
        {
            Dan2 = val;
            return this;
        }

        /// <summary>
        /// Sets Celkova DPH v prvni snizene sazbe v Kc.
        /// </summary>
        /// <param name="val">Celkova DPH v prvni snizene sazbe v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetDan2(string val)
        {
            Dan2 = double.Parse(val);
            return this;
        }

        /// <summary>
        /// Sets Celkovy zaklad dane s druhou snizenou sazbou DPH v Kc.
        /// </summary>
        /// <param name="val">Celkovy zaklad dane s druhou snizenou sazbou DPH v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetZaklDan3(double val)
        {
            ZaklDan3 = val;
            return this;
        }

        /// <summary>
        /// Sets Celkovy zaklad dane s druhou snizenou sazbou DPH v Kc.
        /// </summary>
        /// <param name="val">Celkovy zaklad dane s druhou snizenou sazbou DPH v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetZaklDan3(string val)
        {
            ZaklDan3 = double.Parse(val);
            return this;
        }

        /// <summary>
        /// Sets Celkova DPH v druhe snizene sazbe v Kc.
        /// </summary>
        /// <param name="val">Celkova DPH v druhe snizene sazbe v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetDan3(double val)
        {
            Dan3 = val;
            return this;
        }

        /// <summary>
        /// Sets Celkova DPH v druhe snizene sazbe v Kc.
        /// </summary>
        /// <param name="val">Celkova DPH v druhe snizene sazbe v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetDan3(string val)
        {
            Dan3 = double.Parse(val);
            return this;
        }

        /// <summary>
        /// Sets Celkova castka DPH pro cestovni sluzbu v Kc.
        /// </summary>
        /// <param name="val">Celkova castka DPH pro cestovni sluzbu v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetCestSluz(double val)
        {
            CestSluz = val;
            return this;
        }

        /// <summary>
        /// Sets Celkova castka DPH pro cestovni sluzbu v Kc.
        /// </summary>
        /// <param name="val">Celkova castka DPH pro cestovni sluzbu v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetCestSluz(string val)
        {
            CestSluz = double.Parse(val);
            return this;
        }

        /// <summary>
        /// Sets Celkovy zaklad dane v zakladni sazbe DPH z <c>Pouziteho</c> zbozi v Kc.
        /// </summary>
        /// <param name="val">Celkovy zaklad dane v zakladni sazbe DPH z <c>Pouziteho</c> zbozi v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetPouzitZboz1(double val)
        {
            PouzitZboz1 = val;
            return this;
        }

        /// <summary>
        /// Sets Celkovy zaklad dane v zakladni sazbe DPH z <c>Pouziteho</c> zbozi v Kc.
        /// </summary>
        /// <param name="val">Celkovy zaklad dane v zakladni sazbe DPH z <c>Pouziteho</c> zbozi v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetPouzitZboz1(string val)
        {
            PouzitZboz1 = double.Parse(val);
            return this;
        }

        /// <summary>
        /// Sets Celkovy zaklad dane v prvni snizene sazbe DPH z <c>Pouziteho</c> zbozi v Kc.
        /// </summary>
        /// <param name="val">Celkovy zaklad dane v prvni snizene sazbe DPH z <c>Pouziteho</c> zbozi v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetPouzitZboz2(double val)
        {
            PouzitZboz2 = val;
            return this;
        }

        /// <summary>
        /// Sets Celkovy zaklad dane v prvni snizene sazbe DPH z <c>Pouziteho</c> zbozi v Kc.
        /// </summary>
        /// <param name="val">Celkovy zaklad dane v prvni snizene sazbe DPH z <c>Pouziteho</c> zbozi v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetPouzitZboz2(string val)
        {
            PouzitZboz2 = double.Parse(val);
            return this;
        }

        /// <summary>
        /// Sets Celkovy zaklad dane ve druhe snizene sazbe DPH z <c>Pouziteho</c> zbozi v Kc.
        /// </summary>
        /// <param name="val">Celkovy zaklad dane ve druhe snizene sazbe DPH z <c>Pouziteho</c> zbozi v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetPouzitZboz3(double val)
        {
            PouzitZboz3 = val;
            return this;
        }

        /// <summary>
        /// Sets Celkovy zaklad dane ve druhe snizene sazbe DPH z <c>Pouziteho</c> zbozi v Kc.
        /// </summary>
        /// <param name="val">Celkovy zaklad dane ve druhe snizene sazbe DPH z <c>Pouziteho</c> zbozi v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetPouzitZboz3(string val)
        {
            PouzitZboz3 = double.Parse(val);
            return this;
        }

        /// <summary>
        /// Sets Celkova castka plateb urcena k naslednemu cerpani nebo zuctovani v Kc.
        /// </summary>
        /// <param name="val">Celkova castka plateb urcena k naslednemu cerpani nebo zuctovani v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetUrcenoCerpZuct(double val)
        {
            UrcenoCerpZuct = val;
            return this;
        }

        /// <summary>
        /// Sets Celkova castka plateb urcena k naslednemu cerpani nebo zuctovani v Kc.
        /// </summary>
        /// <param name="val">Celkova castka plateb urcena k naslednemu cerpani nebo zuctovani v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetUrcenoCerpZuct(string val)
        {
            UrcenoCerpZuct = double.Parse(val);
            return this;
        }

        /// <summary>
        /// Sets Celkova castka plateb, ktere jsou naslednym cerpanim nebo zuctovanim platby v Kc.
        /// </summary>
        /// <param name="val">Celkova castka plateb, ktere jsou naslednym cerpanim nebo zuctovanim platby v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetCerpZuct(double val)
        {
            CerpZuct = val;
            return this;
        }

        /// <summary>
        /// Sets Celkova castka plateb, ktere jsou naslednym cerpanim nebo zuctovanim platby v Kc.
        /// </summary>
        /// <param name="val">Celkova castka plateb, ktere jsou naslednym cerpanim nebo zuctovanim platby v Kc.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetCerpZuct(string val)
        {
            CerpZuct = double.Parse(val);
            return this;
        }

        /// <summary>
        /// Sets Rezim trzby. Standardni bezny rezim nebo zjednoduseny rezim. Default je Standardni.
        /// </summary>
        /// <param name="val">Rezim trzby. Standardni bezny rezim nebo zjednoduseny rezim.</param>
        /// <returns></returns>
        public EetRequestBuilder SetRezim(RezimEnum val)
        {
            Rezim = val;
            return this;
        }

        /// <summary>
        /// Sets Rezim trzby. Standardni bezny rezim nebo zjednoduseny rezim. Default je Standardni.
        /// </summary>
        /// <param name="val">Rezim trzby. Standardni bezny rezim nebo zjednoduseny rezim.</param>
        /// <returns></returns>
        public EetRequestBuilder SetRezim(int val)
        {
            if (val == 0)
                return SetRezim(RezimEnum.Standardni);
            if (val == 1)
                return SetRezim(RezimEnum.Zjednoduseny);
            throw new ArgumentException("only 0 and 1 is allowed as int value");
        }

        /// <summary>
        /// Sets Rezim trzby. Standardni bezny rezim nebo zjednoduseny rezim. Default je Standardni.
        /// </summary>
        /// <param name="val">Rezim trzby. Standardni bezny rezim nebo zjednoduseny rezim.</param>
        /// <returns></returns>
        public EetRequestBuilder SetRezim(string val)
        {
            return SetRezim(int.Parse(val));
        }

        /// <summary>
        /// Sets Bezpecnostni kod poplatnika. Jedna se o hash Kodu PKP. Computed when pkp available during build() call.
        /// </summary>
        /// <param name="val">The BKP.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetBkp(byte[] val)
        {
            Bkp = val;
            return this;
        }

        /// <summary>
        /// Sets Bezpecnostni kod poplatnika. Jedna se o hash Kodu PKP. Computed when pkp available during build() call.
        /// Parses string according to EET spec e.g 17796128-AED2BB9E-2301FF97-0A75656A-DF2B011D
        /// </summary>
        /// <param name="val">The BKP.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetBkp(string val)
        {// v delphi lze vlozit bkp v textovem tvaru
            Bkp = EetRegisterRequest.ParseBkp(val);
            return this;
        }

        /// <summary>
        /// Sets Podpisovy kod poplatnika. Jedna se o podpis vybranych dat. Computed when private key available during build() call.
        /// </summary>
        /// <param name="val">The PKP.</param>
        /// <returns></returns>
        public EetRequestBuilder SetPkp(byte[] val)
        {
            Pkp = val;
            return this;
        }

        /// <summary>
        /// Sets Podpisovy kod poplatnika. Jedna se o podpis vybranych dat. Computed when private key available during build() call.
        /// </summary>
        /// <param name="val">The PKP.</param>
        /// <returns></returns>
        public EetRequestBuilder SetPkp(string val)
        {// v delphi lze vlozit pkp v textovem tvaru
            Pkp = EetRegisterRequest.ParsePkp(val);
            return this;
        }

        /// <summary>
        /// Sets the PKCS12 from filename.
        /// </summary>
        /// <param name="p12Filename">The Pkcs12 filename.</param>
        /// <returns>This builder</returns>
        public EetRequestBuilder SetPkcs12(string p12Filename)
        {// v delphi lze vlozit nazev souboru 'xxxxxx.p12'
            return SetPkcs12(File.ReadAllBytes(p12Filename));
        }

        /// <summary>
        /// Sets the PKCS12.
        /// </summary>
        /// <param name="p12Bytes">The P12 bytes.</param>
        /// <returns></returns>
        public EetRequestBuilder SetPkcs12(byte[] p12Bytes)
        {
            Pkcs12 = p12Bytes;
            return this;
        }

        /// <summary>
        /// Sets the PKCS12 password.
        /// </summary>
        /// <param name="password">The password.</param>
        /// <returns></returns>
        public EetRequestBuilder SetPkcs12Password(string password)
        {
            Pkcs12Password = password;
            return this;
        }

        /// <summary>
        /// Builds this instance.
        /// </summary>
        /// <returns></returns>
        public EetRegisterRequest Build()
        {
            return new EetRegisterRequest(this);
        }

        #endregion

    }
}
