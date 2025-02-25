unit Unit3;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls, ComCtrls, CRCinit, StrUtils, ExtCtrls;

type
  TForm3 = class(TForm)
    GroupBox1: TGroupBox;
    GroupBox2: TGroupBox;
    ListBox1: TListBox;
    ListBox2: TListBox;
    Memo1: TRichEdit;
    Memo2: TRichEdit;
    Button1: TButton;
    Button2: TButton;
    Button3: TButton;
    CheckBox1: TCheckBox;
    RadioGroup1: TRadioGroup;
    procedure FormShow(Sender: TObject);
    procedure Button1Click(Sender: TObject);
    procedure Button2Click(Sender: TObject);
    procedure Button3Click(Sender: TObject);
    procedure RadioGroup1Click(Sender: TObject);
  private
    { Private declarations }
  public
procedure zbase_read;
procedure zuserbase_read;
    { Public declarations }
  end;

var
  Form3: TForm3;
  z_games,z_gamesu:array of string[255];
  z_size,z_sizeu:array of string[20];
  z_crcs,z_crcsu:array of string[8];
  z_e,z_d:array of byte;

implementation

uses Main;

{$R *.dfm}


procedure TForm3.FormShow(Sender: TObject);
var i:word;
begin
 form3.listbox1.items.clear;
 form3.listbox2.items.clear;
 form1.base_read;
 for i:=0 to length(d_games)-1 do
  if d_games[i]<>'' then form3.listbox2.items.Add(d_games[i]);
 form1.userbase_read;

 RadioGroup1Click(Self);
 form1.memo1.Text:='';
 listbox1.ItemIndex:=0;
 listbox2.ItemIndex:=0;
end;



procedure TForm3.Button1Click(Sender: TObject);
var i,j:word;
fi:textfile;
s,s2,s1:string;
begin
memo1.Text:='';
memo2.Text:='';
s2:=spath+'Base\games\';
if radiogroup1.Itemindex=0 then s1:=spath+user_name+'\' else s1:=s2;
 assignfile(fi, s1+listbox1.Items[listbox1.itemindex]);
// showmessage(s1+listbox1.Items[listbox1.itemindex]);
 Reset(fi);
memo1.PlainText := True;
while not eof(fi) do
 begin
 readln(fi,s);
 memo1.Lines.Add(s);
 end;
memo1.PlainText := false;
 closefile(fi);

 assignfile(fi, s2+listbox2.Items[listbox2.itemindex]);
 Reset(fi);
memo2.PlainText := True;
while not eof(fi) do
 begin
 readln(fi,s);
 memo2.Lines.Add(s);
 end;
memo2.PlainText := false;
 closefile(fi);

groupbox1.caption:=' '+listbox1.Items[listbox1.itemindex]+' ';
groupbox2.caption:=' '+listbox2.Items[listbox2.itemindex]+' ';

zbase_read;
zuserbase_read;

 for i:=0 to length(z_gamesu)-1 do
  for j:=0 to length(z_games)-1 do
    if z_gamesu[i]=z_games[j] then
     begin
      inc(z_e[i]);inc(z_d[j]);
      if z_sizeu[i]=z_size[j] then begin inc(z_e[i]);inc(z_d[j]);end;
      if z_crcsu[i]=z_crcs[j] then begin inc(z_e[i]);inc(z_d[j]);end;
   end;
 memo1.Text:='';
 memo2.Text:='';

 for i:=0 to length(z_gamesu)-1 do
  begin
  case z_e[i] of
  0: memo1.SelAttributes.color:=clred;
  1: memo1.SelAttributes.color:=clfuchsia;
  2: memo1.SelAttributes.color:=clblue;
  3: memo1.SelAttributes.color:=clblack;
  end;
  if z_crcsu[i]=ser then memo1.SelAttributes.color:=clred;
    if ((checkbox1.Checked<>true) or (z_e[i]<3)) then
  memo1.lines.Add(z_gamesu[i]+' ['+z_sizeu[i]+' bytes] '+z_crcsu[i]);
  end;

 for i:=0 to length(z_games)-1 do
  begin
  case z_d[i] of
  0: memo2.SelAttributes.color:=clred;
  1: memo2.SelAttributes.color:=clfuchsia;
  2: memo2.SelAttributes.color:=clblue;
  3: memo2.SelAttributes.color:=clblack;
  end;
  if z_crcs[i]=ser then memo2.SelAttributes.color:=clred;
    if ((checkbox1.Checked<>true) or (z_d[i]<3)) then
  memo2.lines.Add(z_games[i]+' ['+z_size[i]+' bytes] '+z_crcs[i]);
  end;

 memo1.visible:=true;
 memo2.visible:=true;
end;

procedure TForm3.Button2Click(Sender: TObject);
begin
 groupbox1.Caption:=' '+radiogroup1.Items[radiogroup1.Itemindex]+' ';
 groupbox2.Caption:=' эталонная база ';
 memo1.Visible:=false;
 memo2.Visible:=false;
end;

procedure tform3.zbase_read;
var
k,i:word;
s:string;
begin
 k:=0;
 memo2.lines.delete(0);
 for i:=0 to memo2.lines.count do
  if memo2.Lines[i]<>'' then inc(k);
//  k:=k-1;
 setlength(z_games,k);
 setlength(z_crcs,k);
 setlength(z_size,k);
 setlength(z_d,k);
 for i:=1 to length(z_games)-1 do
  begin
   s:=memo2.Lines[i];
   k:=pos(' [',s);
   z_games[i]:=copy(s,1,k-1);
   s:=copy(s,k+2, length(s));
   k:=pos('] - ',s);
   z_size[i]:=AnsiReplaceStr(copy(s,1,k-1), ' bytes', '');
   z_crcs[i]:=copy(s,k+4, length(s)-k);
   z_d[i]:=0;
  end;
end;


procedure tform3.zuserbase_read;
var
k,i:word;
s:string;
begin
 k:=0;
 memo1.lines.delete(0);
 for i:=0 to memo1.lines.count do
  if memo1.Lines[i]<>'' then inc(k);
//  k:=k-1;
 setlength(z_gamesu,k);
 setlength(z_crcsu,k);
 setlength(z_sizeu,k);
 setlength(z_e,k);
 for i:=1 to length(z_gamesu)-1 do
  begin
   s:=memo1.Lines[i];
   k:=pos(' [',s);
   z_gamesu[i]:=copy(s,1,k-1);
   s:=copy(s,k+2, length(s));
   k:=pos('] - ',s);
   z_sizeu[i]:=AnsiReplaceStr(copy(s,1,k-1), ' bytes', '');
   z_crcsu[i]:=copy(s,k+4, length(s)-k);
   z_e[i]:=0;
  end;
end;


procedure TForm3.Button3Click(Sender: TObject);
begin
close;
end;

procedure TForm3.RadioGroup1Click(Sender: TObject);
var i:word;
begin
 groupbox1.Caption:=' '+radiogroup1.Items[radiogroup1.Itemindex]+' ';
 form3.listbox1.items.clear;
 if radiogroup1.Itemindex=1 then
  begin
   for i:=0 to length(d_games)-1 do
   if d_games[i]<>'' then form3.listbox1.items.Add(d_games[i]);
  end
  else
  begin
   for i:=0 to length(d_gamesu)-1 do
   if d_gamesu[i]<>'' then form3.listbox1.items.Add(d_gamesu[i]);
  end;
end;

end.
