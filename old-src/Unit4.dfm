object Form4: TForm4
  Left = 201
  Top = 129
  Width = 653
  Height = 433
  Caption = #1052#1072#1089#1090#1077#1088' '#1085#1072#1079#1074#1072#1085#1080#1103' '#1076#1080#1089#1082#1072
  Color = clBtnFace
  Font.Charset = RUSSIAN_CHARSET
  Font.Color = clWindowText
  Font.Height = -12
  Font.Name = 'Arial'
  Font.Style = []
  OldCreateOrder = False
  OnShow = FormShow
  PixelsPerInch = 96
  TextHeight = 15
  object Panel1: TPanel
    Left = 0
    Top = 25
    Width = 645
    Height = 324
    Align = alClient
    BevelOuter = bvLowered
    TabOrder = 0
    object Panel3: TPanel
      Left = 372
      Top = 1
      Width = 272
      Height = 322
      Align = alRight
      TabOrder = 0
      object RadioGroup3: TRadioGroup
        Left = 8
        Top = 0
        Width = 257
        Height = 60
        Caption = ' '#1071#1079#1099#1082' '
        Columns = 4
        ItemIndex = 0
        Items.Strings = (
          'Rus'
          'Eng'
          'Jap'
          'Fre'
          'Ger'
          'Spa'
          'Multi')
        TabOrder = 0
        OnClick = RadioGroup3Click
      end
      object Panel4: TPanel
        Left = 8
        Top = 66
        Width = 161
        Height = 218
        TabOrder = 1
        object Label4: TLabel
          Left = 76
          Top = 45
          Width = 36
          Height = 14
          Caption = '(NoRG)'
          Font.Charset = RUSSIAN_CHARSET
          Font.Color = clWindowText
          Font.Height = -11
          Font.Name = 'Arial'
          Font.Style = []
          ParentFont = False
        end
        object Label5: TLabel
          Left = 69
          Top = 8
          Width = 73
          Height = 14
          Caption = '(=RusPerevod)'
          Font.Charset = RUSSIAN_CHARSET
          Font.Color = clWindowText
          Font.Height = -11
          Font.Name = 'Arial'
          Font.Style = []
          ParentFont = False
        end
        object Label7: TLabel
          Left = 96
          Top = 100
          Width = 48
          Height = 14
          Caption = '( =Kudos)'
          Font.Charset = RUSSIAN_CHARSET
          Font.Color = clWindowText
          Font.Height = -11
          Font.Name = 'Arial'
          Font.Style = []
          ParentFont = False
        end
        object RadioButton1: TRadioButton
          Left = 8
          Top = 7
          Width = 61
          Height = 17
          Caption = 'Kudos'
          Checked = True
          TabOrder = 0
          TabStop = True
          OnClick = RadioButton1Click
        end
        object RadioButton2: TRadioButton
          Left = 8
          Top = 26
          Width = 68
          Height = 17
          Caption = 'RGR'
          TabOrder = 1
          OnClick = RadioButton1Click
        end
        object RadioButton3: TRadioButton
          Left = 8
          Top = 44
          Width = 68
          Height = 17
          Caption = 'Vector'
          TabOrder = 2
          OnClick = RadioButton1Click
        end
        object RadioButton5: TRadioButton
          Left = 8
          Top = 128
          Width = 81
          Height = 17
          Caption = #1053#1077' '#1079#1085#1072#1102
          TabOrder = 3
          OnClick = RadioButton1Click
        end
        object RadioButton6: TRadioButton
          Left = 8
          Top = 157
          Width = 73
          Height = 17
          Caption = #1044#1088#1091#1075#1086#1081
          TabOrder = 4
          OnClick = RadioButton1Click
        end
        object Edit2: TEdit
          Left = 6
          Top = 183
          Width = 147
          Height = 23
          TabOrder = 5
          Text = 'Kudos'
          OnChange = Edit2Change
        end
        object RadioButton11: TRadioButton
          Left = 8
          Top = 63
          Width = 113
          Height = 17
          Caption = 'Studia Max'
          TabOrder = 6
          OnClick = RadioButton1Click
        end
        object RadioButton12: TRadioButton
          Left = 8
          Top = 82
          Width = 113
          Height = 17
          Caption = 'Paradox'
          TabOrder = 7
          OnClick = RadioButton1Click
        end
        object RadioButton13: TRadioButton
          Left = 8
          Top = 101
          Width = 84
          Height = 17
          Caption = 'Red Station'
          TabOrder = 8
          OnClick = RadioButton1Click
        end
      end
      object Panel5: TPanel
        Left = 8
        Top = 66
        Width = 161
        Height = 218
        TabOrder = 2
        Visible = False
        object Bevel1: TBevel
          Left = 8
          Top = 146
          Width = 145
          Height = 7
          Shape = bsTopLine
        end
        object Label3: TLabel
          Left = 8
          Top = 160
          Width = 111
          Height = 15
          Caption = #1048#1079#1076#1072#1090#1077#1083#1100' ('#1088#1080#1087#1087#1077#1088'):'
        end
        object RadioButton4: TRadioButton
          Left = 8
          Top = 88
          Width = 89
          Height = 17
          Caption = #1053#1077' '#1079#1085#1072#1102
          TabOrder = 0
          OnClick = RadioButton9Click
        end
        object RadioButton7: TRadioButton
          Left = 8
          Top = 49
          Width = 68
          Height = 17
          Caption = 'NTSC-J'
          TabOrder = 1
          OnClick = RadioButton9Click
        end
        object RadioButton8: TRadioButton
          Left = 8
          Top = 28
          Width = 68
          Height = 17
          Caption = 'NTSC-U'
          TabOrder = 2
          OnClick = RadioButton9Click
        end
        object RadioButton9: TRadioButton
          Left = 8
          Top = 7
          Width = 68
          Height = 17
          Caption = 'PAL-E'
          Checked = True
          TabOrder = 3
          TabStop = True
          OnClick = RadioButton9Click
        end
        object RadioButton10: TRadioButton
          Left = 8
          Top = 68
          Width = 113
          Height = 17
          Caption = 'NTSC-U, PAL-E'
          TabOrder = 4
          OnClick = RadioButton9Click
        end
        object Edit5: TEdit
          Left = 8
          Top = 113
          Width = 81
          Height = 23
          ReadOnly = True
          TabOrder = 5
          Text = 'PAL-E'
          OnChange = Edit2Change
        end
        object Edit6: TEdit
          Left = 8
          Top = 184
          Width = 121
          Height = 23
          TabOrder = 6
          Text = '-'
          OnChange = Edit2Change
        end
      end
      object Panel6: TPanel
        Left = 176
        Top = 66
        Width = 89
        Height = 218
        TabOrder = 3
        object Label1: TLabel
          Left = 16
          Top = 6
          Width = 43
          Height = 15
          Caption = #1044#1080#1089#1082#1086#1074':'
        end
        object Label2: TLabel
          Left = 10
          Top = 55
          Width = 46
          Height = 15
          Caption = '# '#1076#1080#1089#1082#1072':'
          Visible = False
        end
        object Label6: TLabel
          Left = 8
          Top = 189
          Width = 70
          Height = 14
          Caption = '('#1073#1077#1089#1087#1083#1072#1090#1085#1099#1077')'
          Font.Charset = RUSSIAN_CHARSET
          Font.Color = clWindowText
          Font.Height = -11
          Font.Name = 'Arial'
          Font.Style = []
          ParentFont = False
        end
        object Bevel2: TBevel
          Left = 8
          Top = 146
          Width = 73
          Height = 2
        end
        object Edit3: TEdit
          Left = 15
          Top = 24
          Width = 33
          Height = 23
          ReadOnly = True
          TabOrder = 0
          Text = '1'
          OnChange = Edit2Change
        end
        object UpDown1: TUpDown
          Left = 48
          Top = 24
          Width = 16
          Height = 23
          Associate = Edit3
          Min = 1
          Max = 16
          Position = 1
          TabOrder = 1
          Wrap = False
          OnClick = UpDown1Click
        end
        object Edit4: TEdit
          Left = 15
          Top = 72
          Width = 33
          Height = 23
          ReadOnly = True
          TabOrder = 2
          Text = '1'
          Visible = False
          OnChange = Edit2Change
        end
        object UpDown2: TUpDown
          Left = 48
          Top = 72
          Width = 16
          Height = 23
          Associate = Edit4
          Min = 1
          Max = 1
          Position = 1
          TabOrder = 3
          Visible = False
          Wrap = False
        end
        object CheckBox1: TCheckBox
          Left = 6
          Top = 171
          Width = 75
          Height = 17
          Caption = 'Homebrew'
          Font.Charset = RUSSIAN_CHARSET
          Font.Color = clWindowText
          Font.Height = -11
          Font.Name = 'Arial'
          Font.Style = []
          ParentFont = False
          TabOrder = 4
          OnClick = Edit2Change
        end
      end
    end
    object ListBox1: TListBox
      Left = 1
      Top = 1
      Width = 371
      Height = 322
      Align = alClient
      Font.Charset = RUSSIAN_CHARSET
      Font.Color = clWindowText
      Font.Height = -11
      Font.Name = 'Arial'
      Font.Style = []
      ItemHeight = 14
      ParentFont = False
      TabOrder = 1
      OnClick = ListBox1Click
    end
  end
  object Panel2: TPanel
    Left = 0
    Top = 0
    Width = 645
    Height = 25
    Align = alTop
    Caption = #1053#1072#1079#1086#1074#1105#1084' '#1087#1088#1072#1074#1080#1083#1100#1085#1086' '#1076#1080#1089#1082
    Font.Charset = RUSSIAN_CHARSET
    Font.Color = clWindowText
    Font.Height = -16
    Font.Name = 'Arial'
    Font.Style = [fsBold, fsItalic]
    ParentFont = False
    TabOrder = 1
  end
  object GroupBox1: TGroupBox
    Left = 0
    Top = 349
    Width = 645
    Height = 50
    Align = alBottom
    Caption = ' '#1056#1077#1079#1091#1083#1100#1090#1072#1090' '
    TabOrder = 2
    object Edit1: TEdit
      Left = 338
      Top = 17
      Width = 194
      Height = 23
      TabOrder = 0
    end
    object Button1: TButton
      Left = 535
      Top = 16
      Width = 48
      Height = 25
      Caption = #1043#1086#1090#1086#1074#1086'!'
      TabOrder = 1
      OnClick = Button1Click
    end
    object Button2: TButton
      Left = 586
      Top = 16
      Width = 53
      Height = 25
      Caption = #1047#1072#1082#1088#1099#1090#1100
      TabOrder = 2
      OnClick = Button2Click
    end
    object Edit7: TEdit
      Left = 2
      Top = 17
      Width = 335
      Height = 23
      TabOrder = 3
      Text = #1053#1072#1079#1074#1072#1085#1080#1077' '#1080#1075#1088#1099
    end
  end
end
