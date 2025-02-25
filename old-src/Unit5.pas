unit Unit5;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls;

type
  TForm5 = class(TForm)
    Button1: TButton;
    Label1: TLabel;
    procedure Button1Click(Sender: TObject);
    procedure FormShow(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  Form5: TForm5;
  t5,t51:cardinal;
implementation

uses Main, Unit7;

{$R *.dfm}

procedure TForm5.Button1Click(Sender: TObject);
begin
 t1:=GetTickCount;
 t:=t1-(t51-t5);
 form1.visible:=true;
 journal.visible:=true; 
 close;
end;

procedure TForm5.FormShow(Sender: TObject);
begin
 t5:=t;
 t51:=t1;
end;

end.
