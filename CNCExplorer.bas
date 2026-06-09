B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=13.5
@EndOfDesignText@
 

#Region  Activity Attributes 
    #FullScreen: True
    #IncludeTitle: False
#End Region

Sub Process_Globals
    ' This variable stores the path to your /android/gcode folder
    'Private CurrentDir As String = "/storage/emulated/0/android/gcode"
	Private CurrentDir As String = "/storage/emulated/0/DCIM/gcode"
End Sub

Sub Globals
    ' Panels loaded from your designer file
    Private pnlMain As Panel
    Private clvFiles As CustomListView ' Handled safely via the layout engine
    
    ' Programmatic Layout Views
    Private pnlTopBar As Panel
    Private lblStatus, lblProcessing, lblDateTime As Label
    Private pnlSourceBar As Panel
    Private pnlTableHeader As Panel
    Private lblHdrName, lblHdrSize, lblHdrTime As Label
    Private pnlRightMenu As Panel
    Private pnlBottomInfo As Panel
    Private lblHardDriveInfo, lblCurrentPath As Label
    Private pnlNavBar As Panel
    
    ' Theme Parameters matching your design
    Private COLOR_BG As Int = 0xFF2A2A2A
    Private COLOR_PANEL_BG As Int = 0xFF333333
    Private COLOR_TOP_BG As Int = 0xFF1F2326
    Private COLOR_TEXT_MUTED As Int = 0xFFBBBBBB
    Private COLOR_ORANGE As Int = 0xFFFFA500
    Private COLOR_CYAN As Int = 0xFF00A2B1
End Sub

Sub Activity_Create(FirstTime As Boolean)
    ' 1. Load the companion designer structure
    Activity.LoadLayout("main_layout")
    
    ' Stretch canvas grid
    pnlMain.SetLayoutAnimated(0, 0, 0, Activity.Width, Activity.Height)
    Activity.Color = COLOR_BG
    pnlMain.Color = COLOR_BG
    
    ' 2. Calculate coordinate grids
    Dim totalW As Int = Activity.Width
    Dim totalH As Int = Activity.Height
    Dim topBarH As Int = 40dip
    Dim sourceBarH As Int = 45dip
    Dim tableHeaderH As Int = 30dip
    Dim bottomInfoH As Int = 50dip
    Dim navBarH As Int = 50dip
    
    Dim leftContentW As Int = totalW * 0.80
    Dim rightMenuW As Int = totalW - leftContentW
    Dim mainContentH As Int = totalH - topBarH - sourceBarH - tableHeaderH - bottomInfoH - navBarH

    ' Position CustomListView safely
    clvFiles.GetBase.Left = 0
    clvFiles.GetBase.Top = topBarH + sourceBarH + tableHeaderH
    clvFiles.GetBase.Width = leftContentW
    clvFiles.GetBase.Height = mainContentH
    clvFiles.GetBase.Color = COLOR_BG
    clvFiles.sv.Color = COLOR_BG

    ' Render dashboard labels and buttons
    BuildUIElements(totalW, totalH, topBarH, sourceBarH, tableHeaderH, bottomInfoH, navBarH, leftContentW, rightMenuW, mainContentH)
    
	' -------------------------------------------------------------
	' ALL FILES ACCESS VERIFICATION LOOP FOR NEW57
	' -------------------------------------------------------------
	Dim phoneInstance As Phone ' Ensure "Phone" is checked in your Libs tab
	If phoneInstance.SdkVersion >= 30 Then
		Dim jo As JavaObject ' Ensure "JavaObject" is checked in your Libs tab
		jo.InitializeStatic("android.os.Environment")
        
		If jo.RunMethod("isExternalStorageManager", Null) = False Then
			ToastMessageShow("Please allow access to view your G-Code files", True)
            
			Dim intentInstance As Intent
			intentInstance.Initialize("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION", "package:" & Application.PackageName)
			StartActivity(intentInstance)
			Return
		End If
	End If

	' Scan the /android/gcode directory
	LoadDirectoryFiles
End Sub


Private Sub BuildUIElements(totalW As Int, totalH As Int, topBarH As Int, sourceBarH As Int, tableHeaderH As Int, bottomInfoH As Int, navBarH As Int, leftContentW As Int, rightMenuW As Int, mainContentH As Int)
    pnlTopBar.Initialize("")
    pnlTopBar.Color = COLOR_TOP_BG
    Activity.AddView(pnlTopBar, 0, 0, totalW, topBarH)
    
    lblStatus.Initialize("")
    lblStatus.Text = " Ready "
    lblStatus.TextColor = Colors.Black
    lblStatus.Color = COLOR_CYAN
    lblStatus.Gravity = Gravity.CENTER
    pnlTopBar.AddView(lblStatus, 0, 0, 90dip, topBarH)
    
    lblProcessing.Initialize("")
    lblProcessing.Text = "No file active"
    lblProcessing.TextColor = Colors.White
    lblProcessing.Gravity = Gravity.CENTER
    pnlTopBar.AddView(lblProcessing, 100dip, 0, totalW - 300dip, topBarH)
    
    lblDateTime.Initialize("")
    lblDateTime.Text = "2026-06-08 11:30"
    lblDateTime.TextColor = COLOR_TEXT_MUTED
    lblDateTime.Gravity = Gravity.CENTER_VERTICAL + Gravity.RIGHT
    pnlTopBar.AddView(lblDateTime, totalW - 200dip, 0, 190dip, topBarH)

    pnlSourceBar.Initialize("")
    pnlSourceBar.Color = 0xFF222222
    Activity.AddView(pnlSourceBar, 0, topBarH, totalW, sourceBarH)
    
    Dim btnLocal As Button = CreateButton("Local", COLOR_ORANGE, Colors.Black)
    pnlSourceBar.AddView(btnLocal, 5dip, 2dip, 100dip, sourceBarH - 4dip)
    
    Dim btnUDisk As Button = CreateButton("U disk", COLOR_PANEL_BG, Colors.White)
    pnlSourceBar.AddView(btnUDisk, 110dip, 2dip, 100dip, sourceBarH - 4dip)
    
    Dim btnNetwork As Button = CreateButton("Network", COLOR_PANEL_BG, Colors.White)
    pnlSourceBar.AddView(btnNetwork, 215dip, 2dip, 110dip, sourceBarH - 4dip)

    pnlTableHeader.Initialize("")
    pnlTableHeader.Color = COLOR_PANEL_BG
    Activity.AddView(pnlTableHeader, 0, topBarH + sourceBarH, leftContentW, tableHeaderH)
    
    Dim w1 As Int = leftContentW * 0.50
    Dim w2 As Int = leftContentW * 0.20
    Dim w3 As Int = leftContentW * 0.30
    
    lblHdrName = CreateLabel("Filename", COLOR_TEXT_MUTED, Gravity.LEFT + Gravity.CENTER_VERTICAL)
    pnlTableHeader.AddView(lblHdrName, 15dip, 0, w1 - 15dip, tableHeaderH)
    
    lblHdrSize = CreateLabel("File size", COLOR_TEXT_MUTED, Gravity.CENTER)
    pnlTableHeader.AddView(lblHdrSize, w1, 0, w2, tableHeaderH)
    
    lblHdrTime = CreateLabel("Modified Time", COLOR_TEXT_MUTED, Gravity.CENTER)
    pnlTableHeader.AddView(lblHdrTime, w1 + w2, 0, w3, tableHeaderH)

    pnlRightMenu.Initialize("")
    pnlRightMenu.Color = COLOR_PANEL_BG
    Activity.AddView(pnlRightMenu, leftContentW, topBarH + sourceBarH, rightMenuW, mainContentH + tableHeaderH)
    
    Dim menuActions() As String = Array As String("Remove", "Rename", "Edit", "New", "Unmount", "Copy", "Paste")
    Dim singleBtnH As Int = (mainContentH + tableHeaderH - 40dip) / 7
    
    For i = 0 To menuActions.Length - 1
        Dim btnAction As Button = CreateButton(menuActions(i), 0xFF44464A, Colors.White)
        pnlRightMenu.AddView(btnAction, 8dip, 5dip + (i * (singleBtnH + 4dip)), rightMenuW - 16dip, singleBtnH)
    Next

    pnlBottomInfo.Initialize("")
    pnlBottomInfo.Color = COLOR_PANEL_BG
    Activity.AddView(pnlBottomInfo, 0, totalH - navBarH - bottomInfoH, leftContentW, bottomInfoH)
    
    lblHardDriveInfo = CreateLabel("Total capacity: 5.9G Used: 4.8G Remaining: 1.2G", COLOR_TEXT_MUTED, Gravity.CENTER_VERTICAL)
    pnlBottomInfo.AddView(lblHardDriveInfo, 10dip, 2dip, leftContentW - 20dip, 20dip)
    
    lblCurrentPath = CreateLabel("Current path: " & CurrentDir, Colors.White, Gravity.CENTER_VERTICAL)
    pnlBottomInfo.AddView(lblCurrentPath, 10dip, 24dip, leftContentW - 20dip, 20dip)

    pnlNavBar.Initialize("")
    pnlNavBar.Color = Colors.DarkGray
    Activity.AddView(pnlNavBar, 0, totalH - navBarH, totalW, navBarH)
    
    Dim navTabs() As String = Array As String("Home", "File", "Offsets", "Tools", "Probe", "Settings")
    Dim navBtnW As Int = totalW / 6
    
    For i = 0 To navTabs.Length - 1
        Dim btnNav As Button
        If navTabs(i) = "File" Then
            btnNav = CreateButton(navTabs(i), Colors.White, Colors.Black)
        Else
            btnNav = CreateButton(navTabs(i), 0xFFE0E0E0, Colors.Black)
        End If
        pnlNavBar.AddView(btnNav, i * navBtnW, 0, navBtnW - 1dip, navBarH)
    Next
End Sub

Private Sub LoadDirectoryFiles
    clvFiles.Clear
    lblCurrentPath.Text = "Current path: " & CurrentDir
    
    Dim FoundFiles As List
    Try
        FoundFiles = File.ListFiles(CurrentDir)
    Catch
        Log(LastException)
        Return
    End Try
    
    If FoundFiles.IsInitialized = False Or FoundFiles.Size = 0 Then
        AddFileRow("(Empty Directory)", "--", "--")
        Return
    End If
    
    For i = 0 To FoundFiles.Size - 1
        Dim NameOfFile As String = FoundFiles.Get(i)
        If NameOfFile.StartsWith(".") Then Continue 
        
        Dim ItemSize As String = "File"
        Dim ModifiedDate As String = "---"
        
        If File.IsDirectory(CurrentDir, NameOfFile) Then
            ItemSize = "Folder"
        Else
            Dim Bytes As Long = File.Size(CurrentDir, NameOfFile)
            If Bytes > 1024 * 1024 Then
                ItemSize = NumberFormat(Bytes / (1024 * 1024), 1, 1) & " MB"
            Else If Bytes > 1024 Then
                ItemSize = NumberFormat(Bytes / 1024, 1, 0) & " KB"
            Else
                ItemSize = Bytes & " B"
            End If
            
            Dim LastMod As Long = File.LastModified(CurrentDir, NameOfFile)
            DateTime.DateFormat = "yyyy-MM-dd HH:mm"
            ModifiedDate = DateTime.Date(LastMod)
        End If
        
        AddFileRow(NameOfFile, ItemSize, ModifiedDate)
    Next
End Sub

Private Sub AddFileRow(filename As String, size As String, modifiedTime As String)
    Dim rowPanel As Panel
    rowPanel.Initialize("")
    Dim rowXUI As B4XView = rowPanel
    
    Dim rowHeight As Int = 50dip
    Dim leftContentW As Int = clvFiles.GetBase.Width
    rowXUI.SetLayoutAnimated(0, 0, 0, leftContentW, rowHeight)
    rowXUI.Color = 0xFF3D3D3D
    
    Dim w1 As Int = leftContentW * 0.50
    Dim w2 As Int = leftContentW * 0.20
    Dim w3 As Int = leftContentW * 0.30
    
    Dim lblName As Label = CreateLabel(filename, Colors.White, Gravity.LEFT + Gravity.CENTER_VERTICAL)
    rowXUI.AddView(lblName, 15dip, 0, w1 - 15dip, rowHeight)
    
    Dim lblSize As Label = CreateLabel(size, Colors.Yellow, Gravity.CENTER)
    rowXUI.AddView(lblSize, w1, 0, w2, rowHeight)
    
    Dim lblTime As Label = CreateLabel(modifiedTime, Colors.LightGray, Gravity.CENTER)
    rowXUI.AddView(lblTime, w1 + w2, 0, w3, rowHeight)
    
    clvFiles.Add(rowXUI, filename)
End Sub

Sub clvFiles_ItemClick (Index As Int, Value As Object)
    Dim ClickedItemName As String = Value
    If ClickedItemName = "(Empty Directory)" Then Return
    
    If File.IsDirectory(CurrentDir, ClickedItemName) Then
        CurrentDir = File.Combine(CurrentDir, ClickedItemName)
        LoadDirectoryFiles
    Else
        ' 1. COLLECT THE SELECTED PATH AS A STRING
        Dim PathToSend As String = File.Combine(CurrentDir, ClickedItemName)
        
        ' 2. ASSIGN IT DIRECTLY TO YOUR NEW57 APP VARIABLES
        ' This drops the string into a global tracking string variable in Main
        Main.SelectedGCodePath = PathToSend
        
        ' 3. CLOSE THIS EXPLORER AND RETURN TO YOUR CNC APP WINDOW
        Activity.Finish
    End If
End Sub


Sub MenuButtons_Click
	Dim send As Button = Sender
    
	If send.Text = "Home" Then
		' 1. CLOSE THIS EXPLORER SCREEN AND GO BACK TO MAIN
		Activity.Finish
		Return
	Else If send.Text = "Back" Then
		Dim ParentFolder As String = CurrentDir.SubString2(0, CurrentDir.LastIndexOf("/"))
		If ParentFolder = "" Or ParentFolder.Length < 10 Then
			ToastMessageShow("Root limit reached", False)
		Else
			CurrentDir = ParentFolder
			LoadDirectoryFiles
		End If
	Else
		Log("Clicked button action: " & send.Text)
	End If
End Sub


Private Sub CreateButton(txt As String, bgCol As Int, textCol As Int) As Button
    Dim b As Button
    b.Initialize("MenuButtons")
    b.Text = txt
    b.TextColor = textCol
    b.Color = bgCol
    b.TextSize = 14
    Return b
End Sub

Private Sub CreateLabel(txt As String, textCol As Int, gravitySetting As Int) As Label
    Dim l As Label
    l.Initialize("")
    l.Text = txt
    l.TextColor = textCol
    l.Gravity = gravitySetting
    l.TextSize = 14
    Return l
End Sub

Sub Activity_Resume
End Sub

Sub Activity_Pause (UserClosed As Boolean)
End Sub

