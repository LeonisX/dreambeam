object Journal: TJournal
  Left = 708
  Top = 148
  Width = 240
  Height = 326
  Caption = #1046#1091#1088#1085#1072#1083
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -11
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  OldCreateOrder = False
  OnClose = FormClose
  OnShow = FormShow
  PixelsPerInch = 96
  TextHeight = 13
  object Panel3: TPanel
    Left = 0
    Top = 0
    Width = 232
    Height = 292
    Align = alClient
    BevelInner = bvLowered
    BorderWidth = 1
    TabOrder = 0
    object Memo2: TRichEdit
      Left = 3
      Top = 3
      Width = 226
      Height = 286
      Align = alClient
      BevelInner = bvNone
      BevelOuter = bvNone
      BorderStyle = bsNone
      Ctl3D = False
      Font.Charset = RUSSIAN_CHARSET
      Font.Color = clWindowText
      Font.Height = -9
      Font.Name = 'Arial'
      Font.Style = []
      ParentCtl3D = False
      ParentFont = False
      ScrollBars = ssVertical
      TabOrder = 0
    end
  end
  object FormMagnet1: TFormMagnet
    Active = True
    Glue = False
    FormDragable = True
    MagnetType = mkOnMoving
    Power = 20
    ScreenMagnet.Area = saWorkArea
    ScreenMagnet.DesktopPower = 15
    ScreenMagnet.Top = True
    ScreenMagnet.Bottom = True
    ScreenMagnet.Left = True
    ScreenMagnet.Right = True
    Left = 112
    Top = 56
  end
end
