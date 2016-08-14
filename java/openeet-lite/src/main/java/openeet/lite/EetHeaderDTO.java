package openeet.lite;


/**
 * DTO for sale data - no checks, no verifications, just a data holder. 
 * All data is expected in the form usable for corresponding builder method in EetRegisterRequest.
 * @author rasekl
 */
public class EetHeaderDTO {
	public String dat_odesl;
	public String prvni_zaslani;
	public String uuid_zpravy;
	public String overeni;
}
