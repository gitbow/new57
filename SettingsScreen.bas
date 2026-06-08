B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=13.5
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: False
#End Region

Sub Process_Globals
End Sub

Sub Globals
	Private btnBack As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.Color = Colors.RGB(40, 40, 40)
	
	' Simple back button at top
	btnBack.Initialize("btnBack")
	btnBack.Text = "< BACK"
	btnBack.TextSize = 18
	btnBack.Color = Colors.Blue
	btnBack.TextColor = Colors.White
	Activity.AddView(btnBack, 10dip, 10dip, 100dip, 50dip)
	
	' Title
	Dim lblTitle As Label
	lblTitle.Initialize("")
	lblTitle.Text = "SETTINGS"
	lblTitle.TextColor = Colors.White
	lblTitle.TextSize = 22
	lblTitle.Gravity = Gravity.CENTER
	Activity.AddView(lblTitle, 0, 10dip, 100%x, 50dip)
	
	' Create 25 rows manually (no scrollview for now to test)
	Dim startY As Int = 80dip
	Dim rowH As Int = 55dip
	
	For i = 0 To 24
		Dim yPos As Int = startY + (i * rowH)
		
		' Only show if fits on screen (first 15 rows)
		If yPos + rowH < 100%y - 20dip Then
			' Row background
			Dim pnl As Panel
			pnl.Initialize("")
			If i Mod 2 = 0 Then
				pnl.Color = Colors.RGB(50, 50, 50)
			Else
				pnl.Color = Colors.RGB(60, 60, 60)
			End If
			Activity.AddView(pnl, 10dip, yPos, 100%x - 20dip, rowH - 2dip)
			
			' Label
			Dim lbl As Label
			lbl.Initialize("")
			lbl.Text = "Option " & (i+1)
			lbl.TextColor = Colors.White
			lbl.TextSize = 16
			lbl.Gravity = Gravity.CENTER_VERTICAL
			pnl.AddView(lbl, 15dip, 0, 180dip, rowH)
			
			' Button
			Dim btn As Button
			btn.Initialize("btnToggle")
			btn.Tag = i
			btn.Text = "OFF"
			btn.TextSize = 14
			btn.Color = Colors.RGB(150, 0, 0)
			btn.TextColor = Colors.White
			pnl.AddView(btn, pnl.Width - 80dip, (rowH - 36dip)/2, 70dip, 36dip)
		End If
	Next
End Sub

Sub btnToggle_Click
	Dim btn As Button = Sender
	
	If btn.Text = "OFF" Then
		btn.Text = "ON"
		btn.Color = Colors.RGB(0, 150, 0)
		ToastMessageShow("Option " & (btn.Tag + 1) & " ENABLED", True)
	Else
		btn.Text = "OFF"
		btn.Color = Colors.RGB(150, 0, 0)
		ToastMessageShow("Option " & (btn.Tag + 1) & " DISABLED", True)
	End If
End Sub

Sub btnBack_Click
	Activity.Finish
End Sub