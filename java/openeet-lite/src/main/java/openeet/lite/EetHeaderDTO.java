package openeet.lite;

import java.io.Serializable;

/**
 * DTO for sale data - no checks, no verifications, just a data holder. 
 * All data is expected in the form usable for corresponding builder method in EetRegisterRequest.
 * @author rasekl
 */
public class EetHeaderDTO implements Serializable{
	private static final long serialVersionUID = 1L;

	public String dat_odesl;
	public String prvni_zaslani;
	public String uuid_zpravy;
	public String overeni;
	
	public String toString() {
    	return String.format("%s|%s|%s|%s",dat_odesl, uuid_zpravy, prvni_zaslani, overeni);
    }   
}

