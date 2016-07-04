#How to get templates
The templates derived from example XML message using xmlsec1 utility. 
The derivation extracts digested byte streams after canonicalization.
The byte streams are then carefully manipulated using string replacements.
Due to carefull manipulation, no advanced XML processing, no c14n, 
no WS-Security tooling is required to generate valid registration request.

If you need to modify templates (should not be necessary), the recommended approach is:

Take working example of the message from Ministry of finance or somewhere else, use the scripts 
in the shell part of the repository - prepare-tamplates.sh and the templates will be prepared for you. 
Keep in mind there need to be placeholders (e.g. ${xxx}) instead of real values. The placeholders 
are used to manipulate the request to fill in business data. The template regeneration should not be needed 
unless the specification version changes.  