unit SDIMAIN;
 {
 Demoverze pouziti openeet-lite.dll z projektu

 OPENEET https://github.com/vlastikcocek/openeet
 OPENEET https://github.com/HightowerCZ/openeet
 OPENEET https://github.com/l-ra/openeet

 autor pro Delphi Vlastimil Èoèek  PC-KASA.SK

 Spojeni s DLL za pomoci knihovny JEDI http://wiki.delphi-jedi.org/wiki/Main_Page
 }
interface

uses Windows, Classes, Graphics, Forms, Controls, Menus,
  Dialogs, StdCtrls, Buttons, ExtCtrls, ComCtrls, ImgList, StdActns,
  ActnList, ToolWin,SysUtils,JclDotNet;


type
  TSDIAppForm = class(TForm)
    Button2: TButton;
    Memo1: TMemo;
    Memo2: TMemo;
    Memo3: TMemo;
    Memo4: TMemo;
    Memo5: TMemo;
    Label1: TLabel;
    Label2: TLabel;
    Label3: TLabel;
    Label4: TLabel;
    Label5: TLabel;



    procedure Button2Click(Sender: TObject);
    procedure FormShow(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;
 type
  TToolFunc = procedure; stdcall;
var
  SDIAppForm: TSDIAppForm;

  trzba:array[0..25] of widestring;

implementation

uses about,math;

{$R *.dfm}

function zaokro4(num:real48):real48;   {presne zaokrouhleni na dve desetinne}
var acc:real48;
begin
   SetRoundMode(rmNearest);
   num:=RoundTo(num,-4);
   zaokro4:=num;
end;
function wdelete(co:widestring;odd,ddo:integer):widestring; //mazani stringu ve widestringu
var i:integer;
    pom:widestring;
begin
   for i := 1 to length(co) do
   begin
      if (i=odd)or(i=ddo)or((i>odd)and(i<ddo))then begin end
        else pom:=pom+co[i];
   end;
   result:=pom;
end;
Function Str_to_Real(Str:string):real48;
var
  code : integer;
  Temp : real48;
begin
  if str='' then
  begin
    Str_to_Real := 0;
  end
  else
  begin
   try
    If length(Str) = 0 then
       Str_to_Real := 0
    else
    begin
        while Pos(',', str) > 0 do
        begin
        str[Pos(',', str)] := '.';
        end;
        If Copy(Str,1,1)='.' Then
           Str:='0'+Str;
        If (Copy(Str,1,1)='-') and (Copy(Str,2,1)='.') Then
           Insert('0',Str,2);
        If Str[length(Str)] = '.' then
           Delete(Str,length(Str),1);
       val(Str,temp,code);
       if code = 0 then
          Str_to_Real := zaokro4(temp)
       else
          Str_to_Real := 0;
    end;
   except
      Str_to_Real := 0;
   end;
  end;
end;
function dej_polozku(co:string):string;
var pom:string;
begin
    pom:=co;
    if pom>'' then
          if pom[length(pom)]= '=' then if pom[length(pom)-1]<>'=' then pom:=''; // pokud polozka je prazdna nulujeme
    if pom>'' then pom:=WDelete(pom,1,(pos('=',pom))); // nechame pouze polozky bez popisu
    result:=pom;
end;

procedure TSDIAppForm.Button2Click(Sender: TObject);
var
  Host: TJclClrHost;
  Obj: OleVariant;
  date:tdatetime;  pkp,bkp:string;
  odpoved,xml:widestring;
  f:textfile;
begin
   button2.Enabled:=false;
   memo1.Clear;
   memo2.Clear;
   memo3.Clear;
   memo4.Clear;
   memo5.Clear;
 try
   Host := TJclClrHost.Create('v4.0.30319');
   Host.Start();
   Obj := Host.DefaultAppDomain       //spojeni s DLL
        .CreateInstancefrom('openeet-lite.dll',
        'openeet_lite.EetRequestBuilder')
        .UnWrap();
            // priklad naplneni pole daty
            trzba[0]:=dej_polozku('dic_popl=CZ1212121218');
            trzba[1]:=dej_polozku('dic_poverujiciho=');
            trzba[2]:=dej_polozku('id_provoz=1');
            trzba[3]:=dej_polozku('id_pokl=POKLADNA01');
            trzba[4]:=dej_polozku('porad_cis=2');
            trzba[5]:=dej_polozku('dat_trzby=2016-09-17T12:49:05+02:00');
            trzba[6]:=dej_polozku('celk_trzba=100.00');
            trzba[7]:=dej_polozku('zakl_nepodl_dph=');
            trzba[8]:=dej_polozku('zakl_dan1=');
            trzba[9]:=dej_polozku('dan1=');
            trzba[10]:=dej_polozku('zakl_dan2=');
            trzba[11]:=dej_polozku('dan2=');
            trzba[12]:=dej_polozku('zakl_dan3=');
            trzba[13]:=dej_polozku('dan3=');
            trzba[14]:=dej_polozku('cest_sluz=');
            trzba[15]:=dej_polozku('pouzit_zboz1=');
            trzba[16]:=dej_polozku('pouzit_zboz2=');
            trzba[17]:=dej_polozku('pouzit_zboz3=');
            trzba[18]:=dej_polozku('urceno_cerp_zuct=');
            trzba[19]:=dej_polozku('cerp_zuct=');
            trzba[20]:=dej_polozku('rezim=0');
            trzba[21]:=dej_polozku('pkp=');
            trzba[22]:=dej_polozku('bkp=');
            trzba[23]:=dej_polozku('prvni_zaslani=1');

            trzba[24]:=dej_polozku('heslo=eet');
            trzba[25]:=dej_polozku('fcert=01000003.p12');

            // volame jen naplnene
            if trzba[0]>''  then obj.SetDicPopl                    (trzba[0]);
            if trzba[1]>''  then obj.SetDicPoverujiciho            (trzba[1]);
            if trzba[2]>''  then obj.SetIdProvoz                   (trzba[2]);
            if trzba[3]>''  then obj.SetIdPokl                     (trzba[3]);
            if trzba[4]>''  then obj.SetPoradCis                   (trzba[4]);
            if trzba[5]>''  then obj.SetDatTrzbys                  (trzba[5]);
            if trzba[6]>''  then obj.SetCelkTrzba      (str_to_real(trzba[6]));
            if trzba[7]>''  then obj.SetZaklNepodlDph  (str_to_real(trzba[7]));
            if trzba[8]>''  then obj.SetZaklDan1       (str_to_real(trzba[8]));
            if trzba[9]>''  then obj.SetDan1           (str_to_real(trzba[9]));
            if trzba[10]>'' then obj.SetZaklDan2       (str_to_real(trzba[10]));
            if trzba[11]>'' then obj.SetDan2           (str_to_real(trzba[11]));
            if trzba[12]>'' then obj.SetZaklDan3       (str_to_real(trzba[12]));
            if trzba[13]>'' then obj.SetDan3           (str_to_real(trzba[13]));
            if trzba[14]>'' then obj.SetCestSluz       (str_to_real(trzba[14]));
            if trzba[15]>'' then obj.SetPouzitZboz1    (str_to_real(trzba[15]));
            if trzba[16]>'' then obj.SetPouzitZboz2    (str_to_real(trzba[16]));
            if trzba[17]>'' then obj.SetPouzitZboz3    (str_to_real(trzba[17]));
            if trzba[18]>'' then obj.SetUrcenoCerpZuct (str_to_real(trzba[18]));
            if trzba[19]>'' then obj.SetCerpZuct       (str_to_real(trzba[19]));
            if trzba[20]>'' then obj.SetRezim          (strtoint   (trzba[20]));
            if trzba[21]>'' then obj.SetPkps                       (trzba[21]);
            if trzba[22]>'' then obj.SetBkps                       (trzba[22]);
            if trzba[23]>'' then obj.SetPrvniZaslani   (strtoint   (trzba[23]));
            //ocekavame vyplnene jmeno a heslo souboru p12
            obj.SetPkcs12Password(trzba[24]);
            obj.SetPkcs12(trzba[25]);


   obj.Build;
   pkp:=obj.build.FormatPkp;
   memo3.Lines.Add(pkp);
   bkp:=obj.build.FormatBkp;
   memo4.Lines.Add(bkp);

   xml:=obj.build.generateSoapRequest;

   memo1.Lines.Add(xml);

   assignfile(f,'test.xml');
   rewrite(f);
   writeln(f,xml);
   closefile(f);
   odpoved:=obj.build.sendRequest(xml, 'https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3');

   memo2.Lines.Add(odpoved);

   if pos('Potvrzeni fik=',odpoved)>0 then
   begin
     memo5.Lines.Add(copy(odpoved,pos('Potvrzeni fik=',odpoved)+15,39));
   end;

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
       button2.Enabled:=true;
     end;
  end;
  button2.Enabled:=true;
end;

procedure TSDIAppForm.FormShow(Sender: TObject);
begin
   memo1.Clear;
   memo2.Clear;
   memo3.Clear;
   memo4.Clear;
   memo5.Clear;

end;

end.
