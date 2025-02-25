object Base: TBase
  Left = 192
  Top = 114
  Width = 697
  Height = 500
  Caption = #1041#1072#1079#1072' '#1076#1072#1085#1085#1099#1093
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -11
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  OldCreateOrder = False
  OnShow = FormShow
  PixelsPerInch = 96
  TextHeight = 13
  object Splitter1: TSplitter
    Left = 369
    Top = 36
    Width = 16
    Height = 430
    Cursor = crHSplit
    Beveled = True
  end
  object Panel2: TPanel
    Left = 385
    Top = 36
    Width = 304
    Height = 430
    Align = alClient
    TabOrder = 1
    object Memo1: TRichEdit
      Left = 1
      Top = 1
      Width = 302
      Height = 428
      Align = alClient
      Font.Charset = RUSSIAN_CHARSET
      Font.Color = clWindowText
      Font.Height = -11
      Font.Name = 'Arial'
      Font.Style = []
      ParentFont = False
      ScrollBars = ssVertical
      TabOrder = 0
      OnExit = Memo1Exit
    end
  end
  object Panel1: TPanel
    Left = 0
    Top = 36
    Width = 369
    Height = 430
    Align = alLeft
    TabOrder = 0
    object CheckListBox1: TCheckListBox
      Left = 1
      Top = 1
      Width = 367
      Height = 428
      Align = alClient
      ItemHeight = 13
      TabOrder = 0
      OnClick = CheckListBox1Click
    end
  end
  object Panel3: TPanel
    Left = 0
    Top = 0
    Width = 689
    Height = 36
    Align = alTop
    TabOrder = 2
    object Button1: TButton
      Left = 577
      Top = 6
      Width = 75
      Height = 25
      Caption = #1057#1086#1093#1088#1072#1085#1080#1090#1100
      TabOrder = 0
      Visible = False
      OnClick = Button1Click
    end
    object Button2: TButton
      Left = 432
      Top = 5
      Width = 75
      Height = 25
      Caption = #1047#1072#1082#1088#1099#1090#1100
      TabOrder = 1
      OnClick = Button2Click
    end
    object RadioGroup1: TRadioGroup
      Left = 2
      Top = -2
      Width = 201
      Height = 35
      Columns = 2
      ItemIndex = 0
      Items.Strings = (
        #1041#1072#1079#1072' '#1076#1072#1085#1085#1099#1093
        #1052#1086#1103' '#1073#1072#1079#1072)
      TabOrder = 2
      OnClick = RadioGroup1Click
    end
    object Panel4: TPanel
      Left = 205
      Top = 3
      Width = 220
      Height = 30
      BevelInner = bvRaised
      BevelOuter = bvLowered
      Ctl3D = False
      ParentCtl3D = False
      TabOrder = 3
      object Label1: TLabel
        Left = 8
        Top = 13
        Width = 205
        Height = 13
        Caption = #1084#1086#1078#1085#1086' '#1088#1072#1089#1090#1103#1075#1080#1074#1072#1090#1100' '#1085#1072' '#1089#1074#1086#1105' '#1091#1089#1084#1086#1090#1088#1077#1085#1080#1077
      end
      object Label2: TLabel
        Left = 51
        Top = 2
        Width = 122
        Height = 13
        Caption = #1054#1082#1085#1086' '#1080' '#1077#1075#1086' '#1089#1086#1076#1077#1088#1078#1080#1084#1086#1077
      end
    end
  end
end
