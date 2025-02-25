unit Unit2;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls, ComCtrls;

type
  TForm2 = class(TForm)
    Memo1: TRichEdit;
    procedure Memo1KeyPress(Sender: TObject; var Key: Char);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  Form2: TForm2;
     zflag:boolean;
  zbreak:boolean;
  fsize:cardinal;
  zsize:cardinal;
implementation

{$R *.dfm}

procedure TForm2.Memo1KeyPress(Sender: TObject; var Key: Char);
begin
if key=#27 then form2.close;
end;

end.
 