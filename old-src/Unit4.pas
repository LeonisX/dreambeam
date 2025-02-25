unit Unit4;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls, ExtCtrls, ComCtrls;

type
  TForm4 = class(TForm)
    Panel1: TPanel;
    Panel2: TPanel;
    GroupBox1: TGroupBox;
    Edit1: TEdit;
    Button1: TButton;
    Panel3: TPanel;
    RadioGroup3: TRadioGroup;
    Panel4: TPanel;
    Panel5: TPanel;
    RadioButton1: TRadioButton;
    RadioButton2: TRadioButton;
    RadioButton3: TRadioButton;
    RadioButton5: TRadioButton;
    RadioButton6: TRadioButton;
    Edit2: TEdit;
    Panel6: TPanel;
    Edit3: TEdit;
    UpDown1: TUpDown;
    Label1: TLabel;
    Label2: TLabel;
    Edit4: TEdit;
    UpDown2: TUpDown;
    RadioButton4: TRadioButton;
    RadioButton7: TRadioButton;
    RadioButton8: TRadioButton;
    RadioButton9: TRadioButton;
    RadioButton10: TRadioButton;
    Edit5: TEdit;
    CheckBox1: TCheckBox;
    Bevel1: TBevel;
    Label3: TLabel;
    Edit6: TEdit;
    Button2: TButton;
    ListBox1: TListBox;
    Edit7: TEdit;
    Label4: TLabel;
    Label5: TLabel;
    Label6: TLabel;
    RadioButton11: TRadioButton;
    RadioButton12: TRadioButton;
    Bevel2: TBevel;
    RadioButton13: TRadioButton;
    Label7: TLabel;
    procedure RadioGroup3Click(Sender: TObject);
    procedure UpDown1Click(Sender: TObject; Button: TUDBtnType);
    procedure RadioButton1Click(Sender: TObject);
    procedure RadioButton9Click(Sender: TObject);
    procedure FormShow(Sender: TObject);
    procedure Button2Click(Sender: TObject);
    procedure Edit2Change(Sender: TObject);
    procedure Button1Click(Sender: TObject);
    procedure ListBox1Click(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  Form4: TForm4;


implementation
  uses main, Unit7;
{$R *.dfm}

procedure TForm4.RadioGroup3Click(Sender: TObject);
begin
 if RadioGroup3.itemindex<>0 then begin
  panel4.Visible:=false;
  panel5.visible:=true;
  end else
begin
  panel4.Visible:=true;
  panel5.visible:=false;
  end;
  edit2change(self);
end;

procedure TForm4.UpDown1Click(Sender: TObject; Button: TUDBtnType);
begin
 updown2.Max:=strtoint(edit3.text);
 if strtoint(edit4.text)>updown2.Max then edit4.text:=inttostr(updown2.Max);

end;

procedure TForm4.RadioButton1Click(Sender: TObject);
var s, s1:string;
begin
 s:=(sender as tradiobutton).caption;
 s1:=(sender as tradiobutton).name;
 edit2.text:=s;
 if s1='RadioButton5' then edit2.text:='-';
 if s1='RadioButton6' then edit2.text:='';
end;

procedure TForm4.RadioButton9Click(Sender: TObject);
var s, s1:string;
begin
 s:=(sender as tradiobutton).caption;
 s1:=(sender as tradiobutton).name;
 edit5.text:=s;

 if s1='RadioButton4' then edit5.text:='-';
end;

procedure TForm4.FormShow(Sender: TObject);
var i:word;
s, s1:string;
begin
listbox1.Items.clear;
s:='';
for i:=0 to length(d_games)-1 do
 begin
 s1:=form1.deattr(d_games[i]);
 if ((s1<>'') and (s1<>s)) then listbox1.Items.add(s1);
 s:=s1;
 end;
 ListBox1.ItemIndex:=0;
end;

procedure TForm4.Button2Click(Sender: TObject);
begin
 form4.Close;
end;


procedure TForm4.Edit2Change(Sender: TObject);
begin
 edit1.text:='';
 if edit3.text<>'1' then begin label2.Visible:=true;edit4.Visible:=true;updown2.Visible:=true;edit1.Text:=edit1.text+' (Disc '+edit4.Text+' of '+edit3.text+')';end
 else begin label2.Visible:=false;edit4.Visible:=false;updown2.Visible:=false;end;
 if radiogroup3.itemindex=0 then
  begin
   edit1.Text:=edit1.text+' ('+radiogroup3.Items[radiogroup3.itemindex]+')';
   if edit2.text<>'' then    edit1.Text:=edit1.text+' ('+edit2.text+')';
  end
  else
  begin
   if edit5.text<>'' then    edit1.Text:=edit1.text+' ('+edit5.text+')';  
   edit1.Text:=edit1.text+' ('+radiogroup3.Items[radiogroup3.itemindex]+')';
   if edit6.text<>'' then    edit1.Text:=edit1.text+' ('+edit6.text+')';
  end;
 if checkbox1.checked then edit1.Text:=edit1.text+' ('+checkbox1.Caption+')';
end;

procedure TForm4.Button1Click(Sender: TObject);
begin
 form1.Edit1.text:=edit7.text+edit1.text;
 journal.memo2.lines.add('Дали название образу:');
 journal.memo2.lines.add(form1.Edit1.text);
 close;
end;

procedure TForm4.ListBox1Click(Sender: TObject);
begin
  if listbox1.itemindex>=0 then edit7.text:=listbox1.Items[listbox1.itemindex];
end;

end.
