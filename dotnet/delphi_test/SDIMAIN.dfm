object SDIAppForm: TSDIAppForm
  Left = 197
  Top = 111
  Width = 634
  Height = 718
  Caption = 'SDI Application'
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -13
  Font.Name = 'System'
  Font.Style = []
  OldCreateOrder = False
  OnShow = FormShow
  PixelsPerInch = 96
  TextHeight = 16
  object Label1: TLabel
    Left = 16
    Top = 40
    Width = 72
    Height = 16
    Caption = 'Pozadavek'
  end
  object Label2: TLabel
    Left = 16
    Top = 240
    Width = 58
    Height = 16
    Caption = 'Odpoved'
  end
  object Label3: TLabel
    Left = 16
    Top = 424
    Width = 27
    Height = 16
    Caption = 'PKP'
  end
  object Label4: TLabel
    Left = 16
    Top = 536
    Width = 28
    Height = 16
    Caption = 'BKP'
  end
  object Label5: TLabel
    Left = 16
    Top = 600
    Width = 21
    Height = 16
    Caption = 'FIK'
  end
  object Button2: TButton
    Left = 16
    Top = 8
    Width = 75
    Height = 25
    Caption = 'Start'
    TabOrder = 0
    OnClick = Button2Click
  end
  object Memo1: TMemo
    Left = 16
    Top = 64
    Width = 577
    Height = 169
    Lines.Strings = (
      'Memo1')
    TabOrder = 1
  end
  object Memo2: TMemo
    Left = 16
    Top = 256
    Width = 577
    Height = 161
    Lines.Strings = (
      'Memo2')
    TabOrder = 2
  end
  object Memo3: TMemo
    Left = 16
    Top = 440
    Width = 577
    Height = 89
    Lines.Strings = (
      'Memo3')
    TabOrder = 3
  end
  object Memo4: TMemo
    Left = 16
    Top = 552
    Width = 577
    Height = 41
    Lines.Strings = (
      'Memo4')
    TabOrder = 4
  end
  object Memo5: TMemo
    Left = 16
    Top = 616
    Width = 577
    Height = 49
    Lines.Strings = (
      'Memo5')
    TabOrder = 5
  end
end
