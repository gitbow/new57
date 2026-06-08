package cnc.controller2;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "cnc.controller2", "cnc.controller2.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "cnc.controller2", "cnc.controller2.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "cnc.controller2.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create " + (isFirst ? "(first time)" : "") + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (main) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.Timer _tmrblink = null;
public static anywheresoftware.b4a.objects.B4XViewWrapper.XUI _xui = null;
public static anywheresoftware.b4a.phone.Phone.ContentChooser _viewercc = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnltopbar = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblstatus = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbltitle = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbldatetime = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlleftmain = null;
public anywheresoftware.b4a.objects.LabelWrapper[] _lblcols = null;
public anywheresoftware.b4a.objects.ButtonWrapper[] _btnaxis = null;
public anywheresoftware.b4a.objects.LabelWrapper[] _lblworkvals = null;
public anywheresoftware.b4a.objects.LabelWrapper[] _lblmachvals = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnstart = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnpause = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnreset = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlterminal = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtgcodedisplay = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlrightmain = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnl3dplaceholder = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl3dstats = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnspindlecw = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnspindlestop = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnspindleccw = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtspindlespeed = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnsingleblock = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btncoolant = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btngotozero = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnoverride = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnmdi = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnjog = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnmore = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlbottomnav = null;
public anywheresoftware.b4a.objects.ButtonWrapper[] _btnnav = null;
public anywheresoftware.b4a.objects.collections.Map _buttonstates = null;
public static boolean _isblinkactivestate = false;
public anywheresoftware.b4a.objects.B4XCanvas _viewercanvas = null;
public anywheresoftware.b4a.objects.collections.List _viewerpoints = null;
public static float _viewerminx = 0f;
public static float _viewermaxx = 0f;
public static float _viewerminy = 0f;
public static float _viewermaxy = 0f;
public static float _vieweranglex = 0f;
public static float _vieweranglez = 0f;
public static float _viewerstartx = 0f;
public static float _viewerstarty = 0f;
public static float _viewerstartanglex = 0f;
public static float _viewerstartanglez = 0f;
public b4a.example.dateutils _dateutils = null;
public cnc.controller2.starter _starter = null;
public cnc.controller2.settingsscreen _settingsscreen = null;
public cnc.controller2.xuiviewsutils _xuiviewsutils = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (settingsscreen.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
int _maintop = 0;
int _mainheight = 0;
int _colw = 0;
String[] _headers = null;
int _i = 0;
String[] _axisnames = null;
int _rowh = 0;
int _rowspacing = 0;
int _starty = 0;
int _currenty = 0;
int _btny = 0;
int _btnw = 0;
int _btnh = 0;
int _termy = 0;
int _termh = 0;
int _viewh = 0;
int _gridtop = 0;
int _gridw = 0;
int _gridh_btn = 0;
int _gspace = 0;
int _r2top = 0;
int _r3top = 0;
int _navw = 0;
String[] _navlabels = null;
 //BA.debugLineNum = 66;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 67;BA.debugLine="ButtonStates.Initialize";
mostCurrent._buttonstates.Initialize();
 //BA.debugLineNum = 68;BA.debugLine="tmrBlink.Initialize(\"tmrBlink\", 500)";
_tmrblink.Initialize(processBA,"tmrBlink",(long) (500));
 //BA.debugLineNum = 69;BA.debugLine="Activity.Color = Colors.RGB(45, 45, 45)";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (45),(int) (45),(int) (45)));
 //BA.debugLineNum = 72;BA.debugLine="pnlTopBar.Initialize(\"\")";
mostCurrent._pnltopbar.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 73;BA.debugLine="pnlTopBar.Color = Colors.RGB(60, 60, 60)";
mostCurrent._pnltopbar.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (60),(int) (60),(int) (60)));
 //BA.debugLineNum = 74;BA.debugLine="Activity.AddView(pnlTopBar, 0, 0, 100%x, 35dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._pnltopbar.getObject()),(int) (0),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (35)));
 //BA.debugLineNum = 76;BA.debugLine="lblStatus.Initialize(\"\")";
mostCurrent._lblstatus.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 77;BA.debugLine="lblStatus.Text = \"Ready\"";
mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Ready"));
 //BA.debugLineNum = 78;BA.debugLine="lblStatus.TextColor = Colors.Black";
mostCurrent._lblstatus.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 79;BA.debugLine="lblStatus.Color = Colors.RGB(0, 180, 216)";
mostCurrent._lblstatus.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (180),(int) (216)));
 //BA.debugLineNum = 80;BA.debugLine="lblStatus.Gravity = Gravity.CENTER";
mostCurrent._lblstatus.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 81;BA.debugLine="pnlTopBar.AddView(lblStatus, 0, 0, 15%x, 35dip)";
mostCurrent._pnltopbar.AddView((android.view.View)(mostCurrent._lblstatus.getObject()),(int) (0),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (15),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (35)));
 //BA.debugLineNum = 83;BA.debugLine="lblTitle.Initialize(\"\")";
mostCurrent._lbltitle.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 84;BA.debugLine="lblTitle.Text = \"TEST.nc\"";
mostCurrent._lbltitle.setText(BA.ObjectToCharSequence("TEST.nc"));
 //BA.debugLineNum = 85;BA.debugLine="lblTitle.TextColor = Colors.White";
mostCurrent._lbltitle.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 86;BA.debugLine="lblTitle.Gravity = Gravity.CENTER";
mostCurrent._lbltitle.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 87;BA.debugLine="pnlTopBar.AddView(lblTitle, 15%x, 0, 55%x, 35dip)";
mostCurrent._pnltopbar.AddView((android.view.View)(mostCurrent._lbltitle.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (15),mostCurrent.activityBA),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (55),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (35)));
 //BA.debugLineNum = 89;BA.debugLine="lblDateTime.Initialize(\"\")";
mostCurrent._lbldatetime.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 90;BA.debugLine="lblDateTime.Text = DateTime.Date(DateTime.Now) &";
mostCurrent._lbldatetime.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.getNow())+" "+anywheresoftware.b4a.keywords.Common.DateTime.Time(anywheresoftware.b4a.keywords.Common.DateTime.getNow())));
 //BA.debugLineNum = 91;BA.debugLine="lblDateTime.TextColor = Colors.LightGray";
mostCurrent._lbldatetime.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 92;BA.debugLine="lblDateTime.Gravity = Gravity.CENTER_VERTICAL + G";
mostCurrent._lbldatetime.setGravity((int) (anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL+anywheresoftware.b4a.keywords.Common.Gravity.RIGHT));
 //BA.debugLineNum = 93;BA.debugLine="pnlTopBar.AddView(lblDateTime, 70%x, 0, 28%x, 35d";
mostCurrent._pnltopbar.AddView((android.view.View)(mostCurrent._lbldatetime.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (70),mostCurrent.activityBA),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (28),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (35)));
 //BA.debugLineNum = 95;BA.debugLine="Dim mainTop As Int = 35dip";
_maintop = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (35));
 //BA.debugLineNum = 96;BA.debugLine="Dim mainHeight As Int = 100%y - 80dip";
_mainheight = (int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (80)));
 //BA.debugLineNum = 99;BA.debugLine="pnlLeftMain.Initialize(\"\")";
mostCurrent._pnlleftmain.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 100;BA.debugLine="pnlLeftMain.Color = Colors.Transparent";
mostCurrent._pnlleftmain.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 101;BA.debugLine="Activity.AddView(pnlLeftMain, 0, mainTop, 46%x, m";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._pnlleftmain.getObject()),(int) (0),_maintop,anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (46),mostCurrent.activityBA),_mainheight);
 //BA.debugLineNum = 104;BA.debugLine="Dim colW As Int = pnlLeftMain.Width / 3";
_colw = (int) (mostCurrent._pnlleftmain.getWidth()/(double)3);
 //BA.debugLineNum = 105;BA.debugLine="Dim headers() As String = Array As String(\"Zero\",";
_headers = new String[]{"Zero","Work co.","Machine co."};
 //BA.debugLineNum = 106;BA.debugLine="For i = 0 To 2";
{
final int step30 = 1;
final int limit30 = (int) (2);
_i = (int) (0) ;
for (;_i <= limit30 ;_i = _i + step30 ) {
 //BA.debugLineNum = 107;BA.debugLine="lblCols(i).Initialize(\"\")";
mostCurrent._lblcols[_i].Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 108;BA.debugLine="lblCols(i).Text = headers(i)";
mostCurrent._lblcols[_i].setText(BA.ObjectToCharSequence(_headers[_i]));
 //BA.debugLineNum = 109;BA.debugLine="lblCols(i).TextColor = Colors.LightGray";
mostCurrent._lblcols[_i].setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 110;BA.debugLine="lblCols(i).TextSize = 14";
mostCurrent._lblcols[_i].setTextSize((float) (14));
 //BA.debugLineNum = 111;BA.debugLine="lblCols(i).Gravity = Gravity.CENTER";
mostCurrent._lblcols[_i].setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 112;BA.debugLine="pnlLeftMain.AddView(lblCols(i), i * colW, 5dip,";
mostCurrent._pnlleftmain.AddView((android.view.View)(mostCurrent._lblcols[_i].getObject()),(int) (_i*_colw),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)),_colw,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (25)));
 }
};
 //BA.debugLineNum = 116;BA.debugLine="Dim axisNames() As String = Array As String(\"X\",";
_axisnames = new String[]{"X","Y","Z","A","B"};
 //BA.debugLineNum = 117;BA.debugLine="Dim rowH As Int = 38dip";
_rowh = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (38));
 //BA.debugLineNum = 118;BA.debugLine="Dim rowSpacing As Int = 5dip";
_rowspacing = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5));
 //BA.debugLineNum = 119;BA.debugLine="Dim startY As Int = 30dip";
_starty = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (30));
 //BA.debugLineNum = 121;BA.debugLine="For i = 0 To 4";
{
final int step42 = 1;
final int limit42 = (int) (4);
_i = (int) (0) ;
for (;_i <= limit42 ;_i = _i + step42 ) {
 //BA.debugLineNum = 122;BA.debugLine="Dim currentY As Int = startY + i * (rowH + rowSp";
_currenty = (int) (_starty+_i*(_rowh+_rowspacing));
 //BA.debugLineNum = 123;BA.debugLine="btnAxis(i).Initialize(\"Axis\")";
mostCurrent._btnaxis[_i].Initialize(mostCurrent.activityBA,"Axis");
 //BA.debugLineNum = 124;BA.debugLine="btnAxis(i).Text = axisNames(i)";
mostCurrent._btnaxis[_i].setText(BA.ObjectToCharSequence(_axisnames[_i]));
 //BA.debugLineNum = 125;BA.debugLine="btnAxis(i).TextColor = Colors.White";
mostCurrent._btnaxis[_i].setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 126;BA.debugLine="btnAxis(i).Color = Colors.RGB(40, 167, 69)";
mostCurrent._btnaxis[_i].setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (40),(int) (167),(int) (69)));
 //BA.debugLineNum = 127;BA.debugLine="pnlLeftMain.AddView(btnAxis(i), 10dip, currentY,";
mostCurrent._pnlleftmain.AddView((android.view.View)(mostCurrent._btnaxis[_i].getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),_currenty,(int) (_colw-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))),_rowh);
 //BA.debugLineNum = 129;BA.debugLine="lblWorkVals(i).Initialize(\"\")";
mostCurrent._lblworkvals[_i].Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 130;BA.debugLine="lblWorkVals(i).Text = \"0.000\"";
mostCurrent._lblworkvals[_i].setText(BA.ObjectToCharSequence("0.000"));
 //BA.debugLineNum = 131;BA.debugLine="lblWorkVals(i).TextColor = Colors.White";
mostCurrent._lblworkvals[_i].setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 132;BA.debugLine="lblWorkVals(i).TextSize = 18";
mostCurrent._lblworkvals[_i].setTextSize((float) (18));
 //BA.debugLineNum = 133;BA.debugLine="lblWorkVals(i).Gravity = Gravity.CENTER";
mostCurrent._lblworkvals[_i].setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 134;BA.debugLine="pnlLeftMain.AddView(lblWorkVals(i), colW, curren";
mostCurrent._pnlleftmain.AddView((android.view.View)(mostCurrent._lblworkvals[_i].getObject()),_colw,_currenty,_colw,_rowh);
 //BA.debugLineNum = 136;BA.debugLine="lblMachVals(i).Initialize(\"\")";
mostCurrent._lblmachvals[_i].Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 137;BA.debugLine="lblMachVals(i).Text = \"0.000\"";
mostCurrent._lblmachvals[_i].setText(BA.ObjectToCharSequence("0.000"));
 //BA.debugLineNum = 138;BA.debugLine="lblMachVals(i).TextColor = Colors.White";
mostCurrent._lblmachvals[_i].setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 139;BA.debugLine="lblMachVals(i).TextSize = 18";
mostCurrent._lblmachvals[_i].setTextSize((float) (18));
 //BA.debugLineNum = 140;BA.debugLine="lblMachVals(i).Gravity = Gravity.CENTER";
mostCurrent._lblmachvals[_i].setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 141;BA.debugLine="pnlLeftMain.AddView(lblMachVals(i), colW * 2, cu";
mostCurrent._pnlleftmain.AddView((android.view.View)(mostCurrent._lblmachvals[_i].getObject()),(int) (_colw*2),_currenty,_colw,_rowh);
 }
};
 //BA.debugLineNum = 145;BA.debugLine="Dim btnY As Int = startY + 5 * (rowH + rowSpacing";
_btny = (int) (_starty+5*(_rowh+_rowspacing)+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)));
 //BA.debugLineNum = 146;BA.debugLine="Dim btnW As Int = (pnlLeftMain.Width - 40dip) / 3";
_btnw = (int) ((mostCurrent._pnlleftmain.getWidth()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (40)))/(double)3);
 //BA.debugLineNum = 147;BA.debugLine="Dim btnH As Int = 45dip";
_btnh = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (45));
 //BA.debugLineNum = 149;BA.debugLine="btnStart.Initialize(\"Start\")";
mostCurrent._btnstart.Initialize(mostCurrent.activityBA,"Start");
 //BA.debugLineNum = 150;BA.debugLine="btnStart.Text = \"Start\"";
mostCurrent._btnstart.setText(BA.ObjectToCharSequence("Start"));
 //BA.debugLineNum = 151;BA.debugLine="btnStart.Color = Colors.RGB(40, 167, 69)";
mostCurrent._btnstart.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (40),(int) (167),(int) (69)));
 //BA.debugLineNum = 152;BA.debugLine="btnStart.TextColor = Colors.White";
mostCurrent._btnstart.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 153;BA.debugLine="btnStart.TextSize = 14";
mostCurrent._btnstart.setTextSize((float) (14));
 //BA.debugLineNum = 154;BA.debugLine="pnlLeftMain.AddView(btnStart, 10dip, btnY, btnW,";
mostCurrent._pnlleftmain.AddView((android.view.View)(mostCurrent._btnstart.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),_btny,_btnw,_btnh);
 //BA.debugLineNum = 156;BA.debugLine="btnPause.Initialize(\"Pause\")";
mostCurrent._btnpause.Initialize(mostCurrent.activityBA,"Pause");
 //BA.debugLineNum = 157;BA.debugLine="btnPause.Text = \"Pause\"";
mostCurrent._btnpause.setText(BA.ObjectToCharSequence("Pause"));
 //BA.debugLineNum = 158;BA.debugLine="btnPause.Color = Colors.RGB(220, 53, 69)";
mostCurrent._btnpause.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (220),(int) (53),(int) (69)));
 //BA.debugLineNum = 159;BA.debugLine="btnPause.TextColor = Colors.White";
mostCurrent._btnpause.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 160;BA.debugLine="btnPause.TextSize = 14";
mostCurrent._btnpause.setTextSize((float) (14));
 //BA.debugLineNum = 161;BA.debugLine="pnlLeftMain.AddView(btnPause, 20dip + btnW, btnY,";
mostCurrent._pnlleftmain.AddView((android.view.View)(mostCurrent._btnpause.getObject()),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))+_btnw),_btny,_btnw,_btnh);
 //BA.debugLineNum = 163;BA.debugLine="btnReset.Initialize(\"Reset\")";
mostCurrent._btnreset.Initialize(mostCurrent.activityBA,"Reset");
 //BA.debugLineNum = 164;BA.debugLine="btnReset.Text = \"Reset\"";
mostCurrent._btnreset.setText(BA.ObjectToCharSequence("Reset"));
 //BA.debugLineNum = 165;BA.debugLine="btnReset.Color = Colors.RGB(255, 193, 7)";
mostCurrent._btnreset.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (255),(int) (193),(int) (7)));
 //BA.debugLineNum = 166;BA.debugLine="btnReset.TextColor = Colors.Black";
mostCurrent._btnreset.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 167;BA.debugLine="btnReset.TextSize = 14";
mostCurrent._btnreset.setTextSize((float) (14));
 //BA.debugLineNum = 168;BA.debugLine="pnlLeftMain.AddView(btnReset, 30dip + (btnW * 2),";
mostCurrent._pnlleftmain.AddView((android.view.View)(mostCurrent._btnreset.getObject()),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (30))+(_btnw*2)),_btny,_btnw,_btnh);
 //BA.debugLineNum = 171;BA.debugLine="Dim termY As Int = btnY + btnH + 10dip";
_termy = (int) (_btny+_btnh+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)));
 //BA.debugLineNum = 172;BA.debugLine="Dim termH As Int = pnlLeftMain.Height - termY - 5";
_termh = (int) (mostCurrent._pnlleftmain.getHeight()-_termy-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)));
 //BA.debugLineNum = 173;BA.debugLine="pnlTerminal.Initialize(\"\")";
mostCurrent._pnlterminal.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 174;BA.debugLine="pnlTerminal.Color = Colors.RGB(30, 30, 30)";
mostCurrent._pnlterminal.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (30),(int) (30),(int) (30)));
 //BA.debugLineNum = 175;BA.debugLine="pnlLeftMain.AddView(pnlTerminal, 10dip, termY, pn";
mostCurrent._pnlleftmain.AddView((android.view.View)(mostCurrent._pnlterminal.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),_termy,(int) (mostCurrent._pnlleftmain.getWidth()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))),_termh);
 //BA.debugLineNum = 177;BA.debugLine="txtGCodeDisplay.Initialize(\"\")";
mostCurrent._txtgcodedisplay.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 178;BA.debugLine="txtGCodeDisplay.Color = Colors.Transparent";
mostCurrent._txtgcodedisplay.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 179;BA.debugLine="txtGCodeDisplay.TextColor = Colors.RGB(40, 167, 6";
mostCurrent._txtgcodedisplay.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (40),(int) (167),(int) (69)));
 //BA.debugLineNum = 180;BA.debugLine="txtGCodeDisplay.TextSize = 12";
mostCurrent._txtgcodedisplay.setTextSize((float) (12));
 //BA.debugLineNum = 181;BA.debugLine="txtGCodeDisplay.SingleLine = False";
mostCurrent._txtgcodedisplay.setSingleLine(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 182;BA.debugLine="txtGCodeDisplay.Wrap = True";
mostCurrent._txtgcodedisplay.setWrap(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 183;BA.debugLine="txtGCodeDisplay.Enabled = False";
mostCurrent._txtgcodedisplay.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 184;BA.debugLine="txtGCodeDisplay.Text = \"1: %\" & CRLF & \"2: ( Mach";
mostCurrent._txtgcodedisplay.setText(BA.ObjectToCharSequence("1: %"+anywheresoftware.b4a.keywords.Common.CRLF+"2: ( Machine Name: CNC )"+anywheresoftware.b4a.keywords.Common.CRLF+"3: ( Tu Hao: _model23.prt )"+anywheresoftware.b4a.keywords.Common.CRLF+"4: ( BianCheng: Administrator )"));
 //BA.debugLineNum = 185;BA.debugLine="pnlTerminal.AddView(txtGCodeDisplay, 5dip, 5dip,";
mostCurrent._pnlterminal.AddView((android.view.View)(mostCurrent._txtgcodedisplay.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)),(int) (mostCurrent._pnlterminal.getWidth()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))),(int) (mostCurrent._pnlterminal.getHeight()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))));
 //BA.debugLineNum = 188;BA.debugLine="pnlRightMain.Initialize(\"\")";
mostCurrent._pnlrightmain.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 189;BA.debugLine="pnlRightMain.Color = Colors.Transparent";
mostCurrent._pnlrightmain.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 190;BA.debugLine="Activity.AddView(pnlRightMain, 46%x, mainTop, 54%";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._pnlrightmain.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (46),mostCurrent.activityBA),_maintop,anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (54),mostCurrent.activityBA),_mainheight);
 //BA.debugLineNum = 193;BA.debugLine="Dim viewH As Int = pnlRightMain.Height * 0.55";
_viewh = (int) (mostCurrent._pnlrightmain.getHeight()*0.55);
 //BA.debugLineNum = 194;BA.debugLine="pnl3DPlaceholder.Initialize(\"\")";
mostCurrent._pnl3dplaceholder.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 195;BA.debugLine="pnl3DPlaceholder.Color = Colors.RGB(20, 20, 20)";
mostCurrent._pnl3dplaceholder.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (20),(int) (20),(int) (20)));
 //BA.debugLineNum = 196;BA.debugLine="pnlRightMain.AddView(pnl3DPlaceholder, 5dip, 5dip";
mostCurrent._pnlrightmain.AddView((android.view.View)(mostCurrent._pnl3dplaceholder.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)),(int) (mostCurrent._pnlrightmain.getWidth()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))),_viewh);
 //BA.debugLineNum = 198;BA.debugLine="lbl3DStats.Initialize(\"\")";
mostCurrent._lbl3dstats.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 199;BA.debugLine="lbl3DStats.Text = \"X: -11.060 ... 8.773\" & CRLF &";
mostCurrent._lbl3dstats.setText(BA.ObjectToCharSequence("X: -11.060 ... 8.773"+anywheresoftware.b4a.keywords.Common.CRLF+"Y: -8.035 ... 8.035"+anywheresoftware.b4a.keywords.Common.CRLF+"Z: 0.000 ... 16.000"));
 //BA.debugLineNum = 200;BA.debugLine="lbl3DStats.TextColor = Colors.RGB(0, 180, 216)";
mostCurrent._lbl3dstats.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (180),(int) (216)));
 //BA.debugLineNum = 201;BA.debugLine="lbl3DStats.TextSize = 12";
mostCurrent._lbl3dstats.setTextSize((float) (12));
 //BA.debugLineNum = 202;BA.debugLine="pnl3DPlaceholder.AddView(lbl3DStats, 10dip, viewH";
mostCurrent._pnl3dplaceholder.AddView((android.view.View)(mostCurrent._lbl3dstats.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),(int) (_viewh-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (60))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (200)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (55)));
 //BA.debugLineNum = 205;BA.debugLine="Dim gridTop As Int = viewH + 10dip";
_gridtop = (int) (_viewh+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)));
 //BA.debugLineNum = 206;BA.debugLine="Dim gridW As Int = (pnlRightMain.Width - 25dip) /";
_gridw = (int) ((mostCurrent._pnlrightmain.getWidth()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (25)))/(double)4);
 //BA.debugLineNum = 207;BA.debugLine="Dim gridH_btn As Int = (pnlRightMain.Height - gri";
_gridh_btn = (int) ((mostCurrent._pnlrightmain.getHeight()-_gridtop-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)))/(double)3);
 //BA.debugLineNum = 208;BA.debugLine="Dim gSpace As Int = 5dip";
_gspace = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5));
 //BA.debugLineNum = 211;BA.debugLine="btnSpindleCW.Initialize(\"SpindleCW\")";
mostCurrent._btnspindlecw.Initialize(mostCurrent.activityBA,"SpindleCW");
 //BA.debugLineNum = 212;BA.debugLine="btnSpindleCW.Text = \"Spindle CW\"";
mostCurrent._btnspindlecw.setText(BA.ObjectToCharSequence("Spindle CW"));
 //BA.debugLineNum = 213;BA.debugLine="btnSpindleCW.Color = Colors.RGB(64, 64, 64)";
mostCurrent._btnspindlecw.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (64),(int) (64),(int) (64)));
 //BA.debugLineNum = 214;BA.debugLine="btnSpindleCW.TextColor = Colors.White";
mostCurrent._btnspindlecw.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 215;BA.debugLine="btnSpindleCW.TextSize = 12";
mostCurrent._btnspindlecw.setTextSize((float) (12));
 //BA.debugLineNum = 216;BA.debugLine="pnlRightMain.AddView(btnSpindleCW, 5dip, gridTop,";
mostCurrent._pnlrightmain.AddView((android.view.View)(mostCurrent._btnspindlecw.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)),_gridtop,_gridw,_gridh_btn);
 //BA.debugLineNum = 218;BA.debugLine="btnSpindleStop.Initialize(\"SpindleStop\")";
mostCurrent._btnspindlestop.Initialize(mostCurrent.activityBA,"SpindleStop");
 //BA.debugLineNum = 219;BA.debugLine="btnSpindleStop.Text = \"Spindle Stop\"";
mostCurrent._btnspindlestop.setText(BA.ObjectToCharSequence("Spindle Stop"));
 //BA.debugLineNum = 220;BA.debugLine="btnSpindleStop.Color = Colors.RGB(64, 64, 64)";
mostCurrent._btnspindlestop.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (64),(int) (64),(int) (64)));
 //BA.debugLineNum = 221;BA.debugLine="btnSpindleStop.TextColor = Colors.White";
mostCurrent._btnspindlestop.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 222;BA.debugLine="btnSpindleStop.TextSize = 12";
mostCurrent._btnspindlestop.setTextSize((float) (12));
 //BA.debugLineNum = 223;BA.debugLine="pnlRightMain.AddView(btnSpindleStop, 5dip + (grid";
mostCurrent._pnlrightmain.AddView((android.view.View)(mostCurrent._btnspindlestop.getObject()),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5))+(_gridw+_gspace)),_gridtop,_gridw,_gridh_btn);
 //BA.debugLineNum = 225;BA.debugLine="btnSpindleCCW.Initialize(\"SpindleCCW\")";
mostCurrent._btnspindleccw.Initialize(mostCurrent.activityBA,"SpindleCCW");
 //BA.debugLineNum = 226;BA.debugLine="btnSpindleCCW.Text = \"Spindle CCW\"";
mostCurrent._btnspindleccw.setText(BA.ObjectToCharSequence("Spindle CCW"));
 //BA.debugLineNum = 227;BA.debugLine="btnSpindleCCW.Color = Colors.RGB(64, 64, 64)";
mostCurrent._btnspindleccw.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (64),(int) (64),(int) (64)));
 //BA.debugLineNum = 228;BA.debugLine="btnSpindleCCW.TextColor = Colors.White";
mostCurrent._btnspindleccw.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 229;BA.debugLine="btnSpindleCCW.TextSize = 12";
mostCurrent._btnspindleccw.setTextSize((float) (12));
 //BA.debugLineNum = 230;BA.debugLine="pnlRightMain.AddView(btnSpindleCCW, 5dip + (gridW";
mostCurrent._pnlrightmain.AddView((android.view.View)(mostCurrent._btnspindleccw.getObject()),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5))+(_gridw+_gspace)*2),_gridtop,_gridw,_gridh_btn);
 //BA.debugLineNum = 232;BA.debugLine="txtSpindleSpeed.Initialize(\"\")";
mostCurrent._txtspindlespeed.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 233;BA.debugLine="txtSpindleSpeed.Text = \"3500\"";
mostCurrent._txtspindlespeed.setText(BA.ObjectToCharSequence("3500"));
 //BA.debugLineNum = 234;BA.debugLine="txtSpindleSpeed.Gravity = Gravity.CENTER";
mostCurrent._txtspindlespeed.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 235;BA.debugLine="txtSpindleSpeed.TextColor = Colors.Black";
mostCurrent._txtspindlespeed.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 236;BA.debugLine="txtSpindleSpeed.Color = Colors.White";
mostCurrent._txtspindlespeed.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 237;BA.debugLine="pnlRightMain.AddView(txtSpindleSpeed, 5dip + (gri";
mostCurrent._pnlrightmain.AddView((android.view.View)(mostCurrent._txtspindlespeed.getObject()),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5))+(_gridw+_gspace)*3),_gridtop,_gridw,_gridh_btn);
 //BA.debugLineNum = 240;BA.debugLine="Dim r2Top As Int = gridTop + gridH_btn + gSpace";
_r2top = (int) (_gridtop+_gridh_btn+_gspace);
 //BA.debugLineNum = 241;BA.debugLine="btnSingleBlock.Initialize(\"SingleBlock\")";
mostCurrent._btnsingleblock.Initialize(mostCurrent.activityBA,"SingleBlock");
 //BA.debugLineNum = 242;BA.debugLine="btnSingleBlock.Text = \"Single Block\"";
mostCurrent._btnsingleblock.setText(BA.ObjectToCharSequence("Single Block"));
 //BA.debugLineNum = 243;BA.debugLine="btnSingleBlock.Color = Colors.RGB(64, 64, 64)";
mostCurrent._btnsingleblock.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (64),(int) (64),(int) (64)));
 //BA.debugLineNum = 244;BA.debugLine="btnSingleBlock.TextColor = Colors.White";
mostCurrent._btnsingleblock.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 245;BA.debugLine="btnSingleBlock.TextSize = 12";
mostCurrent._btnsingleblock.setTextSize((float) (12));
 //BA.debugLineNum = 246;BA.debugLine="pnlRightMain.AddView(btnSingleBlock, 5dip, r2Top,";
mostCurrent._pnlrightmain.AddView((android.view.View)(mostCurrent._btnsingleblock.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)),_r2top,_gridw,_gridh_btn);
 //BA.debugLineNum = 248;BA.debugLine="btnCoolant.Initialize(\"Coolant\")";
mostCurrent._btncoolant.Initialize(mostCurrent.activityBA,"Coolant");
 //BA.debugLineNum = 249;BA.debugLine="btnCoolant.Text = \"Coolant\"";
mostCurrent._btncoolant.setText(BA.ObjectToCharSequence("Coolant"));
 //BA.debugLineNum = 250;BA.debugLine="btnCoolant.Color = Colors.RGB(64, 64, 64)";
mostCurrent._btncoolant.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (64),(int) (64),(int) (64)));
 //BA.debugLineNum = 251;BA.debugLine="btnCoolant.TextColor = Colors.White";
mostCurrent._btncoolant.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 252;BA.debugLine="btnCoolant.TextSize = 12";
mostCurrent._btncoolant.setTextSize((float) (12));
 //BA.debugLineNum = 253;BA.debugLine="pnlRightMain.AddView(btnCoolant, 5dip + (gridW +";
mostCurrent._pnlrightmain.AddView((android.view.View)(mostCurrent._btncoolant.getObject()),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5))+(_gridw+_gspace)),_r2top,_gridw,_gridh_btn);
 //BA.debugLineNum = 255;BA.debugLine="btnGotoZero.Initialize(\"GotoZero\")";
mostCurrent._btngotozero.Initialize(mostCurrent.activityBA,"GotoZero");
 //BA.debugLineNum = 256;BA.debugLine="btnGotoZero.Text = \"GOTO Zero\"";
mostCurrent._btngotozero.setText(BA.ObjectToCharSequence("GOTO Zero"));
 //BA.debugLineNum = 257;BA.debugLine="btnGotoZero.Color = Colors.RGB(64, 64, 64)";
mostCurrent._btngotozero.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (64),(int) (64),(int) (64)));
 //BA.debugLineNum = 258;BA.debugLine="btnGotoZero.TextColor = Colors.White";
mostCurrent._btngotozero.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 259;BA.debugLine="btnGotoZero.TextSize = 12";
mostCurrent._btngotozero.setTextSize((float) (12));
 //BA.debugLineNum = 260;BA.debugLine="pnlRightMain.AddView(btnGotoZero, 5dip + (gridW +";
mostCurrent._pnlrightmain.AddView((android.view.View)(mostCurrent._btngotozero.getObject()),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5))+(_gridw+_gspace)*2),_r2top,_gridw,_gridh_btn);
 //BA.debugLineNum = 263;BA.debugLine="Dim r3Top As Int = r2Top + gridH_btn + gSpace";
_r3top = (int) (_r2top+_gridh_btn+_gspace);
 //BA.debugLineNum = 264;BA.debugLine="btnOverride.Initialize(\"Override\")";
mostCurrent._btnoverride.Initialize(mostCurrent.activityBA,"Override");
 //BA.debugLineNum = 265;BA.debugLine="btnOverride.Text = \"Override\"";
mostCurrent._btnoverride.setText(BA.ObjectToCharSequence("Override"));
 //BA.debugLineNum = 266;BA.debugLine="btnOverride.Color = Colors.RGB(64, 64, 64)";
mostCurrent._btnoverride.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (64),(int) (64),(int) (64)));
 //BA.debugLineNum = 267;BA.debugLine="btnOverride.TextColor = Colors.White";
mostCurrent._btnoverride.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 268;BA.debugLine="btnOverride.TextSize = 12";
mostCurrent._btnoverride.setTextSize((float) (12));
 //BA.debugLineNum = 269;BA.debugLine="pnlRightMain.AddView(btnOverride, 5dip, r3Top, gr";
mostCurrent._pnlrightmain.AddView((android.view.View)(mostCurrent._btnoverride.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)),_r3top,_gridw,_gridh_btn);
 //BA.debugLineNum = 271;BA.debugLine="btnMDI.Initialize(\"MDI\")";
mostCurrent._btnmdi.Initialize(mostCurrent.activityBA,"MDI");
 //BA.debugLineNum = 272;BA.debugLine="btnMDI.Text = \"MDI\"";
mostCurrent._btnmdi.setText(BA.ObjectToCharSequence("MDI"));
 //BA.debugLineNum = 273;BA.debugLine="btnMDI.Color = Colors.RGB(64, 64, 64)";
mostCurrent._btnmdi.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (64),(int) (64),(int) (64)));
 //BA.debugLineNum = 274;BA.debugLine="btnMDI.TextColor = Colors.White";
mostCurrent._btnmdi.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 275;BA.debugLine="btnMDI.TextSize = 12";
mostCurrent._btnmdi.setTextSize((float) (12));
 //BA.debugLineNum = 276;BA.debugLine="pnlRightMain.AddView(btnMDI, 5dip + (gridW + gSpa";
mostCurrent._pnlrightmain.AddView((android.view.View)(mostCurrent._btnmdi.getObject()),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5))+(_gridw+_gspace)),_r3top,_gridw,_gridh_btn);
 //BA.debugLineNum = 278;BA.debugLine="btnJog.Initialize(\"Jog\")";
mostCurrent._btnjog.Initialize(mostCurrent.activityBA,"Jog");
 //BA.debugLineNum = 279;BA.debugLine="btnJog.Text = \"Jog\"";
mostCurrent._btnjog.setText(BA.ObjectToCharSequence("Jog"));
 //BA.debugLineNum = 280;BA.debugLine="btnJog.Color = Colors.RGB(64, 64, 64)";
mostCurrent._btnjog.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (64),(int) (64),(int) (64)));
 //BA.debugLineNum = 281;BA.debugLine="btnJog.TextColor = Colors.White";
mostCurrent._btnjog.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 282;BA.debugLine="btnJog.TextSize = 12";
mostCurrent._btnjog.setTextSize((float) (12));
 //BA.debugLineNum = 283;BA.debugLine="pnlRightMain.AddView(btnJog, 5dip + (gridW + gSpa";
mostCurrent._pnlrightmain.AddView((android.view.View)(mostCurrent._btnjog.getObject()),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5))+(_gridw+_gspace)*2),_r3top,_gridw,_gridh_btn);
 //BA.debugLineNum = 285;BA.debugLine="btnMore.Initialize(\"More\")";
mostCurrent._btnmore.Initialize(mostCurrent.activityBA,"More");
 //BA.debugLineNum = 286;BA.debugLine="btnMore.Text = \"More\"";
mostCurrent._btnmore.setText(BA.ObjectToCharSequence("More"));
 //BA.debugLineNum = 287;BA.debugLine="btnMore.Color = Colors.RGB(64, 64, 64)";
mostCurrent._btnmore.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (64),(int) (64),(int) (64)));
 //BA.debugLineNum = 288;BA.debugLine="btnMore.TextColor = Colors.White";
mostCurrent._btnmore.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 289;BA.debugLine="btnMore.TextSize = 12";
mostCurrent._btnmore.setTextSize((float) (12));
 //BA.debugLineNum = 290;BA.debugLine="pnlRightMain.AddView(btnMore, 5dip + (gridW + gSp";
mostCurrent._pnlrightmain.AddView((android.view.View)(mostCurrent._btnmore.getObject()),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5))+(_gridw+_gspace)*3),_r3top,_gridw,_gridh_btn);
 //BA.debugLineNum = 293;BA.debugLine="ButtonStates.Put(\"Spindle CW\", False)";
mostCurrent._buttonstates.Put((Object)("Spindle CW"),(Object)(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 294;BA.debugLine="ButtonStates.Put(\"Spindle Stop\", True)";
mostCurrent._buttonstates.Put((Object)("Spindle Stop"),(Object)(anywheresoftware.b4a.keywords.Common.True));
 //BA.debugLineNum = 295;BA.debugLine="ButtonStates.Put(\"Spindle CCW\", False)";
mostCurrent._buttonstates.Put((Object)("Spindle CCW"),(Object)(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 296;BA.debugLine="ButtonStates.Put(\"Single Block\", False)";
mostCurrent._buttonstates.Put((Object)("Single Block"),(Object)(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 297;BA.debugLine="ButtonStates.Put(\"Coolant\", False)";
mostCurrent._buttonstates.Put((Object)("Coolant"),(Object)(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 298;BA.debugLine="ButtonStates.Put(\"GOTO Zero\", False)";
mostCurrent._buttonstates.Put((Object)("GOTO Zero"),(Object)(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 299;BA.debugLine="ButtonStates.Put(\"Override\", False)";
mostCurrent._buttonstates.Put((Object)("Override"),(Object)(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 300;BA.debugLine="ButtonStates.Put(\"MDI\", False)";
mostCurrent._buttonstates.Put((Object)("MDI"),(Object)(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 301;BA.debugLine="ButtonStates.Put(\"Jog\", False)";
mostCurrent._buttonstates.Put((Object)("Jog"),(Object)(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 302;BA.debugLine="ButtonStates.Put(\"More\", False)";
mostCurrent._buttonstates.Put((Object)("More"),(Object)(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 305;BA.debugLine="btnSpindleStop.Color = Colors.RGB(212, 163, 89)";
mostCurrent._btnspindlestop.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (212),(int) (163),(int) (89)));
 //BA.debugLineNum = 306;BA.debugLine="btnSpindleStop.TextColor = Colors.Black";
mostCurrent._btnspindlestop.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 309;BA.debugLine="pnlBottomNav.Initialize(\"\")";
mostCurrent._pnlbottomnav.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 310;BA.debugLine="pnlBottomNav.Color = Colors.RGB(90, 90, 90)";
mostCurrent._pnlbottomnav.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (90),(int) (90),(int) (90)));
 //BA.debugLineNum = 311;BA.debugLine="Activity.AddView(pnlBottomNav, 0, 100%y - 45dip,";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._pnlbottomnav.getObject()),(int) (0),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (45))),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (45)));
 //BA.debugLineNum = 313;BA.debugLine="Dim navW As Int = 100%x / 6";
_navw = (int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)6);
 //BA.debugLineNum = 314;BA.debugLine="Dim navLabels() As String = Array As String(\"Home";
_navlabels = new String[]{"Home","File","Offsets","Tools","Probe","Settings"};
 //BA.debugLineNum = 316;BA.debugLine="For i = 0 To 5";
{
final int step198 = 1;
final int limit198 = (int) (5);
_i = (int) (0) ;
for (;_i <= limit198 ;_i = _i + step198 ) {
 //BA.debugLineNum = 317;BA.debugLine="btnNav(i).Initialize(\"Nav\")";
mostCurrent._btnnav[_i].Initialize(mostCurrent.activityBA,"Nav");
 //BA.debugLineNum = 318;BA.debugLine="btnNav(i).Text = navLabels(i)";
mostCurrent._btnnav[_i].setText(BA.ObjectToCharSequence(_navlabels[_i]));
 //BA.debugLineNum = 319;BA.debugLine="btnNav(i).TextColor = Colors.Black";
mostCurrent._btnnav[_i].setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 320;BA.debugLine="btnNav(i).TextSize = 15";
mostCurrent._btnnav[_i].setTextSize((float) (15));
 //BA.debugLineNum = 321;BA.debugLine="btnNav(i).Color = Colors.White";
mostCurrent._btnnav[_i].setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 322;BA.debugLine="pnlBottomNav.AddView(btnNav(i), i * navW + 1dip,";
mostCurrent._pnlbottomnav.AddView((android.view.View)(mostCurrent._btnnav[_i].getObject()),(int) (_i*_navw+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2)),(int) (_navw-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (41)));
 }
};
 //BA.debugLineNum = 326;BA.debugLine="Init3DViewer";
_init3dviewer();
 //BA.debugLineNum = 327;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 667;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 668;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 664;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 665;BA.debugLine="End Sub";
return "";
}
public static String  _axis_click() throws Exception{
anywheresoftware.b4a.objects.ButtonWrapper _clickedbtn = null;
 //BA.debugLineNum = 446;BA.debugLine="Sub Axis_Click";
 //BA.debugLineNum = 447;BA.debugLine="Dim clickedBtn As Button = Sender";
_clickedbtn = new anywheresoftware.b4a.objects.ButtonWrapper();
_clickedbtn = (anywheresoftware.b4a.objects.ButtonWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ButtonWrapper(), (android.widget.Button)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
 //BA.debugLineNum = 448;BA.debugLine="ToastMessageShow(\"Axis: \" & clickedBtn.Text & \" z";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Axis: "+_clickedbtn.getText()+" zeroed"),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 449;BA.debugLine="End Sub";
return "";
}
public static String  _calculate3dbounds() throws Exception{
anywheresoftware.b4a.objects.collections.Map _p = null;
float _px = 0f;
float _py = 0f;
 //BA.debugLineNum = 578;BA.debugLine="Sub Calculate3DBounds";
 //BA.debugLineNum = 579;BA.debugLine="viewerMinX = 999999 : viewerMaxX = -999999";
_viewerminx = (float) (999999);
 //BA.debugLineNum = 579;BA.debugLine="viewerMinX = 999999 : viewerMaxX = -999999";
_viewermaxx = (float) (-999999);
 //BA.debugLineNum = 580;BA.debugLine="viewerMinY = 999999 : viewerMaxY = -999999";
_viewerminy = (float) (999999);
 //BA.debugLineNum = 580;BA.debugLine="viewerMinY = 999999 : viewerMaxY = -999999";
_viewermaxy = (float) (-999999);
 //BA.debugLineNum = 581;BA.debugLine="For Each p As Map In viewerPoints";
_p = new anywheresoftware.b4a.objects.collections.Map();
{
final anywheresoftware.b4a.BA.IterableList group5 = mostCurrent._viewerpoints;
final int groupLen5 = group5.getSize()
;int index5 = 0;
;
for (; index5 < groupLen5;index5++){
_p = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(group5.Get(index5)));
 //BA.debugLineNum = 582;BA.debugLine="Dim px As Float = Project3DX(p)";
_px = _project3dx(_p);
 //BA.debugLineNum = 583;BA.debugLine="Dim py As Float = Project3DY(p)";
_py = _project3dy(_p);
 //BA.debugLineNum = 584;BA.debugLine="If px < viewerMinX Then viewerMinX = px";
if (_px<_viewerminx) { 
_viewerminx = _px;};
 //BA.debugLineNum = 585;BA.debugLine="If px > viewerMaxX Then viewerMaxX = px";
if (_px>_viewermaxx) { 
_viewermaxx = _px;};
 //BA.debugLineNum = 586;BA.debugLine="If py < viewerMinY Then viewerMinY = py";
if (_py<_viewerminy) { 
_viewerminy = _py;};
 //BA.debugLineNum = 587;BA.debugLine="If py > viewerMaxY Then viewerMaxY = py";
if (_py>_viewermaxy) { 
_viewermaxy = _py;};
 }
};
 //BA.debugLineNum = 589;BA.debugLine="End Sub";
return "";
}
public static String  _coolant_click() throws Exception{
 //BA.debugLineNum = 346;BA.debugLine="Sub Coolant_Click";
 //BA.debugLineNum = 347;BA.debugLine="ToggleButton(btnCoolant, \"Coolant\")";
_togglebutton(mostCurrent._btncoolant,"Coolant");
 //BA.debugLineNum = 348;BA.debugLine="End Sub";
return "";
}
public static String  _draw3dmessage() throws Exception{
 //BA.debugLineNum = 486;BA.debugLine="Sub Draw3DMessage";
 //BA.debugLineNum = 487;BA.debugLine="viewerCanvas.ClearRect(viewerCanvas.TargetRect)";
mostCurrent._viewercanvas.ClearRect(mostCurrent._viewercanvas.getTargetRect());
 //BA.debugLineNum = 488;BA.debugLine="viewerCanvas.DrawText(\"Click 'File' to load G-Cod";
mostCurrent._viewercanvas.DrawText(processBA,"Click 'File' to load G-Code",(float) (mostCurrent._viewercanvas.getTargetRect().getWidth()/(double)2),(float) (mostCurrent._viewercanvas.getTargetRect().getHeight()/(double)2),_xui.CreateDefaultFont((float) (16)),anywheresoftware.b4a.keywords.Common.Colors.Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 //BA.debugLineNum = 489;BA.debugLine="viewerCanvas.Invalidate";
mostCurrent._viewercanvas.Invalidate();
 //BA.debugLineNum = 490;BA.debugLine="End Sub";
return "";
}
public static String  _draw3dpath() throws Exception{
float _w = 0f;
float _h = 0f;
float _scale = 0f;
float _offx = 0f;
float _offy = 0f;
anywheresoftware.b4a.objects.collections.Map _prev = null;
float _prevx = 0f;
float _prevy = 0f;
int _i = 0;
anywheresoftware.b4a.objects.collections.Map _cur = null;
float _curx = 0f;
float _cury = 0f;
float _x1 = 0f;
float _y1 = 0f;
float _x2 = 0f;
float _y2 = 0f;
int _color = 0;
 //BA.debugLineNum = 621;BA.debugLine="Sub Draw3DPath";
 //BA.debugLineNum = 622;BA.debugLine="viewerCanvas.ClearRect(viewerCanvas.TargetRect)";
mostCurrent._viewercanvas.ClearRect(mostCurrent._viewercanvas.getTargetRect());
 //BA.debugLineNum = 624;BA.debugLine="If viewerPoints.Size < 2 Then";
if (mostCurrent._viewerpoints.getSize()<2) { 
 //BA.debugLineNum = 625;BA.debugLine="Draw3DMessage";
_draw3dmessage();
 //BA.debugLineNum = 626;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 629;BA.debugLine="Dim w As Float = viewerMaxX - viewerMinX";
_w = (float) (_viewermaxx-_viewerminx);
 //BA.debugLineNum = 630;BA.debugLine="Dim h As Float = viewerMaxY - viewerMinY";
_h = (float) (_viewermaxy-_viewerminy);
 //BA.debugLineNum = 631;BA.debugLine="If w = 0 Then w = 1";
if (_w==0) { 
_w = (float) (1);};
 //BA.debugLineNum = 632;BA.debugLine="If h = 0 Then h = 1";
if (_h==0) { 
_h = (float) (1);};
 //BA.debugLineNum = 634;BA.debugLine="Dim scale As Float = Min((viewerCanvas.TargetRect";
_scale = (float) (anywheresoftware.b4a.keywords.Common.Min((mostCurrent._viewercanvas.getTargetRect().getWidth()*0.7)/(double)_w,(mostCurrent._viewercanvas.getTargetRect().getHeight()*0.7)/(double)_h));
 //BA.debugLineNum = 635;BA.debugLine="Dim offX As Float = (viewerCanvas.TargetRect.Widt";
_offx = (float) ((mostCurrent._viewercanvas.getTargetRect().getWidth()-_w*_scale)/(double)2-_viewerminx*_scale);
 //BA.debugLineNum = 636;BA.debugLine="Dim offY As Float = (viewerCanvas.TargetRect.Heig";
_offy = (float) ((mostCurrent._viewercanvas.getTargetRect().getHeight()-_h*_scale)/(double)2-_viewerminy*_scale);
 //BA.debugLineNum = 638;BA.debugLine="Dim prev As Map = viewerPoints.Get(0)";
_prev = new anywheresoftware.b4a.objects.collections.Map();
_prev = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(mostCurrent._viewerpoints.Get((int) (0))));
 //BA.debugLineNum = 639;BA.debugLine="Dim prevX As Float = Project3DX(prev)";
_prevx = _project3dx(_prev);
 //BA.debugLineNum = 640;BA.debugLine="Dim prevY As Float = Project3DY(prev)";
_prevy = _project3dy(_prev);
 //BA.debugLineNum = 642;BA.debugLine="For i = 1 To viewerPoints.Size - 1";
{
final int step16 = 1;
final int limit16 = (int) (mostCurrent._viewerpoints.getSize()-1);
_i = (int) (1) ;
for (;_i <= limit16 ;_i = _i + step16 ) {
 //BA.debugLineNum = 643;BA.debugLine="Dim cur As Map = viewerPoints.Get(i)";
_cur = new anywheresoftware.b4a.objects.collections.Map();
_cur = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(mostCurrent._viewerpoints.Get(_i)));
 //BA.debugLineNum = 644;BA.debugLine="Dim curX As Float = Project3DX(cur)";
_curx = _project3dx(_cur);
 //BA.debugLineNum = 645;BA.debugLine="Dim curY As Float = Project3DY(cur)";
_cury = _project3dy(_cur);
 //BA.debugLineNum = 647;BA.debugLine="Dim x1 As Float = prevX * scale + offX";
_x1 = (float) (_prevx*_scale+_offx);
 //BA.debugLineNum = 648;BA.debugLine="Dim y1 As Float = viewerCanvas.TargetRect.Height";
_y1 = (float) (mostCurrent._viewercanvas.getTargetRect().getHeight()-(_prevy*_scale+_offy));
 //BA.debugLineNum = 649;BA.debugLine="Dim x2 As Float = curX * scale + offX";
_x2 = (float) (_curx*_scale+_offx);
 //BA.debugLineNum = 650;BA.debugLine="Dim y2 As Float = viewerCanvas.TargetRect.Height";
_y2 = (float) (mostCurrent._viewercanvas.getTargetRect().getHeight()-(_cury*_scale+_offy));
 //BA.debugLineNum = 652;BA.debugLine="Dim color As Int = Colors.RGB(100, 100, 100)";
_color = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (100),(int) (100),(int) (100));
 //BA.debugLineNum = 653;BA.debugLine="If cur.Get(\"cut\") = True Then color = Colors.Cya";
if ((_cur.Get((Object)("cut"))).equals((Object)(anywheresoftware.b4a.keywords.Common.True))) { 
_color = anywheresoftware.b4a.keywords.Common.Colors.Cyan;};
 //BA.debugLineNum = 655;BA.debugLine="viewerCanvas.DrawLine(x1, y1, x2, y2, color, 1.5";
mostCurrent._viewercanvas.DrawLine(_x1,_y1,_x2,_y2,_color,(float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1.5))));
 //BA.debugLineNum = 657;BA.debugLine="prevX = curX";
_prevx = _curx;
 //BA.debugLineNum = 658;BA.debugLine="prevY = curY";
_prevy = _cury;
 }
};
 //BA.debugLineNum = 661;BA.debugLine="viewerCanvas.Invalidate";
mostCurrent._viewercanvas.Invalidate();
 //BA.debugLineNum = 662;BA.debugLine="End Sub";
return "";
}
public static float  _get3dvalue(String _line,String _letter,float _def) throws Exception{
anywheresoftware.b4a.keywords.Regex.MatcherWrapper _m = null;
 //BA.debugLineNum = 572;BA.debugLine="Sub Get3DValue(line As String, letter As String, d";
 //BA.debugLineNum = 573;BA.debugLine="Dim m As Matcher = Regex.Matcher(letter & \"([-+]?";
_m = new anywheresoftware.b4a.keywords.Regex.MatcherWrapper();
_m = anywheresoftware.b4a.keywords.Common.Regex.Matcher(_letter+"([-+]?[0-9]*\\.?[0-9]+)",_line);
 //BA.debugLineNum = 574;BA.debugLine="If m.Find Then Return m.Group(1)";
if (_m.Find()) { 
if (true) return (float)(Double.parseDouble(_m.Group((int) (1))));};
 //BA.debugLineNum = 575;BA.debugLine="Return def";
if (true) return _def;
 //BA.debugLineNum = 576;BA.debugLine="End Sub";
return 0f;
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 21;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 23;BA.debugLine="Private pnlTopBar As Panel";
mostCurrent._pnltopbar = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private lblStatus, lblTitle, lblDateTime As Label";
mostCurrent._lblstatus = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._lbltitle = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._lbldatetime = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private pnlLeftMain As Panel";
mostCurrent._pnlleftmain = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Private lblCols(3) As Label";
mostCurrent._lblcols = new anywheresoftware.b4a.objects.LabelWrapper[(int) (3)];
{
int d0 = mostCurrent._lblcols.length;
for (int i0 = 0;i0 < d0;i0++) {
mostCurrent._lblcols[i0] = new anywheresoftware.b4a.objects.LabelWrapper();
}
}
;
 //BA.debugLineNum = 29;BA.debugLine="Private btnAxis(5) As Button";
mostCurrent._btnaxis = new anywheresoftware.b4a.objects.ButtonWrapper[(int) (5)];
{
int d0 = mostCurrent._btnaxis.length;
for (int i0 = 0;i0 < d0;i0++) {
mostCurrent._btnaxis[i0] = new anywheresoftware.b4a.objects.ButtonWrapper();
}
}
;
 //BA.debugLineNum = 30;BA.debugLine="Private lblWorkVals(5), lblMachVals(5) As Label";
mostCurrent._lblworkvals = new anywheresoftware.b4a.objects.LabelWrapper[(int) (5)];
{
int d0 = mostCurrent._lblworkvals.length;
for (int i0 = 0;i0 < d0;i0++) {
mostCurrent._lblworkvals[i0] = new anywheresoftware.b4a.objects.LabelWrapper();
}
}
;
mostCurrent._lblmachvals = new anywheresoftware.b4a.objects.LabelWrapper[(int) (5)];
{
int d0 = mostCurrent._lblmachvals.length;
for (int i0 = 0;i0 < d0;i0++) {
mostCurrent._lblmachvals[i0] = new anywheresoftware.b4a.objects.LabelWrapper();
}
}
;
 //BA.debugLineNum = 31;BA.debugLine="Private btnStart, btnPause, btnReset As Button";
mostCurrent._btnstart = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._btnpause = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._btnreset = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Private pnlTerminal As Panel";
mostCurrent._pnlterminal = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Private txtGCodeDisplay As EditText";
mostCurrent._txtgcodedisplay = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 38;BA.debugLine="Private pnlRightMain As Panel";
mostCurrent._pnlrightmain = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 39;BA.debugLine="Private pnl3DPlaceholder As Panel";
mostCurrent._pnl3dplaceholder = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 40;BA.debugLine="Private lbl3DStats As Label";
mostCurrent._lbl3dstats = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 43;BA.debugLine="Private btnSpindleCW, btnSpindleStop, btnSpindleC";
mostCurrent._btnspindlecw = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._btnspindlestop = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._btnspindleccw = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 44;BA.debugLine="Private txtSpindleSpeed As EditText";
mostCurrent._txtspindlespeed = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 45;BA.debugLine="Private btnSingleBlock, btnCoolant, btnGotoZero A";
mostCurrent._btnsingleblock = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._btncoolant = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._btngotozero = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 46;BA.debugLine="Private btnOverride, btnMDI, btnJog, btnMore As B";
mostCurrent._btnoverride = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._btnmdi = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._btnjog = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._btnmore = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 49;BA.debugLine="Private pnlBottomNav As Panel";
mostCurrent._pnlbottomnav = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 50;BA.debugLine="Private btnNav(6) As Button";
mostCurrent._btnnav = new anywheresoftware.b4a.objects.ButtonWrapper[(int) (6)];
{
int d0 = mostCurrent._btnnav.length;
for (int i0 = 0;i0 < d0;i0++) {
mostCurrent._btnnav[i0] = new anywheresoftware.b4a.objects.ButtonWrapper();
}
}
;
 //BA.debugLineNum = 52;BA.debugLine="Private ButtonStates As Map";
mostCurrent._buttonstates = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 53;BA.debugLine="Private IsBlinkActiveState As Boolean = False";
_isblinkactivestate = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 56;BA.debugLine="Private viewerCanvas As B4XCanvas";
mostCurrent._viewercanvas = new anywheresoftware.b4a.objects.B4XCanvas();
 //BA.debugLineNum = 57;BA.debugLine="Private viewerPoints As List";
mostCurrent._viewerpoints = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 58;BA.debugLine="Private viewerMinX, viewerMaxX, viewerMinY, viewe";
_viewerminx = 0f;
_viewermaxx = 0f;
_viewerminy = 0f;
_viewermaxy = 0f;
 //BA.debugLineNum = 59;BA.debugLine="Private viewerAngleX As Float = 0.5";
_vieweranglex = (float) (0.5);
 //BA.debugLineNum = 60;BA.debugLine="Private viewerAngleZ As Float = 0.785";
_vieweranglez = (float) (0.785);
 //BA.debugLineNum = 61;BA.debugLine="Private viewerStartX, viewerStartY As Float";
_viewerstartx = 0f;
_viewerstarty = 0f;
 //BA.debugLineNum = 62;BA.debugLine="Private viewerStartAngleX, viewerStartAngleZ As F";
_viewerstartanglex = 0f;
_viewerstartanglez = 0f;
 //BA.debugLineNum = 64;BA.debugLine="End Sub";
return "";
}
public static String  _gotozero_click() throws Exception{
 //BA.debugLineNum = 350;BA.debugLine="Sub GotoZero_Click";
 //BA.debugLineNum = 351;BA.debugLine="ToggleButton(btnGotoZero, \"GOTO Zero\")";
_togglebutton(mostCurrent._btngotozero,"GOTO Zero");
 //BA.debugLineNum = 352;BA.debugLine="End Sub";
return "";
}
public static String  _init3dviewer() throws Exception{
 //BA.debugLineNum = 479;BA.debugLine="Sub Init3DViewer";
 //BA.debugLineNum = 480;BA.debugLine="viewerCanvas.Initialize(pnl3DPlaceholder)";
mostCurrent._viewercanvas.Initialize((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(mostCurrent._pnl3dplaceholder.getObject())));
 //BA.debugLineNum = 481;BA.debugLine="viewerPoints.Initialize";
mostCurrent._viewerpoints.Initialize();
 //BA.debugLineNum = 482;BA.debugLine="viewerCC.Initialize(\"viewerCC\")";
_viewercc.Initialize("viewerCC");
 //BA.debugLineNum = 483;BA.debugLine="Draw3DMessage";
_draw3dmessage();
 //BA.debugLineNum = 484;BA.debugLine="End Sub";
return "";
}
public static String  _jog_click() throws Exception{
 //BA.debugLineNum = 362;BA.debugLine="Sub Jog_Click";
 //BA.debugLineNum = 363;BA.debugLine="ToggleButton(btnJog, \"Jog\")";
_togglebutton(mostCurrent._btnjog,"Jog");
 //BA.debugLineNum = 364;BA.debugLine="End Sub";
return "";
}
public static String  _mdi_click() throws Exception{
 //BA.debugLineNum = 358;BA.debugLine="Sub MDI_Click";
 //BA.debugLineNum = 359;BA.debugLine="ToggleButton(btnMDI, \"MDI\")";
_togglebutton(mostCurrent._btnmdi,"MDI");
 //BA.debugLineNum = 360;BA.debugLine="End Sub";
return "";
}
public static String  _more_click() throws Exception{
 //BA.debugLineNum = 366;BA.debugLine="Sub More_Click";
 //BA.debugLineNum = 367;BA.debugLine="ToggleButton(btnMore, \"More\")";
_togglebutton(mostCurrent._btnmore,"More");
 //BA.debugLineNum = 368;BA.debugLine="End Sub";
return "";
}
public static String  _nav_click() throws Exception{
anywheresoftware.b4a.objects.ButtonWrapper _clickedbtn = null;
 //BA.debugLineNum = 452;BA.debugLine="Sub Nav_Click";
 //BA.debugLineNum = 453;BA.debugLine="Dim clickedBtn As Button = Sender";
_clickedbtn = new anywheresoftware.b4a.objects.ButtonWrapper();
_clickedbtn = (anywheresoftware.b4a.objects.ButtonWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ButtonWrapper(), (android.widget.Button)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
 //BA.debugLineNum = 455;BA.debugLine="Select clickedBtn.Text";
switch (BA.switchObjectToInt(_clickedbtn.getText(),"File","Settings")) {
case 0: {
 //BA.debugLineNum = 457;BA.debugLine="Open3DFile";
_open3dfile();
 break; }
case 1: {
 //BA.debugLineNum = 459;BA.debugLine="StartActivity(\"SettingsScreen\")";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)("SettingsScreen"));
 break; }
default: {
 //BA.debugLineNum = 461;BA.debugLine="ToastMessageShow(\"Navigation: \" & clickedBtn.Te";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Navigation: "+_clickedbtn.getText()),anywheresoftware.b4a.keywords.Common.False);
 break; }
}
;
 //BA.debugLineNum = 463;BA.debugLine="End Sub";
return "";
}
public static String  _oldviewercc_result(boolean _success,String _dir,String _filename) throws Exception{
String _text = "";
 //BA.debugLineNum = 496;BA.debugLine="Sub OLdviewerCC_Result (Success As Boolean, Dir As";
 //BA.debugLineNum = 497;BA.debugLine="If Success Then";
if (_success) { 
 //BA.debugLineNum = 498;BA.debugLine="Dim text As String = File.ReadString(Dir, FileNa";
_text = anywheresoftware.b4a.keywords.Common.File.ReadString(_dir,_filename);
 //BA.debugLineNum = 499;BA.debugLine="Parse3DFile(text)";
_parse3dfile(_text);
 //BA.debugLineNum = 500;BA.debugLine="Draw3DPath";
_draw3dpath();
 //BA.debugLineNum = 501;BA.debugLine="ToastMessageShow(\"Loaded: \" & FileName, False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Loaded: "+_filename),anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 503;BA.debugLine="End Sub";
return "";
}
public static String  _open3dfile() throws Exception{
 //BA.debugLineNum = 492;BA.debugLine="Sub Open3DFile";
 //BA.debugLineNum = 493;BA.debugLine="viewerCC.Show(\"*/*\", \"Select G-Code File\")";
_viewercc.Show(processBA,"*/*","Select G-Code File");
 //BA.debugLineNum = 494;BA.debugLine="End Sub";
return "";
}
public static String  _override_click() throws Exception{
 //BA.debugLineNum = 354;BA.debugLine="Sub Override_Click";
 //BA.debugLineNum = 355;BA.debugLine="ToggleButton(btnOverride, \"Override\")";
_togglebutton(mostCurrent._btnoverride,"Override");
 //BA.debugLineNum = 356;BA.debugLine="End Sub";
return "";
}
public static String  _parse3dfile(String _text) throws Exception{
String[] _lines = null;
float _cx = 0f;
float _cy = 0f;
float _cz = 0f;
String _line = "";
String _l = "";
float _nx = 0f;
float _ny = 0f;
float _nz = 0f;
boolean _cutting = false;
anywheresoftware.b4a.objects.collections.Map _p = null;
 //BA.debugLineNum = 542;BA.debugLine="Sub Parse3DFile(Text As String)";
 //BA.debugLineNum = 543;BA.debugLine="viewerPoints.Clear";
mostCurrent._viewerpoints.Clear();
 //BA.debugLineNum = 544;BA.debugLine="Dim Lines() As String = Regex.Split(CRLF, Text)";
_lines = anywheresoftware.b4a.keywords.Common.Regex.Split(anywheresoftware.b4a.keywords.Common.CRLF,_text);
 //BA.debugLineNum = 545;BA.debugLine="Dim cx As Float = 0, cy As Float = 0, cz As Float";
_cx = (float) (0);
_cy = (float) (0);
_cz = (float) (0);
 //BA.debugLineNum = 547;BA.debugLine="For Each line As String In Lines";
{
final String[] group4 = _lines;
final int groupLen4 = group4.length
;int index4 = 0;
;
for (; index4 < groupLen4;index4++){
_line = group4[index4];
 //BA.debugLineNum = 548;BA.debugLine="Dim l As String = line.Trim.ToUpperCase";
_l = _line.trim().toUpperCase(anywheresoftware.b4a.keywords.Common.stringLocale);
 //BA.debugLineNum = 549;BA.debugLine="If l.StartsWith(\";\") Or l.StartsWith(\"(\") Or l =";
if (_l.startsWith(";") || _l.startsWith("(") || (_l).equals("")) { 
if (true) continue;};
 //BA.debugLineNum = 550;BA.debugLine="If l.Contains(\"(\") Then l = l.SubString2(0, l.In";
if (_l.contains("(")) { 
_l = _l.substring((int) (0),_l.indexOf("(")).trim();};
 //BA.debugLineNum = 552;BA.debugLine="If l.StartsWith(\"G0\") Or l.StartsWith(\"G1\") Then";
if (_l.startsWith("G0") || _l.startsWith("G1")) { 
 //BA.debugLineNum = 553;BA.debugLine="Dim nx As Float = Get3DValue(l, \"X\", cx)";
_nx = _get3dvalue(_l,"X",_cx);
 //BA.debugLineNum = 554;BA.debugLine="Dim ny As Float = Get3DValue(l, \"Y\", cy)";
_ny = _get3dvalue(_l,"Y",_cy);
 //BA.debugLineNum = 555;BA.debugLine="Dim nz As Float = Get3DValue(l, \"Z\", cz)";
_nz = _get3dvalue(_l,"Z",_cz);
 //BA.debugLineNum = 556;BA.debugLine="Dim cutting As Boolean = Not(l.StartsWith(\"G0\")";
_cutting = anywheresoftware.b4a.keywords.Common.Not(_l.startsWith("G0"));
 //BA.debugLineNum = 558;BA.debugLine="Dim p As Map";
_p = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 559;BA.debugLine="p.Initialize";
_p.Initialize();
 //BA.debugLineNum = 560;BA.debugLine="p.Put(\"x\", nx)";
_p.Put((Object)("x"),(Object)(_nx));
 //BA.debugLineNum = 561;BA.debugLine="p.Put(\"y\", ny)";
_p.Put((Object)("y"),(Object)(_ny));
 //BA.debugLineNum = 562;BA.debugLine="p.Put(\"z\", nz)";
_p.Put((Object)("z"),(Object)(_nz));
 //BA.debugLineNum = 563;BA.debugLine="p.Put(\"cut\", cutting)";
_p.Put((Object)("cut"),(Object)(_cutting));
 //BA.debugLineNum = 564;BA.debugLine="viewerPoints.Add(p)";
mostCurrent._viewerpoints.Add((Object)(_p.getObject()));
 //BA.debugLineNum = 566;BA.debugLine="cx = nx : cy = ny : cz = nz";
_cx = _nx;
 //BA.debugLineNum = 566;BA.debugLine="cx = nx : cy = ny : cz = nz";
_cy = _ny;
 //BA.debugLineNum = 566;BA.debugLine="cx = nx : cy = ny : cz = nz";
_cz = _nz;
 };
 }
};
 //BA.debugLineNum = 569;BA.debugLine="Calculate3DBounds";
_calculate3dbounds();
 //BA.debugLineNum = 570;BA.debugLine="End Sub";
return "";
}
public static String  _pause_click() throws Exception{
 //BA.debugLineNum = 397;BA.debugLine="Sub Pause_Click";
 //BA.debugLineNum = 398;BA.debugLine="tmrBlink.Enabled = Not(tmrBlink.Enabled)";
_tmrblink.setEnabled(anywheresoftware.b4a.keywords.Common.Not(_tmrblink.getEnabled()));
 //BA.debugLineNum = 399;BA.debugLine="If tmrBlink.Enabled = False Then";
if (_tmrblink.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 400;BA.debugLine="btnPause.Color = Colors.RGB(220, 53, 69)";
mostCurrent._btnpause.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (220),(int) (53),(int) (69)));
 //BA.debugLineNum = 401;BA.debugLine="btnPause.TextColor = Colors.White";
mostCurrent._btnpause.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 };
 //BA.debugLineNum = 403;BA.debugLine="End Sub";
return "";
}
public static String  _pnl3dplaceholder_touch(int _action,float _x,float _y) throws Exception{
 //BA.debugLineNum = 605;BA.debugLine="Sub pnl3DPlaceholder_Touch(Action As Int, X As Flo";
 //BA.debugLineNum = 606;BA.debugLine="If Action = 0 Then";
if (_action==0) { 
 //BA.debugLineNum = 607;BA.debugLine="viewerStartX = X";
_viewerstartx = _x;
 //BA.debugLineNum = 608;BA.debugLine="viewerStartY = Y";
_viewerstarty = _y;
 //BA.debugLineNum = 609;BA.debugLine="viewerStartAngleX = viewerAngleX";
_viewerstartanglex = _vieweranglex;
 //BA.debugLineNum = 610;BA.debugLine="viewerStartAngleZ = viewerAngleZ";
_viewerstartanglez = _vieweranglez;
 }else if(_action==2) { 
 //BA.debugLineNum = 612;BA.debugLine="viewerAngleZ = viewerStartAngleZ + (X - viewerSt";
_vieweranglez = (float) (_viewerstartanglez+(_x-_viewerstartx)*0.007);
 //BA.debugLineNum = 613;BA.debugLine="viewerAngleX = viewerStartAngleX - (Y - viewerSt";
_vieweranglex = (float) (_viewerstartanglex-(_y-_viewerstarty)*0.007);
 //BA.debugLineNum = 614;BA.debugLine="If viewerAngleX < 0.1 Then viewerAngleX = 0.1";
if (_vieweranglex<0.1) { 
_vieweranglex = (float) (0.1);};
 //BA.debugLineNum = 615;BA.debugLine="If viewerAngleX > 1.5 Then viewerAngleX = 1.5";
if (_vieweranglex>1.5) { 
_vieweranglex = (float) (1.5);};
 //BA.debugLineNum = 616;BA.debugLine="Calculate3DBounds";
_calculate3dbounds();
 //BA.debugLineNum = 617;BA.debugLine="Draw3DPath";
_draw3dpath();
 };
 //BA.debugLineNum = 619;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        b4a.example.dateutils._process_globals();
main._process_globals();
starter._process_globals();
settingsscreen._process_globals();
xuiviewsutils._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 16;BA.debugLine="Private tmrBlink As Timer";
_tmrblink = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 17;BA.debugLine="Private xui As XUI";
_xui = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 18;BA.debugLine="Private viewerCC As ContentChooser   ' ADD THIS L";
_viewercc = new anywheresoftware.b4a.phone.Phone.ContentChooser();
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static float  _project3dx(anywheresoftware.b4a.objects.collections.Map _p) throws Exception{
float _x = 0f;
float _y = 0f;
 //BA.debugLineNum = 591;BA.debugLine="Sub Project3DX(p As Map) As Float";
 //BA.debugLineNum = 592;BA.debugLine="Dim x As Float = p.Get(\"x\")";
_x = (float)(BA.ObjectToNumber(_p.Get((Object)("x"))));
 //BA.debugLineNum = 593;BA.debugLine="Dim y As Float = p.Get(\"y\")";
_y = (float)(BA.ObjectToNumber(_p.Get((Object)("y"))));
 //BA.debugLineNum = 594;BA.debugLine="Return x * Cos(viewerAngleZ) - y * Sin(viewerAngl";
if (true) return (float) (_x*anywheresoftware.b4a.keywords.Common.Cos(_vieweranglez)-_y*anywheresoftware.b4a.keywords.Common.Sin(_vieweranglez));
 //BA.debugLineNum = 595;BA.debugLine="End Sub";
return 0f;
}
public static float  _project3dy(anywheresoftware.b4a.objects.collections.Map _p) throws Exception{
float _x = 0f;
float _y = 0f;
float _z = 0f;
float _ry = 0f;
 //BA.debugLineNum = 597;BA.debugLine="Sub Project3DY(p As Map) As Float";
 //BA.debugLineNum = 598;BA.debugLine="Dim x As Float = p.Get(\"x\")";
_x = (float)(BA.ObjectToNumber(_p.Get((Object)("x"))));
 //BA.debugLineNum = 599;BA.debugLine="Dim y As Float = p.Get(\"y\")";
_y = (float)(BA.ObjectToNumber(_p.Get((Object)("y"))));
 //BA.debugLineNum = 600;BA.debugLine="Dim z As Float = p.Get(\"z\")";
_z = (float)(BA.ObjectToNumber(_p.Get((Object)("z"))));
 //BA.debugLineNum = 601;BA.debugLine="Dim ry As Float = x * Sin(viewerAngleZ) + y * Cos";
_ry = (float) (_x*anywheresoftware.b4a.keywords.Common.Sin(_vieweranglez)+_y*anywheresoftware.b4a.keywords.Common.Cos(_vieweranglez));
 //BA.debugLineNum = 602;BA.debugLine="Return ry * Cos(viewerAngleX) - z * Sin(viewerAng";
if (true) return (float) (_ry*anywheresoftware.b4a.keywords.Common.Cos(_vieweranglex)-_z*anywheresoftware.b4a.keywords.Common.Sin(_vieweranglex));
 //BA.debugLineNum = 603;BA.debugLine="End Sub";
return 0f;
}
public static String  _reset_click() throws Exception{
 //BA.debugLineNum = 405;BA.debugLine="Sub Reset_Click";
 //BA.debugLineNum = 406;BA.debugLine="tmrBlink.Enabled = False";
_tmrblink.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 407;BA.debugLine="btnPause.Color = Colors.RGB(220, 53, 69)";
mostCurrent._btnpause.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (220),(int) (53),(int) (69)));
 //BA.debugLineNum = 408;BA.debugLine="btnPause.TextColor = Colors.White";
mostCurrent._btnpause.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 410;BA.debugLine="ButtonStates.Put(\"Spindle CW\", False)";
mostCurrent._buttonstates.Put((Object)("Spindle CW"),(Object)(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 411;BA.debugLine="ButtonStates.Put(\"Spindle Stop\", True)";
mostCurrent._buttonstates.Put((Object)("Spindle Stop"),(Object)(anywheresoftware.b4a.keywords.Common.True));
 //BA.debugLineNum = 412;BA.debugLine="ButtonStates.Put(\"Spindle CCW\", False)";
mostCurrent._buttonstates.Put((Object)("Spindle CCW"),(Object)(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 413;BA.debugLine="ButtonStates.Put(\"Single Block\", False)";
mostCurrent._buttonstates.Put((Object)("Single Block"),(Object)(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 414;BA.debugLine="ButtonStates.Put(\"Coolant\", False)";
mostCurrent._buttonstates.Put((Object)("Coolant"),(Object)(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 415;BA.debugLine="ButtonStates.Put(\"GOTO Zero\", False)";
mostCurrent._buttonstates.Put((Object)("GOTO Zero"),(Object)(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 416;BA.debugLine="ButtonStates.Put(\"Override\", False)";
mostCurrent._buttonstates.Put((Object)("Override"),(Object)(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 417;BA.debugLine="ButtonStates.Put(\"MDI\", False)";
mostCurrent._buttonstates.Put((Object)("MDI"),(Object)(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 418;BA.debugLine="ButtonStates.Put(\"Jog\", False)";
mostCurrent._buttonstates.Put((Object)("Jog"),(Object)(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 419;BA.debugLine="ButtonStates.Put(\"More\", False)";
mostCurrent._buttonstates.Put((Object)("More"),(Object)(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 421;BA.debugLine="btnSpindleCW.Color = Colors.RGB(64, 64, 64)";
mostCurrent._btnspindlecw.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (64),(int) (64),(int) (64)));
 //BA.debugLineNum = 422;BA.debugLine="btnSpindleCW.TextColor = Colors.White";
mostCurrent._btnspindlecw.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 423;BA.debugLine="btnSpindleStop.Color = Colors.RGB(212, 163, 89)";
mostCurrent._btnspindlestop.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (212),(int) (163),(int) (89)));
 //BA.debugLineNum = 424;BA.debugLine="btnSpindleStop.TextColor = Colors.Black";
mostCurrent._btnspindlestop.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 425;BA.debugLine="btnSpindleCCW.Color = Colors.RGB(64, 64, 64)";
mostCurrent._btnspindleccw.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (64),(int) (64),(int) (64)));
 //BA.debugLineNum = 426;BA.debugLine="btnSpindleCCW.TextColor = Colors.White";
mostCurrent._btnspindleccw.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 427;BA.debugLine="btnSingleBlock.Color = Colors.RGB(64, 64, 64)";
mostCurrent._btnsingleblock.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (64),(int) (64),(int) (64)));
 //BA.debugLineNum = 428;BA.debugLine="btnSingleBlock.TextColor = Colors.White";
mostCurrent._btnsingleblock.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 429;BA.debugLine="btnCoolant.Color = Colors.RGB(64, 64, 64)";
mostCurrent._btncoolant.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (64),(int) (64),(int) (64)));
 //BA.debugLineNum = 430;BA.debugLine="btnCoolant.TextColor = Colors.White";
mostCurrent._btncoolant.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 431;BA.debugLine="btnGotoZero.Color = Colors.RGB(64, 64, 64)";
mostCurrent._btngotozero.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (64),(int) (64),(int) (64)));
 //BA.debugLineNum = 432;BA.debugLine="btnGotoZero.TextColor = Colors.White";
mostCurrent._btngotozero.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 433;BA.debugLine="btnOverride.Color = Colors.RGB(64, 64, 64)";
mostCurrent._btnoverride.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (64),(int) (64),(int) (64)));
 //BA.debugLineNum = 434;BA.debugLine="btnOverride.TextColor = Colors.White";
mostCurrent._btnoverride.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 435;BA.debugLine="btnMDI.Color = Colors.RGB(64, 64, 64)";
mostCurrent._btnmdi.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (64),(int) (64),(int) (64)));
 //BA.debugLineNum = 436;BA.debugLine="btnMDI.TextColor = Colors.White";
mostCurrent._btnmdi.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 437;BA.debugLine="btnJog.Color = Colors.RGB(64, 64, 64)";
mostCurrent._btnjog.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (64),(int) (64),(int) (64)));
 //BA.debugLineNum = 438;BA.debugLine="btnJog.TextColor = Colors.White";
mostCurrent._btnjog.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 439;BA.debugLine="btnMore.Color = Colors.RGB(64, 64, 64)";
mostCurrent._btnmore.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (64),(int) (64),(int) (64)));
 //BA.debugLineNum = 440;BA.debugLine="btnMore.TextColor = Colors.White";
mostCurrent._btnmore.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 442;BA.debugLine="Msgbox(\"System Reset\", \"RESET\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("System Reset"),BA.ObjectToCharSequence("RESET"),mostCurrent.activityBA);
 //BA.debugLineNum = 443;BA.debugLine="End Sub";
return "";
}
public static String  _singleblock_click() throws Exception{
 //BA.debugLineNum = 342;BA.debugLine="Sub SingleBlock_Click";
 //BA.debugLineNum = 343;BA.debugLine="ToggleButton(btnSingleBlock, \"Single Block\")";
_togglebutton(mostCurrent._btnsingleblock,"Single Block");
 //BA.debugLineNum = 344;BA.debugLine="End Sub";
return "";
}
public static String  _spindleccw_click() throws Exception{
 //BA.debugLineNum = 338;BA.debugLine="Sub SpindleCCW_Click";
 //BA.debugLineNum = 339;BA.debugLine="ToggleButton(btnSpindleCCW, \"Spindle CCW\")";
_togglebutton(mostCurrent._btnspindleccw,"Spindle CCW");
 //BA.debugLineNum = 340;BA.debugLine="End Sub";
return "";
}
public static String  _spindlecw_click() throws Exception{
 //BA.debugLineNum = 330;BA.debugLine="Sub SpindleCW_Click";
 //BA.debugLineNum = 331;BA.debugLine="ToggleButton(btnSpindleCW, \"Spindle CW\")";
_togglebutton(mostCurrent._btnspindlecw,"Spindle CW");
 //BA.debugLineNum = 332;BA.debugLine="End Sub";
return "";
}
public static String  _spindlestop_click() throws Exception{
 //BA.debugLineNum = 334;BA.debugLine="Sub SpindleStop_Click";
 //BA.debugLineNum = 335;BA.debugLine="ToggleButton(btnSpindleStop, \"Spindle Stop\")";
_togglebutton(mostCurrent._btnspindlestop,"Spindle Stop");
 //BA.debugLineNum = 336;BA.debugLine="End Sub";
return "";
}
public static String  _start_click() throws Exception{
 //BA.debugLineNum = 388;BA.debugLine="Sub Start_Click";
 //BA.debugLineNum = 389;BA.debugLine="If tmrBlink.Enabled Then";
if (_tmrblink.getEnabled()) { 
 //BA.debugLineNum = 390;BA.debugLine="tmrBlink.Enabled = False";
_tmrblink.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 391;BA.debugLine="btnPause.Color = Colors.RGB(220, 53, 69)";
mostCurrent._btnpause.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (220),(int) (53),(int) (69)));
 //BA.debugLineNum = 392;BA.debugLine="btnPause.TextColor = Colors.White";
mostCurrent._btnpause.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 };
 //BA.debugLineNum = 394;BA.debugLine="Msgbox(\"CNC Machine Starting!\", \"START\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("CNC Machine Starting!"),BA.ObjectToCharSequence("START"),mostCurrent.activityBA);
 //BA.debugLineNum = 395;BA.debugLine="End Sub";
return "";
}
public static String  _tmrblink_tick() throws Exception{
 //BA.debugLineNum = 466;BA.debugLine="Sub tmrBlink_Tick";
 //BA.debugLineNum = 467;BA.debugLine="IsBlinkActiveState = Not(IsBlinkActiveState)";
_isblinkactivestate = anywheresoftware.b4a.keywords.Common.Not(_isblinkactivestate);
 //BA.debugLineNum = 468;BA.debugLine="If IsBlinkActiveState Then";
if (_isblinkactivestate) { 
 //BA.debugLineNum = 469;BA.debugLine="btnPause.Color = Colors.RGB(240, 200, 140)";
mostCurrent._btnpause.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (240),(int) (200),(int) (140)));
 //BA.debugLineNum = 470;BA.debugLine="btnPause.TextColor = Colors.Black";
mostCurrent._btnpause.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 }else {
 //BA.debugLineNum = 472;BA.debugLine="btnPause.Color = Colors.RGB(220, 53, 69)";
mostCurrent._btnpause.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (220),(int) (53),(int) (69)));
 //BA.debugLineNum = 473;BA.debugLine="btnPause.TextColor = Colors.White";
mostCurrent._btnpause.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 };
 //BA.debugLineNum = 475;BA.debugLine="End Sub";
return "";
}
public static String  _togglebutton(anywheresoftware.b4a.objects.ButtonWrapper _btn,String _btnname) throws Exception{
boolean _currentstate = false;
boolean _newstate = false;
 //BA.debugLineNum = 370;BA.debugLine="Sub ToggleButton(btn As Button, btnName As String)";
 //BA.debugLineNum = 371;BA.debugLine="Dim currentState As Boolean = ButtonStates.GetDef";
_currentstate = BA.ObjectToBoolean(mostCurrent._buttonstates.GetDefault((Object)(_btnname),(Object)(anywheresoftware.b4a.keywords.Common.False)));
 //BA.debugLineNum = 372;BA.debugLine="Dim newState As Boolean = Not(currentState)";
_newstate = anywheresoftware.b4a.keywords.Common.Not(_currentstate);
 //BA.debugLineNum = 374;BA.debugLine="ButtonStates.Put(btnName, newState)";
mostCurrent._buttonstates.Put((Object)(_btnname),(Object)(_newstate));
 //BA.debugLineNum = 376;BA.debugLine="If newState Then";
if (_newstate) { 
 //BA.debugLineNum = 377;BA.debugLine="btn.Color = Colors.RGB(212, 163, 89)";
_btn.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (212),(int) (163),(int) (89)));
 //BA.debugLineNum = 378;BA.debugLine="btn.TextColor = Colors.Black";
_btn.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 }else {
 //BA.debugLineNum = 380;BA.debugLine="btn.Color = Colors.RGB(64, 64, 64)";
_btn.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (64),(int) (64),(int) (64)));
 //BA.debugLineNum = 381;BA.debugLine="btn.TextColor = Colors.White";
_btn.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 };
 //BA.debugLineNum = 384;BA.debugLine="ToastMessageShow(btnName & \" = \" & IIf(newState,";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(_btnname+" = "+BA.ObjectToString(((_newstate) ? ((Object)("ON")) : ((Object)("OFF"))))),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 385;BA.debugLine="End Sub";
return "";
}
public static String  _viewercc_result(boolean _success,String _dir,String _filename) throws Exception{
String _text = "";
String _realfilename = "";
 //BA.debugLineNum = 506;BA.debugLine="Sub viewerCC_Result (Success As Boolean, Dir As St";
 //BA.debugLineNum = 507;BA.debugLine="If Success Then";
if (_success) { 
 //BA.debugLineNum = 508;BA.debugLine="Dim text As String = File.ReadString(Dir, FileNa";
_text = anywheresoftware.b4a.keywords.Common.File.ReadString(_dir,_filename);
 //BA.debugLineNum = 511;BA.debugLine="txtGCodeDisplay.Text = text";
mostCurrent._txtgcodedisplay.setText(BA.ObjectToCharSequence(_text));
 //BA.debugLineNum = 514;BA.debugLine="txtGCodeDisplay.SelectionStart = 0";
mostCurrent._txtgcodedisplay.setSelectionStart((int) (0));
 //BA.debugLineNum = 517;BA.debugLine="Dim RealFileName As String = FileName";
_realfilename = _filename;
 //BA.debugLineNum = 520;BA.debugLine="If Dir.Length > 0 And FileName.Length > 0 Then";
if (_dir.length()>0 && _filename.length()>0) { 
 //BA.debugLineNum = 522;BA.debugLine="RealFileName = FileName";
_realfilename = _filename;
 };
 //BA.debugLineNum = 526;BA.debugLine="lblTitle.Text = RealFileName";
mostCurrent._lbltitle.setText(BA.ObjectToCharSequence(_realfilename));
 //BA.debugLineNum = 529;BA.debugLine="lblStatus.Text = \"Ready\"";
mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Ready"));
 //BA.debugLineNum = 532;BA.debugLine="ToastMessageShow(\"Loaded: \" & RealFileName, True";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Loaded: "+_realfilename),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 535;BA.debugLine="Parse3DFile(text)";
_parse3dfile(_text);
 //BA.debugLineNum = 536;BA.debugLine="Draw3DPath";
_draw3dpath();
 };
 //BA.debugLineNum = 538;BA.debugLine="End Sub";
return "";
}
}
