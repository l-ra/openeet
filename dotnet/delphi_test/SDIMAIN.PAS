unit SDIMAIN;
 {
 Demoverze  napojení na OPENEET
 autor  Vlastimil Èoèek  PC-KASA
 }
interface

uses Windows, Classes, Graphics, Forms, Controls, Menus,
  Dialogs, StdCtrls, Buttons, ExtCtrls, ComCtrls, ImgList, StdActns,
  ActnList, ToolWin,SysUtils,JclDotNet;


type
  TSDIAppForm = class(TForm)
    Button2: TButton;



    procedure Button2Click(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;
 type
  TToolFunc = procedure; stdcall;
var
  SDIAppForm: TSDIAppForm;

implementation

uses about;

{$R *.dfm}








procedure TSDIAppForm.Button2Click(Sender: TObject);
var
  Host: TJclClrHost;
  Obj: OleVariant;
  date:tdatetime;  pkp:string;
  odpoved,xml:widestring;
  f:textfile;
begin
 try
   Host := TJclClrHost.Create('v4.0.30319');
   Host.Start();
   Obj := Host.DefaultAppDomain       //spojeni s DLL
        .CreateInstancefrom('openeet-lite.dll',
        'openeet_lite.Builder')
        .UnWrap();
   
   obj.dic_popl('CZ1212121218');
   obj.dat_trzbys('2016-09-12T08:43:28+02:00');   //moznost zadat datum v textovem tvaru
   obj.id_provoz('1');
   obj.id_pokl('POKLADNA01');
   obj.porad_cis('1');
   obj.celk_trzba(100.0);
   obj.rezim(0);
   obj.pkcs12s('01000003.p12');       // upravena moznost zadani nazvu souboru s p12
   obj.pkcs12password('eet');
   obj.build;
   pkp:=obj.build.FormatPkp;
   xml:=obj.build.generateSoapRequest;
   assignfile(f,'test.xml');
   rewrite(f);
   writeln(f,xml);
   closefile(f);
   odpoved:=obj.build.sendRequest(xml, 'https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3');
   assignfile(f,'odpoved.xml');
   rewrite(f);
   writeln(f,odpoved);
   closefile(f);
   Host.Stop();
   Host.Free;
  Except
     on E : Exception do
     begin
       ShowMessage('Exception class name = '+E.ClassName + ' ' + 'Exception message = '+E.Message);
     end;
  end;
end;

end.
