package com.control.amigo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.control.amigo.BluetoothService.AmigoState;
import com.control.amigo.BluetoothService.BTState;
import com.control.amigo.drive.AmigoCommunication;
import com.example.amigo.R;

public class Wander extends Fragment implements OnTouchListener {
	private ImageButton startWander;
	
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
			warning = "�Ť�";
			if( BluetoothService.mAmigoState == AmigoState.stopped ){
				warning = warning + "�BAmigoBot";
			}
		}
		else {
			if( BluetoothService.mAmigoState == AmigoState.stopped ){
				warning = warning + "AmigoBot";
			}
		}
		
		if( (BluetoothService.mBTState == BTState.stopped || BluetoothService.mBTState == BTState.connecting) 
				|| BluetoothService.mAmigoState == AmigoState.stopped ){
			InputFragmentView = inflater.inflate(R.layout.unconnect_view, container, false);
			warning = warning + "�|���s�u";
			
			TextView warningtext = (TextView) InputFragmentView.findViewById(R.id.warningtext);
			warningtext.setText(warning);
		}
		else if( AmigoCommunication.MonitorMode ){
			InputFragmentView = inflater.inflate(R.layout.unconnect_view, container, false);
			warning = "MonitorMode is running!!!";
			
			TextView warningtext = (TextView) InputFragmentView.findViewById(R.id.warningtext);
			warningtext.setText(warning);
		}
		else {
			InputFragmentView = inflater.inflate(R.layout.wander_view, container, false);
			startWander = (ImageButton) InputFragmentView.findViewById(R.id.startwander);
			
			startWander.setOnTouchListener(this);
			
			if( AmigoCommunication.WanderMode ){
				startWander.setBackgroundResource(R.drawable.wander_stop);
			}
			else if( !AmigoCommunication.WanderMode ){
				startWander.setBackgroundResource(R.drawable.wander_start);
			}
		}
		
		return InputFragmentView;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if( v.getId()==R.id.startwander ){
			if( !AmigoCommunication.WanderMode ){
				if( event.getAction()==MotionEvent.ACTION_DOWN ){
					startWander.setBackgroundResource(R.drawable.wander_start_press);
				}
				else if( event.getAction()==MotionEvent.ACTION_UP ){
					try {
						BluetoothService.Amigo.startWanderMode();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					startWander.setBackgroundResource(R.drawable.wander_stop);
					AmigoCommunication.WanderMode = true;
				}
			}
			else if( AmigoCommunication.WanderMode ){
				if( event.getAction()==MotionEvent.ACTION_DOWN ){
					startWander.setBackgroundResource(R.drawable.wander_stop_press);
				}
				else if( event.getAction()==MotionEvent.ACTION_UP ){
					try {
						BluetoothService.Amigo.stopWanderMode();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					startWander.setBackgroundResource(R.drawable.wander_start);
					AmigoCommunication.WanderMode = false;
				}
			}
		}
		return false;
	}
	
}
