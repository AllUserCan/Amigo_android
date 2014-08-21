package com.control.amigo;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.control.amigo.BluetoothService.AmigoState;
import com.control.amigo.BluetoothService.BTState;
import com.example.amigo.R;

public class FragtabsActivity extends FragmentActivity {
	private TabHost mTabHost;
    private TabManager mTabManager;
    private static boolean isExit = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.fragment_tabs);
        
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,Gravity.CENTER);
        View viewTitleBar = getLayoutInflater().inflate(R.layout.actionbar_view, null);
        getActionBar().setCustomView(viewTitleBar, lp);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setDisplayShowCustomEnabled(true);
        
        TextView title = (TextView)getActionBar().getCustomView().findViewById(R.id.title);
        title.setText("Amigo");
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ARBERKLEY.TTF"));
        
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        
        mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);
        
        mTabHost.setCurrentTab(0);
        
        View tab1 = LayoutInflater.from(this).inflate(R.layout.tab_view, null);
        TextView text1 = (TextView) tab1.findViewById(R.id.tabtext);
        text1.setText("Connect");
        mTabManager.addTab(mTabHost.newTabSpec("Connect").setIndicator(tab1),
        		Connect.class, null);
        View tab2 = LayoutInflater.from(this).inflate(R.layout.tab_view, null);
        TextView text2 = (TextView) tab2.findViewById(R.id.tabtext);
        text2.setText("Teleop");
        mTabManager.addTab(mTabHost.newTabSpec("Teleop").setIndicator(tab2),
        		Teleop.class, null);
        View tab3 = LayoutInflater.from(this).inflate(R.layout.tab_view, null);
        TextView text3 = (TextView) tab3.findViewById(R.id.tabtext);
        text3.setText("Wander");
        mTabManager.addTab(mTabHost.newTabSpec("Wander").setIndicator(tab3),
        		Wander.class, null);
        View tab4 = LayoutInflater.from(this).inflate(R.layout.tab_view, null);
        TextView text4 = (TextView) tab4.findViewById(R.id.tabtext);
        text4.setText("Monitor");
        mTabManager.addTab(mTabHost.newTabSpec("Monitor").setIndicator(tab4),
        		Monitor.class, null);

		   
        DisplayMetrics dm = new DisplayMetrics();   
        getWindowManager().getDefaultDisplay().getMetrics(dm); 
        int screenWidth = dm.widthPixels;
           
           
        TabWidget tabWidget = mTabHost.getTabWidget(); 
        int count = tabWidget.getChildCount(); 
        if (count > 3) { 
            for (int i = 0; i < count; i++) {   
                tabWidget.getChildTabViewAt(i).setMinimumWidth((screenWidth) / 4);
            }   
        }
        
        Intent floatwindowIntent = new Intent();
		floatwindowIntent.setClass(this, FloatWindowService.class);
		startService(floatwindowIntent);
		
		Intent monitorIntent = new Intent(MonitorService.ACTION_MONITOR_WORK);
		startService(monitorIntent);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if( BluetoothService.mBTState.equals(BTState.running) ){
			if( BluetoothService.mAmigoState.equals(AmigoState.running) ){
				BluetoothService.AmigoSwitch();
			}
			
			Intent intentBluetooth = new Intent(BluetoothService.ACTION_STOP);
			startService(intentBluetooth);
			stopService(intentBluetooth);
		}
		
		Intent intentFloatWindow = new Intent(this, FloatWindowService.class);
		stopService(intentFloatWindow);
		
		if( MonitorService.mWifiPosStatus.equals(MonitorService.WifiPosStatus.wificonnected) ){
			Intent monitorIntent = new Intent(MonitorService.ACTION_MONITOR_WIFIPOS_STOP);
			startService(monitorIntent);
		}
		
		Intent monitorIntent = new Intent(this, MonitorService.class);
		stopService(monitorIntent);
		
		System.exit(0);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if( keyCode == KeyEvent.KEYCODE_BACK ){
			exitBy2Click();
		}
		return false;
	}
	
	private void exitBy2Click() {  
	    Timer tExit = null;  
	    if (isExit == false) {  
	        isExit = true;  
	        Toast.makeText(this, "再按一次退出程式", Toast.LENGTH_SHORT).show();  
	        tExit = new Timer();  
	        tExit.schedule(new TimerTask() {  
	            @Override  
	            public void run() {  
	                isExit = false;
	            }  
	        }, 2000);
	  
	    } else {
	    	finish();
	    }  
	}
	
	@SuppressWarnings("deprecation")
	private GestureDetector detector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        	if ((e2.getRawX() - e1.getRawX()) > 80) {
        		showPre();
                return true;
        	}
        	else if ((e1.getRawX() - e2.getRawX()) > 80) {
                showNext();
                return true;
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
	});
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
	   	return super.onTouchEvent(event);
	}
	
	protected void showNext() {
		int hostIndex = mTabHost.getCurrentTab();
		if( hostIndex == 3 ){
			hostIndex = 0;
			mTabHost.setCurrentTab(hostIndex);
		}
		else{
			hostIndex++;
			mTabHost.setCurrentTab(hostIndex);
		}
	}
	
	protected void showPre() {
		int hostIndex = mTabHost.getCurrentTab();
		if( hostIndex == 0 ){
			hostIndex = 3;
			mTabHost.setCurrentTab(hostIndex);
		}
		else {
			hostIndex--;
			mTabHost.setCurrentTab(hostIndex);
		}
	}
	
}
