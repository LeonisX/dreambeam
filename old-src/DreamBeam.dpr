program DreamBeam;

uses
  Forms,
  Main in 'main.pas' {Form1},
  Unit7 in 'Unit7.pas' {Journal},
  CRCinit in 'CRCInit.pas',
  Unit2 in 'Unit2.pas' {Form2},
  Unit3 in 'Unit3.pas' {Form3},
  Unit4 in 'Unit4.pas' {Form4},
  Unit5 in 'Unit5.pas' {Form5},
  Unit6 in 'Unit6.pas' {Form6},
  Unit8 in 'Unit8.pas' {Base};

{$R *.res}

begin
  Application.Initialize;
  Application.Title := 'DreamBeam';
  Application.CreateForm(TForm1, Form1);
  Application.CreateForm(TJournal, Journal);
  Application.CreateForm(TForm2, Form2);
  Application.CreateForm(TForm3, Form3);
  Application.CreateForm(TForm4, Form4);
  Application.CreateForm(TForm5, Form5);
  Application.CreateForm(TForm6, Form6);
  Application.CreateForm(TBase, Base);
  Application.Run;
end.
