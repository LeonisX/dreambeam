unit Main;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls, INIfiles, ComCtrls, ExtCtrls, ShellAPI, ShlObj, CRCinit, unit2,
  Gauges, strutils, Menus, FormMagnet, MMSystem, GraphicEx, Buttons;

const  ser='Error!!!';
s_user='User';
card=2147483600;
const ext: array[0..5] of string=('!!','rus)','pal-e','ntsc-u','ntsc-j','(homebrew)');
const ext1: array[0..5] of string=('Всего в базе данных: ','Русские игры: ','Европа: ','США: ','Япония: ','Домашние разработки: ');
hr='------------------------------------------------------------------';
type
  TForm1 = class(TForm)
    Button2: TButton;
    ComboBox1: TComboBox;
    Button4: TButton;
    Panel1: TPanel;
    Edit1: TEdit;
    Button1: TButton;
    Panel2: TPanel;
    Timer1: TTimer;
    ProgressBar1: TGauge;
    ProgressBar2: TGauge;
    Label2: TLabel;
    Button3: TButton;
    Memo1: TRichEdit;
    Button5: TButton;
    PopupMenu1: TPopupMenu;
    N1: TMenuItem;
    N2: TMenuItem;
    Button6: TButton;
    MainMenu1: TMainMenu;
    N3: TMenuItem;
    N4: TMenuItem;
    N5: TMenuItem;
    N6: TMenuItem;
    N7: TMenuItem;
    N8: TMenuItem;
    N9: TMenuItem;
    N10: TMenuItem;
    StatusBar1: TStatusBar;
    Readmetxt1: TMenuItem;
    Changelogtxt1: TMenuItem;
    Button7: TButton;
    Button8: TButton;
    N11: TMenuItem;
    Label1: TLabel;
    Label3: TLabel;
    Timer2: TTimer;
    FormMagnet1: TFormMagnet;
    Button9: TButton;
    Image1: TImage;
    SpeedButton1: TSpeedButton;
    Label4: TLabel;
    N12: TMenuItem;
    N13: TMenuItem;
    procedure FormCreate(Sender: TObject);
    procedure Button2Click(Sender: TObject);
    procedure Button4Click(Sender: TObject);
    procedure Button1Click(Sender: TObject);
    procedure ComboBox1Change(Sender: TObject);
    procedure Timer1Timer(Sender: TObject);
    procedure Button3Click(Sender: TObject);
    procedure Button5Click(Sender: TObject);
    procedure N1Click(Sender: TObject);
    procedure N2Click(Sender: TObject);
    procedure Button6Click(Sender: TObject);
    procedure N6Click(Sender: TObject);
    procedure Readmetxt1Click(Sender: TObject);
    procedure Changelogtxt1Click(Sender: TObject);
    procedure FormShow(Sender: TObject);
    procedure Button7Click(Sender: TObject);
    procedure Button8Click(Sender: TObject);
    procedure Timer2Timer(Sender: TObject);
    procedure Button9Click(Sender: TObject);
    procedure Button10Click(Sender: TObject);
    procedure N13Click(Sender: TObject);
    procedure FormClose(Sender: TObject; var Action: TCloseAction);
  private
    { Private declarations }
  public
procedure savejournal;
procedure CDscan;
function dirselect:string;
function GetFileSize(namefile: string): int64;
procedure ScanDir (Path:string;SearchMask:TStrings;ScanSub:boolean);
procedure m_sort;
procedure m_sorta;
procedure base_read;
procedure userbase_read;
function LinesVisible(Memo: TRichEdit): integer;
procedure ReadParams;
    function deattr(s:string):string;
    function reject(s,s1:string):string;
function OpenCD(Drive: Char): Boolean;
function CloseCD(Drive: Char): Boolean;
procedure calker(z:byte);
function VolumeID(DriveChar: Char): string;
    { Public declarations }
  end;

var
  Form1: TForm1;
  spath, user_name:string;
  totalsize:longint;
  StringList: TStrings;
  d_games,d_gamess,d_gamesu:array of string[255];
  d_crcs,d_crcsu:array of string[8];
  d_e,d_d:array of byte;
  fi:textfile;
  t,t1:cardinal;
  stime:char;
  cdopen:boolean;
  sears:string;
  ferror:boolean;

implementation

uses Unit3, Unit4, Unit5, Unit6, Unit7, Unit8;

{$R *.dfm}

function tform1.deattr(s:string):string;
label l1,l2;
var
st,stt:string;
begin
 st:=reject(s,'[');
 st:=reject(st,' + st bios (st');
l1:
 stt:=reject(st,'[');
 if st<>stt then begin st:=stt;goto l1;end;
 st:=reject(st,'(');
l2:
 stt:=reject(st,'(');
 if st<>stt then begin st:=stt;goto l1;end;
 result:=st;
end;

function tform1.reject(s,s1:string):string;
var c:char;
k:word;
st,si:string;
begin
 si:=s;
 k:=pos(s1,si);
 if k<>0 then
  begin
   if s1[1]='[' then c:=']' else c:=')';
   si:=copy(s,1,k-1);
   if (si[length(si)]=' ') or (si[length(si)]='_') then si:=copy(si,1,length(si)-1);
   st:=copy(s,k+1,length(s)-k);
   si:=si+copy(st,pos(c,st)+1,length(st)-pos(c,st)+1);
  end;
  result:=si;
end;

function tform1.GetFileSize(namefile: string): int64;
var
  InfoFile: TSearchRec;
  AttrFile: Integer;
  ErrorReturn: Integer;
begin
  AttrFile := $0000003F; {Any file}
  ErrorReturn := FindFirst(namefile, AttrFile, InfoFile);
  if ErrorReturn <> 0 then
    Result := -1
  else
    Result := InfoFile.Size;
  FindClose(InfoFile);
end;

function TForm1.dirselect:string;
var
 TitleName : string;
 lpItemID : PItemIDList;
 BrowseInfo : TBrowseInfo;
 DisplayName : array[0..MAX_PATH] of char;
 TempPath : array[0..MAX_PATH] of char;
begin
 result:='';
 FillChar(BrowseInfo, sizeof(TBrowseInfo), #0);
 BrowseInfo.hwndOwner := Form1.Handle;
 BrowseInfo.pszDisplayName := @DisplayName;
 TitleName := 'Выберите нужную папку';
 BrowseInfo.lpszTitle := PChar(TitleName);
 BrowseInfo.ulFlags := BIF_RETURNONLYFSDIRS;
 lpItemID := SHBrowseForFolder(BrowseInfo);
 if lpItemId <> nil then begin
 SHGetPathFromIDList(lpItemID, TempPath);
 result:=TempPath;
 GlobalFreePtr(lpItemID);
 end;
end;

procedure tform1.base_read;
var
k,i,p:word;
s:string;
begin
memo1.text:='';
setlength(d_gamess,2000);
s:=spath+'Base\games.dat';
if not fileexists(s) then Button6Click(Self);
if fileexists(s) then begin
 assignfile(fi, s);
 Reset(fi);
 k:=0;
while not eof(fi) do begin
readln(fi,d_gamess[k]);
inc(k);
end;
 closefile(fi);
 setlength(d_games,k+1);p:=0;
 setlength(d_crcs,k+1);
 setlength(d_d,k+1);
for i:=0 to k do
 begin
  d_games[i]:=copy(d_gamess[i],1,length(d_gamess[i])-11);
  d_crcs[i]:=copy(d_gamess[i],length(d_gamess[i])-7,8);
  d_d[i]:=0;
  if pos('[!]', ansilowercase(d_games[i]))<>0 then inc(p);
 end;
 statusbar1.SimpleText:='  В базе данных '+inttostr(k)+' записи; '+inttostr(p)+' проверены на 100%';
 end;
 d_gamess:=nil;
end;


procedure tform1.userbase_read;
var
k,i,l:word;
begin
memo1.text:='';
 scandir(spath+user_name,StringList, false);
 k:=0;
 for i:=0 to memo1.lines.count-1 do
  if memo1.Lines[i]<>'' then inc(k);
 setlength(d_gamesu,k);
 setlength(d_crcsu,k);
 setlength(d_e,k);
 l:=0;
 for i:=0 to k-1 do
  if fileexists(memo1.Lines[i]) then begin
  d_gamesu[l]:=extractfilename(memo1.Lines[i]);
  d_crcsu[l]:=IntToHex(GetFileCRC(memo1.Lines[i]),8);
  d_e[l]:=0;
  inc(l);
 end;
 m_sort;
end;

procedure TForm1.FormCreate(Sender: TObject);
Var IniFile:TIniFile;
s:string;
begin
cdopen:=false;
if fileexists(spath+'readme.txt') then Readmetxt1.Enabled:=true;
if fileexists(spath+'changelog.txt') then Changelogtxt1.Enabled:=true;

StringList := TStringList.Create;
    with StringList do begin
      Add('*.*');
    end;
  spath:=ExtractFilePath(ParamStr(0));
  if sPath[Length(sPath)]<>'\' then sPath:=sPath+'\';
  if fileexists(spath+'DreamBeam.ini') then ReadParams else
  begin
    s:= InputBox('Новый пользователь', 'Приветствую! Для учёта информации по дискам, введи своё имя, желательно на английском языке', s_user);
    if s='' then s:=s_user;
    IniFile:=TIniFile.Create(spath+'DreamBeam.ini');
    IniFile.WriteString('Main','Name',s);
    IniFile.free;
    user_name:=s;
  end;
  CreateDir(spath+'Base');
  CreateDir(spath+'Base\txtz');
  CreateDir(spath+'Base\games');
  CreateDir(spath+user_name);
  base_read;
  userbase_read;
memo1.text:='';
  cdscan;
  form1.caption:=form1.caption+' - '+ user_name;
end;

procedure Tform1.CDscan;
var
  w: dword;
  Root: string;
  i: integer;
begin
  combobox1.Items.Clear;
  w := GetLogicalDrives;
  Root := '#:\';
  for i := 0 to 25 do
  begin
    Root[1] := Char(Ord('A') + i);
    if (W and (1 shl i)) > 0 then
      if GetDriveType(Pchar(Root)) = DRIVE_CDROM then
        combobox1.items.add(copy(Root,1,length(root)-1));
  end;
  combobox1.items.add('Директория');
  combobox1.items.add('Обновить');  
  if combobox1.Items.count>0 then
   begin
    combobox1.Text:=combobox1.Items[0];
    combobox1.ItemIndex:=0;
   end;
end;

procedure tform1.ScanDir (Path:string;SearchMask:TStrings;ScanSub:boolean);
var
SearchRec:TSearchrec;
a,i:integer;
begin
memo1.PlainText:=true;
if ScanSub then
begin
a:=FindFirst(path+'\*.*',faDirectory,SearchRec);
while a=0 do
begin

if (SearchRec.Attr and faDirectory)>0 then
 if ((SearchRec.Name<>'.') and (SearchRec.Name<>'..')) then
ScanDir(Path+'\'+SearchRec.Name,SearchMask,ScanSub);
a:=FindNext(SearchRec);
end;{while}
FindClose(SearchRec);
end;{if}

for i:=0 to SearchMask.Count-1 do
begin
a:=FindFirst(Path+'\'+SearchMask[i],faAnyFile,SearchRec);
while a=0 do
begin
if ((SearchRec.Name<>'.') and (SearchRec.Name<>'..') and (SearchRec.Name<>'')) then if (SearchRec.Attr and faDirectory =0) then
form1.Memo1.lines.add(Path+'\'+SearchRec.Name);
{operation on file}
a:=FindNext(SearchRec);
end;{while}
FindClose(SearchRec);
end;{for}
end; {ScanDir}

procedure TForm1.Button2Click(Sender: TObject);
var s:string;
begin
sears:=combobox1.text;
memo1.text:='';
panel1.Visible:=false;
s:=combobox1.text;
 scandir(s,StringList, true);
 memo1.Text:=ansilowercase(memo1.text);
if memo1.text<>'' then
 begin
  button4.enabled:=true;button4.caption:='Просканировать';
  journal.memo2.lines.add(hr);  
  journal.memo2.lines.add('Файлы успешно найдены, можно сканировать.');
  journal.memo2.SelAttributes.Color:= clblue;
  journal.memo2.lines.add('Метка диска (Volume Label): '+VolumeID(s[1]));

  end;
memo1.perform(wm_vscroll, sb_top,0);
end;

procedure TForm1.ReadParams;
Var IniFile:TIniFile;
begin
spath:=ExtractFilePath(ParamStr(0));
if sPath[Length(sPath)]<>'\' then sPath:=sPath+'\';
IniFile:=TIniFile.Create(spath+'DreamBeam.ini');
user_name:=IniFile.ReadString('Main','Name','User');
IniFile.Free;
end;

function tform1.LinesVisible(Memo: TRichEdit): integer;
    Var
      OldFont : HFont;
      Hand : THandle;
      TM : TTextMetric;
      Rect  : TRect;
      tempint : integer;
    begin
      Hand := GetDC(Memo.Handle);
      try
        OldFont := SelectObject(Hand, Memo.Font.Handle);
        try
          GetTextMetrics(Hand, TM);
          Memo.Perform(EM_GETRECT, 0, longint(@Rect));
          tempint := (Rect.Bottom - Rect.Top) div
             (TM.tmHeight + TM.tmExternalLeading);
        finally
          SelectObject(Hand, OldFont);
        end;
      finally
        ReleaseDC(Memo.Handle, Hand);
      end;
      Result := tempint;
    end;

procedure TForm1.Button4Click(Sender: TObject);
label l2;
var s,s1,dir, filename:string;
i, j:longint;
k:word;
SearchRec:TSearchRec;
flu:boolean;
f:textfile;
begin
t:=GetTickCount;
 k:=LinesVisible(Memo1)-1;
  ferror:=false;
  progressbar1.progress:=0;
  progressbar2.progress:=0;
  label2.caption:='';
if button4.caption='Прервать' then begin journal.memo2.SelAttributes.Color:= clred;journal.memo2.lines.add('Операция прервана!');panel2.Visible:=false;zbreak:=true;goto l2;end;

 panel2.Visible:=true;
 button4.caption:='Прервать';

 zbreak:=false;
 totalsize:=0;
 ProgressBar2.MaxValue:=memo1.lines.count-1;
 for i:=0 to memo1.lines.count-1 do
 begin
  if zbreak<>true then begin
  if length(trim(memo1.Lines[i]))>3 then begin
  ProgressBar2.progress:=i;
  filename:=memo1.Lines[i];
  label2.caption:=ansiuppercase(extractfilename(filename));
  application.ProcessMessages;
  assignfile(f,filename);
  {$I-}
  reset(f);
  {$I+}
  if IOResult=0 then begin
  closefile(f);
  fsize:=getfilesize(filename);
  label2.caption:=label2.caption+' ('+inttostr(fsize)+' байт)';
  label2.Left:=progressbar1.left+trunc((progressbar1.Width-label2.Width)/2);
  if fsize>card then fsize:=card;
  progressbar1.maxValue:=fsize;
  progressbar1.progress:=0;
  s1:=IntToHex(GetFileCRC(FileName),8);
    end else
      begin

/// file1.cfg и file2.cfg хак
       if (pos('file1.cfg', filename)=0) and (pos('file2.cfg', filename)=0) then ferror:=true;
       fsize:=0;zflag:=true;
      end;
//  if fsize=0 then begin zflag:=false;s1:='00000000';end;
  if zflag=true then
    begin
     s1:=ser;
     journal.memo2.SelAttributes.Color:= clred;
     journal.memo2.Lines.add(Filename+' - ошибка чтения!');
     journal.memo2.perform(wm_vscroll, sb_linedown,0);
    end;
  application.ProcessMessages;
  totalsize:=totalsize+fsize;
  s:=rightstr(filename,length(filename)-length(sears)-1)+' ['+inttostr(fsize)+' bytes] - '+s1;
  memo1.lines[i]:=s;
  if i>k then memo1.perform(wm_vscroll, sb_linedown,0);
  end;
  end;
 end;
if zbreak=true then goto l2;

 memo1.Lines.Insert(0,'Total size: '+inttostr(totalsize)+' bytes.');
 journal.memo2.SelAttributes.Color:= clgreen;journal.memo2.lines.Add('Время сканирования: '+AnsiReplaceStr(label3.caption,' ',':'));

 s:=spath+user_name+'\'+'temp.txt';

 assignfile(fi, s);
 ReWrite(fi);
memo1.PlainText := True;
for i:=0 to memo1.lines.count-1 do
 write(fi,memo1.lines[i]+chr($0D)+chr($0A));
memo1.PlainText := false;
closefile(fi);
 s1:=IntToHex(GetFileCRC(s),8);

 if ferror then begin
 flu:=false;
 edit1.text:='';
 j:=getfilesize(s);
 dir:=spath+'Base\games\';
 if FindFirst(Dir+'*.*', faAnyFile, SearchRec)=0 then
 repeat
 if (SearchRec.name='.') or (SearchRec.name='..') then continue;
 if (SearchRec.Attr and faDirectory)<>0 then continue;
 if getfilesize(Dir+SearchRec.name)=j then
  begin
   journal.memo2.SelAttributes.Color:= clgreen;journal.memo2.Lines.add('По размеру образ совпадает с:');journal.memo2.SelAttributes.Color:= clgreen;journal.memo2.Lines.add(searchrec.name);memo1.perform(wm_vscroll, sb_linedown,0);flu:=true;
  end;
 until FindNext(SearchRec)<>0;
 FindClose(SearchRec);
 if flu=false then journal.memo2.SelAttributes.Color:= clred;journal.memo2.Lines.add('В базе данных не удалось найти похожий образ диска');memo1.perform(wm_vscroll, sb_linedown,0);
end;

 deletefile(s);
 s:='';
 for i:=0 to length(d_games)-1 do
  begin
   if d_crcs[i]=s1 then s:=d_games[i];
  end;
 panel1.visible:=true;
 panel2.visible:=false;

 if s<>'' then begin journal.memo2.SelAttributes.Color:= clgreen;journal.memo2.lines.Add('Диск распознан как: '+s);end else if not ferror then begin journal.memo2.SelAttributes.Color:= clblue;edit1.Text:='';journal.memo2.lines.add('Этого диска нет в базе данных!');form4.ShowModal;end;
 if s<>'' then edit1.text:=s;
l2:
 button4.caption:='Просканировать';
 button4.Enabled:=false;
end;

procedure TForm1.Button1Click(Sender: TObject);
label l1;
var
i:word;
s:string;
begin
 savejournal;
if edit1.text<>'' then begin
 edit1.Text:=AnsiReplaceStr(edit1.Text, ':', ' -');
 edit1.Text:=AnsiReplaceStr(edit1.Text, '/', ', ');
 edit1.Text:=AnsiReplaceStr(edit1.Text, '\', ', ');
 edit1.Text:=AnsiReplaceStr(edit1.Text, '  ', ' ');
 if edit1.text[1]=' ' then edit1.Text:=copy(edit1.Text,2,length(edit1.text)-1);
 if edit1.text[length(edit1.text)]=' ' then edit1.Text:=copy(edit1.Text,1,length(edit1.text)-1);
 if ferror then
  if pos('(bad',ansilowercase(edit1.text))=0 then edit1.text:=edit1.text+' (Bad '+user_name+')';

 s:=spath+user_name+'\'+edit1.text;
 if fileexists(s) then
    if MessageDlg('Файл '+edit1.text+' существует!'+chr(13)+'Перезаписать его?', mtWarning, [mbYes, mbNo], 0)= mrNo then goto l1;
 assignfile(fi, s);
 ReWrite(fi);
memo1.PlainText := True;
for i:=0 to memo1.lines.count-1 do
 write(fi,memo1.lines[i]+chr($0D)+chr($0A));
memo1.PlainText := false;
closefile(fi);
memo1.SelAttributes.Color:= clwhite;
form1.userbase_read;
memo1.Text:='';
memo1.SelAttributes.Color:= clblack;
journal.memo2.lines.add('Образ сохранён');
journal.memo2.lines.add(hr);
l1:
 end
 else showmessage('Назовите файл!');
end;


procedure TForm1.ComboBox1Change(Sender: TObject);
label l1;
var s:string;
begin
 if (combobox1.ItemIndex=combobox1.Items.Count-1) then begin  cdscan;goto l1;end;
 if (combobox1.ItemIndex<combobox1.Items.Count-2) or (copy(combobox1.items[combobox1.Items.Count-1],2,1)=':') then goto l1;

   s:=dirselect;
   if s<>'' then
   begin
    if s[length(s)]='\' then s:=copy(s,1,length(s)-1);
    combobox1.items[combobox1.Items.Count-2]:=s;
    combobox1.ItemIndex:=combobox1.Items.Count-2;
    end else combobox1.ItemIndex:=0;

  l1:
end;

procedure TForm1.Timer1Timer(Sender: TObject);
var
mm,ss:byte;
smm,sss:string;
tt:longword;
begin
 application.ProcessMessages;
 t1:=GetTickCount;
 tt:=trunc((t1-t)/1000);
 mm:=trunc(tt/60);
 ss:=tt-mm*60;
 smm:=inttostr(mm);if length(smm)=1 then smm:='0'+smm;
 sss:=inttostr(ss);if length(sss)=1 then sss:='0'+sss;
 if panel2.visible=true then label3.Caption:=smm+stime+sss;
 if zsize>card then zsize:=card;
 progressbar1.progress:=zsize;
 application.ProcessMessages;
end;

procedure TForm1.savejournal;
var s:string;
begin
if length(journal.Memo2.text)>10 then begin
Journal.Memo2.lines.add('');
Journal.Memo2.lines.add(hr);
Journal.Memo2.lines.add('Завершение работы. '+ DateToStr(Date)+' - '+TimeToStr(Time));
Journal.Memo2.lines.add(hr);end;
s:=spath+user_name+'.log';
Journal.Memo2.PlainText:=true;
Journal.Memo2.lines.savetofile(s);
end;

procedure Tform1.m_sort;
var
  i,k : Integer;
  Temp : string;
  Flag : Boolean;
begin
 if length(d_gamesu)>1 then begin
  repeat
    Flag := False;
    for i := 0 to length(d_gamesu)-2 do
      if d_gamesu[i] > d_gamesu [i + 1] then begin
        Temp := d_gamesu [i]; d_gamesu [i] := d_gamesu [i + 1]; d_gamesu [i + 1] := Temp;
        Temp := d_crcsu [i]; d_crcsu [i] := d_crcsu [i + 1]; d_crcsu [i + 1] := Temp;
        k:=d_e[i];d_e[i]:=d_e[i+1];d_e[i+1]:=k;
        Flag := True;
      end;
  until Flag = False;
  end;
 end;

procedure Tform1.m_sorta;
var
  i,k : Integer;
  Temp : string;
  Flag : Boolean;
begin
 if length(d_games)>1 then begin
  repeat
    Flag := False;
    for i := 0 to length(d_games)-2 do
      if d_games[i] > d_games [i + 1] then begin
        Temp := d_games [i]; d_games [i] := d_games [i + 1]; d_games [i + 1] := Temp;
        Temp := d_crcs [i]; d_crcs [i] := d_crcs [i + 1]; d_crcs [i + 1] := Temp;
        k:=d_d[i];d_d[i]:=d_d[i+1];d_d[i+1]:=k;
        Flag := True;
      end;
  until Flag = False;
  end;
 end;

procedure TForm1.Button3Click(Sender: TObject);
label l2;
var i,j,k,ku,ku2,ku3,m,n,o,p:word;
flu:boolean;
begin
//base_read;
//userbase_read;
 memo1.text:='';

for j:=0 to 5 do calker(j);

memo1.Lines.add('');

if length(d_gamesu)>0 then begin
 for i:=0 to length(d_gamesu)-1 do
  for k:=0 to length(d_games)-1 do
   if d_crcsu[i]=d_crcs[k] then begin d_d[k]:=1;d_e[i]:=1;renamefile(spath+user_name+'\'+d_gamesu[i],spath+user_name+'\'+d_games[k]);d_gamesu[i]:=d_games[k];end;
 end else begin memo1.Lines.Add('Ваша база данных пуста');goto l2;end;
 m_sort;

for j:=1 to 5 do //на самом деле, чтобы вывести полную статистику, надо начинать с нуля
 begin
  m:=0;n:=0;p:=0;o:=0;
  for i:=0 to length(d_gamesu)-1 do
   begin
    ku:=pos(ext[j],ansilowercase(d_gamesu[i])+'!!');
    ku2:=pos('(bad',ansilowercase(d_gamesu[i]));
    ku3:=pos(ext[5],ansilowercase(d_gamesu[i]));
    if (ku<>0) then if (ku3=0) then inc(p);
    if j=5 then if (ku3<>0) then inc(p);
    if (ku<>0) and (d_e[i]=1) and (ku2=0) then
      begin
       inc(m);
       if (j<>5) and (ku3<>0) then dec(m);
      end;
    if (ku<>0) and (d_e[i]=1) and (ku2<>0) then
     begin
      inc(n);
      if (j<>5) and (ku3<>0) then dec(n);
     end;
    //p - все-все мои игры
    //m - все мои игры bad
    //n - все мои игры не bad
    //o - игры в основной базе
   end;
  for i:=0 to length(d_games)-1 do
   begin
    flu:=false;
    ku3:=pos(ext[5],ansilowercase(d_games[i]));
    if (pos(ext[j],ansilowercase(d_games[i])+'!!')<>0) then flu:=true;
    if (pos('(bad',ansilowercase(d_games[i]))<>0) then flu:=false;
    if (j<>5) and (ku3<>0) then flu:=false;
    if flu then inc(o);
   end;


memo1.Lines.add('');
memo1.SelAttributes.Style:=[fsbold];memo1.SelAttributes.Height := 10;
memo1.Lines.Add(hr);
memo1.SelAttributes.Style:=[fsbold];memo1.SelAttributes.Height := 10;
memo1.Lines.Add(ext1[j]);
memo1.SelAttributes.Style:=[fsbold];memo1.SelAttributes.Height := 10;
memo1.Lines.Add(hr);
memo1.Lines.add('');
memo1.SelAttributes.Style:=[fsbold];memo1.SelAttributes.Height := 10;
memo1.Lines.add('Ваши игры, которых нет в базе данных: '+inttostr(p-m-n));
 for i:=0 to length(d_gamesu)-1 do
   if (d_e[i]<>1) then
   begin
     ku3:=pos(ext[5],ansilowercase(d_gamesu[i]));
    if (pos(ext[j],ansilowercase(d_gamesu[i])+'!!')<>0)then
    begin
     flu:=true;
     if (pos('(bad',ansilowercase(d_gamesu[i]))<>0) then memo1.SelAttributes.color:=clred;
     if (j<>5) and (ku3<>0) then flu:=false;
     if flu then memo1.lines.Add(d_gamesu[i]);
   end;
   end;
 memo1.Lines.add('');
 memo1.SelAttributes.Style:=[fsbold];
 memo1.SelAttributes.Height := 10;
 memo1.Lines.add('Игры, входящие в базу данных: '+inttostr(m));
 for i:=0 to length(d_gamesu)-1 do
  begin
   if d_e[i]=1 then
   begin
     ku3:=pos(ext[5],ansilowercase(d_gamesu[i]));
    if (pos(ext[j],ansilowercase(d_gamesu[i])+'!!')<>0)then
    begin
     flu:=true;
     if (pos('(bad',ansilowercase(d_gamesu[i]))<>0) then memo1.SelAttributes.color:=clred;
     if (j<>5) and (ku3<>0) then flu:=false;
     if flu then memo1.lines.Add(d_gamesu[i]);
   end;
   end;
  end;

 memo1.Lines.add('');
 memo1.SelAttributes.Style:=[fsbold];
 memo1.SelAttributes.Height := 10;
 memo1.Lines.add('Игры, отсутствующие в вашей коллекции: '+inttostr(o-m));
 for i:=0 to length(d_games)-1 do
  begin
     flu:=false;
     if (d_d[i]=0) and (pos(ext[j],ansilowercase(d_games[i])+'!!')<>0) then flu:=true;
     ku3:=pos(ext[5],ansilowercase(d_games[i]));
     if (j<>5) and (ku3<>0) then flu:=false;
     if flu then memo1.lines.Add(d_games[i]);
 end;
 end;
l2:
end;

procedure TForm1.calker(z:byte);
var k,o,p:word;
flu:boolean;
begin
 o:=0;p:=0;
  for k:=0 to length(d_games)-1 do
   if pos(ext[z],ansilowercase(d_games[k])+'!!')<>0 then
   begin
     flu:=false;
     if d_games[k]<>'' then flu:=true;
     if (z<>5) and (z<>0) and (pos(ext[5],ansilowercase(d_games[k]))<>0) then flu:=false;
     if flu then
      begin inc(o);
      if pos('[!]', ansilowercase(d_games[k]))<>0 then inc(p);
     end;
   end;
 memo1.SelAttributes.Style:=[fsbold];
 memo1.SelAttributes.Height := 10;
 memo1.lines.add(ext1[z]+inttostr(o)+' записи; '+inttostr(p)+' проверены на 100%');
end;


procedure TForm1.Button5Click(Sender: TObject);
begin
if length(d_gamesu)>0 then form3.showmodal else form6.ShowModal;
end;

procedure TForm1.N1Click(Sender: TObject);
begin
 memo1.copytoclipboard;
end;

procedure TForm1.N2Click(Sender: TObject);
begin
 memo1.selectAll;
end;

procedure TForm1.Button6Click(Sender: TObject);

var
k,i,l:word;
begin
memo1.text:='';
 scandir(spath+'Base\games',StringList, false);
 k:=0;
 for i:=0 to memo1.lines.count-1 do
  if memo1.Lines[i]<>'' then inc(k);
 setlength(d_games,k);
 setlength(d_crcs,k);
 setlength(d_d,k);
 l:=0;
 if k>0 then begin
 for i:=0 to k-1 do
  if fileexists(memo1.Lines[i]) then begin
  d_games[l]:=extractfilename(memo1.Lines[i]);
  d_crcs[l]:=IntToHex(GetFileCRC(memo1.Lines[i]),8);
  d_d[i]:=0;
  inc(l);
 end;
 m_sorta;
 assignfile(fi, spath+'Base\games.dat');
 ReWrite(fi);
for i:=0 to length(d_games)-1 do
 write(fi,d_games[i]+' - '+d_crcs[i]+chr($0D)+chr($0A));
 closefile(fi);
showmessage('Создания краткого списка закончено!');
end else begin showmessage('Не найдено ни одного образа для подсчёта краткого списка!!!');end;
memo1.perform(wm_vscroll, sb_linedown,0);
end;

procedure TForm1.N6Click(Sender: TObject);
begin
close;
end;

procedure TForm1.Readmetxt1Click(Sender: TObject);
begin
ShellExecute(0,'Open',pchar(spath+'readme.txt'),nil,nil,1);
end;

procedure TForm1.Changelogtxt1Click(Sender: TObject);
begin
ShellExecute(0,'Open',pchar(spath+'changelog.txt'),nil,nil,1);
end;

procedure TForm1.FormShow(Sender: TObject);
var s:string;
begin
s:=spath+'Base\games.dat';
if not fileexists(s) then begin memo1.SelAttributes.Color:= clwhite;base_read;memo1.Text:='';end;
if not fileexists(s) then begin
 showmessage('Отсутствует файл '+s+'!!!'+chr(13)+'и файлы-образы для его построения.'+chr(13)+'Нормальная работа программы не гарантируется...');
 close;
 end;

  Journal.show;

s:=spath+user_name+'.log';
if fileexists(s) then Journal.Memo2.lines.loadfromfile(s);

Journal.Memo2.lines.add('');
Journal.Memo2.lines.add(hr);
Journal.Memo2.lines.add('Начало работы. '+ DateToStr(Date)+' - '+TimeToStr(Time));
Journal.Memo2.lines.add(hr);
end;

procedure TForm1.Button7Click(Sender: TObject);
begin
 form1.visible:=false;
 journal.visible:=false; 
 form5.showmodal;
end;

procedure TForm1.Button8Click(Sender: TObject);
begin
form4.Top:=form1.Top;
form4.Left:=form1.Left-110;
 form4.showmodal;
end;

procedure TForm1.Timer2Timer(Sender: TObject);
begin
 if stime=':' then stime:=' ' else stime:=':';
end;

procedure TForm1.Button9Click(Sender: TObject);
begin
base.showmodal;
end;

function tform1.OpenCD(Drive: Char): Boolean;
var

  Res: MciError;
  OpenParm: TMCI_Open_Parms; 
  Flags: DWord; 
  S: string; 
  DeviceID: Word; 
begin 

  Result := false; 
  S := Drive + ':'; 
  Flags := mci_Open_Type or mci_Open_Element; 
  with OpenParm do 
  begin 
    dwCallback := 0; 
    lpstrDeviceType := 'CDAudio'; 
    lpstrElementName := PChar(S); 
  end; 
  Res := mciSendCommand(0, mci_Open, Flags, Longint(@OpenParm)); 
  if Res <> 0 then 
    exit; 
  DeviceID := OpenParm.wDeviceID; 
  try 
    Res := mciSendCommand(DeviceID, MCI_SET, MCI_SET_DOOR_OPEN, 0); 
    if Res = 0 then 
      exit; 
    Result := True; 
  finally 
    mciSendCommand(DeviceID, mci_Close, Flags, Longint(@OpenParm)); 
  end; 
end; 

function tform1.CloseCD(Drive: Char): Boolean;
var 

  Res: MciError; 
  OpenParm: TMCI_Open_Parms; 
  Flags: DWord; 
  S: string; 
  DeviceID: Word; 
begin 

  Result := false; 
  S := Drive + ':'; 
  Flags := mci_Open_Type or mci_Open_Element; 
  with OpenParm do 
  begin 
    dwCallback := 0; 
    lpstrDeviceType := 'CDAudio'; 
    lpstrElementName := PChar(S); 
  end; 
  Res := mciSendCommand(0, mci_Open, Flags, Longint(@OpenParm)); 
  if Res <> 0 then 
    exit; 
  DeviceID := OpenParm.wDeviceID; 
  try 
    Res := mciSendCommand(DeviceID, MCI_SET, MCI_SET_DOOR_CLOSED, 0); 
    if Res = 0 then 
      exit; 
    Result := True; 
  finally 
    mciSendCommand(DeviceID, mci_Close, Flags, Longint(@OpenParm)); 
  end; 
end; 

procedure TForm1.Button10Click(Sender: TObject);
begin
 if length(combobox1.Text)=2 then begin
  if cdopen=false then
   begin OpenCD(combobox1.Text[1]);
         cdopen:=true;
         end
         else begin
     CloseCD(combobox1.Text[1]); cdopen:=false;end;
 end;
end;

procedure TForm1.N13Click(Sender: TObject);
begin
 if n13.checked=true then begin journal.hide;n13.checked:=false;end
 else begin journal.show; form1.n13.checked:=true;end;
end;


function tform1.VolumeID(DriveChar: Char): string;
var
  OldErrorMode: Integer;
  NotUsed, VolFlags: DWORD;
  Buf: array [0..MAX_PATH] of Char;
begin
  OldErrorMode := SetErrorMode(SEM_FAILCRITICALERRORS);
  try
    Buf[0] := #$00;
    if GetVolumeInformation(PChar(DriveChar + ':\'), Buf, DWORD(sizeof(Buf)),
      nil, NotUsed, VolFlags, nil, 0) then
      SetString(Result, Buf, StrLen(Buf))
    else Result := '';
    if DriveChar < 'a' then
      Result := AnsiUpperCaseFileName(Result)
    else
      Result := AnsiLowerCaseFileName(Result);
//    Result := Format('[%s]',[Result]);
  finally
    SetErrorMode(OldErrorMode);
  end;
end;

procedure TForm1.FormClose(Sender: TObject; var Action: TCloseAction);
begin
 savejournal;
 journal.close;
 StringList.free;
end;

end.


