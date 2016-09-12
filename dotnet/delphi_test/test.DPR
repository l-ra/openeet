program test;
 {
 Demoverze  napojení na OPENEET
 autor  Vlastimil Èoèek PC-KASA
 }



uses
  Forms,
  SDIMAIN in 'SDIMAIN.PAS' {SDIAppForm},
  About in 'About.pas' {AboutBox},
  JclDotNet in 'JclDotNet.pas';

{$R *.RES}

begin
  Application.Initialize;
  Application.CreateForm(TSDIAppForm, SDIAppForm);
  Application.CreateForm(TAboutBox, AboutBox);
  Application.Run;
end.
 
