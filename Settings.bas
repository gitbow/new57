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
	Private ScrollView1 As ScrollView
	Private btnBack As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.Color = Colors.RGB(40, 40, 40)
	
	' Back button
	btnBack.Initialize("btnBack")
	btnBack.Text = "< BACK"
	btnBack.TextSize = 18
	btnBack.Color = Colors.Blue
	btnBack.TextColor = Colors.White
	Activity.AddView(btnBack, 10dip, 10dip, 80dip, 50dip)
	
	' Scroll view
	ScrollView1.Initialize("")
	Activity.AddView(ScrollView1, 0, 70dip, 100%x, 100%y - 80dip)
	
	' Create 25 rows
	Dim rowHeight As Int = 60dip
	ScrollView1.Panel.Height = 25 * rowHeight
	
	For i = 0 To 24
		' Create row
		Dim pnl As Panel
		pnl.Initialize("")
		pnl.Color = Colors.RGB(60, 60, 60)
		ScrollView1.Panel.AddView(pnl, 5dip, i * rowHeight, 100%x - 10dip, rowHeight - 2dip)
		
		' Label
		Dim lbl As Label
		lbl.Initialize("")
		lbl.Text = "Option " & (i+1)
		lbl.TextColor = Colors.White
		lbl.TextSize = 16
		lbl.Gravity = Gravity.CENTER_VERTICAL
		pnl.AddView(lbl, 15dip, 0, 200dip, rowHeight)
		
		' Simple ON/OFF button
		Dim btn As Button
		btn.Initialize("btnOption")
		btn.Tag = i
		btn.TextSize = 14
		btn.Text = "OFF"
		btn.Color = Colors.RGB(100, 100, 100)
		btn.TextColor = Colors.White
		pnl.AddView(btn, pnl.Width - 70dip, (rowHeight - 36dip)/2, 60dip, 36dip)
	Next
End Sub

Sub btnOption_Click
	Dim btn As Button = Sender
	Dim idx As Int = btn.Tag
	
	' Toggle button
	If btn.Text = "OFF" Then
		btn.Text = "ON"
		btn.Color = Colors.Green
	Else
		btn.Text = "OFF"
		btn.Color = Colors.RGB(100, 100, 100)
	End If
	
	ToastMessageShow("Option " & (idx+1) & " = " & btn.Text, False)
End Sub

Sub btnBack_Click
	Activity.Finish
End Sub