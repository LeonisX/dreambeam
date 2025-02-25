unit Unit8;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls, CheckLst, ComCtrls, ToolWin, ImgList, ExtCtrls;

type
  TBase = class(TForm)
    Splitter1: TSplitter;
    Panel1: TPanel;
    CheckListBox1: TCheckListBox;
    Panel2: TPanel;
    Memo1: TRichEdit;
    Panel3: TPanel;
    Button1: TButton;
    Button2: TButton;
    RadioGroup1: TRadioGroup;
    Panel4: TPanel;
    Label1: TLabel;
    Label2: TLabel;
    procedure FormShow(Sender: TObject);
    procedure CheckListBox1Click(Sender: TObject);
    procedure Button2Click(Sender: TObject);
    procedure Button1Click(Sender: TObject);
    procedure RadioGroup1Click(Sender: TObject);
    procedure Memo1Exit(Sender: TObject);
  private
    { Private declarations }
    procedure fillbase;
    procedure fillbase2;
  public
    { Public declarations }
  end;

var
  Base: TBase;
  s:string;
  ki:word;

implementation

uses Main;
{$R *.dfm}

procedure TBase.fillbase;
var
i,k:word;
begin
checklistbox1.Items.clear;
  if radiogroup1.itemindex=0 then k:=length(d_games)
  else k:=length(d_gamesu);

  if k>0 then begin
for i:=0 to k-1 do
 begin
  if radiogroup1.itemindex=0 then checklistbox1.Items.add(d_games[i])
  else checklistbox1.Items.add(d_gamesu[i]);
 end;
end;
end;

procedure TBase.fillbase2;
var
i,k:word;
s1:string;
begin
  if radiogroup1.itemindex=0 then k:=length(d_games)
  else k:=length(d_gamesu);

if k>0 then begin
for i:=0 to k-1 do
 begin
  if radiogroup1.itemindex=0 then s1:=d_crcs[i]
  else s1:=d_crcsu[i];

 if fileexists(s+s1) then
  checklistbox1.checked[i]:=true else checklistbox1.checked[i]:=false;
 end;
 if ki>=checklistbox1.Items.count then ki:=checklistbox1.Items.count-1;
 checklistbox1.ItemIndex:=ki;
 CheckListBox1Click(Self);
end;
end;

procedure TBase.FormShow(Sender: TObject);
begin
 s:=spath+'Base\txtz\';
 ki:=0;
 fillbase;
 fillbase2;
end;

procedure TBase.CheckListBox1Click(Sender: TObject);
var s1,s2:string;
begin
  if radiogroup1.itemindex=0 then s2:=d_crcs[CheckListBox1.itemindex]
  else s2:=d_crcsu[CheckListBox1.itemindex];

 s1:=s+s2;
 if CheckListBox1.Checked[CheckListBox1.itemindex]=true then begin if fileexists(s1) then memo1.lines.loadfromfile(s1) end
  else memo1.text:='';
end;

procedure TBase.Button2Click(Sender: TObject);
begin
close;
end;

procedure TBase.Button1Click(Sender: TObject);
var s1,s2:string;
begin
 memo1.PlainText:=true;
 ki:=CheckListBox1.itemindex;

  if radiogroup1.itemindex=0 then s2:=d_crcs[ki]
  else s2:=d_crcsu[ki];

 s1:=s+s2;
 if memo1.text='' then if fileexists(s1) then deletefile(s1);
 if memo1.text<>'' then memo1.lines.savetofile(s1);
 fillbase2;
memo1.PlainText:=false; 
end;


procedure TBase.RadioGroup1Click(Sender: TObject);
begin
 ki:=CheckListBox1.itemindex;
 fillbase;
 fillbase2;
end;


procedure TBase.Memo1Exit(Sender: TObject);
begin
Button1Click(Self);
end;

end.
