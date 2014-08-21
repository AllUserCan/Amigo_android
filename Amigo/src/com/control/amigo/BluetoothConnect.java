package com.control.amigo;

import com.example.amigo.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothConnect extends Activity implements OnClickListener {
	public static ArrayAdapter<String> adapterdevices;
	public static Context mContext;
	public static BTdevicestate mBTdevicestate = BTdevicestate.closed;
	private ListView deviceList;
	private Button openBtn,searchBtn;
	private ImageButton backbtn;
	
	enum BTdevicestate{ opened, closed, connected };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.bluetooth_connect);
		
		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,Gravity.CENTER);
        View viewTitleBar = getLayoutInflater().inflate(R.layout.actionbar_view_bt, null);
        getActionBar().setCustomView(viewTitleBar, lp);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setDisplayShowCustomEnabled(true);
        
        TextView title = (TextView)getActionBar().getCustomView().findViewById(R.id.title);
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ARBERKLEY.TTF"));
        
        openBtn = (Button) getActionBar().getCustomView().findViewById(R.id.openBtn);
        searchBtn = (Button) getActionBar().getCustomView().findViewById(R.id.searchBtn);
        backbtn = (ImageButton) getActionBar().getCustomView().findViewById(R.id.backbtn);
        openBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        
		deviceList = (ListView) findViewById(R.id.listView1);
		
		adapterdevices = new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1,BluetoothService.getdevice());
		deviceList.setAdapter(adapterdevices);
		deviceList.setOnItemClickListener(devicelstListener);
		
		mContext = this;
		
		if( BluetoothService.BTisEnabled() ){
			mBTdevicestate = BTdevicestate.opened;
		}
		else {
			mBTdevicestate = BTdevicestate.closed;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
        return true;
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if( v.getId()==R.id.searchBtn ){
			if( mBTdevicestate.equals(BTdevicestate.opened) ){
				BluetoothService.setBluetoothRearch();
			}
			else if( mBTdevicestate.equals(BTdevicestate.closed) ){
				showUnopenedMsg();
			}
		}
		else if( v.getId()==R.id.openBtn ){
			if( BluetoothService.BTisEnabled() ){
				showOpenedMsg();
				mBTdevicestate = BTdevicestate.opened;
			}
			else if( !BluetoothService.BTisEnabled() ){
				OpenDialog();
				mBTdevicestate = BTdevicestate.opened;
			}
		}
		else if( v.getId()==R.id.backbtn ){
			BluetoothService.resetdeveces();
			onBackPressed();
		}
	}
	
	private OnItemClickListener devicelstListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			if( BluetoothService.mBTState.equals(BluetoothService.BTState.running) ){
				showConnectedMsg();
			}
			else if( BluetoothService.mBTState.equals(BluetoothService.BTState.stopped) ){
				Intent intent = new Intent(BluetoothService.ACTION_CONNECT);
				intent.putExtra(BluetoothService.Tag_Position, position);
				startService(intent);
			}
		}
	};
	
	private void showOpenedMsg(){
		Toast.makeText(this, "藍牙已開啟", Toast.LENGTH_SHORT ).show();
	}
	
	private void showConnectedMsg(){
		Toast.makeText(this, "藍牙已連線", Toast.LENGTH_SHORT ).show();
	}
	
	private void showUnopenedMsg(){
		Toast.makeText(this, "藍牙未開啟", Toast.LENGTH_SHORT ).show();
	}
	
	private void OpenDialog(){
		final ProgressDialog progress = ProgressDialog.show(this, "正在開啟藍牙", "請稍候...",false);
		new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                	BluetoothService.setBluetoothOpen();
                    Thread.sleep(3000);
                    BluetoothService.setBluetoothRearch();
                    Thread.sleep(1000);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                finally{
                	progress.dismiss();
                }
            } 
       }).start();
	}
	
}
