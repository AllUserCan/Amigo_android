package com.control.amigo;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.control.amigo.BluetoothService.AmigoState;
import com.control.amigo.BluetoothService.BTState;
import com.control.amigo.MonitorService.MobileCamStatus;
import com.control.amigo.MonitorService.WifiPosStatus;
import com.control.amigo.drive.AmigoCommunication;
import com.control.amigo.drive.PacketReceiver;
import com.example.amigo.R;

public class Monitor extends Fragment implements OnTouchListener {
	private ImageButton startMonitor;
	
	static boolean reach=false;
	static boolean startthr=false;
	static  BufferedReader infoin = null;
	static PrintWriter infoout = null;
	private  rev rv=new rev();
	infoth ifo=new infoth();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View InputFragmentView;
		String warning = "";
		if( BluetoothService.mBTState == BTState.stopped || BluetoothService.mBTState == BTState.connecting ){
			warning = "藍牙";
			if( BluetoothService.mAmigoState == AmigoState.stopped ){
				warning = warning + "、AmigoBot";
			}
		}
		else {
			if( BluetoothService.mAmigoState == AmigoState.stopped ){
				warning = warning + "AmigoBot";
			}
		}
		if( MonitorService.mWifiPosStatus == WifiPosStatus.stopped && BluetoothService.mAmigoState == AmigoState.stopped ){
			warning = warning + "、Wifi定位\r\n";
		}
		else if( MonitorService.mWifiPosStatus == WifiPosStatus.stopped ){
			warning = warning + "Wifi定位";
		}
		
		if( MonitorService.mWifiPosStatus == WifiPosStatus.stopped && BluetoothService.mAmigoState == AmigoState.stopped
				&& MonitorService.mMobileCamStatus == MobileCamStatus.stopped ){
			warning = warning + "、MobileCam";
		}
		else if( MonitorService.mWifiPosStatus == WifiPosStatus.stopped && MonitorService.mMobileCamStatus == MobileCamStatus.stopped ){
			warning = warning + "、MobileCam";
		}
		else if( MonitorService.mMobileCamStatus == MobileCamStatus.stopped ){
			warning = warning + "MobileCam";
		}
		
		if( (BluetoothService.mBTState == BTState.stopped || BluetoothService.mBTState == BTState.connecting) 
				|| BluetoothService.mAmigoState == AmigoState.stopped
				|| MonitorService.mWifiPosStatus == WifiPosStatus.stopped
				|| MonitorService.mMobileCamStatus == MobileCamStatus.stopped ){
			InputFragmentView = inflater.inflate(R.layout.unconnect_view, container, false);
			warning = warning + "尚未連線";
			
			TextView warningtext = (TextView) InputFragmentView.findViewById(R.id.warningtext);
			warningtext.setText(warning);
		}
		else if( AmigoCommunication.WanderMode ){
			InputFragmentView = inflater.inflate(R.layout.unconnect_view, container, false);
			warning = "WanderMode is running!!!";
			
			TextView warningtext = (TextView) InputFragmentView.findViewById(R.id.warningtext);
			warningtext.setText(warning);
		}
		else {
			InputFragmentView = inflater.inflate(R.layout.monitor_view, container, false);
			startMonitor = (ImageButton) InputFragmentView.findViewById(R.id.startmonitor);
			
			startMonitor.setOnTouchListener(this);
			
			if( AmigoCommunication.MonitorMode ){
				startMonitor.setBackgroundResource(R.drawable.monitor_stop);
			}
			else if( AmigoCommunication.MonitorMode ){
				startMonitor.setBackgroundResource(R.drawable.monitor_start);
			}
		}
		
		return InputFragmentView;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if( v.getId()==R.id.startmonitor ){
			if( !AmigoCommunication.MonitorMode ){
				if( event.getAction()==MotionEvent.ACTION_DOWN ){
					startMonitor.setBackgroundResource(R.drawable.monitor_start_press);
				}
				else if( event.getAction()==MotionEvent.ACTION_UP ){
					rv=new rev();
					ifo=new infoth();
					new Thread(rv).start();
					new Thread(ifo).start();
					
					AmigoCommunication.MonitorMode = true;
					startMonitor.setBackgroundResource(R.drawable.monitor_stop);
				}
			}
			else if( AmigoCommunication.MonitorMode ){
				if( event.getAction()==MotionEvent.ACTION_DOWN ){
					startMonitor.setBackgroundResource(R.drawable.monitor_stop_press);
				}
				else if( event.getAction()==MotionEvent.ACTION_UP ){
					try {
						BluetoothService.Amigo.stoptravel();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					rv.setstop(true);
					ifo.setstop(true);
					
					AmigoCommunication.MonitorMode = false;
					startMonitor.setBackgroundResource(R.drawable.monitor_start);
				}
			}
		}
		return false;
	}
	
    int[] path=new int [8];
    int thc=0;
    connPC pc=new connPC();
    
    class rev implements Runnable{
    	public boolean stop=false;
	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int mode=-1;
			while(stop==false){
				try {
					Log.i("path","pc");
					pc=new connPC();
					pc.start();
					Log.i("path","pcstart");
					int x=0;
					while(pc.getlogin()==false){
						Thread.sleep(1000);
						x++;
						if(x>4){
							pc=new connPC();
							pc.start();
							Thread.sleep(1000);
							if(pc.getlogin()==false){
								continue;
							}
						}
					}
					Log.i("path","login"+pc.getlogin()+"1");
					
					int cx=0;
					
					Thread.sleep(1500);
					
					int dx=connPC.pcin.readInt();
					Log.i("path","length");
					path[0]=-5;
					path[1]=-5;
					path=new int[dx];
					while (cx<dx) {
				          path[cx] = connPC.pcin.readInt();
				          cx++;
				    }
					
					if(BluetoothService.Amigo.checktr()==true){//travel unstart or stop
						BluetoothService.Amigo.starttravel(path);
						for(int i=0;i<path.length;i++){
							Log.i("path",""+path[i]);
						}
					}
				}//try
				catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					 Log.i("path","monitor catch"+pc.getlogin());
					
					 e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					 Log.i("path","monitor catch"+pc.getlogin());
					e.printStackTrace();
				}
			
				try {
					connPC.pcsock.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}//while
		}//run
		
		public void setstop(boolean T){
			stop=T;
			Thread.currentThread().interrupt();
			Thread.currentThread();
			while(Thread.interrupted()==false){}
			stop=T;
		}
	}
	class infoth implements Runnable{
		Socket infosock=new Socket();
		PrintWriter infoout =null;
		DataInputStream drivein =null;
		drive dr=new drive();
		public boolean infostop=false;
		
		@Override
		public void run() {
			InetAddress severInetAddr;
			try {
				severInetAddr = InetAddress.getByName("120.105.129.101");
				infosock = new Socket(severInetAddr,405);
				infoout=new PrintWriter(new OutputStreamWriter(infosock.getOutputStream())); 
				drivein = new DataInputStream(infosock.getInputStream());
				
				new Thread(dr).start();
				
				while(infostop==false){
					if(BluetoothService.Amigo.checktr()==true){
						reach=true;
					}
					else {
						reach=false;
					}
					
					infoout.println("Battery: "+PacketReceiver.mAmigoInfo.getBattery()+"Stall: "+PacketReceiver.mAmigoInfo.getstall()
							+"reach: "+reach);
					infoout.flush();
					
					Thread.sleep(1000);
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void setstop(boolean T){
			infostop=T;
			Thread.currentThread().interrupt();
			while(Thread.currentThread().isInterrupted()!=true){}
			infostop=T;
			dr.setstop(T);
		}
		
		public boolean getstop(){
			return infostop;
		}
		
		class drive implements Runnable{
			boolean stop=false;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(stop==false){
					char comm;
					try {
						comm = drivein.readChar();
						if(comm=='w'){
							BluetoothService.Amigo.setTransVelocity(250);
							Thread.sleep(1000);
							BluetoothService.Amigo.setTransVelocity(0);
						}
						if(comm=='s'){
							BluetoothService.Amigo.setTransVelocity(-250);
							Thread.sleep(1000);
						}
						if(comm=='a'){
							BluetoothService.Amigo.setRelativeHeading(30);;
							Thread.sleep(1000);
						}
						if(comm=='d'){
							BluetoothService.Amigo.setRelativeHeading(-30);;
							Thread.sleep(1000);
						}
					
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			public void setstop(boolean T){
				stop=T;
				Thread.currentThread().interrupt();
				while(Thread.currentThread().isInterrupted()!=true){}
				stop=T;
			}
		}
	}
	
}
