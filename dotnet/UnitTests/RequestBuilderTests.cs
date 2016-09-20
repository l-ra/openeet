/*
 * Copyright 2016 Jakub Cermoch and other contributors as 
 * indicated by the @author tags.
 * Upravil Jakub Cermoch
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
using System.Linq;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using openeet_lite;

namespace UnitTests
{
    [TestClass]
    public class RequestBuilderTests
    {
        /// <summary>
        /// Test sets methods.
        /// </summary>
        [TestMethod]
        [DeploymentItem("TestData/SomeText.txt")]
        public void SetMethodsTests()
        {
            var dt = DateTime.Now;
            dt = dt.AddTicks(-(dt.Ticks % TimeSpan.TicksPerSecond)); // remove millisecons
            var guid = Guid.NewGuid();
            var bytes = new byte[] { 23, 121, 97, 40, 174, 210, 187, 158, 35, 1, 255, 151, 10, 117, 101, 106, 223, 43, 1, 29 };
            var bytesString = "17796128-AED2BB9E-2301FF97-0A75656A-DF2B011D";
            var pkpString = @"AA==";
            var pkpBytes = Convert.FromBase64String(pkpString);

            EetRequestBuilder builder = new EetRequestBuilder();

            Assert.IsNull(builder.Certificate);
            Assert.AreSame(builder, builder.SetCertificate(new X509Certificate2()));
            Assert.IsNotNull(builder.Certificate);

            Assert.IsNull(builder.Key);
            Assert.AreSame(builder, builder.SetKey(new RSACryptoServiceProvider()));
            Assert.IsNotNull(builder.Key);

            Assert.AreSame(builder, builder.SetDatOdesl(dt));
            Assert.AreEqual(builder.DatOdesl, dt);
            builder.SetDatOdesl(default(DateTime));
            Assert.AreSame(builder, builder.SetDatOdesl(dt.ToString("yyyy-MM-dd'T'HH:mm:sszzz")));
            Assert.AreEqual(builder.DatOdesl, dt);

            Assert.AreEqual(builder.PrvniZaslani, PrvniZaslaniEnum.Prvni);      // Default value
            Assert.AreSame(builder, builder.SetPrvniZaslani(PrvniZaslaniEnum.Opakovane));
            Assert.AreEqual(builder.PrvniZaslani, PrvniZaslaniEnum.Opakovane);
            Assert.AreSame(builder, builder.SetPrvniZaslani(true));
            Assert.AreEqual(builder.PrvniZaslani, PrvniZaslaniEnum.Prvni);

            Assert.AreSame(builder, builder.SetUuidZpravy(guid));
            Assert.AreEqual(builder.UuidZpravy, guid);

            Assert.AreSame(builder, builder.SetUuidZpravy(guid.ToString()));
            Assert.AreEqual(builder.UuidZpravy, guid);

            Assert.AreEqual(builder.Overeni, OvereniEnum.Produkcni);            // Default value
            Assert.AreSame(builder, builder.SetOvereni(OvereniEnum.Overovaci));
            Assert.AreEqual(builder.Overeni, OvereniEnum.Overovaci);
            Assert.AreSame(builder, builder.SetOvereni(false));
            Assert.AreEqual(builder.Overeni, OvereniEnum.Produkcni);

            Assert.AreSame(builder, builder.SetDicPopl("dicPopl"));
            Assert.AreEqual(builder.DicPopl, "dicPopl");

            Assert.AreSame(builder, builder.SetDicPoverujiciho("dicPover"));
            Assert.AreEqual(builder.DicPoverujiciho, "dicPover");

            Assert.AreSame(builder, builder.SetIdProvoz("idProvoz"));
            Assert.AreEqual(builder.IdProvoz, "idProvoz");

            Assert.AreSame(builder, builder.SetIdPokl("idPokl"));
            Assert.AreEqual(builder.IdPokl, "idPokl");

            Assert.AreSame(builder, builder.SetPoradCis("poradCisl"));
            Assert.AreEqual(builder.PoradCis, "poradCisl");

            Assert.AreSame(builder, builder.SetDatTrzby(dt));
            Assert.AreEqual(builder.DatTrzby, dt);
            builder.SetDatOdesl(default(DateTime));
            Assert.AreSame(builder, builder.SetDatTrzby(dt.ToString("yyyy-MM-dd'T'HH:mm:sszzz")));
            Assert.AreEqual(builder.DatTrzby, dt);

            Assert.AreSame(builder, builder.SetCelkTrzba(5.0));
            Assert.AreEqual(builder.CelkTrzba, 5.0);
            Assert.AreSame(builder, builder.SetCelkTrzba("1,0"));
            Assert.AreEqual(builder.CelkTrzba, 1.0);

            Assert.AreSame(builder, builder.SetZaklNepodlDph(5.0));
            Assert.AreEqual(builder.ZaklNepodlDph, 5.0);
            Assert.AreSame(builder, builder.SetZaklNepodlDph("1,0"));
            Assert.AreEqual(builder.ZaklNepodlDph, 1.0);

            Assert.AreSame(builder, builder.SetZaklDan1(5.0));
            Assert.AreEqual(builder.ZaklDan1, 5.0);
            Assert.AreSame(builder, builder.SetZaklDan1("1,0"));
            Assert.AreEqual(builder.ZaklDan1, 1.0);

            Assert.AreSame(builder, builder.SetDan1(5.0));
            Assert.AreEqual(builder.Dan1, 5.0);
            Assert.AreSame(builder, builder.SetDan1("1,0"));
            Assert.AreEqual(builder.Dan1, 1.0);

            Assert.AreSame(builder, builder.SetZaklDan2(5.0));
            Assert.AreEqual(builder.ZaklDan2, 5.0);
            Assert.AreSame(builder, builder.SetZaklDan2("1,0"));
            Assert.AreEqual(builder.ZaklDan2, 1.0);

            Assert.AreSame(builder, builder.SetDan2(5.0));
            Assert.AreEqual(builder.Dan2, 5.0);
            Assert.AreSame(builder, builder.SetDan2("1,0"));
            Assert.AreEqual(builder.Dan2, 1.0);

            Assert.AreSame(builder, builder.SetZaklDan2(5.0));
            Assert.AreEqual(builder.ZaklDan2, 5.0);
            Assert.AreSame(builder, builder.SetZaklDan2("1,0"));
            Assert.AreEqual(builder.ZaklDan2, 1.0);

            Assert.AreSame(builder, builder.SetDan3(5.0));
            Assert.AreEqual(builder.Dan3, 5.0);
            Assert.AreSame(builder, builder.SetDan3("1,0"));
            Assert.AreEqual(builder.Dan3, 1.0);

            Assert.AreSame(builder, builder.SetCestSluz(5.0));
            Assert.AreEqual(builder.CestSluz, 5.0);
            Assert.AreSame(builder, builder.SetCestSluz("1,0"));
            Assert.AreEqual(builder.CestSluz, 1.0);

            Assert.AreSame(builder, builder.SetPouzitZboz1(5.0));
            Assert.AreEqual(builder.PouzitZboz1, 5.0);
            Assert.AreSame(builder, builder.SetPouzitZboz1("1,0"));
            Assert.AreEqual(builder.PouzitZboz1, 1.0);

            Assert.AreSame(builder, builder.SetPouzitZboz2(5.0));
            Assert.AreEqual(builder.PouzitZboz2, 5.0);
            Assert.AreSame(builder, builder.SetPouzitZboz2("1,0"));
            Assert.AreEqual(builder.PouzitZboz2, 1.0);

            Assert.AreSame(builder, builder.SetPouzitZboz3(5.0));
            Assert.AreEqual(builder.PouzitZboz3, 5.0);
            Assert.AreSame(builder, builder.SetPouzitZboz3("1,0"));
            Assert.AreEqual(builder.PouzitZboz3, 1.0);

            Assert.AreSame(builder, builder.SetUrcenoCerpZuct(5.0));
            Assert.AreEqual(builder.UrcenoCerpZuct, 5.0);
            Assert.AreSame(builder, builder.SetUrcenoCerpZuct("1,0"));
            Assert.AreEqual(builder.UrcenoCerpZuct, 1.0);

            Assert.AreSame(builder, builder.SetCerpZuct(5.0));
            Assert.AreEqual(builder.CerpZuct, 5.0);
            Assert.AreSame(builder, builder.SetCerpZuct("1,0"));
            Assert.AreEqual(builder.CerpZuct, 1.0);

            Assert.AreEqual(builder.Rezim, RezimEnum.Standardni);       // Default value
            Assert.AreSame(builder, builder.SetRezim(RezimEnum.Zjednoduseny));
            Assert.AreEqual(builder.Rezim, RezimEnum.Zjednoduseny);
            Assert.AreSame(builder, builder.SetRezim("0"));
            Assert.AreEqual(builder.Rezim, RezimEnum.Standardni);

            Assert.AreSame(builder, builder.SetBkp(bytes));
            Assert.IsTrue(builder.Bkp.SequenceEqual(bytes));
            Assert.AreSame(builder, builder.SetBkp("00000000-00000000-00000000-00000000-00000000"));
            Assert.AreSame(builder, builder.SetBkp(bytesString));
            Assert.IsTrue(builder.Bkp.SequenceEqual(bytes));

            Assert.AreSame(builder, builder.SetPkp(bytes));
            Assert.IsTrue(builder.Pkp.SequenceEqual(bytes));
            Assert.AreSame(builder, builder.SetPkp("BB=="));
            Assert.AreSame(builder, builder.SetPkp(pkpString));
            Assert.IsTrue(builder.Pkp.SequenceEqual(pkpBytes));

            Assert.AreSame(builder, builder.SetPkcs12("SomeText.txt"));
            Assert.IsTrue(builder.Pkcs12.SequenceEqual(new byte[] { 239, 187, 191, 48 }));
            Assert.AreSame(builder, builder.SetPkcs12(bytes));
            Assert.IsTrue(builder.Pkcs12.SequenceEqual(bytes));

            Assert.AreSame(builder, builder.SetPkcs12Password(pkpString));
            Assert.AreEqual(builder.Pkcs12Password, pkpString);
        }

        /// <summary>
        /// Test rezim set with bad value.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentException))]
        public void InvalidIntRezimTests()
        {
            var builder = new EetRequestBuilder();
            builder.SetRezim(2);
        }

        /// <summary>
        /// Test build method.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentException))]
        public void BuildTest()
        {
            EetRegisterRequest request = new EetRequestBuilder()
            {
                DicPopl = "CZ1212121218",
                IdProvoz = "1",
                IdPokl = "POKLADNA01",
                PoradCis = "1",
                DatTrzby = DateTime.Now,
                CelkTrzba = 100.0,
                Rezim = RezimEnum.Standardni,
                Pkcs12 = TestData._01000003,
                Pkcs12Password = "eet",
                CerpZuct = 1,
                CestSluz = 2,
                Dan1 = 3,
                Dan2 = 4,
                Dan3 = 5,
                DatOdesl = DateTime.Now,
                DicPoverujiciho = "CZ1212121219",
                Overeni = OvereniEnum.Overovaci,
                PouzitZboz1 = 6,
                PouzitZboz2 = 7,
                PouzitZboz3 = 8,
                PrvniZaslani = PrvniZaslaniEnum.Opakovane,
                UrcenoCerpZuct = 9,
                ZaklDan1 = 10,
                ZaklDan2 = 11,
                ZaklDan3 = 12,
                ZaklNepodlDph = 13
            }.Build();

            Assert.IsNotNull(request.Certificate);

            new EetRequestBuilder()
            {
                DicPopl = "CZ1212121218",
                IdProvoz = "1",
                IdPokl = "POKLADNA01",
                PoradCis = "1",
                DatTrzby = DateTime.Now,
                CelkTrzba = 100.0,
                Rezim = RezimEnum.Standardni,
                Pkcs12 = TestData._01000003,
                CerpZuct = 1,
                CestSluz = 2,
                Dan1 = 3,
                Dan2 = 4,
                Dan3 = 5,
                DatOdesl = DateTime.Now,
                DicPoverujiciho = "CZ1212121219",
                Overeni = OvereniEnum.Overovaci,
                PouzitZboz1 = 6,
                PouzitZboz2 = 7,
                PouzitZboz3 = 8,
                PrvniZaslani = PrvniZaslaniEnum.Opakovane,
                UrcenoCerpZuct = 9,
                ZaklDan1 = 10,
                ZaklDan2 = 11,
                ZaklDan3 = 12,
                ZaklNepodlDph = 13
            }.Build();
        }
    }
}
