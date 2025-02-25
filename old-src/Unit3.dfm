object Form3: TForm3
  Left = 207
  Top = 184
  Width = 773
  Height = 577
  Caption = #1057#1088#1072#1074#1085#1077#1085#1080#1077' '#1092#1072#1081#1083#1086#1074
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -11
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  OldCreateOrder = False
  Position = poScreenCenter
  OnShow = FormShow
  PixelsPerInch = 96
  TextHeight = 13
  object GroupBox1: TGroupBox
    Left = 6
    Top = 42
    Width = 371
    Height = 495
    Ctl3D = False
    Font.Charset = RUSSIAN_CHARSET
    Font.Color = clWindowText
    Font.Height = -9
    Font.Name = 'Arial'
    Font.Style = []
    ParentCtl3D = False
    ParentFont = False
    TabOrder = 0
    object ListBox1: TListBox
      Left = 8
      Top = 16
      Width = 353
      Height = 473
      Font.Charset = RUSSIAN_CHARSET
      Font.Color = clWindowText
      Font.Height = -9
      Font.Name = 'Arial'
      Font.Style = []
      ItemHeight = 12
      ParentFont = False
      TabOrder = 0
    end
    object Memo1: TRichEdit
      Left = 8
      Top = 16
      Width = 353
      Height = 473
      Font.Charset = RUSSIAN_CHARSET
      Font.Color = clWindowText
      Font.Height = -9
      Font.Name = 'Arial'
      Font.Style = []
      ParentFont = False
      ScrollBars = ssVertical
      TabOrder = 1
      Visible = False
      WordWrap = False
    end
  end
  object GroupBox2: TGroupBox
    Left = 386
    Top = 42
    Width = 375
    Height = 495
    Caption = ' '#1101#1090#1072#1083#1086#1085#1085#1072#1103' '#1073#1072#1079#1072' '
    Ctl3D = False
    Font.Charset = RUSSIAN_CHARSET
    Font.Color = clWindowText
    Font.Height = -9
    Font.Name = 'Arial'
    Font.Style = []
    ParentCtl3D = False
    ParentFont = False
    TabOrder = 1
    object ListBox2: TListBox
      Left = 8
      Top = 16
      Width = 361
      Height = 473
      Font.Charset = RUSSIAN_CHARSET
      Font.Color = clWindowText
      Font.Height = -9
      Font.Name = 'Arial'
      Font.Style = []
      ItemHeight = 12
      ParentFont = False
      TabOrder = 0
    end
    object Memo2: TRichEdit
      Left = 8
      Top = 16
      Width = 361
      Height = 473
      Font.Charset = RUSSIAN_CHARSET
      Font.Color = clWindowText
      Font.Height = -9
      Font.Name = 'Arial'
      Font.Style = []
      ParentFont = False
      ScrollBars = ssVertical
      TabOrder = 1
      Visible = False
      WordWrap = False
    end
  end
  object Button1: TButton
    Left = 8
    Top = 11
    Width = 75
    Height = 25
    Caption = #1057#1088#1072#1074#1085#1080#1090#1100
    TabOrder = 2
    OnClick = Button1Click
  end
  object Button2: TButton
    Left = 104
    Top = 11
    Width = 75
    Height = 25
    Caption = #1042#1077#1088#1085#1091#1090#1100#1089#1103
    TabOrder = 3
    OnClick = Button2Click
  end
  object Button3: TButton
    Left = 640
    Top = 11
    Width = 75
    Height = 25
    Caption = #1042#1099#1093#1086#1076
    TabOrder = 4
    OnClick = Button3Click
  end
  object CheckBox1: TCheckBox
    Left = 205
    Top = 3
    Width = 176
    Height = 15
    Caption = #1055#1086#1082#1072#1079#1099#1074#1072#1090#1100' '#1090#1086#1083#1100#1082#1086' '#1088#1072#1079#1083#1080#1095#1080#1103
    TabOrder = 5
  end
  object RadioGroup1: TRadioGroup
    Left = 205
    Top = 17
    Width = 172
    Height = 31
    Columns = 2
    Ctl3D = False
    ItemIndex = 0
    Items.Strings = (
      #1084#1086#1080
      #1073#1072#1079#1072)
    ParentCtl3D = False
    TabOrder = 6
    OnClick = RadioGroup1Click
  end
end
