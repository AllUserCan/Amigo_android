package com.control.amigo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceView;

public class MonitorService extends Service {
	public static final String ACTION_MONITOR_WORK = "com.control.amigo.action.MONITOR_WORK";
	public static final String ACTION_MONITOR_WIFIPOS = "com.control.amigo.action.MONITOR_WIFIPOS";
	public static final String ACTION_MONITOR_WIFIPOS_STOP = "com.control.amigo.action.MONITOR_WIFIPOS_STOP";
	public static final String ACTION_MONITOR_MOBILECAM = "com.control.amigo.action.MONITOR_MOBILECAM";
	public static final String ACTION_MONITOR_MOBILECAM_STOP = "com.control.amigo.action.MONITOR_MOBILECAM_STOP";
	
	private Camera camera;
	private PendingIntent pi = null;
	private Socket wifiSocket, camSocket;
	private PrintWriter wifiOut = null;
	private OutputStream camOut = null;
	private WifiManager wifi;
	private List<ScanResult> list;
	private StringBuilder stringBuilder;
	public static int size, wififlag = 0, camflag = 0;
	private String serverAddr = "120.105.129.101", wifiPort = "861", camPort = "168";
	private boolean wifiRun = false, camRun = false;
	
	public static WifiPosStatus mWifiPosStatus = WifiPosStatus.stopped;
	public static MobileCamStatus mMobileCamStatus = MobileCamStatus.stopped;
	
	enum WifiPosStatus{ stopped, wificonnected }
	enum MobileCamStatus{ stopped, Camconnected }
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if( action.equals(ACTION_MONITOR_WORK) ){
			
		}
		else if( action.equals(ACTION_MONITOR_WIFIPOS) ){
			startWifiPosition();
		}
		else if( action.equals(ACTION_MONITOR_MOBILECAM) ){
			startCam();
		}
		else if( action.equals(ACTION_MONITOR_WIFIPOS_STOP) ){
			stopWifiPosition();
		}
		else if( action.equals(ACTION_MONITOR_MOBILECAM_STOP) ){
			stopCam();
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void startWifiPosition(){
		wifiRun = true;
		wififlag = 0;
		new Wifilevel().start();
	}
	
	public void stopWifiPosition(){
		wififlag = 1;
		unregisterReceiver(wifi_receiver);
	}
	
	private void startCam(){
		camRun = true;
		camflag = 0;
		
		camInit();
		new Cam().start();
	}
	
	private void stopCam(){
		camflag = 2;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	class Wifilevel extends Thread{
		@Override
		public void run(){
			while( wifiRun ){
				if( wififlag == 0 ){
					try{
	    				InetAddress severInetAddr=InetAddress.getByName(serverAddr);
	    				wifiSocket=new Socket(severInetAddr, Integer.parseInt(wifiPort));
	        			wifiOut = new PrintWriter(wifiSocket.getOutputStream());
	        			Log.i("wifiPosition", "Connect!!");
	        			
	        			mWifiPosStatus = WifiPosStatus.wificonnected;
	        			
	        			wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
						
						IntentFilter intent = new IntentFilter();
						intent.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
						registerReceiver(wifi_receiver, intent);
						
						wifi.startScan();
	        			
	        			wififlag = 2;
	        		}catch(Exception e){
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        			Log.e("WifiPosition", "Socket Connect Error!!");
	        			mWifiPosStatus = WifiPosStatus.stopped;
	        		}
				}
				else if( wififlag == 1 ){
					
					try {
						wifiRun = false;
						if( wifiOut != null ){
							wifiOut.close();
							wifiOut = null;
						}
						wifiSocket.close();
						
						camCancel();
						
						mWifiPosStatus = WifiPosStatus.stopped;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						Log.e("WifiPosition", "Socket Close Error!!");
					}
				}
				else if( wififlag == 2 ){
					
				}
			}	
		}
	}
	
	private BroadcastReceiver wifi_receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if( mWifiPosStatus.equals(WifiPosStatus.wificonnected) ){
				list = wifi.getScanResults();
				size = list.size();
				stringBuilder = new StringBuilder();
				for (int i = 0; i < list.size(); i++){
					//將ScanResult信息轉換成一個字符串包  
					//BSSID、SSID、capabilities、frequency、level
					stringBuilder.append("N"+(list.get(i)).toString()); 
				}
				stringBuilder.append("size:"+size);
				
				wifiOut.println(stringBuilder);
    			wifiOut.flush();
    			
    			wififlag = 0;
    			Log.e("WifiPosition", "sendWifiInfo");
			}
			
		}
	};
	
	class Cam extends Thread{
		@Override
		public void run(){
			while( camRun ){
				if( camflag == 0 ){
					try{
		    			InetAddress severAddr=InetAddress.getByName(serverAddr);
		    			camSocket=new Socket(severAddr, Integer.parseInt(camPort));
		    			camOut=camSocket.getOutputStream();
		    			
		    			mMobileCamStatus = MobileCamStatus.Camconnected;
		    			Log.e("Socket", "Cam Connecting");
		    			
		    			pi.send();
		    			
		    			camflag = 3;
		    		}catch(Exception e){
		    			Log.e("Socket", "Final fail!");
		    			mMobileCamStatus = MobileCamStatus.stopped;
		    		}
				}
				else if( camflag == 1 ){
					try{
						File myFile = getDir();
						String file_name = myFile.getPath() + File.separator + "test.jpeg";
						myFile=new File(file_name);
	    				if(myFile.exists()){
	    					byte[] mybytearray = new byte[(int) myFile.length()];
	    					FileInputStream fis = new FileInputStream(myFile);
	    					
	    					synchronized (fis) {
	    						BufferedInputStream bis = new BufferedInputStream(fis, 8*1024);
		    					bis.read(mybytearray, 0, mybytearray.length);
		    					bis.close();
							}
	    				    
	    					camOut.write(mybytearray, 0, mybytearray.length);  //輸出到電腦
	    					camOut.flush();
	    					
	    					Log.i("Socket", "send picture");
	    					
	    					camSocket.close();
	    					camflag = 0;
						}
	    				else Log.e("Socket", "Picture doesn't exist!");
	    				
					}catch(Exception e){
							Log.e("Socket", "camSocket: Error!", e);
					}
				}
				else if( camflag == 2 ){
					try {
						camRun = false;
						if( camOut != null ){
							camOut.close();
							camOut = null;
						}
						
						if( camSocket != null ){
							camSocket.close();
						}
						
						mMobileCamStatus = MobileCamStatus.stopped;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if( camflag == 3 ){
					
				}
				
			}
		}
	}
	
	private void camInit() {
		camera = openFacingBackCamera();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.vegetables_source.alarm");
		registerReceiver(camReceiver, filter);

		Intent intent = new Intent();
		intent.setAction("com.vegetables_source.alarm");
		pi = PendingIntent.getBroadcast(this, 0, intent, 0);
	}
	
	private void camCancel() {
		if (camera != null) {
			camera.release();
			camera = null;
		}
		
		pi.cancel();
		pi = null;
		unregisterReceiver(camReceiver);
	}
	
	BroadcastReceiver camReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if ("com.vegetables_source.alarm".equals(intent.getAction())) {

				if (camera != null) {
					SurfaceView dummy = new SurfaceView(getBaseContext());
					try {
						Camera.Parameters parameters=camera.getParameters();
						parameters.setPictureFormat(ImageFormat.JPEG);
						parameters.setPreviewSize(320, 240);
						camera.setParameters(parameters);
						camera.setPreviewDisplay(dummy.getHolder());
					} catch (IOException e) {
						e.printStackTrace();
					}
					camera.startPreview();
					
					PhotoHandler ph = new PhotoHandler();
					synchronized (ph) {
						camera.takePicture(null, null, ph);
					}
				}

			}

		}
	};
	
	private Camera openFacingBackCamera() {  
        Camera cam = null;  
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();  
        
        for (int camIdx=0, cameraCount= Camera.getNumberOfCameras(); camIdx<cameraCount; camIdx++) {  
            Camera.getCameraInfo(camIdx, cameraInfo);  
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {  
                try {  
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {  
                    e.printStackTrace();
                }  
            }  
        }  
  
        return cam;  
    }
	
	private File getDir() {
        File sdDir = Environment
          .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "ServiceCamera");
    }
	
	class PhotoHandler implements PictureCallback {  
		  
	    @Override  
	    public void onPictureTaken(byte[] data, Camera camera) {  
	  
	    	File pictureFileDir = getDir();

	        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
	        	Log.e("pictureFileDir", "Can't create directory to save image.");
	            return;

	        }
	    	
	    	Bitmap bmp=BitmapFactory.decodeByteArray(data, 0, data.length);
			//byte轉Bitmap
			FileOutputStream fop;
			try{
				String path=pictureFileDir.getPath() + File.separator + "test.jpeg";
				File pictureFile = new File(path);
				
				fop=new FileOutputStream(pictureFile);
				bmp.compress(Bitmap.CompressFormat.JPEG, 20, fop); //壓縮bitmap(格式, 輸出質量, 目標路徑);
				
				fop.close();
				System.out.println("picture store Successful!");
				
				camflag = 1;
			}catch(IOException e){
				e.printStackTrace();
			}
	    }
	    
	    private File getDir() {
	        File sdDir = Environment
	          .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	        return new File(sdDir, "ServiceCamera");
	    }
	    
	}
	
}
