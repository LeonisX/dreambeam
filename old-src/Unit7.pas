unit Unit7;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls, ComCtrls, ExtCtrls, FormMagnet;

type
  TJournal = class(TForm)
    Panel3: TPanel;
    Memo2: TRichEdit;
    FormMagnet1: TFormMagnet;
    procedure FormShow(Sender: TObject);
    procedure FormClose(Sender: TObject; var Action: TCloseAction);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  Journal: TJournal;

implementation

uses Main;

{$R *.dfm}

procedure TJournal.FormShow(Sender: TObject);
begin
 journal.Left:=form1.Left+form1.Width;
 journal.Top:=form1.Top;
 journal.height:=form1.height;
end;

procedure TJournal.FormClose(Sender: TObject; var Action: TCloseAction);
begin
 form1.n13.checked:=false;
end;

end.
