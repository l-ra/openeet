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
using System.Threading.Tasks;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using openeet_lite;

namespace UnitTests
{
    [TestClass]
    public class RegisterRequestTest
    {
        [TestMethod]
        public void BuilderTest()
        {
            Assert.IsNotNull(EetRegisterRequest.Builder());
        }

        [TestMethod]
        public void LoadP12Test()
        {
            var request = GenerateBasicRequestWithoutCertificate();

            PrivateObject obj = new PrivateObject(request);
            obj.Invoke("LoadP12", TestData._01000003, "eet");
            Assert.IsNotNull(request.Key);
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentException))]
        public void LoadP12BadArgumentsTest()
        {
            var request = GenerateBasicRequestWithoutCertificate();

            PrivateObject obj = new PrivateObject(request);
            obj.Invoke("LoadP12", TestData._01000003, null);
            Assert.IsNotNull(request.Key);
        }

        [TestMethod]
        public void FormatToBeSignedDataTest()
        {
            var request = GenerateBasicRequestWithoutCertificate();
            request.DatTrzby = DateTime.MinValue;
            var formatedData = request.FormatToBeSignedData();
            Assert.AreEqual(formatedData, "CZ1212121218|1|POKLADNA01|1|0001-01-01T00:00:00+01:00|100.00");
        }

        [TestMethod]
        public void FormatDateTest()
        {
            var request = GenerateBasicRequest();
            var dt = DateTime.Now;
            Assert.AreEqual(request.FormatDate(dt), dt.ToString("yyyy-MM-dd'T'HH:mm:sszzz"));
        }

        [TestMethod]
        public void FormatBkpTest()
        {
            var bkp = EetRegisterRequest.FormatBkp(new byte[] { 251, 180, 252, 81, 76, 130, 199, 228, 240, 128, 253, 136, 84, 201, 220, 178, 242, 100, 67, 231 });
            Assert.AreEqual(bkp, "FBB4FC51-4C82C7E4-F080FD88-54C9DCB2-F26443E7");
        }

        [TestMethod]
        public void ParseBkpTest()
        {
            var bkp = EetRegisterRequest.ParseBkp("FBB4FC51-4C82C7E4-F080FD88-54C9DCB2-F26443E7");
            Assert.IsTrue(bkp.SequenceEqual(new byte[] { 251, 180, 252, 81, 76, 130, 199, 228, 240, 128, 253, 136, 84, 201, 220, 178, 242, 100, 67, 231 }));
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentException))]
        public void ParseBkpFail1Test()
        {
            EetRegisterRequest.ParseBkp("FBB4FC51-4C82C7E4-F080FD88-54C9DCB2");
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentException))]
        public void ParseBkpFail2Test()
        {
            EetRegisterRequest.ParseBkp("FBB4XXXX-4C82C7E4-F080FD88-54C9DCB2-F26443E7");
        }

        [TestMethod]
        public void FormatAmountTest()
        {
            Assert.AreEqual(EetRegisterRequest.FormatAmount(5.0), "5.00");
        }

        [TestMethod]
        public void FormatPkpTest()
        {
            var pkpString = @"AA==";
            var pkpBytes = Convert.FromBase64String(pkpString);
            Assert.AreEqual(pkpString, EetRegisterRequest.FormatPkp(pkpBytes));
        }

        [TestMethod]
        public void ParsePkpTest()
        {
            var pkpString = @"AA==";
            var pkpBytes = Convert.FromBase64String(pkpString);
            Assert.IsTrue(pkpBytes.SequenceEqual(EetRegisterRequest.ParsePkp(pkpString)));
        }

        [TestMethod]
        public void RemoveUnusedPlaceholdersTest()
        {
            Assert.AreEqual(EetRegisterRequest.RemoveUnusedPlaceholders(" <Data celk_trzba=\"100.00\" cerp_zuct=\"${cerp_zuct}\">"), " <Data celk_trzba=\"100.00\">");
        }

        [TestMethod]
        public void FormatPrvniZaslaniTest()
        {
            var request = GenerateBasicRequestWithoutCertificate();

            PrivateObject obj = new PrivateObject(request);
            var result = obj.Invoke("FormatPrvniZaslani", PrvniZaslaniEnum.Prvni);
            Assert.AreEqual(result, "true");
            result = obj.Invoke("FormatPrvniZaslani", PrvniZaslaniEnum.Opakovane);
            Assert.AreEqual(result, "false");
        }

        [TestMethod]
        public void FormatOvereniTest()
        {
            var request = GenerateBasicRequestWithoutCertificate();

            PrivateObject obj = new PrivateObject(request);
            var result = obj.Invoke("FormatOvereni", OvereniEnum.Overovaci);
            Assert.AreEqual(result, "true");
            result = obj.Invoke("FormatOvereni", OvereniEnum.Produkcni);
            Assert.AreEqual(result, "false");
        }

        [TestMethod]
        public void FormatRezimTest()
        {
            var request = GenerateBasicRequestWithoutCertificate();

            PrivateObject obj = new PrivateObject(request);
            var result = obj.Invoke("FormatRezim", RezimEnum.Standardni);
            Assert.AreEqual(result, "0");
            result = obj.Invoke("FormatRezim", RezimEnum.Zjednoduseny);
            Assert.AreEqual(result, "1");
        }

        [TestMethod]
        public async Task SendRequestTest()
        {
            EetRegisterRequest request = new EetRequestBuilder()
            {
                DicPopl = "CZ1212121218",
                IdProvoz = "1",
                IdPokl = "POKLADNA01",
                PoradCis = "1",
                DatTrzby = DateTime.Now,
                CelkTrzba = 100.0,
                Rezim = 0,
                Pkcs12 = TestData._01000003,
                Pkcs12Password = "eet"
            }.Build();

            //for receipt printing in online mode
            string bkp = request.FormatBkp();
            if (bkp == null) throw new ApplicationException("BKP is null");

            //for receipt printing in offline mode
            string pkp = request.FormatPkp();
            if (pkp == null) throw new ApplicationException("PKP is null");
            //the receipt can be now stored for offline processing

            //try send
            string requestBody = request.GenerateSoapRequest();
            if (requestBody == null) throw new ApplicationException("SOAP request is null");
            string response = await request.SendRequestAsync(requestBody, "https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3");

            // TODO
            //zde by to chtelo dodelat kontrolu jestli prijata zprava nebyla zmenena, jestli souhlasi podpis zpravy
            //muzete to nekdo doplnit ?

            //extract FIK
            if (response == null) throw new ApplicationException("response is null");
            if (response.IndexOf("Potvrzeni fik=", StringComparison.Ordinal) < 0) throw new ApplicationException("FIK not found in the response");
        }

        /// <summary>
        /// Generates the basic request.
        /// </summary>
        /// <returns></returns>
        private EetRegisterRequest GenerateBasicRequestWithoutCertificate()
        {
            return new EetRequestBuilder
            {
                DicPopl = "CZ1212121218",
                IdProvoz = "1",
                IdPokl = "POKLADNA01",
                PoradCis = "1",
                DatTrzby = DateTime.Now,
                CelkTrzba = 100.0,
                Rezim = 0
            }.Build();
        }

        /// <summary>
        /// Generates the basic request.
        /// </summary>
        /// <returns></returns>
        private EetRegisterRequest GenerateBasicRequest()
        {
            return new EetRequestBuilder
            {
                DicPopl = "CZ1212121218",
                IdProvoz = "1",
                IdPokl = "POKLADNA01",
                PoradCis = "1",
                DatTrzby = DateTime.Now,
                CelkTrzba = 100.0,
                Rezim = 0,
                Pkcs12 = TestData._01000003,
                Pkcs12Password = "eet"
            }.Build();
        }
    }
}
