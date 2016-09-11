package openeet.lite;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class EetResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String errCode;
	private String errText;
	private String uuid;
	private String bkp;
	private String time;
	private String fik;
	private String test;
	private String warnings; // all warnings joined into one string as [code1]
								// text1\n[code2] text2...

	public EetResponse(String response) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		saxParser.parse(response, new EetHandler());
	}

	public EetResponse(File file) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		saxParser.parse(file, new EetHandler());
	}

	public boolean isError() {
		return errCode != null;
	}

	public boolean hasWarnings() {
		return warnings != null;
	}

	private class EetHandler extends DefaultHandler {
		private String element;

		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			element = qName;
			if (element.equals("eet:Hlavicka")) {
				uuid = attributes.getValue("uuid_zpravy");
				bkp = attributes.getValue("bkp");
				time = attributes.getValue("dat_prij");
				String h = attributes.getValue("dat_odmit");
				if (h != null)
					time = h;
			} else if (element.equals("eet:Potvrzeni")) {
				fik = attributes.getValue("fik");
				test = attributes.getValue("test");
			} else if (element.equals("eet:Chyba")) {
				errCode = attributes.getValue("kod");
				test = attributes.getValue("test");
			} else if (element.equals("eet:Varovani")) {
				String code = attributes.getValue("kod_varov");
				code = "[" + code + "]";
				if (warnings == null)
					warnings = code;
				else
					warnings += "\n" + code;
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			element = "";
		}

		public void characters(char ch[], int start, int length) throws SAXException {
			if (element.equals("eet:Chyba"))
				errText = createPureString(ch, start, length);
			else if (element.equals("eet:Varovani"))
				warnings += " " + createPureString(ch, start, length);
		}

		private String createPureString(char ch[], int start, int length) {
			String h = new String(ch, start, length);
			h = h.replace('\n', ' ');
			h = h.trim();
			return h;
		}
	}

	public static void main(String argv[]) {
		try {
			EetResponse r = new EetResponse(new File("response_test.xml"));
			if (r.isError()) {
				System.out.println("Err code: " + r.errCode);
				System.out.println("Err text: " + r.errText);
			}
			System.out.println("Time: " + r.time);
			System.out.println("Uuid: " + r.uuid);
			System.out.println("Bkp: " + r.bkp);
			System.out.println("Fik: " + r.fik);
			System.out.println("Test: " + r.test);
			if (r.hasWarnings())
				System.out.println("Warnings:\n" + r.warnings);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
